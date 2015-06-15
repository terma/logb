/*
 Copyright 2015 Artem Stasiuk

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

var App = angular.module("App", ["ngAnimate", "ngRoute"]);

App.config(function ($routeProvider) {
    $routeProvider.
        when("/tail/:app/:host?/:file*", {
            templateUrl: "tail.html",
            controller: "TailController"
        })
        .when("/ls/:app", {
            templateUrl: "ls.html",
            controller: "LsController"
        })
        .otherwise({
            templateUrl: "enter.html",
            controller: "EnterController"
        });
});

App.filter("humanSize", function () {
    return function (value) {
        if (value == undefined) return "";

        var oneMb = 1024 * 1024;
        var oneGb = oneMb * 1024;
        if (value > oneGb) {
            return (value / oneGb).toFixed(0) + "g";
        } else if (value > oneMb) {
            return (value / oneMb).toFixed(0) + "m";
        } else if (value > 1024) {
            return (value / 1024).toFixed(0) + "k";
        } else {
            return value + "b";
        }
    };
});

App.filter("humanDate", function () {
    return function (value) {
        if (value == undefined) return "";
        //return new Date(parseInt(value)).toGMTString();
        return new Date(parseInt(value)).toLocaleString();
    };
});

App.factory("apps", function ($rootScope, $http) {
    $rootScope.apps = undefined;
    return {
        load: function () {
            $http({
                url: "app",
                headers: {"Content-Type": "application/json"}
            }).success(function (res) {
                $rootScope.apps = res.apps;
            }).error(function (res) {
                alert("OPA!");
            });
        }
    }
});

App.controller("EnterController", function (apps) {
    apps.load();
});

App.controller("LsController", function ($scope, $http, $timeout, $routeParams, apps) {
    function cancelCheck() {
        if ($scope.checkPromise) $timeout.cancel($scope.checkPromise);
    }

    function scheduleCheck() {
        cancelCheck();
        $scope.checkPromise = $timeout($scope.check, 5000);
    }

    $scope.$on("$destroy", cancelCheck);

    apps.load();

    $scope.app = $routeParams.app;

    $scope.lastUsage = Date.now();
    $scope.inactive = undefined;

    $scope.parseQuery = function (string) {
        if (!string) return {app: undefined, fileName: undefined, content: undefined};

        var contentPrefix = "content: ";
        var contentIndex = string.indexOf(contentPrefix);

        var file = contentIndex < 0 ? string : string.substring(0, contentIndex > 1 ? contentIndex - 1 : 0).realOrNull();
        var content = contentIndex < 0 ? undefined : string.substring(contentIndex + contentPrefix.length).realOrNull();

        function parse(string) {
            if (!string) return undefined;

            var regexPrefix = "regex: ";
            var regexIndex = string.indexOf(regexPrefix);
            if (regexIndex > -1) return {type: "regex", value: string.substring(regexPrefix.length)};
            else return {type: "plain", value: string};
        }

        file = parse(file);
        content = parse(content);

        return {app: undefined, file: file, content: content};
    };

    $scope.backToActive = function () {
        $scope.lastUsage = Date.now();
        $scope.inactive = undefined;
        $scope.check();
    };

    $scope.$watch("inactive", fixHeaderContent);
    $scope.$watch("filter", function () {
        $scope.check();
    });

    $scope.check = function () {
        if (Date.now() - $scope.lastUsage > 15 * 60 * 1000) {
            //if (Date.now() - $scope.lastUsage > 1000) {
            $scope.inactive = true;
            scheduleCheck();
            return;
        }

        var request = $scope.parseQuery($scope.filter);
        request.app = $scope.app;

        $http({
            url: "list",
            method: "post",
            data: request,
            headers: {"Content-Type": "application/json"}
        }).success(function (res) {
            scheduleCheck();
            $scope.logs = res;
        }).error(function (res) {
            scheduleCheck();
            $scope.logs = [{name: "can't connect"}];
        });
    };

    $scope.check();
});

App.controller("TailController", [
    "$scope", "$http", "$timeout", "$routeParams",
    function ($scope, $http, $timeout, $routeParams) {
        $scope.log = {
            app: $routeParams.app,
            host: $routeParams.host,
            file: $routeParams.file
        };

        $scope.removeFile = function () {
            if (confirm("Remove file " + $scope.log.file + "?")) {
                $http({
                    url: "log",
                    method: "DELETE",
                    data: {
                        app: $scope.log.app,
                        host: $scope.log.host,
                        file: $scope.log.file
                    }
                }).success(function () {
                    window.close();
                });
            }
        };

        $scope.viewport = {
            start: undefined,
            length: undefined
        };

        var PIECE_SIZE = 10000;

        $scope.lastUsage = Date.now();
        $scope.inactive = undefined;

        $scope.parseQuery = function (string) {
            if (!string) return {app: undefined, fileName: undefined, content: undefined};

            var contentPrefix = "content: ";
            var contentIndex = string.indexOf(contentPrefix);

            var file = contentIndex < 0 ? string : string.substring(0, contentIndex > 1 ? contentIndex - 1 : 0).realOrNull();
            var content = contentIndex < 0 ? undefined : string.substring(contentIndex + contentPrefix.length).realOrNull();

            function parse(string) {
                if (!string) return undefined;

                var regexPrefix = "regex: ";
                var regexIndex = string.indexOf(regexPrefix);
                if (regexIndex > -1) return {type: "regex", value: string.substring(regexPrefix.length)};
                else return {type: "plain", value: string};
            }

            file = parse(file);
            content = parse(content);

            return {app: undefined, file: file, content: content};
        };

        $scope.backToActive = function () {
            $scope.lastUsage = Date.now();
            $scope.inactive = undefined;
            $scope.check();
        };

        $scope.$watch("inactive", fixHeaderContent);

        $scope.showPrevious = function () {
            $scope.lastUsage = Date.now();

            $http({
                url: "log",
                method: "POST",
                data: {
                    app: $scope.log.app,
                    host: $scope.log.host,
                    file: $scope.log.file,
                    start: Math.max($scope.viewport.start - PIECE_SIZE, 0),
                    length: $scope.viewport.start
                },
                headers: {"Content-Type": "application/json"}
                //transformResponse: transformResponse
            }).success(function (res) {
                $scope.viewport.start = res.start;
                $scope.viewport.length += res.length;

                var parent = document.getElementById("log");
                parent.insertBefore(document.createTextNode(res.content), parent.firstChild);

                log.log("new piece of content");
                log.log(res);

            }).error(function (res) {
                //$scope.logs = [{name: "can't connect"}];
            });
        };

        $scope.tailLog = function () {
            $http({
                url: "log",
                method: "POST",
                data: {
                    app: $scope.log.app,
                    host: $scope.log.host,
                    file: $scope.log.file,
                    start: $scope.viewport.start != undefined ? $scope.viewport.start + $scope.viewport.length : undefined,
                    length: PIECE_SIZE
                },
                headers: {"Content-Type": "application/json"}
            }).success(function (res) {
                scheduleCheck();

                $scope.log.length = res.length;
                $scope.log.lastModified = res.lastModified;
                if ($scope.viewport.start == undefined) $scope.viewport.start = res.start;
                if ($scope.viewport.length == undefined) $scope.viewport.length = res.content.length;
                else $scope.viewport.length += res.content.length;

                console.log($scope.viewport);

                var parent = document.getElementById("log");
                parent.appendChild(document.createTextNode(res.content));

            }).error(function (res) {
                scheduleCheck();
            });
        };

        function scheduleCheck() {
            if ($scope.checkPromise) $timeout.cancel($scope.checkPromise);
            $scope.checkPromise = $timeout($scope.check, 5000);
        }

        $scope.check = function () {
            if (Date.now() - $scope.lastUsage > 15 * 60 * 1000) {
                //if (Date.now() - $scope.lastUsage > 1000) {
                $scope.inactive = true;
                scheduleCheck();
                return;
            }

            $scope.tailLog();
        };

        $scope.tailLog();
    }]);

$(window).on("resize", fixHeaderContent);

function fixHeaderContent() {
    setTimeout(function () {
        var height = $("#header").height();
        $("#content").css("margin-top", height);
        log.log("correct content margin: " + height);
    }, 0);
}

String.prototype.realOrNull = function () {
    return this.length > 0 ? this : undefined;
}
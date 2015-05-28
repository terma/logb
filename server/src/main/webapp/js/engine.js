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

var App = angular.module("App", []);

App.controller("GigaSpaceBrowserController", [
    "$scope", "$http", "$interval",
    function ($scope, $http, $interval) {

        var PIECE_SIZE = 10000;

        $scope.view = undefined;
        $scope.selectedLog = undefined;
        $scope.selectedApp = undefined;

        $scope.selectApp = function (app) {
            $scope.selectedLogName = undefined;
            $scope.selectedLog = undefined;
            var element = document.getElementById("log");
            while (element.firstChild) {
                element.removeChild(element.firstChild);
            }
            $scope.selectedApp = app;
            $scope.check();
        };

        $scope.selectLog = function (log) {
            $scope.view = "log";
            $scope.selectedLog = log;
            $scope.log = {start: undefined, length: undefined};
            $scope.tailLog()
        };

        $scope.showPrevious = function () {
            if (!$scope.selectedApp || !$scope.selectedLog) return;

            $http({
                url: "log",
                method: "POST",
                data: {
                    app: $scope.selectedApp.name,
                    host: $scope.selectedLog.host,
                    file: $scope.selectedLog.file,
                    start: Math.max($scope.log.start - PIECE_SIZE, 0),
                    length: $scope.log.start
                },
                headers: {"Content-Type": "application/json"}
                //transformResponse: transformResponse
            }).success(function (res) {
                $scope.log.start = res.start;
                $scope.log.length += res.length;

                var parent = document.getElementById("log");
                parent.insertBefore(document.createTextNode(res.content), parent.firstChild);

                log.log("new piece of content");
                log.log(res);

            }).error(function (res) {
                //$scope.logs = [{name: "can't connect"}];
            });
        };

        $scope.tailLog = function () {
            if (!$scope.selectedApp || !$scope.selectedLog) return;

            if ($scope.log.start == undefined) {
                $scope.log.length = 0;
                $scope.log.start = Math.max(0, $scope.selectedLog.length - PIECE_SIZE);
                log.log("init log");
                log.log($scope.log);
            }

            $http({
                url: "log",
                method: "POST",
                data: {
                    app: $scope.selectedApp.name,
                    host: $scope.selectedLog.host,
                    file: $scope.selectedLog.file,
                    start: $scope.log.start + $scope.log.length,
                    length: PIECE_SIZE
                },
                headers: {"Content-Type": "application/json"}
                //transformResponse: transformResponse
            }).success(function (res) {
                if (res.start === $scope.log.start + $scope.log.length) {
                    $scope.log.length += res.length;

                    document.getElementById("log")
                        .appendChild(document.createTextNode(res.content));

                    log.log("new piece of content");
                    log.log(res);
                } else {
                    log.log("wrong response");
                    log.log(res);
                }

            }).error(function (res) {
                //$scope.logs = [{name: "can't connect"}];
            });
        };

        $scope.check = function () {
            if (!$scope.selectedApp) {
                log.log("check - nothing todo");
                return;
            }

            if ($scope.view === "log") {
                log.log("check - check");
                $scope.tailLog();
            } else {
                $http({
                    url: "list",
                    method: "post",
                    data: {app: $scope.selectedApp.name},
                    headers: {"Content-Type": "application/json"}
                }).success(function (res) {
                    $scope.logs = res;
                    for (var i = 0; i < $scope.logs.length; i++) {
                        $scope.logs[i].lastModifiedDate = new Date(parseInt($scope.logs[i].lastModified));
                    }
                }).error(function (res) {
                    $scope.logs = [{name: "can't connect"}];
                });
            }
        };

        $scope.loadConfig = function () {
            $http({
                url: "app",
                headers: {"Content-Type": "application/json"}
            }).success(function (res) {
                $scope.view = "logs";
                $scope.apps = res.apps;
                $scope.check();
                $interval($scope.check, 5000);
            }).error(function (res) {
                alert("OPA!");
            });
        };

        $scope.loadConfig();
    }]);

Array.prototype.mkString = function (delimiter) {
    var result = "";
    for (var i = 0; i < this.length; i++) {
        if (i > 0) result += delimiter;
        result += this[i];
    }
    return result;
};
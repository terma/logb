var App = angular.module("App", []);

App.controller("GigaSpaceBrowserController", [
    "$scope", "$http", "$interval",
    function ($scope, $http, $interval) {

        $scope.view = undefined;
        $scope.selectedLog = undefined;
        $scope.selectedAppName = undefined;
        $scope.selectedApp = undefined;

        $scope.$watch("selectedAppName", function () {
            $scope.selectedLogName = undefined;
            $scope.selectedLog = undefined;
            var element = document.getElementById("log");
            while (element.firstChild) {
                element.removeChild(element.firstChild);
            }
            $scope.selectedApp = findApp($scope.selectedAppName);
            $scope.check();
        });

        function findApp(name) {
            if ($scope.apps) {
                for (var i = 0; i < $scope.apps.length; i++) {
                    if ($scope.apps[i].name === name) {
                        return $scope.apps[i];
                    }
                }
            }
            return undefined;
        }

        $scope.selectLog = function (log) {
            $scope.view = "log";
            $scope.selectedLog = log;
            $scope.log = {start: undefined, length: undefined};
            $scope.tailLog()
        };

        $scope.showPrevious = function () {
            if (!$scope.selectedLog) return;

            $http({
                url: "log",
                method: "POST",
                data: {
                    app: $scope.selectedAppName,
                    host: $scope.selectedLog.host,
                    file: $scope.selectedLog.file,
                    start: Math.max($scope.log.start - 1000, 0),
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
            if (!$scope.selectedLog) return;

            if ($scope.log.start == undefined) {
                $scope.log.length = 0;
                $scope.log.start = Math.max(0, $scope.selectedLog.length - 1000);
                log.log("init log");
                log.log($scope.log);
            }

            $http({
                url: "log",
                method: "POST",
                data: {
                    app: $scope.selectedAppName,
                    host: $scope.selectedLog.host,
                    file: $scope.selectedLog.file,
                    start: $scope.log.start + $scope.log.length,
                    length: 1000
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

            log.log("check - check");
            $scope.tailLog();

            $http({
                url: "list",
                method: "post",
                data: {app: $scope.selectedApp.name},
                headers: {"Content-Type": "application/json"}
            }).success(function (res) {
                $scope.logs = res;
                for (var i = 0; i < $scope.logs.length; i++) {
                    $scope.logs[i].lastModifiedDate = new Date($scope.logs[i].lastModified);
                }
            }).error(function (res) {
                $scope.logs = [{name: "can't connect"}];
            });
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
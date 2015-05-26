var App = angular.module("App", []);

App.controller("GigaSpaceBrowserController", [
    "$scope", "$http", "$interval",
    function ($scope, $http, $interval) {

        $scope.selectedLogName = undefined;

        $scope.selectLog = function (log) {
            $scope.selectedLogName = log.name;
            $scope.log = {start: undefined, length: undefined};
            $scope.tailLog()
        };

        function findSelectedLog() {
            if (!$scope.selectedLogName) return undefined;

            for (var i = 0; i < $scope.logs.length; i++) {
                if ($scope.logs[i].name === $scope.selectedLogName) {
                    return $scope.logs[i];
                }
            }

            return undefined;
        }

        $scope.showPrevious = function () {
            var selectedLog = findSelectedLog();
            if (!selectedLog) return undefined;

            $http({
                url: "log",
                method: "POST",
                data: {
                    name: selectedLog.name,
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
            var selectedLog = findSelectedLog();
            if (!selectedLog) return undefined;

            if ($scope.log.start == undefined) {
                $scope.log.length = 0;
                $scope.log.start = Math.max(0, selectedLog.length - 1000);
                log.log("init log");
                log.log($scope.log);
            }

            $http({
                url: "log",
                method: "POST",
                data: {
                    name: selectedLog.name,
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

        $scope.logs = function () {
            $scope.tailLog();

            $http({
                url: "list",
                //method: "POST",
                headers: {"Content-Type": "application/json"}
                //transformResponse: transformResponse
            }).success(function (res) {
                $scope.logs = res;
            }).error(function (res) {
                $scope.logs = [{name: "can't connect"}];
            });
        };

        $scope.logs();

        $interval($scope.logs, 5000);
    }]);

Array.prototype.mkString = function (delimiter) {
    var result = "";
    for (var i = 0; i < this.length; i++) {
        if (i > 0) result += delimiter;
        result += this[i];
    }
    return result;
};
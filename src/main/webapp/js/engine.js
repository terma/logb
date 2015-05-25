var App = angular.module("App", []);

App.controller("GigaSpaceBrowserController", [
    "$scope", "$http", "$interval",
    function ($scope, $http, $interval) {

        $scope.logs = function () {
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

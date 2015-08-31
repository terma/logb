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

var App = angular.module("App", ["ngAnimate"]);

App.controller("Controller", function ($http, $scope) {
    $scope.filterTimestampRange = 'last5m';
    $scope.filterTags = [];
    $scope.filterApp = void 0;

    $scope.loadTags = function (app) {
        $http({
            url: "tags",
            method: "post",
            headers: {"Content-Type": "application/json"},
            data: {app: app.name}
        }).success(function (res) {
            $scope.tags = res;
        }).error(function (res) {
            alert("OPA!");
        });
    };

    $scope.$watch('filterPattern', function () {
        $scope.loadStream();
    });

    $scope.loadStream = function () {
        if ($scope.filterApp === void 0) return;

        var timestampRange = {from: Date.now() - 15 * 60 * 1000, to: Date.now()};
        if ($scope.filterTimestampRange === 'last1h') {
            timestampRange = {from: Date.now() - 60 * 60 * 1000, to: Date.now()};
        } else if ($scope.filterTimestampRange === 'last2h') {
            timestampRange = {from: Date.now() - 2 * 60 * 60 * 1000, to: Date.now()};
        } else if ($scope.filterTimestampRange === 'last1d') {
            timestampRange = {from: Date.now() - 24 * 60 * 60 * 1000, to: Date.now()};
        }

        $http({
            url: "stream",
            method: "post",
            headers: {"Content-Type": "application/json"},
            data: {
                app: $scope.filterApp.name,
                tags: $scope.filterTags,
                pattern: $scope.filterPattern,
                from: timestampRange.from, to: timestampRange.to
            }
        }).success(function (res) {
            $scope.stream = res;
        }).error(function (res) {
            alert("OPA!");
        });
    };

    $scope.selectApp = function (app) {
        $scope.filterApp = app;
        $scope.loadTags(app);
        $scope.loadStream();
    };

    $scope.toggleTag = function (tag) {
        var index = $scope.filterTags.indexOf(tag);
        if (index > -1) {
            $scope.filterTags.splice(index, 1);
        } else {
            $scope.filterTags.push(tag);
        }
        $scope.loadStream();
    };

    $scope.selectOneTag = function (tag) {
        $scope.filterTags = [tag];
        $scope.loadStream();
    };

    $scope.selectTimestampRange = function (timestampRange) {
        $scope.filterTimestampRange = timestampRange;
        $scope.loadStream();
    };

    $http({
        url: "app",
        headers: {"Content-Type": "application/json"}
    }).success(function (res) {
        $scope.apps = res.apps;
    }).error(function (res) {
        alert("OPA!");
    });
});
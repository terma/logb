describe("UI query result presentation suite", function () {

    beforeEach(module("App"));

    var $scope = undefined;
    var $controller = undefined;

    beforeEach(inject(function ($rootScope, _$controller_) {
        $scope = $rootScope.$new();
        $controller = _$controller_;
    }));

    describe("Can parse user queries", function () {

        it("empty query as no constraints", function () {
            $controller("GigaSpaceBrowserController", {$scope: $scope});

            var result = $scope.parseQuery("");

            expect(result).toEqual({app: undefined, fileName: undefined, content: undefined});
        });

        it("undefined query as no constraints", function () {
            $controller("GigaSpaceBrowserController", {$scope: $scope});

            var result = $scope.parseQuery(undefined);

            expect(result).toEqual({app: undefined, fileName: undefined, content: undefined});
        });

        it("accept file name pattern", function () {
            $controller("GigaSpaceBrowserController", {$scope: $scope});

            var result = $scope.parseQuery("file-name pt");

            expect(result).toEqual({app: undefined, fileName: "file-name pt", content: undefined});
        });

        it("accept content pattern", function () {
            $controller("GigaSpaceBrowserController", {$scope: $scope});

            var result = $scope.parseQuery("content: file content");

            expect(result).toEqual({app: undefined, fileName: undefined, content: "file content"});
        });

        it("accept file name and content pattern", function () {
            $controller("GigaSpaceBrowserController", {$scope: $scope});

            var result = $scope.parseQuery("file name content: file content");

            expect(result).toEqual({app: undefined, fileName: "file name", content: "file content"});
        });
    });

});
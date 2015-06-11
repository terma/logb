describe("UI", function () {

    beforeEach(module("App"));

    var $scope = undefined;
    var $controller = undefined;

    beforeEach(inject(function ($rootScope, _$controller_) {
        $scope = $rootScope.$new();
        $controller = _$controller_;

        $controller("LsController", {$scope: $scope});
    }));

    describe("accept logs search query", function () {

        it("empty as no constraints", function () {
            var result = $scope.parseQuery("");

            expect(result).toEqual({app: undefined, file: undefined, content: undefined});
        });

        it("undefined as no constraints", function () {
            var result = $scope.parseQuery(undefined);

            expect(result).toEqual({app: undefined, file: undefined, content: undefined});
        });

        it("accept file name pattern", function () {
            var result = $scope.parseQuery("file-name pt");

            expect(result).toEqual({app: undefined, file: {type: "plain", value: "file-name pt"}, content: undefined});
        });

        it("accept content pattern", function () {
            var result = $scope.parseQuery("content: file content");

            expect(result).toEqual({app: undefined, file: undefined, content: {type: "plain", value: "file content"}});
        });

        it("file and content pattern", function () {
            var result = $scope.parseQuery("file name content: file content");

            expect(result).toEqual({
                app: undefined,
                file: {type: "plain", value: "file name"},
                content: {type: "plain", value: "file content"}
            });
        });

        it("accept regex for file", function () {
            var result = $scope.parseQuery("regex: \d+.?");
            expect(result).toEqual({app: undefined, file: {type: "regex", value: "\d+.?"}, content: undefined});
        });

        it("accept regex for content", function () {
            var result = $scope.parseQuery("content: regex: \d+.?");
            expect(result).toEqual({app: undefined, file: undefined, content: {type: "regex", value: "\d+.?"}});
        });

        it("accept regex for file and content", function () {
            var result = $scope.parseQuery("regex: \w* content: regex: \d+.?");
            expect(result).toEqual({
                app: undefined,
                file: {type: "regex", value: "\w*"},
                content: {type: "regex", value: "\d+.?"}
            });
        });

        //it("and file condition", function () {
        //    var result = $scope.parseQuery("file1 and file2");
        //    expect(result).toEqual({
        //        app: undefined,
        //        file: {type: "or", left: {type: "plain", value: "file1"}, right: {type: "plain", value: "file2"}},
        //        content: undefined
        //    });
        //});

        //assert("file1 or file2",
        //    {type: "or", left: {type: "plain", value: "file1"}, right: {type: "plain", value: "file2"}}, undefined);

        function assert(query, exceptedFile, expectedContent) {
            it(query, function () {
                expect($scope.parseQuery(query)).toEqual({
                    app: undefined,
                    file: exceptedFile,
                    content: expectedContent
                });
            });
        }
    });

});
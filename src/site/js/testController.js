function testGet($scope, $http){
    $http.get('http://localhost:8080/angular').
        success(function(data){
            $scope.info = data;
        });
}

/*function testGet($scope){
    json = {"test":"works!"};
    $scope.data = json;
}*/
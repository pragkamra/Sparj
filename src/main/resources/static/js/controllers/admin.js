myApp.requires.push('ng-fusioncharts');
myApp.controller('AdminController', ['$scope', '$http' , '$timeout', function($scope,$http,$timeout) 
{
var projs={};

$scope.myDataSource = {
                chart: {
                    caption: "Priority",
                    subCaption: "Charts",
                },
                data:[
              
            ]
              };
	 $http({
    method: "get",
    url: "https://sparj-seemaraheja.c9users.io/sparj//GETPROJDETAILS",
	}).then(function successCallback(response){
//console.log("Vivek verma"+response.data[0].Projects[0]);
	 $scope.myDataSource = {
                chart: {
                    caption: "Priority",
                    subCaption: "Charts",
                },
                data:[{
                    label: "Highest Priority",
                    value: response.data.Highest
                },
                {
                    label: "High Priority",
                    value: response.data.High
                }
            ]
              };
    console.log("vivek"+response.data);

},function errorCallback(response){

    console.log("err");
    console.log(response);

});    /*    */
	 $http({
    method: "get",
    url: "https://sparj-seemaraheja.c9users.io/sparj/GETPROJ?email=seema.makkar@gmail.com",
	}).then(function successCallback(response){
    $scope.projects = response.data;
    console.log("vivek"+response.data[0].key);

},function errorCallback(response){

    console.log("err");
    console.log(response.data);

});

myApp.requires.push('ng-fusioncharts');
myApp.controller('MyController', function ($scope) {
      
            });
$scope.sendMsg=function(msg){
	$scope.usermsg="";
$http.get('https://sparj-seemaraheja.c9users.io/sparj/dashboardtospark?roomname='+$scope.roomname+'&txt='+msg)
	  .then(function successCallback(response){
    console.log(response);
    $scope.first($scope.roomname);

},function errorCallback(response){

    console.log("err");
    console.log(response);
    $scope.first($scope.roomname);
	
});
}
$scope.spark=function(projname){
 //var id = projname.split("-")[0];
window.open("https://web.ciscospark.com")
 
 }
 $scope.first=function(projname){
 //var id = projname.split("-")[0];
 $scope.projjira=projname;
 $http({
    method: "get",
    url: "https://sparj-seemaraheja.c9users.io/sparj/GETPROJJIRA?projname="+projname,
	}).then(function successCallback(response){
    $scope.jiraname = response.data;
    console.log(response.data);

},function errorCallback(response){

    console.log("err");
    console.log(response);

});
 
 
	
 
 }
}]);
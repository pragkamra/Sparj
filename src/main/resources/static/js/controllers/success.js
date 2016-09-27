myApp.controller('SuccessController', ['$scope', '$http' ,'$timeout', '$interval','Authentication','$rootScope', function($scope,$http,$timeout,$interval,$Authentication,$rootScope) 
{
	currentUser=$rootScope.credential;
	getjira(currentUser);
	$interval(function(){ $scope.first($scope.roomname); },20000);
	$interval(function(){ $scope.jiralist(currentUser); },20000);
	function getjira(currentUser){
	 $http({
    method: "get",
    url: "https://sparj-seemaraheja.c9users.io/sparj/GETJIRA?email="+currentUser,
	}).then(function successCallback(response){
    $scope.members = response.data;
    console.log(response);

},function errorCallback(response){

    console.log("err");
    console.log(response);

});
}
$scope.jiralist=function(currentUser){
$http({
    method: "get",
    url: "https://sparj-seemaraheja.c9users.io/sparj/GETJIRA?email="+currentUser,
	}).then(function successCallback(response){
    $scope.members = response.data;
    console.log(response);

},function errorCallback(response){

    console.log("err");
    console.log(response);

});	
}
$scope.sendMsg=function(msg){
	$scope.usermsg="";
$http.get('https://sparj-seemaraheja.c9users.io/sparj/dashboardtospark?roomname='+$scope.roomname+'&txt='+msg)
	  .then(function successCallback(response){
    $scope.first($scope.roomname);
	console.log(response);
    

},function errorCallback(response){

    console.log("err");
    console.log(response);
	
});
     $timeout(function(){ $scope.first($scope.roomname); }, 10000);
}
	
$scope.first=function(jiraId){
 var id = jiraId.split("-")[0];
 var desc = jiraId.split("-")[1];
 $scope.desc=desc;
 $scope.roomname=jiraId;
 $http({
    method: "get",
    url: "https://sparj-seemaraheja.c9users.io/sparj/GETMessages?ID="+id,
	}).then(function successCallback(response){
    $scope.comments = response.data;
    console.log(response);
    $timeout(function() {
      var scroller = document.getElementById("scbo");
      scroller.scrollTop = scroller.scrollHeight;
    }, 0, false);

},function errorCallback(response){

    console.log("err");
    console.log(response);

});
 
 }
}]);
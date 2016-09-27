myApp.controller('RegistrationController',
  ['$scope','$http','$location','$rootScope',
  function($scope,$http,$location, $rootScope) {
  $rootScope.credential="";
  $scope.login=function(){
	  //alert("hi   "+$scope.user.username+"   "+$scope.user.password);
      $http.get("https://sparj-seemaraheja.c9users.io/sparj/login?username="+$scope.user.username+"&password="+$scope.user.password)
	  .success(function(data, status, headers, config) {
	    if($scope.user.username=="sparj")
	    {
	      $rootScope.credential = $scope.user.username;
	      $location.path('/admin');
	    }
	    else
	    {
	      $rootScope.credential = $scope.user.username;
		 $location.path('/success');
      }
		}).error(function(data, status, headers, config) {
			alert("Please register");
			$location.path('/register');
		});
    }
	  

  
  $scope.register=function(){
  $http.get("https://sparj-seemaraheja.c9users.io/sparj/register?sparkusername="+$scope.user.Spark_id+"&sparkpassword="+$scope.user.Spark_pass+"&jirausername="+$scope.user.jira_id+"&jirapassword="+$scope.user.Spark_pass+"&accessToken=null&refreshToken=null")
	  .success(function(data, status, headers, config) {
	      alert("SIGN UP COMPLETED");
	      $location.path('/');
		}).error(function(data, status, headers, config) {
			alert("Please register");
			$location.path('/register');
		});
    }
  $scope.logout=function(){
	  alert("logout");
  }

}]); // Controller

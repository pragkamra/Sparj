myApp.factory('Authentication', 
  ['$rootScope', '$location', '$http',
  function($rootScope, $location,$http) {
  var auth = {};

  return {
    login: function(user) {
      $http.post("http://10.203.166.143:7070/JIRASPARKInt/test/adservice/login?username="+user.username+"&password="+user.password)
	  .then(function(response)
	  { $scope.status = response.status;
	    console.log(response.status);
	  });
    if(status.response=="200")
    {
	  $location.path('/success');
      alert("welcome "+$scope.username);
    }
    else
    {
      $rootScope.message = error.message;
      alert("Invalid user");
    }
    }, //login

    logout: function() {
      $location.path('/login');
    }, //logout

    /*requireAuth: function() {
      return auth.$requireAuth();
    }, //require Authentication*/

    register: function(user) {
   alert("hit the code");
   $http({
    method: "POST",
    url: "http://gsd.company.com/jira/rest/auth/1/session",
    data:({"username": "user@company.com", "password": "passwordrandom"}),
    headers:{
        "Content-Type": "application/json"
    }
}).then(function successCallback(response){

    console.log(response);

},function errorCallback(response){

    console.log("err");
    console.log(response);

});
	  
	  
	  
	  /*$http.get("http://10.203.166.143:7070/JIRASPARKInt/test/adservice/register?username="+user.username+"&password="+user.username+"&ciscousr="+user.Spark_id+"&ciscopwd="+user.Spark_pass+"&jirausr="+user.jira_id+"&jirapwd="+user.jira-pass)
	  .then(function(response){ 
	  $scope.status = response.status;
      $rootScope.message = "Hi " + user.firstname +
        ", Thanks for registering";	  
	  });
	    if(status.response=="200")
	    {
	      alert("User registered "+$scope.username);
	    }
	    else if(status.response=="201")
	    {
	      alert("User already registered "+$scope.username);
	    }
	    else
	    {
	      alert("Something went wrong");
	    }
	  };
	 
	 
	 /* auth.$createUser({
        email: user.email,
        password: user.password
      }).then(function(regUser) {

        var regRef = new Firebase(FIREBASE_URL + 'users')
        .child(regUser.uid).set({
          date: Firebase.ServerValue.TIMESTAMP,
          regUser: regUser.uid,
          firstname: user.firstname,
          lastname: user.lastname,
          email:  user.email
        }); //user info

        $rootScope.message = "Hi " + user.firstname +
        ", Thanks for registering";
      }).catch(function(error) {
        $rootScope.message = error.message;
      }); // //createUser*/
    } // register
  };

}]); //factory
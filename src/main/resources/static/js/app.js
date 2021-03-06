var myApp = angular.module('myApp',
  ['ngRoute', 'ngAnimate']);
  //.constant('FIREBASE_URL', 'https://angreg77.firebaseIO.com/');


/*myApp.run(['$rootScope', '$location',
  function($rootScope, $location) {
    $rootScope.$on('$routeChangeError',
      function(event, next, previous, error) {
        if (error=='AUTH_REQUIRED') {
          $rootScope.message = 'Sorry, you must log in to access that page';
          $location.path('/login');
        } // AUTH REQUIRED
      }); //event info
  }]); *///run

	
myApp.config(['$routeProvider','$httpProvider', function($routeProvider,$httpProvider) {
  $routeProvider.
    when('/login', {
      templateUrl: 'views/login.html',
      controller: 'RegistrationController'
    }).   
	when('/admin', {
      templateUrl: 'views/admin.html',
      controller: 'AdminController'
    }).
    when('/register', {
      templateUrl: 'views/register.html',
      controller: 'RegistrationController'
    }).
    when('/success', {
      templateUrl: 'views/success.html',
      controller: 'SuccessController',
     /* resolve: {
        currentAuth: function(Authentication) {
          return Authentication.requireAuth();
        } //current Auth
      } *///resolve
    }).
    otherwise({
      redirectTo: '/login'
    });
	
	 $httpProvider.defaults.useXDomain = true;
	 delete $httpProvider.defaults.headers.common['X-Requested-With'];

}
]);
'use strict';

angular.module('pfad', ['ngRoute', 'ngResource', 'ngMaterial'])
        .config(['$routeProvider', function ($routeProvider) {
              $routeProvider
                      .when('/', {
                         templateUrl: 'views/landing.html',
                         controller: 'LandingPageController'
                      })
                      .when('/Activities', {
                         templateUrl: 'views/Activity/search.html',
                         controller: 'SearchActivityController'
                      })
//                      .when('/Activities/new', {
//                         templateUrl: 'views/Activity/detail.html',
//                         controller: 'NewActivityController'
//                      })
//                      .when('/Activities/edit/:ActivityId', {
//                         templateUrl: 'views/Activity/detail.html',
//                         controller: 'EditActivityController'
//                      })
                      .when('/Bookings', {
                         templateUrl: 'views/Booking/search.html',
                         controller: 'SearchBookingController'
                      })
                      .when('/Bookings/new', {
                         templateUrl: 'views/Booking/detail.html',
                         controller: 'NewBookingController'
                      })
                      .when('/Bookings/edit/:BookingId', {
                         templateUrl: 'views/Booking/detail.html',
                         controller: 'EditBookingController'
                      })
                      .when('/Configurations', {
                         templateUrl: 'views/Configuration/search.html',
                         controller: 'SearchConfigurationController'
                      })
//                      .when('/Configurations/new', {
//                         templateUrl: 'views/Configuration/detail.html',
//                         controller: 'NewConfigurationController'
//                      })
//                      .when('/Configurations/edit/:ConfigurationId', {
//                         templateUrl: 'views/Configuration/detail.html',
//                         controller: 'EditConfigurationController'
//                      })
                      .when('/Functions', {
                         templateUrl: 'views/Function/search.html',
                         controller: 'SearchFunctionController'
                      })
//                      .when('/Functions/new', {
//                         templateUrl: 'views/Function/detail.html',
//                         controller: 'NewFunctionController'
//                      })
//                      .when('/Functions/edit/:FunctionId', {
//                         templateUrl: 'views/Function/detail.html',
//                         controller: 'EditFunctionController'
//                      })
                      .when('/Members', {
                         templateUrl: 'views/Member/search.html',
                         controller: 'SearchMemberController'
                      })
                      .when('/Members/new', {
                         templateUrl: 'views/Member/detail.html',
                         controller: 'NewMemberController'
                      })
                      .when('/Members/edit/:MemberId', {
                         templateUrl: 'views/Member/detail.html',
                         controller: 'EditMemberController'
                      })
                      .when('/Payments', {
                         templateUrl: 'views/Payment/search.html',
                         controller: 'SearchPaymentController'
                      })
//                      .when('/Payments/new', {
//                         templateUrl: 'views/Payment/detail.html',
//                         controller: 'NewPaymentController'
//                      })
//                      .when('/Payments/edit/:PaymentId', {
//                         templateUrl: 'views/Payment/detail.html',
//                         controller: 'EditPaymentController'
//                      })
                      .when('/Squads', {
                         templateUrl: 'views/Squad/search.html',
                         controller: 'SearchSquadController'
                      })
                      .when('/Squads/new', {
                         templateUrl: 'views/Squad/detail.html',
                         controller: 'NewSquadController'
                      })
                      .when('/Squads/edit/:SquadId', {
                         templateUrl: 'views/Squad/detail.html',
                         controller: 'EditSquadController'
                      })
                      .otherwise({
                         redirectTo: '/'
                      });
           }])
        .controller('LandingPageController', function LandingPageController() {
        })
        .controller('NavController', function NavController($scope, $location) {
           $scope.matchesRoute = function (route) {
              var path = $location.path();
              return (path === ("/" + route) || path.indexOf("/" + route + "/") == 0);
           };
        });

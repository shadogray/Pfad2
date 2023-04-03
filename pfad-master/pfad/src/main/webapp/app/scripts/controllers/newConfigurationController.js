
angular.module('pfad').controller('NewConfigurationController', function ($scope, $location, locationParser, flash, ConfigurationResource) {
   $scope.disabled = false;
   $scope.$location = $location;
   $scope.configuration = $scope.configuration || {};

   $scope.roleList = [
      "admin",
      "gruppe",
      "leiter",
      "kassier",
      "vorstand",
      "none"
   ];

   $scope.typeList = [
      "simple",
      "query",
      "nativeQuery"
   ];


   $scope.save = function () {
      var successCallback = function (data, responseHeaders) {
         var id = locationParser(responseHeaders);
         flash.setMessage({
            'type': 'success',
            'text': 'The configuration was created successfully.'
         });
         $location.path('/Configurations');
      };
      var errorCallback = function (response) {
         if (response && response.data && response.data.message) {
            flash.setMessage({
               'type': 'error',
               'text': response.data.message
            },
            true);
         } else {
            flash.setMessage({
               'type': 'error',
               'text': 'Something broke. Retry, or cancel and start afresh.'
            },
            true);
         }
      };
      ConfigurationResource.save($scope.configuration, successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Configurations");
   };
});
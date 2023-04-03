

angular.module('pfad').controller('EditConfigurationController', function ($scope, $routeParams, $location, flash, ConfigurationResource) {
   var self = this;
   $scope.disabled = false;
   $scope.$location = $location;

   $scope.get = function () {
      var successCallback = function (data) {
         self.original = data;
         $scope.configuration = new ConfigurationResource(self.original);
      };
      var errorCallback = function () {
         flash.setMessage({
            'type': 'error',
            'text': 'The configuration could not be found.'
         });
         $location.path("/Configurations");
      };
      ConfigurationResource.get({
         ConfigurationId: $routeParams.ConfigurationId
      },
      successCallback, errorCallback);
   };

   $scope.isClean = function () {
      return angular.equals(self.original, $scope.configuration);
   };

   $scope.save = function () {
      var successCallback = function () {
         flash.setMessage({
            'type': 'success',
            'text': 'The configuration was updated successfully.'
         },
         true);
         $scope.get();
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
      $scope.configuration.$update(successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Configurations");
   };

   $scope.remove = function () {
      var successCallback = function () {
         flash.setMessage({
            'type': 'error',
            'text': 'The configuration was deleted.'
         });
         $location.path("/Configurations");
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
      $scope.configuration.$remove(successCallback, errorCallback);
   };

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

   $scope.get();
});
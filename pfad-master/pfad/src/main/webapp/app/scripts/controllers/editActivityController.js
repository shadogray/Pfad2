

angular.module('pfad').controller('EditActivityController', function ($scope, $routeParams, $location, flash, ActivityResource) {
   var self = this;
   $scope.disabled = false;
   $scope.$location = $location;

   $scope.get = function () {
      var successCallback = function (data) {
         self.original = data;
         $scope.activity = new ActivityResource(self.original);
      };
      var errorCallback = function () {
         flash.setMessage({
            'type': 'error',
            'text': 'The activity could not be found.'
         });
         $location.path("/Activities");
      };
      ActivityResource.get({
         ActivityId: $routeParams.ActivityId
      },
      successCallback, errorCallback);
   };

   $scope.isClean = function () {
      return angular.equals(self.original, $scope.activity);
   };

   $scope.save = function () {
      var successCallback = function () {
         flash.setMessage({
            'type': 'success',
            'text': 'The activity was updated successfully.'
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
      $scope.activity.$update(successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Activities");
   };

   $scope.remove = function () {
      var successCallback = function () {
         flash.setMessage({
            'type': 'error',
            'text': 'The activity was deleted.'
         });
         $location.path("/Activities");
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
      $scope.activity.$remove(successCallback, errorCallback);
   };

   $scope.typeList = [
      "Membership",
      "Camp",
      "Other"
   ];
   $scope.statusList = [
      "planned",
      "started",
      "finished",
      "cancelled"
   ];

   $scope.get();
});
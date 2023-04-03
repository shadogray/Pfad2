

angular.module('pfad').controller('EditFunctionController', function ($scope, $routeParams, $location, flash, FunctionResource) {
   var self = this;
   $scope.disabled = false;
   $scope.$location = $location;

   $scope.get = function () {
      var successCallback = function (data) {
         self.original = data;
         $scope.function = new FunctionResource(self.original);
      };
      var errorCallback = function () {
         flash.setMessage({
            'type': 'error',
            'text': 'The function could not be found.'
         });
         $location.path("/Functions");
      };
      FunctionResource.get({
         FunctionId: $routeParams.FunctionId
      },
      successCallback, errorCallback);
   };

   $scope.isClean = function () {
      return angular.equals(self.original, $scope.function);
   };

   $scope.save = function () {
      var successCallback = function () {
         flash.setMessage({
            'type': 'success',
            'text': 'The function was updated successfully.'
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
      $scope.function.$update(successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Functions");
   };

   $scope.remove = function () {
      var successCallback = function () {
         flash.setMessage({
            'type': 'error',
            'text': 'The function was deleted.'
         });
         $location.path("/Functions");
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
      $scope.function.$remove(successCallback, errorCallback);
   };

   $scope.exportRegList = [
      "true",
      "false"
   ];
   $scope.freeList = [
      "true",
      "false"
   ];

   $scope.get();
});
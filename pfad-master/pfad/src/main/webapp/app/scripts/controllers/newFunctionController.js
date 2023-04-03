
angular.module('pfad').controller('NewFunctionController', function ($scope, $location, locationParser, flash, FunctionResource) {
   $scope.disabled = false;
   $scope.$location = $location;
   $scope.function = $scope.function || {};

   $scope.exportRegList = [
      "true",
      "false"
   ];

   $scope.freeList = [
      "true",
      "false"
   ];


   $scope.save = function () {
      var successCallback = function (data, responseHeaders) {
         var id = locationParser(responseHeaders);
         flash.setMessage({
            'type': 'success',
            'text': 'The function was created successfully.'
         });
         $location.path('/Functions');
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
      FunctionResource.save($scope.function, successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Functions");
   };
});
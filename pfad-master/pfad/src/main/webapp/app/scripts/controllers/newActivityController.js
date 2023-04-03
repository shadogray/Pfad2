
angular.module('pfad').controller('NewActivityController', function ($scope, $location, locationParser, flash, ActivityResource, BookingResource) {
   $scope.disabled = false;
   $scope.$location = $location;
   $scope.activity = $scope.activity || {};

   $scope.bookingsList = BookingResource.queryAll(function (items) {
      $scope.bookingsSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("bookingsSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.activity.bookings = [];
         $.each(selection, function (idx, selectedItem) {
            var collectionItem = {};
            collectionItem.id = selectedItem.value;
            $scope.activity.bookings.push(collectionItem);
         });
      }
   });

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


   $scope.save = function () {
      var successCallback = function (data, responseHeaders) {
         var id = locationParser(responseHeaders);
         flash.setMessage({
            'type': 'success',
            'text': 'The activity was created successfully.'
         });
         $location.path('/Activities');
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
      ActivityResource.save($scope.activity, successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Activities");
   };
});
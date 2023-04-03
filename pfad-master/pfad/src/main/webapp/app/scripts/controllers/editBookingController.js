

angular.module('pfad').controller('EditBookingController', function ($scope, $routeParams, $location, flash, BookingResource, PaymentResource, MemberResource,
        ActivityResource, SquadResource) {
   var self = this;
   $scope.disabled = false;
   $scope.$location = $location;

   $scope.get = function () {
      var successCallback = function (data) {
         self.original = data;
         $scope.booking = new BookingResource(self.original);
         PaymentResource.queryAll(function (items) {
            $scope.paymentsSelectionList = $.map(items, function (item) {
               var wrappedObject = {
                  id: item.id
               };
               var labelObject = {
                  value: item.id,
                  text: item.id
               };
               if ($scope.booking.payments) {
                  $.each($scope.booking.payments, function (idx, element) {
                     if (item.id == element.id) {
                        $scope.paymentsSelection.push(labelObject);
                        $scope.booking.payments.push(wrappedObject);
                     }
                  });
                  self.original.payments = $scope.booking.payments;
               }
               return labelObject;
            });
         });
         MemberResource.queryAll(function (items) {
            $scope.memberSelectionList = $.map(items, function (item) {
               var wrappedObject = {
                  id: item.id
               };
               var labelObject = {
                  value: item.id,
                  text: item.id
               };
               if ($scope.booking.member && item.id == $scope.booking.member.id) {
                  $scope.memberSelection = labelObject;
                  $scope.booking.member = wrappedObject;
                  self.original.member = $scope.booking.member;
               }
               return labelObject;
            });
         });
         ActivityResource.queryAll(function (items) {
            $scope.activitySelectionList = $.map(items, function (item) {
               var wrappedObject = {
                  id: item.id
               };
               var labelObject = {
                  value: item.id,
                  text: item.id
               };
               if ($scope.booking.activity && item.id == $scope.booking.activity.id) {
                  $scope.activitySelection = labelObject;
                  $scope.booking.activity = wrappedObject;
                  self.original.activity = $scope.booking.activity;
               }
               return labelObject;
            });
         });
         SquadResource.queryAll(function (items) {
            $scope.squadSelectionList = $.map(items, function (item) {
               var wrappedObject = {
                  id: item.id
               };
               var labelObject = {
                  value: item.id,
                  text: item.id
               };
               if ($scope.booking.squad && item.id == $scope.booking.squad.id) {
                  $scope.squadSelection = labelObject;
                  $scope.booking.squad = wrappedObject;
                  self.original.squad = $scope.booking.squad;
               }
               return labelObject;
            });
         });
      };
      var errorCallback = function () {
         flash.setMessage({
            'type': 'error',
            'text': 'The booking could not be found.'
         });
         $location.path("/Bookings");
      };
      BookingResource.get({
         BookingId: $routeParams.BookingId
      },
      successCallback, errorCallback);
   };

   $scope.isClean = function () {
      return angular.equals(self.original, $scope.booking);
   };

   $scope.save = function () {
      var successCallback = function () {
         flash.setMessage({
            'type': 'success',
            'text': 'The booking was updated successfully.'
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
      $scope.booking.$update(successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Bookings");
   };

   $scope.remove = function () {
      var successCallback = function () {
         flash.setMessage({
            'type': 'error',
            'text': 'The booking was deleted.'
         });
         $location.path("/Bookings");
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
      $scope.booking.$remove(successCallback, errorCallback);
   };

   $scope.paymentsSelection = $scope.paymentsSelection || [];
   $scope.$watch("paymentsSelection", function (selection) {
      if (typeof selection != 'undefined' && $scope.booking) {
         $scope.booking.payments = [];
         $.each(selection, function (idx, selectedItem) {
            var collectionItem = {};
            collectionItem.id = selectedItem.value;
            $scope.booking.payments.push(collectionItem);
         });
      }
   });
   $scope.$watch("memberSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.booking.member = {};
         $scope.booking.member.id = selection.value;
      }
   });
   $scope.$watch("activitySelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.booking.activity = {};
         $scope.booking.activity.id = selection.value;
      }
   });
   $scope.$watch("squadSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.booking.squad = {};
         $scope.booking.squad.id = selection.value;
      }
   });
   $scope.statusList = [
      "created",
      "storno"
   ];

   $scope.get();
});
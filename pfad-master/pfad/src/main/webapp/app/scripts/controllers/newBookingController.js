
angular.module('pfad').controller('NewBookingController', function ($scope, $location, locationParser, flash, BookingResource, PaymentResource, MemberResource,
        ActivityResource, SquadResource) {
   $scope.disabled = false;
   $scope.$location = $location;
   $scope.booking = $scope.booking || {};

   $scope.paymentsList = PaymentResource.queryAll(function (items) {
      $scope.paymentsSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("paymentsSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.booking.payments = [];
         $.each(selection, function (idx, selectedItem) {
            var collectionItem = {};
            collectionItem.id = selectedItem.value;
            $scope.booking.payments.push(collectionItem);
         });
      }
   });

   $scope.memberList = MemberResource.queryAll(function (items) {
      $scope.memberSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("memberSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.booking.member = {};
         $scope.booking.member.id = selection.value;
      }
   });

   $scope.activityList = ActivityResource.queryAll(function (items) {
      $scope.activitySelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("activitySelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.booking.activity = {};
         $scope.booking.activity.id = selection.value;
      }
   });

   $scope.squadList = SquadResource.queryAll(function (items) {
      $scope.squadSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
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


   $scope.save = function () {
      var successCallback = function (data, responseHeaders) {
         var id = locationParser(responseHeaders);
         flash.setMessage({
            'type': 'success',
            'text': 'The booking was created successfully.'
         });
         $location.path('/Bookings');
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
      BookingResource.save($scope.booking, successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Bookings");
   };
});
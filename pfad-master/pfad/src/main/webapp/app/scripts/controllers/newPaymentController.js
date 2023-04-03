
angular.module('pfad').controller('NewPaymentController', function ($scope, $location, locationParser, flash, PaymentResource, MemberResource,
        BookingResource) {
   $scope.disabled = false;
   $scope.$location = $location;
   $scope.payment = $scope.payment || {};

   $scope.payerList = MemberResource.queryAll(function (items) {
      $scope.payerSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("payerSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.payment.payer = {};
         $scope.payment.payer.id = selection.value;
      }
   });

   $scope.finishedList = [
      "true",
      "false"
   ];

   $scope.acontoList = [
      "true",
      "false"
   ];

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
         $scope.payment.bookings = [];
         $.each(selection, function (idx, selectedItem) {
            var collectionItem = {};
            collectionItem.id = selectedItem.value;
            $scope.payment.bookings.push(collectionItem);
         });
      }
   });

   $scope.typeList = [
      "Membership",
      "Camp",
      "Donation",
      "Advert"
   ];


   $scope.save = function () {
      var successCallback = function (data, responseHeaders) {
         var id = locationParser(responseHeaders);
         flash.setMessage({
            'type': 'success',
            'text': 'The payment was created successfully.'
         });
         $location.path('/Payments');
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
      PaymentResource.save($scope.payment, successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Payments");
   };
});


angular.module('pfad').controller('EditPaymentController', function ($scope, $routeParams, $location, flash, PaymentResource, MemberResource,
        BookingResource) {
   var self = this;
   $scope.disabled = false;
   $scope.$location = $location;

   $scope.get = function () {
      var successCallback = function (data) {
         self.original = data;
         $scope.payment = new PaymentResource(self.original);
         MemberResource.queryAll(function (items) {
            $scope.payerSelectionList = $.map(items, function (item) {
               var wrappedObject = {
                  id: item.id
               };
               var labelObject = {
                  value: item.id,
                  text: item.id
               };
               if ($scope.payment.payer && item.id == $scope.payment.payer.id) {
                  $scope.payerSelection = labelObject;
                  $scope.payment.payer = wrappedObject;
                  self.original.payer = $scope.payment.payer;
               }
               return labelObject;
            });
         });
         BookingResource.queryAll(function (items) {
            $scope.bookingsSelectionList = $.map(items, function (item) {
               var wrappedObject = {
                  id: item.id
               };
               var labelObject = {
                  value: item.id,
                  text: item.id
               };
               if ($scope.payment.bookings) {
                  $.each($scope.payment.bookings, function (idx, element) {
                     if (item.id == element.id) {
                        $scope.bookingsSelection.push(labelObject);
                        $scope.payment.bookings.push(wrappedObject);
                     }
                  });
                  self.original.bookings = $scope.payment.bookings;
               }
               return labelObject;
            });
         });
      };
      var errorCallback = function () {
         flash.setMessage({
            'type': 'error',
            'text': 'The payment could not be found.'
         });
         $location.path("/Payments");
      };
      PaymentResource.get({
         PaymentId: $routeParams.PaymentId
      },
      successCallback, errorCallback);
   };

   $scope.isClean = function () {
      return angular.equals(self.original, $scope.payment);
   };

   $scope.save = function () {
      var successCallback = function () {
         flash.setMessage({
            'type': 'success',
            'text': 'The payment was updated successfully.'
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
      $scope.payment.$update(successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Payments");
   };

   $scope.remove = function () {
      var successCallback = function () {
         flash.setMessage({
            'type': 'error',
            'text': 'The payment was deleted.'
         });
         $location.path("/Payments");
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
      $scope.payment.$remove(successCallback, errorCallback);
   };

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
   $scope.bookingsSelection = $scope.bookingsSelection || [];
   $scope.$watch("bookingsSelection", function (selection) {
      if (typeof selection != 'undefined' && $scope.payment) {
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

   $scope.get();
});
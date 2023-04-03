
angular.module('pfad').controller('NewMemberController', function ($scope, $location, locationParser, flash, MemberResource, SquadResource,  FunctionResource) {
   $scope.disabled = false;
   $scope.$location = $location;
   $scope.member = $scope.member || {};

   MemberResource.gebJahr(function(list) {
	$scope.gebJahr = list;
   });

   MemberResource.gebMonat(function(list) {
	$scope.gebMonat = list;
   });

   MemberResource.gebTag(function(list) {
	$scope.gebTag = list;
   });
   
   $scope.geschlechtList = [
      "W",
      "M",
      "X"
   ];

   $scope.rolleList = [
      "Scout",
      "Leader",
      "Assistant",
      "Gilde",
      "Support",
      "undef"
   ];

   $scope.truppList = SquadResource.queryAll(function (items) {
      $scope.truppSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("truppSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.member.trupp = {};
         $scope.member.trupp.id = selection.value;
      }
   });

   $scope.funktionenList = FunctionResource.queryAll(function (items) {
      $scope.funktionenSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("funktionenSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.member.funktionen = [];
         $.each(selection, function (idx, selectedItem) {
            var collectionItem = {};
            collectionItem.id = selectedItem.value;
            $scope.member.funktionen.push(collectionItem);
         });
      }
   });

   $scope.aktivList = [
      "true",
      "false"
   ];

   $scope.aktivExternList = [
      "true",
      "false"
   ];

   $scope.trailList = [
      "true",
      "false"
   ];

   $scope.gildeList = [
      "true",
      "false"
   ];

   $scope.altERList = [
      "true",
      "false"
   ];

   $scope.infoMailList = [
      "true",
      "false"
   ];

   $scope.supportList = [
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
            'text': 'The member was created successfully.'
         });
         $location.path('/Members');
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
      MemberResource.save($scope.member, successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Members");
   };
});

angular.module('pfad').controller('NewSquadController', function ($scope, $location, locationParser, flash, SquadResource, MemberResource, MemberResource,
        MemberResource, MemberResource) {
   $scope.disabled = false;
   $scope.$location = $location;
   $scope.squad = $scope.squad || {};

   $scope.typeList = [
      "WIWO",
      "GUSP",
      "CAEX",
      "RARO"
   ];

   $scope.leaderMaleList = MemberResource.queryAll(function (items) {
      $scope.leaderMaleSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("leaderMaleSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.squad.leaderMale = {};
         $scope.squad.leaderMale.id = selection.value;
      }
   });

   $scope.leaderFemaleList = MemberResource.queryAll(function (items) {
      $scope.leaderFemaleSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("leaderFemaleSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.squad.leaderFemale = {};
         $scope.squad.leaderFemale.id = selection.value;
      }
   });

   $scope.assistantsList = MemberResource.queryAll(function (items) {
      $scope.assistantsSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("assistantsSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.squad.assistants = [];
         $.each(selection, function (idx, selectedItem) {
            var collectionItem = {};
            collectionItem.id = selectedItem.value;
            $scope.squad.assistants.push(collectionItem);
         });
      }
   });

   $scope.scoutsList = MemberResource.queryAll(function (items) {
      $scope.scoutsSelectionList = $.map(items, function (item) {
         return ({
            value: item.id,
            text: item.id
         });
      });
   });
   $scope.$watch("scoutsSelection", function (selection) {
      if (typeof selection != 'undefined') {
         $scope.squad.scouts = [];
         $.each(selection, function (idx, selectedItem) {
            var collectionItem = {};
            collectionItem.id = selectedItem.value;
            $scope.squad.scouts.push(collectionItem);
         });
      }
   });


   $scope.save = function () {
      var successCallback = function (data, responseHeaders) {
         var id = locationParser(responseHeaders);
         flash.setMessage({
            'type': 'success',
            'text': 'The squad was created successfully.'
         });
         $location.path('/Squads');
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
      SquadResource.save($scope.squad, successCallback, errorCallback);
   };

   $scope.cancel = function () {
      $location.path("/Squads");
   };
});
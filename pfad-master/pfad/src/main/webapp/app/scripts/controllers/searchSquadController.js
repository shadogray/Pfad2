

angular.module('pfad').controller('SearchSquadController', function ($scope, $http, $filter, SquadResource, MemberResource, MemberResource, MemberResource,
        MemberResource) {

   $scope.search = {};
   $scope.currentPage = 0;
   $scope.pageSize = 10;
   $scope.searchResults = [];
   $scope.filteredResults = [];
   $scope.pageRange = [];
   $scope.numberOfPages = function () {
      var result = Math.ceil($scope.filteredResults.length / $scope.pageSize);
      var max = (result == 0) ? 1 : result;
      $scope.pageRange = [];
      for (var ctr = 0; ctr < max; ctr++) {
         $scope.pageRange.push(ctr);
      }
      return max;
   };
   $scope.typeList = [
      "WIWO",
      "GUSP",
      "CAEX",
      "RARO"
   ];
   $scope.leaderMaleList = MemberResource.queryAll();
   $scope.leaderFemaleList = MemberResource.queryAll();

   $scope.performSearch = function () {
      $scope.searchResults = SquadResource.queryAll(function () {
         $scope.filteredResults = $filter('searchFilter')($scope.searchResults, $scope);
         $scope.currentPage = 0;
      });
   };

   $scope.previous = function () {
      if ($scope.currentPage > 0) {
         $scope.currentPage--;
      }
   };

   $scope.next = function () {
      if ($scope.currentPage < ($scope.numberOfPages() - 1)) {
         $scope.currentPage++;
      }
   };

   $scope.setPage = function (n) {
      $scope.currentPage = n;
   };

   $scope.performSearch();
});
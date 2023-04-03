angular.module('pfad').controller(
		'SearchMemberController',
		function($scope, $http, $filter, $q, $timeout, MemberResource,
				SquadResource, FunctionResource, PaymentResource,
				BookingResource) {

			$scope.filter = "";
			$scope.trupp = {};
			$scope.selectedItem = {};
			$scope.search = {};
			$scope.currentPage = 0;
			$scope.pageSize = 30;
			$scope.searchResults = [];
			$scope.filteredResults = [];
			$scope.pageRange = [];
			$scope.numberOfPages = function() {
				var result = Math.ceil($scope.filteredResults.length
						/ $scope.pageSize);
				var max = (result == 0) ? 1 : result;
				$scope.pageRange = [];
				for (var ctr = 0; ctr < max; ctr++) {
					$scope.pageRange.push(ctr);
				}
				return max;
			};

			$scope.geschlechtList = [ "W", "M", "X" ];
			$scope.rolleList = [ "Scout", "Leader", "Assistant", "Gilde",
					"Support", "undef" ];
			$scope.truppList = SquadResource.queryAll();
			// $scope.VollzahlerList = MemberResource.queryAll();
			$scope.aktivList = [ "true", "false" ];
			$scope.aktivExternList = [ "true", "false" ];
			$scope.trailList = [ "true", "false" ];
			$scope.gildeList = [ "true", "false" ];
			$scope.altERList = [ "true", "false" ];
			$scope.infoMailList = [ "true", "false" ];
			$scope.supportList = [ "true", "false" ];
			$scope.freeList = [ "true", "false" ];

			$scope.performSearch = function() {
				$scope.searchResults = MemberResource.queryAll({
					'start' : 0,
					'max' : 10
				}, function() {
					$scope.filteredResults = $filter('searchFilter')(
							$scope.searchResults, $scope);
					$scope.currentPage = 0;
				});
			};

			$scope.filtered = function(filter, trupp) {
				var results = MemberResource.filtered({
					"filter" : filter,
					"truppId" : trupp ? trupp.id : null
				});
				return results.$promise.then(function(list) {
					$scope.filteredResults = list;
					return list;
				});
			};

			$scope.selectedItemChanged = function(item) {
				console.log("selectedItem: name=" + item.name + ", vorname="
						+ item.vorname);
				console.log("SelectedItem: " + $scope.selectedItem);
				$scope.selectedItem = item;
			}

			$scope.previous = function() {
				if ($scope.currentPage > 0) {
					$scope.currentPage--;
				}
			};

			$scope.next = function() {
				if ($scope.currentPage < ($scope.numberOfPages() - 1)) {
					$scope.currentPage++;
				}
			};

			$scope.setPage = function(n) {
				$scope.currentPage = n;
			};

			$scope.performSearch();
		});
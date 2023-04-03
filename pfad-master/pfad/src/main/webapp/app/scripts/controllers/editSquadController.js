angular
	.module('pfad')
	.controller(
		'EditSquadController',
		function($scope, $routeParams, $location, flash, SquadResource, MemberResource) {
		    var self = this;
		    $scope.SquadId = $routeParams.SquadId;
		    $scope.disabled = false;
		    $scope.changed = false;
		    $scope.$location = $location;
		    $scope.scouts = [];
		    $scope.memberFilter = "";
		    $scope.selectedAssistant;

		    $scope.get = function() {
			var successCallback = function(data) {
			    self.original = data;
			    $scope.squad = new SquadResource(self.original);
			};
			var errorCallback = function() {
			    flash.setMessage({
				'type' : 'error',
				'text' : 'The squad could not be found.'
			    });
			    $location.path("/Squads");
			};
			SquadResource.get({
			    SquadId : $routeParams.SquadId
			}, successCallback, errorCallback);

			SquadResource.scouts({
			    SquadId : $routeParams.SquadId
			}, function(scouts) {
			    $scope.scouts = scouts;
			});
		    };
		    
		    $scope.filteredMembers = function(filter) {
			var results = MemberResource.filtered({
				"filter" : filter,
			});
			return results.$promise;
		    };

		    $scope.removeAssistant = function(chip, index) {
			//var removed = $scope.squad.assistants.splice(index, 1);
			$scope.changed = true;
		    };

		    $scope.addAssistant = function(chip, index) {
			//$scope.squad.assistants.push(chip);
			$scope.changed = true;
		    };

		    $scope.isClean = function() {
			if (!self.original) {
			    return true;
			}
			if (!$scope.changed && $scope.squad.assistants.length > 0 && $scope.squad.scouts.length > 0) {
			    $scope.changed = (self.original.assistants.length != $scope.squad.assistants.length || self.original.scouts.length != $scope.squad.scouts.length);
			}
			return !$scope.changed && angular.equals(self.original, $scope.squad);
		    };

		    $scope.save = function() {
			var successCallback = function() {
			    $scope.changed = false;
			    flash.setMessage({
				'type' : 'success',
				'text' : 'The squad was updated successfully.'
			    }, true);
			    $scope.get();
			};
			var errorCallback = function(response) {
			    if (response && response.data && response.data.message) {
				flash.setMessage({
				    'type' : 'error',
				    'text' : response.data.message
				}, true);
			    } else {
				flash.setMessage({
				    'type' : 'error',
				    'text' : 'Something broke. Retry, or cancel and start afresh.'
				}, true);
			    }
			};
			$scope.squad.$update(successCallback, errorCallback);
		    };

		    $scope.cancel = function() {
			$location.path("/Squads");
		    };

		    $scope.remove = function() {
			var successCallback = function() {
			    flash.setMessage({
				'type' : 'error',
				'text' : 'The squad was deleted.'
			    });
			    $location.path("/Squads");
			};
			var errorCallback = function(response) {
			    if (response && response.data && response.data.message) {
				flash.setMessage({
				    'type' : 'error',
				    'text' : response.data.message
				}, true);
			    } else {
				flash.setMessage({
				    'type' : 'error',
				    'text' : 'Something broke. Retry, or cancel and start afresh.'
				}, true);
			    }
			};
			$scope.squad.$remove(successCallback, errorCallback);
		    };

		    $scope.typeList = [ "WIWO", "GUSP", "CAEX", "RARO" ];

		    $scope.get();
		});
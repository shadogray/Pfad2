angular.module('pfad').factory('SquadResource', function($resource) {
    var resource = $resource('../rest/squads/:SquadId', {
	SquadId : '@id'
    }, {
	'queryAll' : {
	    method : 'GET',
	    isArray : true
	},
	'query' : {
	    method : 'GET',
	    isArray : false
	},
	'update' : {
	    method : 'PUT'
	},
	'assistants' : {
	    url : '../rest/squads/:SquadId/assistants',
	    method : 'GET',
	    isArray : true
	},
	'scouts' : {
	    url : '../rest/squads/:SquadId/scouts',
	    method : 'GET',
	    isArray : true
	},
    });
    return resource;
});
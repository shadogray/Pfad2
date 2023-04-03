angular.module('pfad').factory('ActivityResource', function ($resource) {
   var resource = $resource('../rest/activities/:ActivityId', {
      ActivityId: '@id'
   },
   {
      'queryAll': {
         method: 'GET',
         isArray: true
      },
      'query': {
         method: 'GET',
         isArray: false
      },
      'update': {
         method: 'PUT'
      }
   });
   return resource;
});
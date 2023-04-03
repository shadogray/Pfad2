angular.module('pfad').factory('ConfigurationResource', function ($resource) {
   var resource = $resource('../rest/configurations/:ConfigurationId', {
      ConfigurationId: '@id'
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
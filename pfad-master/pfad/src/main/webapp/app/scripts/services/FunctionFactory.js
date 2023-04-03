angular.module('pfad').factory('FunctionResource', function ($resource) {
   var resource = $resource('../rest/functions/:FunctionId', {
      FunctionId: '@id'
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
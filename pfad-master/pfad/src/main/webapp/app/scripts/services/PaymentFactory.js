angular.module('pfad').factory('PaymentResource', function ($resource) {
   var resource = $resource('../rest/payments/:PaymentId', {
      PaymentId: '@id'
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
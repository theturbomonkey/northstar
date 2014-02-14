define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'utils', 'appConfigModel'], function ($, jui, _, Backbone, utils, AppConfigModel) 
{
	var AppConfigCollection = Backbone.Collection.extend({
		
		url: function () {
			return ( utils.buildURL ( "app/service/appconfig" ) );
		},
		
		model: AppConfigModel,
		
		initialize: function (){
		},
		
		save: function(successFunction, errorFunction) {
		    Backbone.sync ( 'update', this, { success: successFunction, error: errorFunction });
		}
	});
	
	return AppConfigCollection;
});
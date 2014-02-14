define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'utils'], function ($, jui, _, Backbone, utils) 
{
	var AppInfoModel = Backbone.Model.extend({
		
		url: function () {
			return ( utils.buildURL ( "app/service/appinfo" ) );
		},
		
		appVersionNumber: function (){
			return this.get('appVersionNumber');
		},
		
		buildNumber: function (){
			return this.get('buildNumber');
		},
		
		projectURL: function (){
			return this.get('projectURL');
		},
		
		initialize: function (){
		}
	});
	
	return AppInfoModel;
});
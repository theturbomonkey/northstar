// The require.js configuration file for the wormhole application.
require.config(
{
	deps: ['main'],
	
	baseUrl: 'app/js',
	
	paths: {
		// Third Party Libraries
		backbone:                '../../resources/js/lib/backbone',
		datatables:              '../../resources/js/lib/jquery.dataTables.min',
		handlebars:              '../../resources/js/lib/handlebars',
		jquery:                  '../../resources/js/lib/jquery',
		jqueryui:                '../../resources/js/lib/jquery-ui',
		text:                    '../../resources/js/lib/text',
		underscore:              '../../resources/js/lib/underscore',
		utils:                   'util/utils',
		
		// Custom Modules
		datatablesExt:           'datatables-extensions',
		
		// Backbone Routers
		router:                  'router',
			
		// Backbone Models
		appConfigModel:          'model/appconfig-model',
		appInfoModel:            'model/appinfo-model',
		authorizationModel:      'model/authorization-model',
		
		// Backbone Collections
		appConfigCollection:     'collection/appconfig-collection',
			
		// Backbone Views
		appConfigView:           'view/appconfig-view',
		appConfigEdit:           'view/appconfig-edit',
		authorizationActionView: 'view/authorization-action-view',
		tabAboutView:            'view/tab-about-view',
		tabAuthorizationView:    'view/tab-authorization-view',
		tabConfigView:           'view/tab-config-view',
		tabConfigEditView:       'view/tab-config-edit-view',
		tabLogView:              'view/tab-log-view',
		tabsView:                'view/tabs-view'
	}, 
	
	shim: {
		jqueryui: {
			deps: ['jquery']
		},
		underscore: {
	      exports: "_"
	    },
	    backbone: {
	      deps: ['underscore', 'jquery'],
	      exports: 'Backbone'
	    },
	    handlebars: {
	      exports: 'Handlebars'
	    }
	}
});
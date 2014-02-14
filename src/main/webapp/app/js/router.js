define(['jquery', 'jqueryui', 'underscore', 'backbone', 'utils', 'tabAboutView', 'tabAuthorizationView', 'tabConfigView', 'tabConfigEditView', 'tabLogView', 'appInfoModel'], 
	   function($, jui, _, Backbone, utils, TabAboutView, TabAuthorizationView, TabConfigView, TabConfigEditView, TabLogView, AppInfoModel)
{
  var AppRouter = Backbone.Router.extend({
    routes: {
      '':                        'index',
      'authorizationDelete/:id': 'authorizationDelete',
      'authorizationEdit/:id':   'authorizationEdit',
      'editConfig':              'editConfig',
      'tabConfig':               'index',
      'tabConfigReturn':         'tabConfigReturn',
      'tabAuth':                 'tabAuth',
      'tabLog':                  'tabLog',
      'tabAbout':                'tabAbout'
    },
    
    authorizationDelete: function (id) {
    	// Make sure that the authorization view instance has been created, and that the authorization 
    	// tab is visible.
    	if ( ( typeof tabAuthorizationViewInstance != "undefined" ) && 
    		 ( tabAuthorizationViewInstance != null ) &&
    		 ( $("ul li.ui-state-active").index() == 1 ) )
    	{
    		// Tell the authorization tab view to perform the delete.
    		tabAuthorizationViewInstance.onDeleteButtonClicked ( id );
    	}
    	
    	// Remove this route from the URL. If we don't, then the user won't be able to click on the 
    	// same button if they cancel the delete event and try again. This is because routes are not
    	// events.
    	window.location = utils.buildURL ( "console#action" );
    },
    
    authorizationEdit: function (id) {
    	// Make sure that the authorization view instance has been created, and that the authorization 
    	// tab is visible.
    	if ( ( typeof tabAuthorizationViewInstance != "undefined" ) && 
    		 ( tabAuthorizationViewInstance != null ) &&
    		 ( $("ul li.ui-state-active").index() == 1 ) )
    	{
    		// Tell the authorization tab view to perform the edit.
    		tabAuthorizationViewInstance.onEditButtonClicked ( id );
    	}
    	
    	// Remove this route from the URL. If we don't, then the user won't be able to click on the 
    	// same button if they cancel the edit event and try again. This is because routes are not
    	// events.
    	window.location = utils.buildURL ( "console#action" );
    },
    
    editConfig: function () {
    	console.log ( "appRouter.editConfig" );
    	// Make sure that the config tab is actually selected. It's at index 0 in the tab
    	// list.
    	if ( $("ul li.ui-state-active").index() != 0 )
    	{
    		// The tab isn't active, which means that someone is navigating to the tab using
    		// the URL, which JQuery Tabs doesn't really appreciate. Manually activate 
    		// the config tab.
    		tabsViewInstance.$el.tabs("option", "active", 0);
    	}
    	
    	// Scroll to the top of the page. It's possible for the window to not
    	// be scrolled to the top when a tab is selected, and the window
    	// scrolls down to the anchor for the tab.
    	window.scrollTo(0,0);
    	
    	// Don't do anything else unless the configuration edit view instance doesn't
    	// exist. We are trying to make this safe for use with URL based navigation and 
    	// back buttons.
    	if ( ( typeof tabConfigEditInstance == "undefined" ) || ( tabConfigEditInstance == null ) )
    	{
	    	// TODO: Future role based security check.
	    	
	    	// Time to switch to the edit view. Make sure that if a view instance is visible, it is destroyed.
    		if ( ( typeof tabConfigViewInstance != "undefined") && ( tabConfigViewInstance != null ) )
        	{
		    	tabsViewInstance.$el.find('#tabConfig').empty();
		    	
		    	tabConfigViewInstance.destroy ();
		    	tabConfigViewInstance = null;
        	}
	    	
	    	// Fetch the latest application configuration data from the server.
	    	appConfigCollection.fetch ();
		      
	    	// Create a new edit configuration view instance. The render function will be called once the collection synce
	    	// is completed. See the view implementation for details.
		    tabConfigEditInstance = 
		    	new TabConfigEditView ( { el: tabsViewInstance.$el.find('#tabConfig'), collection: appConfigCollection } );
    	}
    },
    
    index: function () {
    	// Make sure that the config tab is actually selected. It's at index 0 in the tab
    	// list.
    	if ( $("ul li.ui-state-active").index() != 0 )
    	{
    		// The tab isn't active, which means that someone is navigating to the tab using
    		// the URL, which JQuery Tabs doesn't really appreciate. Manually activate 
    		// the config tab.
    		tabsViewInstance.$el.tabs("option", "active", 0);
    	}
    	
    	// Scroll to the top of the page. It's possible for the window to not
    	// be scrolled to the top when a tab is selected, and the window
    	// scrolls down to the anchor for the tab.
    	window.scrollTo(0,0);
    	
    	// This index route will be invoked after the tabs are initialized the first time, so 
    	// it doesn't always signify that a tab has been clicked. Refresh the routes data table
    	// only if the tab has been clicked.
    	if ( typeof firstCallToIndexOccurred == "undefined" )
    	{
    		// This is the first call to index and all we do is set the first call variable.
    		firstCallToIndexOccurred = true;
    	}
    	else if ( ( typeof tabConfigViewInstance == "undefined" ) || ( tabConfigViewInstance == null ) )
    	{
    		// Determine if there is an edit view instance that needs to be deleted. This can happen if
    		// the user is using URL-based application navigation.
    		if ( ( typeof tabConfigEditInstance != "undefined") && ( tabConfigEditInstance != null ) )
        	{
        		// Empty the configuration tab content.
            	tabsViewInstance.$el.find('#tabConfig').empty();
            	
        		// An edit was in progress. Destroy the edit configuration view 
        		tabConfigEditInstance.destroy ();
        		tabConfigEditInstance = null;
        	}
    		
    		// Fetch the latest application configuration data from the server.
        	appConfigCollection.fetch ();
        	
        	// Create the view instance.
    		tabConfigViewInstance = new TabConfigView ( { el: tabsViewInstance.$el.find('#tabConfig'), collection: appConfigCollection } );
    	}
    },
    
    tabAbout: function () {
    	// Make sure that the configuration tab is actually selected. It's at index 1 in the tab
    	// list.
    	if ( $("ul li.ui-state-active").index() != 3 )
    	{
    		// The tab isn't active, which means that someone is navigating to the tab using
    		// the URL, which JQuery Tabs doesn't really appreciate. Manually activate 
    		// the configuration tab.
    		tabsViewInstance.$el.tabs("option", "active", 3);
    	}
    	
    	// Scroll to the top of the page. It's possible for the window to not
    	// be scrolled to the top when a tab is selected, and the window
    	// scrolls down to the anchor for the tab.
    	window.scrollTo(0,0);
    	
    	// Determine if the tab view has been created.
    	if ( typeof tabAboutViewInstance == "undefined" )
    	{
    		// The tab view has never been created, so create it. Start with the model.
    		var appInfoModel = new AppInfoModel ();
    		appInfoModel.fetch ();
    		
    		// The model will invoke the render operation on the view once the model data
    		// has been fetched.
    		tabAboutViewInstance = new TabAboutView ( { el: tabsViewInstance.$el.find('#tabAbout'), model: appInfoModel } );
    	}
    },
    
    tabAuth: function () {
    	// Make sure that the configuration tab is actually selected. It's at index 1 in the tab
    	// list.
    	if ( $("ul li.ui-state-active").index() != 1 )
    	{
    		// The tab isn't active, which means that someone is navigating to the tab using
    		// the URL, which JQuery Tabs doesn't really appreciate. Manually activate 
    		// the configuration tab.
    		tabsViewInstance.$el.tabs("option", "active", 1);
    	}
    	
    	// Scroll to the top of the page. It's possible for the window to not
    	// be scrolled to the top when a tab is selected, and the window
    	// scrolls down to the anchor for the tab.
    	window.scrollTo(0,0);
    	
    	//TabAuthorizationView
    	// Determine if the tab view has been created.
    	if ( typeof tabAuthorizationViewInstance == "undefined" )
    	{
    		// The tab view has never been created, so create it.
    		tabAuthorizationViewInstance = new TabAuthorizationView ( { el: tabsViewInstance.$el.find('#tabAuth') } );
    	}
    },
    
    tabConfigReturn: function () {
    	// Make sure that the config tab is actually selected. It's at index 0 in the tab
    	// list.
    	if ( $("ul li.ui-state-active").index() != 0 )
    	{
    		// The tab isn't active, which means that someone is navigating to the tab using
    		// the URL, which JQuery Tabs doesn't really appreciate. Manually activate 
    		// the config tab.
    		tabsViewInstance.$el.tabs("option", "active", 0);
    	}
    	
    	// Scroll to the top of the page. It's possible for the window to not
    	// be scrolled to the top when a tab is selected, and the window
    	// scrolls down to the anchor for the tab.
    	window.scrollTo(0,0);
    	
    	// Make sure that an edit was in progress. 
    	if ( ( typeof tabConfigEditInstance != "undefined") && ( tabConfigEditInstance != null ) )
    	{
    		// Empty the configuration tab content.
        	tabsViewInstance.$el.find('#tabConfig').empty();
        	
    		// An edit was in progress. Destroy the edit configuration view 
    		tabConfigEditInstance.destroy ();
    		tabConfigEditInstance = null;
    		
    		// Call the index function to ensure that the read only configuration view is 
    		// created.
    		this.index ();
    	}
    },
    
    tabLog: function () {
    	// Make sure that the plug-ins tab is actually selected. It's at index 2 in the tab
    	// list.
    	if ( $("ul li.ui-state-active").index() != 2 )
    	{
    		// The tab isn't active, which means that someone is navigating to the tab using
    		// the URL, which JQuery Tabs doesn't really appreciate. Manually activate 
    		// the plug-ins tab.
    		tabsViewInstance.$el.tabs("option", "active", 2);
    	}
    	
    	// Scroll to the top of the page. It's possible for the window to not
    	// be scrolled to the top when a tab is selected, and the window
    	// scrolls down to the anchor for the tab.
    	window.scrollTo(0,0);
    	
    	// Determine if the tab view has been created.
    	if ( typeof tabLogViewInstance == "undefined" )
    	{
    		tabLogViewInstance = new TabLogView ( { el: tabsViewInstance.$el.find('#tabLog') } );
    	}
    }
  });

  var initialize = function () {
	  // Create an instance of our router.
      appRouter = new AppRouter;

      // Start backbone history tracking so that routing actually works.
      Backbone.history.start();
  };
  
  return { initialize: initialize };
});
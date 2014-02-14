define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'handlebars', 'text!../../app/pages/template/tabs-template.html', 'tabConfigView', 'appConfigCollection'], 
		 function ($, jui, _, Backbone, Handlebars, tabsTemplate, TabConfigView, AppConfigCollection) 
{
	var TabsView = Backbone.View.extend({
	    el: $('#tabs'),
	    
	    template: Handlebars.compile ( tabsTemplate ),
	    
	    events: {
		       'tabsactivate' : function ( e, ui ) {
		           // Since JQuery UI isn't aware of Backbone, and doesn't care about
		           // our Backbone router, we capture the tabactivate event so that we 
		           // the appropriate route in our Router when a tab is clicked. 
		           // This event would otherwise go un-noticed by the router, since
		           // the URL hash change is suppressed by the tabs implementation.
		           // So this ensures that our router is invoked, and that the route
		           // is accounted for in the document history.
		           var hash = ui.newTab.find('a[href^=#]').attr('href');
		           appRouter.navigate ( hash, {trigger: true} );
		       }
		},
	    
	    initialize: function () {
	    	console.log ('Initializing tabs...');
	    },
	    
	    render: function () {
	      console.log ('Rendering tabs...');
	      
	      // Render the content of our template into the body of the EL div.
	      this.$el.html ( this.template( this ) );
	      
	      // Initialize the display of the tabs.
	      this.$el.tabs();
	      
	      // Bind the child view to the first tab. I'd love to be able to do this in the router, but 
	      // the DOM isn't ready when the index route fires, so we have to do it this way.
	      // Also, we don't call the render function of the view, because the encapsulated collection
	      // fetch request has to first be fulfilled.
	      appConfigCollection = new AppConfigCollection ();
	      appConfigCollection.fetch ();
	      
	      tabConfigViewInstance = new TabConfigView ( { el: this.$('#tabConfig'), collection: appConfigCollection } );
	      
	      return this;
	    }
	  });
	
	  // Return the constructed view object.
	  return TabsView;
});
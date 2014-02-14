define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'handlebars', 'appConfigModel', 'appConfigCollection', 'appConfigView', 'text!../../app/pages/template/tab-config-template.html'], 
		 function ($, jui, _, Backbone, Handlebars, AppConfigModel, AppConfigCollection, AppConfigView, tabConfigTemplate) 
{
	var TabConfigView = Backbone.View.extend({
		template: Handlebars.compile ( tabConfigTemplate ),
		
	    initialize: function () {
	    	// Add the render function as a listener to the model. Once the model fetch request has
	    	// completed, then the render function will be invoked. The model object will have been
	    	// set on initialization.
	    	this.listenTo ( this.collection, 'sync', this.render );
	    	//this.collection.on ( 'sync', function () { this.render (); }, this );
	    },
	    
	    events: {
	    	'click #btnConfigEdit': 'onEditButtonClicked',
	    	'click #btnConfigRefresh': 'onRefreshButtonClicked'
	    },
	    
	    render: function () {
	      // Make sure that the root node is empty. It won't be if render is firing after a refresh.
	      this.$el.empty();
	      
	      // Hand-over the compiled template over to the view's root element.
	      this.$el.html ( this.template ( this ) );
	      
	      // Get the configuration item template that is located within the configuration 
	      // tab template.
	      var configItemTemplateHTML = this.$el.find ( '#tmplConfigItem' ).html();
	      
	      // We now have to replace all double parenthesis with angle brackets for use by Handlebars.
	      // Double parenthesis had to be used, because the angle brackets would have gone missing
	      // when the view template was added as the content to EL.
	      configItemTemplateHTML = configItemTemplateHTML.replace ( /\(\(/g, "{{" );
	      configItemTemplateHTML = configItemTemplateHTML.replace ( /\)\)/g, "}}" );
	     
	      // Compile the configuration item template once for use by all items.
	      var configItemTemplate = Handlebars.compile ( configItemTemplateHTML );
	      
	      // Render the content of our template into the body of the EL div.
	      // The value of EL is passed into the app during construction.
	      this.collection.each ( function ( appConfig ) {
	    	  // Pass the current model and the compiled configuration item template to a new instance of a
	    	  // configuration item view.
	    	  var appConfigView = new AppConfigView ( { model : appConfig, template : configItemTemplate } );
	    	  
	    	  // Append the resulting view HTML to the content panel within the EL of this view.
	    	  this.$el.find ( '#pnlConfigContent' ).append ( appConfigView.el );
	      }, this );

	      // Return this object so that chaining is possible.
	      return this;
	    },
	    
	    onEditButtonClicked: function (){
	    	// The Edit button was clicked in the view. Bubble the event up to the router.
	    	// This also ensures that the route hash is added to the URL.
	    	appRouter.navigate ( this.$el.find ( '#btnConfigEdit' ).attr ( "value" ), {trigger: true} );
	    },
	    
	    onRefreshButtonClicked: function (){
	    	// Have the collection refreshed. This will trigger the sync function within this view instance.
	    	this.collection.fetch ();
	    },
	    
	    // Deconstructor
	    destroy: function() {
	    	// Remove the view from the DOM.
	    	this.undelegateEvents();
	    	$(this).empty;  
	        this.unbind();
	    }
	  });
	
	  // Return the constructed view object.
	  return TabConfigView;
});
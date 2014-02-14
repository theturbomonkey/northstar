define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'handlebars', 'text!../../app/pages/template/tab-about-template.html', 'appInfoModel'], 
		 function ($, jui, _, Backbone, Handlebars, tabAboutTemplate, AppInfoModel) 
{
	var TabAboutView = Backbone.View.extend({
	    template: Handlebars.compile ( tabAboutTemplate ),
	    
	    initialize: function () {
	    	console.log ('Initializing the About tab...');
	    	
	    	// Add the render function as a listener to the model. The model object
	    	// will have been set on initialization.
	    	this.model.on ( 'sync', function () { this.render (); }, this );
	    },
	    
	    render: function () {
	      console.log ('Rendering the About tab...');
	      
	      // Render the content of our template into the body of the EL div.
	      // The value of EL is passed into the app during construction.
	      this.$el.html ( this.template ( this.model ) );
	      
	      return this;
	    }
	  });
	
	  // Return the constructed view object.
	  return TabAboutView;
});
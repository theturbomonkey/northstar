define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'handlebars', 'appConfigModel'], 
		 function ($, jui, _, Backbone, Handlebars, AppConfigModel) 
{
	var AppConfigView = Backbone.View.extend({		
	    initialize: function (options) {
	    	this.template = options.template;
	    	this.render ();
	    },
	    
	    render: function () {
	      this.$el.html ( this.template ( this.model ) );
	      return this;
	    }
	  });
	
	  // Return the constructed view object.
	  return AppConfigView;
});
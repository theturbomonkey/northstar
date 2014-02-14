define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'handlebars', 'appConfigModel'], 
		 function ($, jui, _, Backbone, Handlebars, AppConfigModel) 
{
	var AppConfigEdit = Backbone.View.extend({		
	    initialize: function (options) {
	    	this.templateText = options.templateText;
	    	this.templateInt = options.templateInt;
	    	this.templateConfidential = options.templateConfidential;
	    	this.templateBool = options.templateBool;
	    	this.templateDate = options.templateDate;
	    	this.render ();
	    },
	    
	    render: function () {
	        // Use the template that is appropriate to the type of the model.
	    	if ( this.model.confidentialVal() == 1 )
	    	{
	    		// Use the confidential template.
	    		this.$el.html ( this.templateConfidential ( this.model ) );
	    	}
	    	else
	    	{
		    	switch ( this.model.appConfigType() )
		    	{
			    	case 0:
			    		this.$el.html ( this.templateInt ( this.model ) );
			    		break;
			    	case 1:
			    		this.$el.html ( this.templateText ( this.model ) );
			    		break;
			    	case 2:
			    		this.$el.html ( this.templateDate ( this.model ) );
			    		break;
			    	case 3:
			    		this.$el.html ( this.templateBool ( this.model ) );
			    		break;
		    	}
	    	}

	        return this;
	    }
	  });
	
	  // Return the constructed view object.
	  return AppConfigEdit;
});
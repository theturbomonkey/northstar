define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'handlebars', 'utils', 'text!../../app/pages/template/tab-log-template.html'], 
		 function ($, jui, _, Backbone, Handlebars, utils, tabLogTemplate) 
{
	var TabLogView = Backbone.View.extend({
		template: Handlebars.compile ( tabLogTemplate ),
		
	    initialize: function () {
	    	this.render ();
	    },
	    
	    events: {
	    	'click #btnLogReset': 'onResetButtonClicked',
	    	'click #btnLogRefresh': 'onRefreshButtonClicked'
	    },
	    
	    render: function () {
	      // Make sure that the root node is empty. It won't be if render is firing after a refresh.
	      this.$el.empty();
	      
	      // Hand-over the compiled template over to the view's root element.
	      this.$el.html ( this.template ( this ) );
	      
	      // Use the refresh capability to have the text area loaded.
	      this.onRefreshButtonClicked ();

	      // Return this object so that chaining is possible.
	      return this;
	    },
	    
	    onResetButtonClicked: function (){
	    	$.ajax ( {
	    		context: this,
	    		type: "DELETE",
	    		url: 'app/service/log',
	    		dataType: 'json',
	    		success: function ( jsonResult ) {
	    			// Make sure that the server indicated that the request was valid. Also,
	    			// make sure that a result object was returned, otherwise what we have here
	    			// is a session timeout.
	    			if ( ( typeof jsonResult.result != "undefined") && ( jsonResult.result != null ) )
	    			{
		    			if ( jsonResult.result )
		    			{
			    			// Delegate the refresh to the refresh button handler.
			    			this.onRefreshButtonClicked ();
		    			}
		    			else
		    			{
		    				// Insert the error message into the text area.
		    				this.$el.find ( "#taLog" ).val ( jsonResult.message );
		    			}
	    			}
	    			else
	    			{
	    				// The response wasn't what we were expecting. There must be a 
	    				// session timeout.
	    				window.location = utils.buildURL ( "login" );
	    			}
	    		},
	    		error: function (xhr, ajaxOptions, thrownError) {
	    			alert ( "A server error communication error occurred: " + thrownError );
	    		}
	    	} );
	    },
	    
	    onRefreshButtonClicked: function (){
	    	// Clear the contents of the text area.
	    	this.$el.find ( "#taLog" ).val ( "Loading..." );
	    	
	    	// Make a call to the server to get the contents of the log file.
	    	$.ajax ( {
	    		context: this,
	    		type: "GET",
	    		url: 'app/service/log',
	    		dataType: 'text',
	    		success: function ( data ) {
	    			// Insert the data into the text area.
	    			this.$el.find ( "#taLog" ).val ( data );
	    		},
	    		error: function (xhr, ajaxOptions, thrownError) {
	    			alert ( "A server error communication error occurred: " + thrownError );
	    		}
	    	} );
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
	  return TabLogView;
});
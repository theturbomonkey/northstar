define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'handlebars', 'utils', 'text!../../app/pages/template/authorization-action-template.html', 'authorizationModel'], 
		 function ($, jui, _, Backbone, Handlebars, utils, authorizationActionTemplate, AuthorizationModel) 
		 {
		 	var AuthorizationActionView = Backbone.View.extend({
		 		template: Handlebars.compile ( authorizationActionTemplate ),
		 		
		 		initialize: function () {
		 			if ( typeof this.model == "undefined" )
		 			{
		 				// No model was specified, which means that this is an authorization add
		 				// view. Create a new instance of the model, and call the render method.
		 				this.model = new AuthorizationModel ( { authAction: 'Add' } );
		 				
		 				// Change the action button name for the dialog to "Add".
		 				this.buttons[1].text = "Add";
		 				
		 				// Render the view.
		 				this.render();
		 			}
		 			else
		 			{
		 				// Change the action button name for the dialog to "Edit".
		 				this.buttons[1].text = "Save";
		 				
		 				// Start listening to the model sync event.
		 				this.listenTo ( this.model, 'sync', this.render );
		 				
		 				// Tell the model to fetch the record from the server.
		 				this.model.fetch ();
		 			}
		 	    },
		 	    
		 	    // Because of how the dialog is rendered in jqueryui, we have to define each of our
		 	    // buttons in an array, and bind them when the dialog is clicked.
		 	    buttons: [ 
		 	       {text:"Cancel", click:"onCancelButtonClicked", id: "btnAuthCancel"},
		 	       {text:"Action", click:"onActionButtonClicked", id: "btnAuthAction"}
		 	    ],
		 	    
		 	    render: function(){
		 	    	// Stop listening to the model sync, or it will cause problems during a save.
		 	    	this.stopListening ( this.model );
		 	    	
		 	        // Set the template mark-up as the content of our EL div.
		 	        this.$el.html ( this.template( this.model ) );
		 	        
		 	        // Standard Backbone view events won't fire when we use jqueryui dialogs.
		 	        // This is because the div for the dialog is actually appended to the body
		 	        // element for the entire window. Therefore, we insert our own button
		 	        // definitions.
		 	        var dialogButtons = _.result ( this, "buttons" );
		 	        dialogButtons = 
		 	        	_.map( dialogButtons, 
		 	        		   function(obj) {
		 	        		     obj = _.clone ( obj );
		 	        		     obj.click = _.bind ( this [obj.click], this );
		 	        		     return obj;
		 	        	}, this);
		 	        this.actionDialog = this.$el.find ( "#addAuthorizationDialog" ).dialog ( {
		 	        	resizable: true,
		 	        	autoOpen:true,
		 	        	modal: true,
		 	        	width:640,
		 	        	height:240,
		 	        	buttons: dialogButtons
		 	        });
		 	        
		 	        return this;
		 	    },
		 	    
		 	    onActionButtonClicked: function () {
		 	    	// Clear any previously reported errros.
		 	    	$(this.actionDialog).find ( "#lblDialogAlert" ).empty ();
		 	    	
		 	    	// Make sure that both an email and description were specified.
		 	    	var authEmail = $(this.actionDialog).find ( "#authEmail" ).val().trim ();
		 	    	var authDesc = $(this.actionDialog).find ( "#authDesc" ).val().trim ();
		 	    	
		 	    	// Check the email address for correct format.
		 	    	if ( !utils.isValidEmailAddress ( authEmail ) && ( authEmail != "*@*" ) )
		 	    	{
		 	    		$(this.actionDialog).find ( "#lblDialogAlert" ).append ("Please enter a valid email address." );
		 	    	}
		 	    	else if ( authDesc.length <= 0 ) 
		 	    	{
		 	    		$(this.actionDialog).find ( "#lblDialogAlert" ).append ("Please enter a description." );
		 	    	}
		 	    	else
		 	    	{
		 	    		// All is well, update the model and have the content saved to the server.
		 	    		// Add ourselves as a listener to the sync of the model.
		 	    		this.listenTo ( this.model, 'sync', this.onModelSaveSuccess );
		 	    		this.listenTo ( this.model, 'syncError', this.onModelSaveError );
		 	    		this.model.update (authEmail, authDesc, true);
		 	    	}
		 	    },
		 	    
		 	    onCancelButtonClicked: function () {
		 	    	this.actionDialog.dialog ( "close" );
		 	    },
		 	    
		 	    onModelSaveError: function () {
		 	    	// Something bad happened during the save.
		 	    },
		 	    
		 	    onModelSaveSuccess: function (saveContext) {
		 	    	// Determine if the result was good or bad.
		 	    	if ( typeof saveContext.changed.result == "undefined" )
		 	    	{
		 	    		// No result was returned. This must mean that our session
		 	    		// is bad. Redirect the browser to the login page.
		 	    		window.location = utils.buildURL ( "login" );
		 	    	}
		 	    	else if ( saveContext.changed.result )
		 	    	{
		 	    		// The save was successful, so close the dialog.
		 	    		this.actionDialog.dialog ( "close" );
		 	    		
		 	    		// Trigger the save successful event to which the parent is listening.
		 	    		this.trigger ( "saveSuccess" );
		 	    	}
		 	    	else
		 	    	{
		 	    		// The save failed. Display the error message.
		 	    		$(this.actionDialog).find ( "#lblDialogAlert" ).empty ();
		 	    		$(this.actionDialog).find ( "#lblDialogAlert" ).append ( saveContext.changed.message );
		 	    	}
		 	    },
			    
			    // Deconstructor
			    destroy: function() {
			    	// Clean-up the model and dialog.
			    	if ( ( typeof this.model != "undefined" ) && ( this.model != null ) )
			    	{
			    		this.model.off ();
				    	delete this.model;
			    	}
			    	
			    	// Remove the DOM for the dialog. We do this here, because the DOM for the
			    	// dialog is created at the bottom of the "body" of the HTML form and not in the
			    	// view DOM space.
			    	if ( ( typeof this.actionDialog != "undefined" ) && ( this.actionDialog != null ) )
			    	{
				    	this.actionDialog.remove ();
				    	delete this.actionDialog;
			    	}
			    	
			    	this.undelegateEvents();
			    	$(this).empty;  
			        this.unbind();
			        
			        // Empty the root element of for the view, which is where the dialog template was 
			        // inserted into the DOM prior to dialog creation.
			        this.$el.empty ();			        
			    }
		 	});
		 	
		 	return AuthorizationActionView;
});
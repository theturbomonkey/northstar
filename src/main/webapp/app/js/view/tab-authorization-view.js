define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'handlebars', 'datatables', 'datatablesExt', 'utils', 'text!../../app/pages/template/tab-authorization-template.html', 'text!../../app/pages/template/table-authorization-row-actions-template.html', 'authorizationActionView', 'authorizationModel'], 
		 function ($, jui, _, Backbone, Handlebars, dt, dtExt, utils, tabAuthorizationTemplate, rowActionsTemplate, AuthorizationActionView, AuthorizationModel) 
{
	var TabAuthorizationView = Backbone.View.extend({
		template: Handlebars.compile ( tabAuthorizationTemplate ),
		
		initialize: function () {
	    	this.render ();
	    },
	    
	    events: {
	    	'click #btnAuthorizationTableRefresh': 'onRefreshButtonClicked',
	    	'click #btnAuthorizationTableAdd': 'onAddButtonClicked'
	    },

	    dialogDeleteButtons: [ 
	        {text:"Cancel", click:"onDeleteDialogCancelButtonClicked", id: "btnAuthDeleteCancel"},
		 	{text:"Delete", click:"onDeleteDialogDeleteButtonClicked", id: "btnAuthDelete"}
		],
	    
	    render: function(){
	      // Compile the template for the table row actions.
	      var rowActions = Handlebars.compile ( rowActionsTemplate );
	      
	      // Set the template mark-up as the content of our EL div.
	      this.$el.html ( this.template( this ) );
	      
	      // Initialize the table. We save the object returned by the init function so that we can
	      // refresh the table later as necessary. 
	      tblAuthorization = this.$el.find('#tblAuthorization').dataTable( 
			{
		        "bProcessing": true,
		        "sAjaxSource":"app/service/authorization",
		        "aoColumns": [
					{ sTitle: "ID", mDataProp: "authID", bSearchable: false, bVisible: false},
		            { sTitle: "Email Address", mDataProp: "authEmail", sClass: "tblHeaderLeft" },
		            { sTitle: "Description", mDataProp: "authDesc", sClass: "tblHeaderLeft" },
		            { sTitle: "Actions", mData: null, sClass: "tblHeaderCenter" }
		        ],
		        "aaSorting": [[ 1, "asc" ]],
		        "fnRowCallback": function( nRow, aData, iDisplayIndex ) 
		        {	    		            
		            // Apply the actions template to the current row with the route ID.
		            $('td:eq(2)', nRow).html( rowActions ( { authID: aData["authID"] } ) );
		            
		            return nRow;
		        },
		        
		        "fnServerData": function ( sSource, aoData, fnCallback, oSettings ) {
		             oSettings.jqXHR = 
		            	 $.ajax( {
		                     "dataType": 'json', 
		                     "type": "GET",
		                     "url": sSource,
		                     "data": aoData,
		                     "timeout": 15000,
		                     "success": fnCallback,
		                     "error": function ( xhr, textStatus, error ) { 
		                    	 if ( textStatus == "parsererror" )
		                    	 {
		                    		// This invalid response likely means that the user's session timed-out.
		                    		 window.location = utils.buildURL ( "login" );
		                    	 }
		                    	 else
		                    	 {
		                    	     tabAuthorizationViewInstance.displayErrorMessage ( { errorCode: xhr.status, errorText: textStatus } );
		                    	 }
		                     }
		                 });
		        }
		    } );
	    },
	    
	    displayErrorMessage: function ( error ) {
	    	// The JQueryUI dialog function destroys the template div in the DOM after it
	    	// is done with it, so we have to fill the dialog div with the template content each time.
	    	// This also gives us a chance to use our model to fill in the message variables.
	    	if ( ( typeof this.authErrorTemplate == "undefined" ) || ( this.authErrorTemplate == null ) )
	    	{
	    		// If the template hasn't yet been fetched and compiled, then perform this activity now.
	    		var authErrorTemplateHTML = this.$el.find ( '#authErrorTemplate' ).html();
	    		
	    		// We now have to replace all double parenthesis with angle brackets for use by Handlebars.
		        // Double parenthesis had to be used, because the angle brackets would have gone missing
		        // when the view template was added as the content to EL. 
	    		authErrorTemplateHTML = authErrorTemplateHTML.replace ( /\(\(/g, "{{" );
	    		authErrorTemplateHTML = authErrorTemplateHTML.replace ( /\)\)/g, "}}" );
	    		this.authErrorTemplate = Handlebars.compile ( authErrorTemplateHTML );
	    	}
	    	
	    	// Insert the resolved dialog content into the form. 
	    	this.$el.find ( "#authDialog" ).empty ().append ( this.authErrorTemplate ( error ) );
	    	
	    	if ( typeof this.authErrorDialogView != "undefined" )
	    	{
	    		delete this.authErrorDialogView;
	    	}
	    	
	    	// Hide the processing div.
	    	this.$el.find ( "#tblAuthorization_processing" ).attr ( "style", "visibility: hidden;" );
	    	
	    	this.authErrorDialogView = 
	    		this.$el.find ( "#authDialog #authErrorDialog" ).dialog ( {
	 	        	resizable: true,
	 	        	autoOpen:true,
	 	        	modal: true,
	 	        	width:640,
	 	        	height:190,
	 	        	buttons:
	 	        	{
	 	        	  "OK": function () {
	 	        		  $(this).dialog ( "close" );
	 	        		  $(this).remove ();
	 	        	  }		
	 	        	}
	 	        	
	 	        });
	    },
	    
	    onAddButtonClicked: function () {
	    	// Make sure that any previous instance of the dialog has been destroyed.
	    	if ( ( typeof this.authDialogView != "undefined" ) && ( this.authDialogView != null ) )
	    	{
	    		this.stopListening ( this.authDialogView );
	    		this.authDialogView.destroy ();
	    		delete this.authDialogView;
	    	}
	    	// Create a new instance of the add authorization view dialog.
	    	this.authDialogView = new AuthorizationActionView ( { el: this.$el.find ( "#authDialog" ) } );
	    	
	    	// Subscribe to the save event for the dialog viewk which will only be triggered when the save
	    	// was successful.
	    	this.listenTo ( this.authDialogView, "saveSuccess", this.onRefreshButtonClicked );
	    	
	    },
	    
	    onDeleteButtonClicked: function (id) {
	    	// Build a new model with the ID set.
	    	var modelOptions = { authID: id };
	    	this.actionModel = new AuthorizationModel ( modelOptions );
	    	
	    	// Add a listener to the action model prior to performing fetch. We fetch the data
	    	// so that we can display a context sensitive warning message.
	    	this.listenTo ( this.actionModel, "sync", this.onDeleteFetchReturn );
	    	
	    	// Do the data fetch.
	    	this.actionModel.fetch ();
	    },
	    
	    onDeleteFetchReturn: function (fetchOptions) {
	    	// Stop listening to the model sync event.
	    	this.stopListening ( this.actionModel );
	    	
	    	// The JQueryUI dialog function destroys the template div in the DOM after it
	    	// is done with it, so we have to fill the dialog div with the template content each time.
	    	// This also gives us a chance to use our model to fill in the message variables.
	    	if ( ( typeof this.authDeleteTemplate == "undefined" ) || ( this.authDeleteTemplate == null ) )
	    	{
	    		// If the template hasn't yet been fetched and compiled, then perform this activity now.
	    		var authDeleteTemplateHTML = this.$el.find ( '#authDeleteTemplate' ).html();
	    		
	    		// We now have to replace all double parenthesis with angle brackets for use by Handlebars.
		        // Double parenthesis had to be used, because the angle brackets would have gone missing
		        // when the view template was added as the content to EL. 
	    		authDeleteTemplateHTML = authDeleteTemplateHTML.replace ( /\(\(/g, "{{" );
	    		authDeleteTemplateHTML = authDeleteTemplateHTML.replace ( /\)\)/g, "}}" );
	    		this.authDeleteTemplate = Handlebars.compile ( authDeleteTemplateHTML );
	    	}
	    	
	    	// Insert the resolved dialog content into the form. 
	    	this.$el.find ( "#authDialog" ).empty ().append ( this.authDeleteTemplate ( this.actionModel) );
	    	
	    	// Build the delete dialog.	    	
	    	var dialogButtons = _.result ( this, "dialogDeleteButtons" );
	    	dialogButtons = 
 	        	_.map( dialogButtons, 
 	        		   function(obj) {
 	        		     obj = _.clone ( obj );
 	        		     obj.click = _.bind ( this [obj.click], this );
 	        		     return obj;
 	        	}, this);
	    	
	    	this.authDeleteDialogView = 
	    		this.$el.find ( "#authDialog #authDeleteDialog" ).dialog ( {
	 	        	resizable: true,
	 	        	autoOpen:true,
	 	        	modal: true,
	 	        	width:640,
	 	        	height:190,
	 	        	buttons: dialogButtons
	 	        });
	    },
	    
	    onDeleteDialogCancelButtonClicked: function () {
	    	// Close the dialog.
	    	this.authDeleteDialogView.dialog ( "close" );
	    	this.authDeleteDialogView.remove ();
	    	delete this.authDeleteDialogView;
	    	
	    	// Clean-up after the model.
	    	this.actionModel.off ();
	    	delete this.actionModel;
	    },
	    
	    onDeleteDialogDeleteButtonClicked: function () {
	    	// Close the dialog.
	    	this.authDeleteDialogView.dialog ( "close" );
	    	this.authDeleteDialogView.remove ();
	    	delete this.authDeleteDialogView;
	    	
	    	// Subscribe to the sync event, which will fire after the model is 
	    	// deleted.
	    	this.listenTo ( this.actionModel, "sync", this.onDeleteComplete );
	    	
	    	// Remove the entity represented by the model.
	    	this.actionModel.destroy ();
	    },
	    
	    onDeleteComplete: function () {
	    	// Stop listening to the model sync event.
	    	this.stopListening ( this.actionModel );
	    	
	    	// Clean-up after the model.
	    	this.actionModel.off ();
	    	delete this.actionModel;
	    	
	    	// Refresh the table.
	    	this.onRefreshButtonClicked();
	    },
	    
	    onEditButtonClicked: function (id) {
	    	// Make sure that any previous instance of the dialog has been destroyed.
	    	if ( ( typeof this.authDialogView != "undefined" ) && ( this.authDialogView != null ) )
	    	{
	    		this.stopListening ( this.authDialogView );
	    		this.authDialogView.destroy ();
	    		delete this.authDialogView;
	    	}
	    	
	    	// Build a new model with the ID set. This informs the dialog that an edit needs to occur. 
	    	var modelOptions = { authID: id, authAction: 'Edit' };
	    	var editModel = new AuthorizationModel ( modelOptions );
	    	
	    	// Create a new instance of the add authorization view dialog.
	    	this.authDialogView = 
	    		new AuthorizationActionView ( { el: this.$el.find ( "#authDialog" ), model: editModel } );
	    	
	    	// Subscribe to the save event for the dialog viewk which will only be triggered when the save
	    	// was successful.
	    	this.listenTo ( this.authDialogView, "saveSuccess", this.onRefreshButtonClicked );
	    },
	    
	    onRefreshButtonClicked: function () {
	    	// Have the data table refreshed.
	    	tblAuthorization.fnReloadAjax();
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
	  return TabAuthorizationView;
});
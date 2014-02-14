define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'handlebars', 'appConfigModel', 'appConfigCollection', 'appConfigEdit', 'text!../../app/pages/template/tab-config-edit-template.html'], 
		 function ($, jui, _, Backbone, Handlebars, AppConfigModel, AppConfigCollection, AppConfigEdit, tabConfigEditTemplate) 
{
	var TabConfigEditView = Backbone.View.extend({
		template: Handlebars.compile ( tabConfigEditTemplate ),
		
	    initialize: function () {
	    	// Add the render function as a listener to the model. Once the model fetch request has
	    	// completed, then the render function will be invoked. The model object will have been
	    	// set on initialization.
	    	this.listenTo ( this.collection, 'sync', this.render );
	    },
	    
	    events: {
	    	'click #btnConfigCancel': 'onCancelButtonClicked',
	    	'click #btnConfigSave': 'onSaveButtonClicked'
	    },
	    
	    render: function () {  
	    	console.log ( "Rendering the configuration edit view." );
	    	
	    	// Hand-over the compiled template over to the view's root element.
	        this.$el.html ( this.template ( this ) );
	      
	        // Get all of configuration item templates that is located within the template.
	        var configItemTextHTML = this.$el.find ( '#tmplConfigItemText' ).html();
	        var configItemIntHTML = this.$el.find ( '#tmplConfigItemInt' ).html();
	        var configItemTextConfidentialHTML = this.$el.find ( '#tmplConfigItemTextConfidential' ).html();
	        var configItemBoolHTML = this.$el.find ( '#tmplConfigItemBool' ).html();
	        var configItemDateHTML = this.$el.find ( '#tmplConfigItemDate' ).html();
	      
	        // We now have to replace all double parenthesis with angle brackets for use by Handlebars.
	        // Double parenthesis had to be used, because the angle brackets would have gone missing
	        // when the view template was added as the content to EL.
	        configItemTextHTML = configItemTextHTML.replace ( /\(\(/g, "{{" );
	        configItemTextHTML = configItemTextHTML.replace ( /\)\)/g, "}}" );
	        configItemIntHTML = configItemIntHTML.replace ( /\(\(/g, "{{" );
	        configItemIntHTML = configItemIntHTML.replace ( /\)\)/g, "}}" );
	        configItemTextConfidentialHTML = configItemTextConfidentialHTML.replace ( /\(\(/g, "{{" );
	        configItemTextConfidentialHTML = configItemTextConfidentialHTML.replace ( /\)\)/g, "}}" );
	        configItemBoolHTML = configItemBoolHTML.replace ( /\(\(/g, "{{" );
	        configItemBoolHTML = configItemBoolHTML.replace ( /\)\)/g, "}}" );
	        configItemDateHTML = configItemDateHTML.replace ( /\(\(/g, "{{" );
	        configItemDateHTML = configItemDateHTML.replace ( /\)\)/g, "}}" );
	     
	        // Compile the configuration item template once for use by all items.
	        var configItemTextTemplate = Handlebars.compile ( configItemTextHTML );
	        var configItemIntTemplate = Handlebars.compile ( configItemIntHTML );
	        var configItemTextConfidentialTemplate = Handlebars.compile ( configItemTextConfidentialHTML );
	        var configItemBoolTemplate = Handlebars.compile ( configItemBoolHTML );
	        var configItemDateTemplate = Handlebars.compile ( configItemDateHTML );
	      
	        // Render the content of our template into the body of the EL div.
	        // The value of EL is passed into the app during construction.
	        this.collection.each ( function ( appConfig ) {
	    	    // Pass the current model and the compiled configuration item template to a new instance of a
	    	    // configuration item view.
	    	    var appConfigEdit = 
	    		    new AppConfigEdit ( { 
	    			    model : appConfig, 
	    			    templateText : configItemTextTemplate,
	    			    templateInt  : configItemIntTemplate,
	    			    templateConfidential : configItemTextConfidentialTemplate,
	    			    templateBool : configItemBoolTemplate,
	    			    templateDate : configItemDateTemplate } );
	    	  
	    	    // Append the resulting view HTML to the content panel within the EL of this view.
	    	    this.$el.find ( '#pnlConfigContent' ).append ( appConfigEdit.el );
	        }, this );

	        // Return this object so that chaining is possible.
	        return this;
	    },
	    
	    onCancelButtonClicked: function () {
	    	// Bubble the cancel event up to the router to have it restore the read-only
	    	// configuration view.
	    	appRouter.navigate ( this.$el.find ( '#btnConfigCancel' ).attr ( "value" ), {trigger: true} );
	    	
	    },
	    
	    onSaveButtonClicked: function () {
	    	// Stop listening to collection sync events.
	    	this.stopListening ( this.collection );
	    	
	    	// Iterate through the collection items.
	    	this.collection.each ( function ( appConfig ) {
	    		// Find the corresponding input control in the form. The input field is tagged with the ID of the 
	    		// the item.
	    		var configInput = this.$el.find ( "#lblConfigVal_" +  appConfig.appConfigID() );
	    		 
	    		// By default, our value is just the value of the input field.
	    		var configVal = configInput.val();
	    		 
	    		// Checkbox input values are accessed a little differently, though. So, if the item type
	    		// is boolean, then look at the value of the checked property.
	    		if ( appConfig.appConfigType() == 3 )
	    	    {
	    			configVal = 0;
	    			if ( configInput.prop ("checked") )
	    			{
	    				configVal = 1;
	    			}
	    	    }
	    		 
	    		// Tell the model to update to the user specified value.
	    		appConfig.update ( configVal, false );
	    		 
	    	}, this );
	    	 
	    	// Have the collection save the updated models to the server. The appropriate
	    	// save success or error function will be called when the outcome is known.
	    	this.collection.save ( this.saveSuccess, this.saveError );
	    },
	    
	    // Deconstructor
	    destroy: function() {
	        // Remove the view from the DOM.
	    	this.undelegateEvents();
	    	$(this).empty;  
	        this.unbind();
	    },
	    
	    saveSuccess: function(model, resp, options) {
	    	// Bubble the cancel event up to the router to have it restore the read-only
	    	// configuration view.
	    	if ( options.responseJSON.result )
	    	{
	    		appRouter.navigate ( "tabConfigReturn", {trigger: true} );
	    	}
	    	else
	    	{
	    		alert ( "The save operation failed. " + options.responseJSON.message );
	    	}
	    },
	    
	    saveError: function(model, resp, options) {
	    	alert ( "The save operation failed. Server Status Code: " + model.status );
	    }
	  });
	
	  // Return the constructed view object.
	  return TabConfigEditView;
});
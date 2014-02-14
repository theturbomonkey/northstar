define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'utils'], function ($, jui, _, Backbone, utils) 
{
	var AppConfigModel = Backbone.Model.extend({
		
		url: function () {
			return ( utils.buildURL ( "app/service/appconfig/" + this.get('appConfigID') ) );
		},
		
		appConfigID: function (){
			return this.get('appConfigID');
		},
		
		appConfigName: function (){
			return this.get('appConfigName');
		},
		
		appConfigType: function (){
			return this.get('appConfigType');
		},
		
		appConfigOrder: function (){
			return this.get('appConfigOrder');
		},
		
		appConfigValInt: function (){
			return this.get('appConfigValInt');
		},
		
		appConfigValChar: function (){
			return this.get('appConfigValChar');
		},
		
		appConfigValDate: function (){
			return this.get('appConfigValDate');
		},
		
		appConfigValChecked: function (){
			// This function returns the text "checked" if 
			// the stored value is a boolean with a value of true.
			var checked = "";
			
			if ( ( this.get('appConfigType') == 3 ) && ( this.get('appConfigValInt') > 0 ) )
			{
				checked = "checked";
			}
			
			return checked;
		},
		
		confidentialVal: function (){
			return this.get('confidentialVal');
		},
		
		initialize: function (){
		},
		
		// Aggregate value that returns the text value of the model based on 
		// the specified config item type.
		appConfigVal: function (){
			// Determine what text should be returned based on the type.
			var displayVal = "";
			
			switch ( this.get('appConfigType') )
			{
				case 0:
					displayVal += this.get('appConfigValInt');
					break;
				case 1:
					displayVal = this.get('appConfigValChar');
					break;
				case 2:
					var dateVal = new Date ( this.get('appConfigValDate') );
					displayVal = dateVal.toLocaleString();
					break;
				case 3:
					// Treat the int value as a boolean, and display the 
					// appropriate text.
					if ( this.get('appConfigValInt') == 0 )
						displayVal = "False";
					else
						displayVal = "True";
					break;
			}
			
			return displayVal;
		},
		
		appConfigValReadOnly: function (){
			var displayVal = this.appConfigVal ();
			
			// If the value is confidential, then replace all characters with asterisks.
			if ( this.get('confidentialVal') )
			{
				displayVal = displayVal.replace ( /[\S]/g, "*" );
			}
			
			return displayVal;
		},
		
		update: function ( newValue, saveImmediately ) {
			switch ( this.get('appConfigType') )
			{
				case 0:
					this.set ( { 'appConfigValInt': parseInt ( newValue ) } );
					break;
				case 1:
					this.set ( { 'appConfigValChar': newValue } );
					break;
				case 2:
					// Unsupported for the moment.
					break;
				case 3:
					// This is a boolean that uses the int value.
					this.set ( { 'appConfigValInt': newValue } );
					break;
			}
			
			if ( saveImmediately )
			{
				// Persist the content to the server. Backbone is smart enough to
				// perform a POST if this is a new model or PUT if this is an
				// exiting model.
				this.save();
			}
		}
	});
	
	return AppConfigModel;
});
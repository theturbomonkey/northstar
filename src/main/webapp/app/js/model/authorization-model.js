define ( ['jquery', 'jqueryui', 'underscore', 'backbone', 'utils'], function ($, jui, _, Backbone, utils) 
{
	var AuthorizationModel = Backbone.Model.extend({
		
		url: function () {
			var returnURL = utils.buildURL ( "app/service/authorization" );
			
			// Determine if the auth ID needs to be included in the URL, which is the case if the
			// model corresponds to an existing entity.
			if ( ( typeof this.get('authID') != "undefined" ) && ( this.get('authID') != null ) )
			{
				// Append the auth ID to the URL.
				returnURL += "/" + this.get ( 'authID' );
			}
			
			return returnURL;
		},
		
		authID: function (){
			return this.get('authID');
		},
		
		authEmail: function (){
			return this.get('authEmail');
		},
		
		authDesc: function (){
			return this.get('authDesc');
		},
		
		initialize: function (options){
			this.authAction = options.authAction;
			
			if ( ( typeof options.authID != "undefined" ) && ( options.authID != null ) )
			{
				// An ID was passed in, so save that ID within the model properties.
				this.set ( {'authID': options.authID} );
			}
		},
		
		update: function ( authEmailVal, authDescVal, saveImmediately ) {
			// We have to drop the authAction attribute. For some reason, it's not
			// simply a local property when it's set on initialize, and it will be
			// included in the save message if we don't unset it here.
			this.unset ( 'authAction' );
			
			// Update the auth email and description from the values specified in the UI.
			this.set ( { 'authEmail': authEmailVal } );
			this.set ( { 'authDesc': authDescVal } );
			
			if ( saveImmediately )
			{
				// Persist the content to the server. Backbone is smart enough to
				// perform a POST if this is a new model or PUT if this is an
				// exiting model.
				this.save();
			}
		}
	});
	
	return AuthorizationModel;
});
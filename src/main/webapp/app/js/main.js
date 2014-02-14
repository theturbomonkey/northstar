require ( ['jquery','jqueryui', 'router', 'tabsView'], function ($, jui, Router, TabsView) 
{
	$(function() 
	{
		// Set the correct copyright year at the bottom of the page.
		var d = new Date();
		$("#spanYear").html(d.getFullYear());
		
		// Ensure that the logout link href specifies an absolute URL. That's the only way in which it will work.
		if ( $("#aLogout").attr("href").indexOf ( "http" ) < 0 )
		{
			var pathArray = window.location.href.split( '/' );
			var protocol = pathArray[0];
			var host = pathArray[2];
			var context = pathArray[3];
			$("#aLogout").attr("href", protocol + '//' + host + "/" + context + $("#aLogout").attr("href"));
		}
		
		// Render the tabs view.
	    tabsViewInstance = new TabsView ();
	    tabsViewInstance.render ();
	    
		// Initialize the Backbone Router for our application.
		Router.initialize ();
		
		// The rest happens via the Backbone router event handlers.
	});
});
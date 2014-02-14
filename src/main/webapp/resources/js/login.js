/**
 * Function: onLoginFormLoad
 * Description: Called after the login page has loaded to handle follow-up tasks like
 *              setting the form action URL and the appropriate control focus. These 
 *              are tasks that can only be performed after the page has completely loaded.
 */
function onLoginFormLoad()
{
	setFieldFocus();
} // End onLoginFormLoad


/**
 * Function: setFocus
 * Description: Set the focus based on whether whether the text fields contain text.
 */
function setFieldFocus()
{
	var userName = $("input[name=j_username]").val();
	var password = $("input[name=j_password]").val();
	
	if ( ( userName == undefined ) || (userName.trim().length <= 0 ) )
	{
		$("input[name=j_username]").focus ();
	}
	else if ( ( password == undefined ) || (password.trim().length <= 0 ) )
	{
		$("input[name=j_password]").focus ();
	}
	else
	{
		$("#btnSubmit").focus ();
	}
} // End setFieldFocus


/**
 * Function: authenticate
 * Description: Ensure that the user entered both a username and password, then submit the specified
 *     credentials to the authentication service of the server. Display error messages when auth fails.
 *     Set the auth cookie and direct to the console if authentication is successful.
 */
function authenticate()
{
	// We always return false, because we need to wait on the outcome from the asynchronous
	// call to the server in order to determine if we get to continue moving forward to the
	// console.
	var returnVal = false;
	
	// Clear any previous alert message;
	$("#lblAlert").empty();
	
	// Get the values that were input by the user into the username and password fields.
	var userName = $("input[name=j_username]").val();
	var password = $("input[name=j_password]").val();
	
	// Ensure that the 
	if ( ( userName == undefined ) || (userName.trim().length <= 0 ) )
	{
		$("#lblAlert").append("Please enter a username.");
	}
	else if ( ( password == undefined ) || (password.trim().length <= 0 ) )
	{
		$("#lblAlert").append("Please enter a password.");
	}
	else
	{
		// Build the authentication object.
		var authenticationObj = { username: userName, password: password };
		
		// Authenticate the user using an ajax call.
		$.ajax({
			type: "POST",
			url: "authenticate",
			data: JSON.stringify ( authenticationObj ),
			processData: false,
			dataType: "json",
			contentType: "application/json",
			success: function ( data, status, xhr ) {
				// Evaluate the auth result.
				if ( data.result )
				{
					// Get the session ID from the response object, and set it as a cookie.
					$.cookie ( "JSESSIONID", data.sessionID );
					window.location = "console";
				}
				else
				{
					// Authentication failed. Report the server response to the user.
					$("#lblAlert").append ( data.message );
				}
			},
			error: function ( xhr, ajaxOptions, thrownError ) {
				// Fatal error.
				$("#lblAlert").append("Server Error: " + thrownError);
			}
		});
	}
	
	return returnVal;
} // End validateForm
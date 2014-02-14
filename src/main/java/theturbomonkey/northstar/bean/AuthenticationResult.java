package theturbomonkey.northstar.bean;

public class AuthenticationResult 
{
	private String message = null;
	private boolean result = false;
	private String sessionID = null;
	private String username = null;
	
	public AuthenticationResult ()
	{
	} // End AuthenticationResult Constructor
	
	
	public AuthenticationResult ( boolean result, String message, String sessionID, String username )
	{
		this.result = result;
		this.message = message;
		this.sessionID = sessionID;
		this.username = username;
	} // End AuthenticationResult Constructor


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public boolean getResult() {
		return result;
	}


	public void setResult(boolean result) {
		this.result = result;
	}
	
	
	public String getSessionID() {
		return sessionID;
	}


	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}
	
} // End AuthenticationResult Class

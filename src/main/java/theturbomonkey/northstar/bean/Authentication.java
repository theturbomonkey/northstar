package theturbomonkey.northstar.bean;

public class Authentication 
{
	private String username = null;
	private String password = null;
	
	public Authentication ()
	{
	} // End Authentication Constructor
	
	
	public Authentication ( String username, String password )
	{
		this.username = username;
		this.password = password;
	} // End Authentication Constructor


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
} // End Authentication Class

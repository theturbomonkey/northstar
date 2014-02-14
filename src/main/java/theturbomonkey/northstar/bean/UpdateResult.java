package theturbomonkey.northstar.bean;

public class UpdateResult 
{
	private boolean result = false;
	private String message = null;
	
	public UpdateResult ()
	{
	} // End UpdateResult Constructor
	
	
	public UpdateResult ( boolean result, String message )
	{
		this.result = result;
		this.message = message;
	} // End UpdateResult Constructor


	public boolean isResult() {
		return result;
	}


	public void setResult(boolean result) {
		this.result = result;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
}

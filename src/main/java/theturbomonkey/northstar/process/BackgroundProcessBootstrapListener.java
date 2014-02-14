package theturbomonkey.northstar.process;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

public class BackgroundProcessBootstrapListener implements ServletContextListener
{
	private BackgroundProcess backgroundProcess = null;
	
	public BackgroundProcessBootstrapListener ()
	{
	} // End BackgroundProcessBootstrapListener Constructor

	
	//**************************************************************************
    // Method: contextInitialized
	/**
	 * Invoked by the servlet container when the application is being started.
	 * It creates a new instance of the background processing thread and starts 
	 * the thread running.
	 */
    //**************************************************************************
	public void contextInitialized ( ServletContextEvent sce ) 
	{
		// Determine if an instance of the thread already exists and is running.
		if ((this.backgroundProcess == null) || !this.backgroundProcess.isAlive ()) 
		{
			// The thread doesn't exist, or has been stopped. Create and start a new instance.
			// We must pass the Spring web application context to the process, and the servlet
			// context is required in order to do this, so we do it here within the listener.
			this.backgroundProcess = 
				new BackgroundProcess ( 
					WebApplicationContextUtils.getWebApplicationContext ( sce.getServletContext() ) );
			
			// Start the read
			this.backgroundProcess.start ();
		}
	}

	
	//**************************************************************************
    // Method: contextDestroyed
	/**
	 * Invoked by the servlet container when the application is being stopped.
	 * It informs the background processing thread to stop processing.
	 */
    //**************************************************************************
	public void contextDestroyed(ServletContextEvent sce) 
	{
		try 
		{
			// Determine if an instance of the thread already exists and is running.
			if ( ( this.backgroundProcess != null ) && this.backgroundProcess.isAlive () ) 
			{
				// Shutdown the thread.
				this.backgroundProcess.doShutdown();
				this.backgroundProcess.interrupt();
			}
		} catch (Exception ex) 
		{
		}
	} // End contextDestroyed
} // End BackgroundProcessBootstrapListener

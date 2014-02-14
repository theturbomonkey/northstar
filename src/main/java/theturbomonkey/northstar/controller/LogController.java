package theturbomonkey.northstar.controller;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import theturbomonkey.northstar.bean.UpdateResult;

@Controller
@RequestMapping ( value = "/app/service" )
public class LogController 
{
	private Logger LOGGER = LoggerFactory.getLogger ( LogController.class );
	
	//****************************************************************************
	// Method: clearLog
	// URL Request Mapping: DELETE:/app/service/log
	// Description:
	/**
	 * Reset the application log.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/log", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8" )
	public @ResponseBody UpdateResult clearLog ()
	{
		UpdateResult updateResult = null;
		
		try
		{
			// Get the log file path.
			String logFilePath = this.fetchLogFilePath();
			
			// Truncate the file. There's no other way to do this. :-(
			FileChannel logChannel = null;;
			FileOutputStream fos = null;
			try
			{
				fos = new FileOutputStream ( logFilePath );
				logChannel = fos.getChannel();
				logChannel.truncate ( 0 );
			}
			finally
			{
				if ( logChannel != null )
				{
					logChannel.close ();
				}
				
				if ( fos != null )
				{
					fos.close ();
				}
			}
			
			updateResult = new UpdateResult ( true, "" );
		}
		catch ( Exception logEx )
		{
			String errorMessage = "An error occurred while attempting to clear the log file contents. " + logEx.getMessage ();
			LOGGER.error ( errorMessage );
			updateResult = new UpdateResult ( false, errorMessage );
		}
		
		return updateResult;
	} // End clearLog
	
	
	//****************************************************************************
	// Method: fetchLogFilePath
	// Description:
	/**
	 * Called to obtain the path to the application log file. Exceptions will be 
	 * thrown if the log file properties are missing or the log file can't be 
	 * found using the values of those properties.
	 */
	//****************************************************************************
	private String fetchLogFilePath () throws Exception
	{
		String logFilePath = null;
		
		// Get the application properties.
		ResourceBundle appProperties = ResourceBundle.getBundle ( "northstar" );
		
		// Get the name of the logging properties file, and the property in that file that 
		// contains the path to the log file.
		String logFileName = appProperties.getString ( "northstar.logging.configuration" );
		String logFilePathPropName = appProperties.getString ( "northstar.logging.filepath_property" );
		
		// Make sure that values were specified for both properties.
		if ( ( logFileName == null ) || ( logFileName.trim().length() <= 0 ) )
		{
			throw new Exception ( 
				"The application property \"northstar.logging.configuration\" is not specified or has " +
			    "no value within the application properties file." );
		}
		else if ( ( logFilePathPropName == null ) || ( logFilePathPropName.trim().length() <= 0 ) )
		{
			throw new Exception ( 
				"The application property \"northstar.logging.filepath_property\" is not specified or has " +
				"no value within the application properties file." );
		}
		
		// Get the logging configuration file properties.
		ResourceBundle log4jProperties = ResourceBundle.getBundle ( logFileName );
		
		// Get the file path property value.
		logFilePath = log4jProperties.getString ( logFilePathPropName );
		
		// Make sure that the log file path was obtained.
		if ( ( logFilePath == null ) || ( logFilePath.trim().length() <= 0 ) )
		{
			throw new Exception ( 
				"The log property \"" + logFilePathPropName + "\" is not specified or has " +
			    "no value within the logging properties file \"" + logFileName + "\"." );
		}
		
		// Make sure that the log file exists.
		File logFile = new File ( logFilePath );
		if ( !logFile.exists() )
		{
			throw new Exception ( "No log file could be found at the path \"" + logFilePath + "\"." );
		}
		else if ( !logFile.canRead() )
		{
			throw new Exception ( "The log file \"" + logFilePath + "\" is not readable." );
		}
		
		return logFilePath;
	} // End fetchLogFilePath
	

	//****************************************************************************
	// Method: getLog
	// URL Request Mapping: GET:/app/service/log
	// Description:
	/**
	 * Get the contents of the application log file. The contents are returned in the
	 * response stream.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/log", method = RequestMethod.GET, produces = "application/text" )
	public void getLog ( Writer responseWriter )
	{
		try
		{
			// Get the log file path.
			String logFilePath = this.fetchLogFilePath();
			
			// Read the log file into the response writer in 1K chunks.
			BufferedReader bufferedReader = null;
			try
			{
				char [] fileBuffer = new char [ 1024 ];
				bufferedReader = new BufferedReader ( new FileReader ( logFilePath ) );
				int bytesRead = 0;
				while ( ( bytesRead = bufferedReader.read ( fileBuffer, 0, 1024 ) ) != -1 )
				{
					// Write the characters to the output stream.
					responseWriter.write ( fileBuffer, 0, bytesRead );
				}
			}
			catch ( Exception fileEx )
			{
				throw new Exception ( "Unable to read the log file. " + fileEx.getMessage () );
			}
			finally
			{
				if ( bufferedReader != null )
				{
					bufferedReader.close();
				}
			}
		}
		catch ( Exception logEx )
		{
			LOGGER.error ( "An error occurred while attempting to return the log file contents. " + logEx.getMessage () );
			
			try
			{
				responseWriter.write ( 
					"A server error was reported while attempting to obtain the contents of the log file. " +
				    "Please review the server logs." );
			}
			catch ( Exception writeEx )
			{
				LOGGER.error ( "Unable to write to the response output stream. " + writeEx.getMessage() );
			}
		}
	} // End getLog	
} // End LogController

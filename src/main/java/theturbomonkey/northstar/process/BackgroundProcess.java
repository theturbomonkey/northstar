package theturbomonkey.northstar.process;

import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

import theturbomonkey.northstar.model.entity.AppConfig;
import theturbomonkey.northstar.model.entity.Authorization;
import theturbomonkey.northstar.model.repository.AppConfigRepository;
import theturbomonkey.northstar.model.repository.AuthorizationRepository;

public class BackgroundProcess extends Thread 
{
	private Logger LOGGER = LoggerFactory.getLogger(BackgroundProcess.class);
	
	private boolean continueRunning = true;
	private int defaultSleepTime = 30;
	private WebApplicationContext applicationContext = null;
	private AppConfigRepository appConfigRepo = null;
	private AuthorizationRepository authRepo = null;


	//**************************************************************************
	// Constructor: BackgroundProcess
	/**
	 * The constructor for the background process, which requires the Spring
	 * web application context as a parameter so that beans may be located
	 * as necessary by the run method.
	 */
	//**************************************************************************
	public BackgroundProcess( WebApplicationContext applicationContext ) 
	{
		this.applicationContext = applicationContext;
	} // End BackgroundProcess Constructor

	
	//**************************************************************************
	// Method: doShutdown
	/**
	 * Called to tell the thread to shutdown.
	 */
	//**************************************************************************
	public void doShutdown() 
	{
		this.continueRunning = false;
	} // End doShutdown

	
	//**************************************************************************
	// Method: run
	/**
	 * Called when the thread is started. It monitors the configured email
	 * address and periodically determines if any new emails have arrived for
	 * processing.
	 */
	//**************************************************************************
	public void run () 
	{
		int sleepTime = this.defaultSleepTime;
		
		LOGGER.info ( "The Northstar background process is now running." );
		
		// Get handles to the singleton instances of the application configuration repository 
		// and authorization repository interfaces.
		try
		{
			this.appConfigRepo = this.applicationContext.getBean ( AppConfigRepository.class );
			this.authRepo = this.applicationContext.getBean ( AuthorizationRepository.class );
		}
		catch ( Exception springEx )
		{
			LOGGER.error ( 
				"The background process was unable to obtain one or more repository instances. " +
				springEx.getMessage() );
			LOGGER.error ( "The background process cannot function and will now exit." );
			this.continueRunning = false;
		}
		
		while ( this.continueRunning ) 
		{
			try 
			{
				LOGGER.debug ( "The background process is going to sleep for " + sleepTime + " seconds..." );
				Thread.sleep ( sleepTime * 1000 );
			} 
			catch (InterruptedException e) 
			{
				LOGGER.info ("The background process thread received an interrupt." );
				break;
			}
			
			LOGGER.debug ( "The background process is waking up..." );
			
			try
			{
				Map<String,Object> appConfigMap = this.getAppConfigMap ();
				// Determine how long we are to sleep for during this next sleep cycle.
				if ( appConfigMap.containsKey ( AppConfig.CONFIG_PROPNAME_POLL_INTERVAL ) )
				{
					sleepTime = ( Integer ) appConfigMap.get ( AppConfig.CONFIG_PROPNAME_POLL_INTERVAL );
				}
				
				// Determine whether processing is enabled. By default it is not. So, if the configuration property
				// isn't specified, then no processing will be performed.
				Boolean processingEnabled = false;
				if ( appConfigMap.containsKey ( AppConfig.CONFIG_PROPNAME_ENABLED ) )
				{
					processingEnabled = ( Boolean ) appConfigMap.get ( AppConfig.CONFIG_PROPNAME_ENABLED );
				}
				
				// Continue only if the processing is enabled.
				if ( processingEnabled )
				{
					LOGGER.debug ( "Background processing is enabled. Connecting to the IMAP server to check email." );
					
					// Get the list of email addresses from the configured authorizations.
					List<String> authorizedEmailList = this.getAuthorizedEmailAddresses ();
					if ( authorizedEmailList.size() <= 0 )
					{
						// Nothing to do.
						continue;
					}
					
					// Interrogate the inbound email and determine if there are individuals who
					// require and authorized reply.
					List<String> replyToEmailRecipientList = this.replyToEmailRecipients ( appConfigMap, authorizedEmailList );
					if ( replyToEmailRecipientList.size() <= 0 )
					{
						// Nothing to do.
						continue;
					}
					
					// Get the external IP address of the host.
					String externalIPAddress = this.getExternalIPAddress ();
					LOGGER.debug ( "The external IP address for the host is " + externalIPAddress + "." );
					
					// Email each of the recipients.
					this.emailIPToRecipientsInList ( appConfigMap, replyToEmailRecipientList, externalIPAddress );
				}
				else
				{
					LOGGER.debug ( "Background processing is disabled." );
				}
			}
			catch ( Exception runEx )
			{
				LOGGER.error ( 
					"The background process experienced an error while attempting to perform it's duties. " +
					runEx.getMessage() );
			}
		}
		
		LOGGER.info ( "The Northstar background process is shutting down." );
	} // End run
	

	//**************************************************************************
	// Method: emailIPToRecipientsInList
	/**
	 * Called to send the specified external IP address to each of the recipients
	 * in the specified list.
	 */
	//**************************************************************************
	private void emailIPToRecipientsInList ( Map<String,Object> appConfigMap, List<String> replyToEmailRecipientList, String externaIPAddress )
	{
		String outboundSMTP = ( String ) appConfigMap.get( AppConfig.CONFIG_PROPNAME_OUTBOUND_SMTP );
		String smtpPort = "587";
		
		// Determine if the smtp port was specified.
		if ( outboundSMTP.contains ( ":") )
		{
			smtpPort = outboundSMTP.substring ( outboundSMTP.indexOf ( ":" ) + 1 );
			outboundSMTP = outboundSMTP.substring ( 0, outboundSMTP.indexOf ( ":" ) );
		}
		
		final String emailAddress = ( String ) appConfigMap.get( AppConfig.CONFIG_PROPNAME_EMAIL_ADDRESS );
		final String password = ( String ) appConfigMap.get( AppConfig.CONFIG_PROPNAME_PASSWORD );
		
		// Build the SMTP properties object.
		Properties props = new Properties();
		props.put ( "mail.smtp.auth", "true" );
		props.put ( "mail.smtp.starttls.enable", "true" );
		props.put ( "mail.smtp.host", outboundSMTP);
		props.put ( "mail.smtp.port", smtpPort );

		try
		{
			Session session = Session.getInstance ( props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() 
					{
						return new PasswordAuthentication ( emailAddress, password );
					}
				});
		
			for ( String currentRecipient : replyToEmailRecipientList )
			{
				LOGGER.debug ( "Sending the external IP address email message to \"" + currentRecipient + "\"." );
				try
				{
					Message message = new MimeMessage ( session );
					message.setFrom  ( new InternetAddress ( emailAddress ) );
					message.setRecipients ( Message.RecipientType.TO, InternetAddress.parse ( currentRecipient ) );
					message.setSubject ( "Northstar Response" );
					message.setText ( 
						"The IP address for \"" + java.net.InetAddress.getLocalHost().getHostName() + "\" is: " + externaIPAddress );
		 
					Transport.send ( message );
					
					LOGGER.info ( "The server external IP was successfully sent to \"" + currentRecipient + "\"." );
				}
				catch ( Exception emailEx )
				{
					LOGGER.error ( 
						"An error occurred while attempting to send the external IP to \"" + currentRecipient + "\". " + 
						emailEx.getMessage() );
				}
			}
		}
		catch ( Exception sessionEx )
		{
			LOGGER.error ( "An error occurred while attempting to obtain an SMTP session. " + sessionEx.getMessage () );
		}
	} // End emailIPToRecipientsInList
	
	
	//**************************************************************************
	// Method: getAppConfigMap
	/**
	 * Called to obtain the map of application configuration properties from the
	 * application configuration repository.
	 */
	//**************************************************************************
	private Map<String,Object> getAppConfigMap () throws Exception
	{
		Map<String,Object> appConfigMap = new HashMap<String, Object> ();
		
		List<AppConfig> appConfigList = null;
		try
		{
			LOGGER.debug ( "Getting the application configuration items..." );
			appConfigList = this.appConfigRepo.findAll ();
		}
		catch ( Exception appConfigEx )
		{
			throw new Exception ( 
				"An error occurred while attempting to get the list of application configuration properties " +
				"from the datasource. " + appConfigEx.getMessage () );
		}
		
		// Iterate through the list, and obtain all of the property values that we require.		
		for ( AppConfig currentAppConfig : appConfigList )
		{
			String appConfigName = currentAppConfig.getAppConfigName ();
			
			LOGGER.debug ( "Processing the application configuration item with name \"" + appConfigName + "\"..." );
			
			// Determine what type of property this is.
			if ( appConfigName.equals( AppConfig.CONFIG_PROPNAME_EMAIL_ADDRESS ) )
			{
				appConfigMap.put ( AppConfig.CONFIG_PROPNAME_EMAIL_ADDRESS, currentAppConfig.getAppConfigValChar() );
			}
			else if ( appConfigName.equals( AppConfig.CONFIG_PROPNAME_ENABLED ) )
			{
				Boolean processingEnabled = false;
				if ( currentAppConfig.getAppConfigValInt () == 1 )
				{
					processingEnabled = true;
				}
				
				appConfigMap.put ( AppConfig.CONFIG_PROPNAME_ENABLED, processingEnabled );
			}
			else if ( appConfigName.equals( AppConfig.CONFIG_PROPNAME_INBOUND_IMAP ) )
			{
				appConfigMap.put ( AppConfig.CONFIG_PROPNAME_INBOUND_IMAP, currentAppConfig.getAppConfigValChar() );
			}
			else if ( appConfigName.equals( AppConfig.CONFIG_PROPNAME_OUTBOUND_SMTP ) )
			{
				appConfigMap.put ( AppConfig.CONFIG_PROPNAME_OUTBOUND_SMTP, currentAppConfig.getAppConfigValChar() );
			}
			else if ( appConfigName.equals( AppConfig.CONFIG_PROPNAME_PASSWORD ) )
			{
				appConfigMap.put ( AppConfig.CONFIG_PROPNAME_PASSWORD, currentAppConfig.getAppConfigValChar() );
			}
			else if ( appConfigName.equals( AppConfig.CONFIG_PROPNAME_POLL_INTERVAL ) )
			{
				appConfigMap.put ( AppConfig.CONFIG_PROPNAME_POLL_INTERVAL, currentAppConfig.getAppConfigValInt() );
			}
			else if ( appConfigName.equals( AppConfig.CONFIG_PROPNAME_SUBJECT_PASSPHRASE ) )
			{
				appConfigMap.put ( AppConfig.CONFIG_PROPNAME_SUBJECT_PASSPHRASE, currentAppConfig.getAppConfigValChar() );
			}
		}
		
		// Make sure that all required values are accounted for.
		if ( !appConfigMap.containsKey ( AppConfig.CONFIG_PROPNAME_EMAIL_ADDRESS ) || 
			 ( ( ( String ) appConfigMap.get( AppConfig.CONFIG_PROPNAME_EMAIL_ADDRESS ) ).trim ().length () <= 0 ) )
		{
			throw new Exception ( "No email address was configured for the application." );
		}
		else if ( !appConfigMap.containsKey ( AppConfig.CONFIG_PROPNAME_INBOUND_IMAP ) || 
				 ( ( ( String ) appConfigMap.get( AppConfig.CONFIG_PROPNAME_INBOUND_IMAP ) ).trim ().length () <= 0 ) )
		{
			throw new Exception ( "No inbound IMAP address was configured for the application." );
		}
		else if ( !appConfigMap.containsKey ( AppConfig.CONFIG_PROPNAME_OUTBOUND_SMTP ) || 
				 ( ( ( String ) appConfigMap.get( AppConfig.CONFIG_PROPNAME_OUTBOUND_SMTP ) ).trim ().length () <= 0 ) )
		{
			throw new Exception ( "No outbound SMTP address was configured for the application." );
		}
		else if ( !appConfigMap.containsKey ( AppConfig.CONFIG_PROPNAME_PASSWORD ) || 
				 ( ( ( String ) appConfigMap.get( AppConfig.CONFIG_PROPNAME_PASSWORD ) ).trim ().length () <= 0 ) )
		{
			throw new Exception ( "No password was configured for the application." );
		}
		else if ( !appConfigMap.containsKey ( AppConfig.CONFIG_PROPNAME_SUBJECT_PASSPHRASE ) || 
				 ( ( ( String ) appConfigMap.get( AppConfig.CONFIG_PROPNAME_SUBJECT_PASSPHRASE ) ).trim ().length () <= 0 ) )
		{
			throw new Exception ( "No subject passphrase was configured for the application." );
		}
		
		return appConfigMap;
	} // End getAppConfigMap
	
	
	//**************************************************************************
	// Method: getAuthorizedEmailAddresses
	/**
	 * Called to obtain the list of authorized email address from the authorization
	 * entries within the authorization repository.
	 */
	//**************************************************************************
	private List<String> getAuthorizedEmailAddresses () throws Exception
	{
		List<String> authorizedEmailList = new ArrayList <String> ();
		
		// Get the list of authorizations.
		List<Authorization> authorizationList = null;
		try
		{
			LOGGER.debug ( "Getting the list of authorizations..." );
			authorizationList = this.authRepo.findAll();
		}
		catch ( Exception authEx )
		{
			throw new Exception ( 
			    "An error occurred while attempting to get the list of authorizations " +
				"from the datasource. " + authEx.getMessage () );
		}
		
		// Make sure that authorizations were specified.
		if ( authorizationList.size() <= 0 )
		{
			LOGGER.warn ( "No authorizations have been configured. There is nothing to do." );
		}
		else
		{
			// Iterate through the authorization list, and build an array list of the
			// authorized email addresses so that it's easy to determine if something 
			// needs to be done.
			for ( Authorization currentAuth : authorizationList )
			{
				authorizedEmailList.add ( currentAuth.getAuthEmail() );
			}
		}
		
		return authorizedEmailList;
	} // End getAuthorizedEmailAddresses
	
	
	//**************************************************************************
	// Method: getExternalIPAddress
	/**
	 * Called to obtain the external IP address for the host.
	 */
	//**************************************************************************
	private String getExternalIPAddress () throws Exception 
	{
		String externalIP = null;
		
		// Get the application properties.
		ResourceBundle appProperties = ResourceBundle.getBundle ( "northstar" );
		
		// Get the URL of the resource where we visit to get the external IP.
		String ipServiceURLString = appProperties.getString ( "ip.service.url" );
		if ( ( ipServiceURLString == null ) || ( ipServiceURLString.trim ().length() <= 0 ) )
		{
			throw new Exception ( 
				"The property ip.service.url is missing or empty within the application properties file." );
		}
		else
		{
			LOGGER.debug ( "The configured IP service URL: " + ipServiceURLString );
		}
		
		// Get the regular expression that is used to parse this result.
		String ipRegExPatternString = appProperties.getString ( "ip.service.regex" );
		if ( ( ipRegExPatternString == null ) || ( ipRegExPatternString.trim ().length() <= 0 ) )
		{
			throw new Exception ( 
				"The property ip.service.regex is missing or empty within the application properties file." );
		}
		else
		{
			LOGGER.debug ( "The configured IP service regex pattern is: " + ipRegExPatternString );
		}
		
		HttpURLConnection connection = null;
		try
		{
			URL url = new URL ( ipServiceURLString );
            connection = ( HttpURLConnection ) url.openConnection ();
            connection.setDoInput(true);
            connection.setDoOutput (false);
            connection.setUseCaches (false);
            connection.setRequestMethod("GET");
            connection.setAllowUserInteraction(false);
            
            // Get the response, and convert it to a string.
            StringWriter writer = new StringWriter();
            IOUtils.copy ( connection.getInputStream (), writer, "utf-8" );
            String ipServiceResponse = writer.toString();
            writer.close();
            
            //LOGGER.debug ( "The complete IP service response was: " + ipServiceResponse );
            
            Pattern regExPattern = Pattern.compile ( ipRegExPatternString );
            Matcher matcher = regExPattern.matcher ( ipServiceResponse );
            if ( matcher.find () )
            {
            	externalIP = matcher.group ();
            	
            	// Determine if there is any text that needs to be stripped out of the IP.
            	// In this first case, we check to determine if the IP address is surrounded by
            	// quotes.
            	if ( externalIP.indexOf ( "\"" ) >= 0 )
            	{
            		externalIP = externalIP.substring ( externalIP.indexOf ( "\"" ) + 1 );
            		
            		if ( externalIP.indexOf ( "\"" ) >= 0 )
                	{
            			externalIP = externalIP.substring ( 0, externalIP.indexOf ( "\"" ) );
                	}
            	}
            }
            else
            {
            	throw new Exception ( "The IP pattern wasn't found in the response from the external IP service." );
            }
		}
		catch ( Exception connEx )
		{
			throw new Exception ( 
				"An error occurred while attempting to connect to the external IP address source at " +
				ipServiceURLString + ". " + connEx.getMessage () );
		}
		finally
		{	
			if ( connection != null )
			{
				connection.disconnect();
			}
		}
		
		return externalIP;
	} // End getExternalIPAddress
	

	//**************************************************************************
	// Method: replyToEmailRecipients
	/**
	 * Called to get the list of email addresses to which the host IP is to be 
	 * sent based on inbound emails that include the subject passphrase and
	 * are from email addresses specified in the authorized email address list.
	 */
	//**************************************************************************
	private List<String> replyToEmailRecipients ( Map<String,Object> appConfigMap, List<String> authorizedEmailList ) throws Exception
	{
		// The following list will be used to capture the list of email address
		List<String> replyToEmailList = new ArrayList <String> ();
		
		// Get a list of email messages from the configured email address.
		Properties imapProps = new Properties ();
		imapProps.setProperty ( "mail.store.protocol", "imaps" );
		Store store = null;
		Folder inbox = null;
		try
		{
			Session imapSession = Session.getInstance ( imapProps, null );
			store = imapSession.getStore ();
			
            store.connect ( ( String ) appConfigMap.get ( AppConfig.CONFIG_PROPNAME_INBOUND_IMAP ), 
            		        ( String ) appConfigMap.get ( AppConfig.CONFIG_PROPNAME_EMAIL_ADDRESS ), 
            		        ( String ) appConfigMap.get ( AppConfig.CONFIG_PROPNAME_PASSWORD ) );
            
            // Get a handle for the inbox folder.
            inbox = store.getFolder ( "INBOX" );
            inbox.open ( Folder.READ_WRITE );
            
            Message [] messages = inbox.getMessages();
            LOGGER.debug ( "There are " + messages.length + " messages in the inbox." );
            
            ArrayList<Message> messagesToDelete = new ArrayList<Message> ();
            
            // Iterate through the array of messages, and determine if they meet both subject
            // and recipient email address.
            for ( Message currentMessage : messages )
            {
            	if ( currentMessage.getSubject().toLowerCase().equals ( 
            			( ( String ) appConfigMap.get ( AppConfig.CONFIG_PROPNAME_SUBJECT_PASSPHRASE ) ).toLowerCase () ) )
            	{
            		LOGGER.debug ( "A message was found containing the proper subject passphrase." );
            		
            		// The passphrase matches. Determine if the reply-to email address is authorized.
            		Address [] replyToAddresses = currentMessage.getReplyTo ();
            		for ( Address currentAddress : replyToAddresses )
            		{
            			// Convert the email address to a string.
            			String currentEmailAddress = currentAddress.toString ();
            			
            			LOGGER.debug ( "Determining if \"" + currentEmailAddress + "\" is authorized." );
            			
            			// The email address may be included in angle brackets, which may be a problem when matching
            			// authorization entries.
            			if ( currentEmailAddress.contains ( "<" ) )
            			{
            				// Remove the angle brackets.
            				currentEmailAddress = 
            					currentEmailAddress.substring ( currentEmailAddress.indexOf ( "<" ) + 1, currentEmailAddress.indexOf ( ">" ));
            			}
            			
            			// We'll reply to this recipient if they are either in the list, or if anonymous users are allowed to
            			// participate.
            			if ( authorizedEmailList.contains ( currentEmailAddress ) || authorizedEmailList.contains ( "*@*" ) )
            			{
            				// Add this email address to the recipients list. This email address
            				// is authorized.
            				replyToEmailList.add ( currentEmailAddress );
            				
            				LOGGER.info ( "The server IP will be sent to email address \"" + currentEmailAddress + "\"." );
            			}
            		}
            		
            		// Add the message to the list of messages to be deleted.
            		messagesToDelete.add ( currentMessage );
            	}
            }
            
            // Mark any messages that were identified for deletion.
            if ( messagesToDelete.size() > 0 )
            {
            	LOGGER.debug ( "Setting the deleted flags for each message that matched the subject criteria." );
	            Flags deleted = new Flags ( Flags.Flag.DELETED );
	            inbox.setFlags ( messagesToDelete.toArray ( new Message [ messagesToDelete.size() ] ), deleted, true );
            }
		}
		catch ( Exception imapEx )
		{
			throw new Exception ( 
				"An error occurred while attempting to receive email for \"" + 
			    appConfigMap.get ( AppConfig.CONFIG_PROPNAME_EMAIL_ADDRESS ) + 
				"\" via email. " + imapEx.getMessage() );
		}
		finally
		{
			if ( inbox != null )
			{
				inbox.close ( true );
			}
			
			if ( store != null )
			{
				store.close ();
			}
		}
		
		return replyToEmailList;
	} // End replyToEmailRecipients
	
	
	//**************************************************************************
	// Method: setApplicationContext
	/**
	 * Called to set the value of the application context property.
	 */
	//**************************************************************************
	public void setApplicationContext ( WebApplicationContext applicationContext )
	{
		this.applicationContext = applicationContext;
	} // End setApplicationContext

} // End BackgroundProcess Class

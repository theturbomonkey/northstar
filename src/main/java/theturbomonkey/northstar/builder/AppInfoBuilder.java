package theturbomonkey.northstar.builder;

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import theturbomonkey.northstar.bean.AppInfo;

public class AppInfoBuilder 
{
	private Logger LOGGER = LoggerFactory.getLogger ( AppInfoBuilder.class );
	
	public AppInfoBuilder ()
	{
	} // End AppInfoBuilder Constructor
	
	
	public AppInfo buildAppInfo ( ServletContext servletContext )
	{
		AppInfo appInfo = new AppInfo ();
		
		try
		{
			// Get the input stream for the web application manifest from the servlet context.
			InputStream inputStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
			
			if ( inputStream != null )
			{
				try
				{
					// Have a Java manifest object built from the contents of the stream.
					Manifest manifest = new Manifest ( inputStream );
					
					// Get all of the main attributes in the manifest.
					Attributes attrs = manifest.getMainAttributes ();
					
					/*Set<Object> attrsKeys = attrs.keySet();
					Iterator<Object> keyItr = attrsKeys.iterator();
					while ( keyItr.hasNext() )
					{
						System.out.println ( "Current key: "  + keyItr.next() );
					}*/
					
					if ( ( attrs != null ) && !attrs.isEmpty() )
					{
						// Get the application version number attribute value.
						String attr = attrs.getValue( AppInfo.APP_VERSION_NUMBER_META );
						if ( attr != null )
						{
							appInfo.setAppVersionNumber ( attr );
						}
						else
						{
							throw new Exception ( 
								"(BAI05) No attribute with the name \"" +  AppInfo.APP_VERSION_NUMBER_META + "\" " +
								"could be found in the manifest." );
						}
						
						// Get the build number value. This is injected by Maven during the build process. If Maven isn't used
						// during the build process, then this attribute won't be set.
						attr = attrs.getValue( AppInfo.BUILD_NUMBER_META );
						if ( attr != null )
						{
							appInfo.setBuildNumber ( attr );
						}
						else
						{
							// This manifest wasn't built by Maven, so the build number isn't set. Therefore, set the
							// default value of the build number to "Undefined".
							appInfo.setBuildNumber ( "Undefined" );
							LOGGER.warn ( 
								"(BAI06) No attribute with the name \"" +  AppInfo.BUILD_NUMBER_META + "\" " +
								"could be found in the manifest." );
						}
						
						// Get the project URL attribute value.
						attr = attrs.getValue( AppInfo.PROJECT_URL_META );
						if ( attr != null )
						{
							appInfo.setProjectURL ( attr );
						}
						else
						{
							throw new Exception ( 
								"(BAI07) No attribute with the name \"" +  AppInfo.PROJECT_URL_META + "\" " +
								"could be found in the manifest." );
						}
					}
					else
					{
						throw new Exception ( "(BAI04) There are no main attributes in the manifest." );
					}
				}
				catch ( Exception manifestEx )
				{
					throw new Exception ( "(BAI03) Unable to read the manifest. " + manifestEx.getMessage () );
				}
				finally
				{
					// Make sure that the input stream that was used to read the manfiest is closed.
					if ( inputStream != null )
					{
						try
						{
							inputStream.close();
						}
						catch ( Exception closeEx )
						{
						}
					}
				}
			}
			else
			{
				throw new Exception ( "(BAI01) Unable to locate the resource META-INF/MANIFEST.MF within the application classpath." );
			}
		}
		catch ( Exception infoEx )
		{
			LOGGER.error ( "An error occurred while attempting to read the application manifest file. " + infoEx.getMessage() );
		}
		
		return appInfo;
	} // End buildAppInfo
	
} // End AppInfoFactory Class

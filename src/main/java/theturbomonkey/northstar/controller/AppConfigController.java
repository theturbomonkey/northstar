package theturbomonkey.northstar.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import theturbomonkey.northstar.bean.UpdateResult;
import theturbomonkey.northstar.model.entity.AppConfig;
import theturbomonkey.northstar.model.repository.AppConfigRepository;

@Controller
@RequestMapping ( value = "/app/service" )
public class AppConfigController 
{	
	@Resource
	private AppConfigRepository appConfigRepo;
	
	private Logger LOGGER = LoggerFactory.getLogger(AppConfigRepository.class);
	
	
	//****************************************************************************
	// Method: getAllAppConfigs
	// URL Request Mapping: GET:/app/service/appconfig
	// Description:
	/**
	 * Obtain a list of all application configuration entries.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/appconfig", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
	public @ResponseBody List<AppConfig> getAllAppConfigs ( @RequestBody String body, ModelMap model )
	{
		List<AppConfig> appConfigList = null;
		try
		{
			appConfigList = this.appConfigRepo.findAll ( new Sort ("appConfigOrder") );
		}
		catch ( Exception dsEx )
		{
			LOGGER.error ( "An error occurred while attempting to obtain the application configuration item list. " + dsEx.getMessage() );
			appConfigList = new ArrayList<AppConfig> ();
		}
		
		return appConfigList;
	} // End getAllAppConfigs
	
	
	//****************************************************************************
	// Method: getAppConfigByID
	// URL Request Mapping: GET:/app/service/appconfig/{id}
	// Description:
	/**
	 * Obtain the details of the application configuration item with the specified ID.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/appconfig/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
	public @ResponseBody AppConfig getAppConfigByID ( @PathVariable Integer id )
	{
		AppConfig appConfig = null;
		
		try
		{
			appConfig = this.appConfigRepo.findOne ( id );
		}
		catch ( Exception dsEx )
		{
			LOGGER.error ( 
				"An error occurred while attempting to obtain the application configuration " +
				"item with ID \"" + id + "\". " + dsEx.getMessage() );
		}
		
		return appConfig;
	} // End getAppConfigByID
	
	
	//****************************************************************************
	// Method: syncAppConfigItem
	// Description:
	/**
	 * This helper method is used to retrieve the application configuration item
	 * with the specified ID from the database, then use the data in the specified
	 * updated application configuration object to update the appropriate value
	 * in the reference version of the object from the DB. This is done this way to 
	 * ensure data integrity; we don't blindly trust the web client.
	 */
	//****************************************************************************
	private AppConfig syncAppConfigItem ( AppConfig updatedAppConfig, Integer id  ) throws Exception
	{
		AppConfig appConfig = this.appConfigRepo.findOne ( id );
		
		if ( appConfig == null )
		{
			throw new Exception ( "No application configuration item exists with ID " + id + "." );
		}
		else
		{
			// Use the application configuration type to determine which property needs to be updated.
			switch ( appConfig.getAppConfigType() )
			{
				case 0:
					// Int
					appConfig.setAppConfigValInt ( updatedAppConfig.getAppConfigValInt() );
					break;
				case 1:
					// Text
					appConfig.setAppConfigValChar ( updatedAppConfig.getAppConfigValChar() );
					break;
				case 2:
					// Date
					appConfig.setAppConfigValDate ( updatedAppConfig.getAppConfigValDate() );
					break;
				case 3:
					// Boolean
					appConfig.setAppConfigValInt ( updatedAppConfig.getAppConfigValInt() );
					break;
				default:
					throw new Exception ( "Unknown application configuration type \"" + appConfig.getAppConfigType() + "\"." );
			}
		}
		
		return appConfig;
	} // End syncAppConfigItem
	
	
	//****************************************************************************
	// Method: updateAllAppConfig
	// URL Request Mapping: PUT:/app/service/appconfig
	// Description:
	/**
	 * Update the list of application configuration items.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/appconfig", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
	public @ResponseBody UpdateResult updateAllAppConfig ( @RequestBody List<AppConfig> appConfigList )
	{
		UpdateResult result = null;
		
		try
		{
			//AppConfig appConfig = this.appConfigRepo.findOne ( id );
			if ( ( appConfigList == null ) || ( appConfigList.size() <= 0 ) )
			{
				throw new Exception ( "No application configuration items were included in the request." );
			}
			else
			{
				// Iterate through the list of application configuration items.
				ArrayList<AppConfig> appConfigItemList = new ArrayList<AppConfig> ();
				
				for ( AppConfig updatedAppConfig :  appConfigList )
				{
					// Sync the updated configuration item value with the reference value found in the DB.
					AppConfig appConfig = this.syncAppConfigItem ( updatedAppConfig, updatedAppConfig.getAppConfigID() );
					
					// Add the updated configuration to the list of items to persist.
					appConfigItemList.add ( appConfig );
				}
				
				// Persist the changes to the DB.
				this.appConfigRepo.save ( appConfigItemList );
				this.appConfigRepo.flush ();
				result = new UpdateResult ( true, "" );
			}
		}
		catch ( Exception dsEx )
		{
			String errorMessage = 
				"An error occurred while attempting to update the application " +
				"configuration items. " + dsEx.getMessage ();
			result = new UpdateResult ( false, errorMessage );
			LOGGER.error ( errorMessage );
		}
		
		return result;
	} // End updateAllAppConfig
	
	
	//****************************************************************************
	// Method: updateAppConfigByID
	// URL Request Mapping: PUT:/app/service/appconfig/{id}
	// Description:
	/**
	 * Update the value for the application configuration item with the specified ID.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/appconfig/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
	public @ResponseBody UpdateResult updateAppConfigByID ( @RequestBody AppConfig updatedAppConfig, @PathVariable Integer id )
	{
		UpdateResult result = null;
		
		try
		{
			// Sync the updated configuration item value with the reference value found in the DB.
			AppConfig appConfig = this.syncAppConfigItem ( updatedAppConfig, id );
			
			// Persist the changes to the DB.
			this.appConfigRepo.saveAndFlush ( appConfig );
			result = new UpdateResult ( true, "" );
		}
		catch ( Exception dsEx )
		{
			String errorMessage = 
				"An error occurred while attempting to update the application " +
				"configuration item with ID " + id + ". " + dsEx.getMessage ();
			result = new UpdateResult ( false, errorMessage );
			LOGGER.error ( errorMessage );
		}
		
		return result;
	} // End updateAppConfigByID
	
} // End RouteConfigController Class


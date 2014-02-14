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

import theturbomonkey.northstar.bean.DataTableData;
import theturbomonkey.northstar.bean.UpdateResult;
import theturbomonkey.northstar.model.entity.Authorization;
import theturbomonkey.northstar.model.repository.AuthorizationRepository;

@Controller
@RequestMapping ( value = "/app/service" )
public class AuthorizationController 
{
	private Logger LOGGER = LoggerFactory.getLogger(AuthorizationController.class);
	
	@Resource
	private AuthorizationRepository authRepo;
	
	//****************************************************************************
	// Method: addAuthorization
	// URL Request Mapping: POST:/app/service/authorization
	// Description:
	/**
	 * Add a new authorization.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/authorization", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8" )
	public @ResponseBody UpdateResult addAuthorization ( @RequestBody Authorization clientAuthorization )
	{
		UpdateResult result = null;
		
		try
		{
			// We make our own local authorization object to prevent any sort of corruption that the client
			// may impose.
			Authorization newAuth = 
				new Authorization ( clientAuthorization.getAuthEmail(), clientAuthorization.getAuthDesc() );
			
			// Save and flush.
			this.authRepo.saveAndFlush ( newAuth );
			
			// Build the update result to reflect a successful save.
			result = new UpdateResult ( true, "" );
		}
		catch ( Exception addEx )
		{
			String errorMessage = 
				"An error occurred while attempting to create the authorization. " + addEx.getMessage();
			LOGGER.error( errorMessage );
			result = new UpdateResult ( false, errorMessage );
		}
		
		return result;
	} // End addAuthorization
	
	
	//****************************************************************************
	// Method: deleteAuthorizationByID
	// URL Request Mapping: DELETE:/app/service/authorization/{id}
	// Description:
	/**
	 * Delete the authorization with the specified ID.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/authorization/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
	public @ResponseBody UpdateResult deleteAuthorizationByID ( @PathVariable Integer id )
	{
		UpdateResult result = null;
		
		try
		{
			this.authRepo.delete ( id );
			result = new UpdateResult ( true, "" );
		}
		catch ( Exception delEx )
		{
			String errorMessage = 
				"An error occurred while attempting to delete the authorization with ID " + 
			    id + ". " + delEx.getMessage();
			LOGGER.error( errorMessage );
			result = new UpdateResult ( false, errorMessage );
		}
		
		return result;
	} // End deleteAuthorizationByID
	
	
	//****************************************************************************
	// Method: getAllAuthorizations
	// URL Request Mapping: GET:/app/service/authorization
	// Description:
	/**
	 * Obtain a list of all authorizations.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/authorization", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8" )
	public @ResponseBody DataTableData<Authorization> getAllAuthorizations ( @RequestBody String body, ModelMap model )
	{
		DataTableData<Authorization> tableData = null;
		List<Authorization> authList = null;
		try
		{
			ArrayList<String> sortProps = new ArrayList<String> ();
			sortProps.add ( "authEmail" );
			authList = this.authRepo.findAll ( new Sort (Sort.Direction.ASC, sortProps) );
		}
		catch ( Exception dsEx )
		{
			LOGGER.error ( "An error occurred while attempting to obtain the complete list of authorizations. " + dsEx.getMessage() );
			authList = new ArrayList<Authorization> ();
		}
		
		// Wrap the authorization list in the format that DataTables requires.
		tableData = new DataTableData<Authorization> ( authList );
		
		return tableData;
	} // End getAllAuthorizations
	
	
	//****************************************************************************
	// Method: getAuthorizationByID
	// URL Request Mapping: GET:/app/service/authorization/{id}
	// Description:
	/**
	 * Obtain the details of the authorization with the specified ID.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/authorization/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
	public @ResponseBody Authorization getAuthorizationByID ( @PathVariable Integer id )
	{
		Authorization auth = null;
		
		try
		{
			auth = this.authRepo.findOne ( id );
		}
		catch ( Exception dsEx )
		{
			LOGGER.error ( 
				"An error occurred while attempting to obtain the authorization " +
				"with ID \"" + id + "\". " + dsEx.getMessage() );
		}
		
		return auth;
	} // End getAuthorizationByID
	
	
	//****************************************************************************
	// Method: updateAuthorization
	// URL Request Mapping: POST:/app/service/authorization
	// Description:
	/**
	 * Update the authorization with the specified ID.
	 */
	//****************************************************************************
	@RequestMapping ( value = "/authorization/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8" )
	public @ResponseBody UpdateResult updateAuthorization ( @RequestBody Authorization clientAuthorization, @PathVariable Integer id )
	{
		UpdateResult result = null;
		
		try
		{
			// Fetch the authorization with the specified ID.
			Authorization auth = this.authRepo.findOne ( id );
			if ( auth == null )
			{
				throw new Exception ( "No authorization exists with the specified ID. " );
			}
			
			// Update the two editable fields of the authorization from the data specified by the client.
			auth.setAuthDesc ( clientAuthorization.getAuthDesc() );
			auth.setAuthEmail ( clientAuthorization.getAuthEmail() );
			
			// Save and flush.
			this.authRepo.saveAndFlush ( auth );
			
			// Build the update result to reflect a successful save.
			result = new UpdateResult ( true, "" );
		}
		catch ( Exception addEx )
		{
			String errorMessage = 
				"An error occurred while attempting to update the authorization with ID " + id + ". " + 
				addEx.getMessage();
			LOGGER.error( errorMessage );
			result = new UpdateResult ( false, errorMessage );
		}
		
		return result;
	} // End updateAuthorization
}

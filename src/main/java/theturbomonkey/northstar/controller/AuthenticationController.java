package theturbomonkey.northstar.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import theturbomonkey.northstar.bean.AuthenticationResult;

@Controller
public class AuthenticationController 
{
	private Logger LOGGER = LoggerFactory.getLogger ( AuthenticationController.class );
	
	@Resource
	private AuthenticationManager authManager;
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String rootContext(ModelMap model) 
    {
        return "redirect:/console";
    }
	
	
	@RequestMapping ( value = "/authenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8"  )
    public @ResponseBody AuthenticationResult authenticate ( @RequestBody theturbomonkey.northstar.bean.Authentication credentials,  HttpServletRequest request ) 
	{
		AuthenticationResult authResult = null;
		
		if ( ( credentials.getUsername() == null ) || (credentials.getUsername().trim().length() <= 0 ) )
		{
			authResult = 
				new AuthenticationResult ( false, "No username was specified in the authentication request.", null, null );
		}
		else if ( ( credentials.getPassword() == null ) || (credentials.getPassword().trim().length() <= 0 ) )
		{
			authResult = 
				new AuthenticationResult ( false, "No password was specified in the authentication request.", null, null );
		}
		else
		{
			try
			{
				// Build the username and password auth token.
				UsernamePasswordAuthenticationToken authentication = 
					new UsernamePasswordAuthenticationToken ( credentials.getUsername(), credentials.getPassword());
				
				// Perform the authentication.
				Authentication successfulAuthentication = this.authManager.authenticate ( authentication );
				
				// Determine whether authentication was successful or failed.
				if ( ( successfulAuthentication != null ) && successfulAuthentication.isAuthenticated () )
				{
					// Authentication successful. Stash the authentication result into the security context.
					SecurityContextHolder.getContext().setAuthentication ( successfulAuthentication );
					
					// Build the authentication result.
					authResult = new AuthenticationResult ( true, null, request.getSession().getId(), credentials.getUsername() );
				}
				else
				{
					// Authentication failed.
					authResult = new AuthenticationResult ( false, "Invalid username or password.", null, null );
				}
			}
			catch ( Exception authEx )
			{
				// This where we also end up if authentication fails.
				LOGGER.error ( "Authentication Error: " + authEx.getMessage() );
				authResult = 
					new AuthenticationResult ( 
						false, 
						"Authentication Error: " + authEx.getMessage(), 
						null,
						null );
			}
		}
		
        return authResult;
    } // End authenticate
	
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(ModelMap model) 
	{
        return "/resources/pages/login.html";
    }
 
	
    @RequestMapping(value = "/accessdenied", method = RequestMethod.GET)
    public String loginerror(ModelMap model) 
    {
        model.addAttribute("error", "true");
        return "/resources/pages/access-denied.html";
    }
 
    
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(ModelMap model) 
    {
    	try
    	{
    		// Invalidate the user's session.
    		SecurityContextHolder.getContext().setAuthentication ( null );
    	}
    	catch ( Exception securityEx )
    	{
    		LOGGER.error ( 
    			"An error occurred while attempting to invalidate a user's session. " + securityEx.getMessage() );
    	}
        return "/resources/pages/login.html";
    }
    
    
    @RequestMapping(value = "/console", method = RequestMethod.GET)
    public String console(ModelMap model) 
    {
        return "/app/pages/index.html";
    }
} // End AuthenticationController Class


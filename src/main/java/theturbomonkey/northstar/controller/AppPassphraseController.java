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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import theturbomonkey.northstar.model.entity.AppPassphrase;
import theturbomonkey.northstar.model.repository.AppPassphraseRepository;

@Controller
@RequestMapping ( value = "/app/service" )
public class AppPassphraseController 
{
	private Logger LOGGER = LoggerFactory.getLogger ( AppPassphraseController.class );
	
	@Resource
	private AppPassphraseRepository appPassphraseRepo;
	
	@RequestMapping ( value = "/apppassphrase", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8"  )
	public @ResponseBody List<AppPassphrase> getAllPassphrases ( @RequestBody String body, ModelMap model )
	{
		List<AppPassphrase> appPassphraseList = null;
		try
		{
			appPassphraseList = this.appPassphraseRepo.findAll ( new Sort ("passphrase") );
		}
		catch ( Exception dsEx )
		{
			LOGGER.error ( "An error occurred while attempting to obtain the route list. " + dsEx.getMessage() );
			appPassphraseList = new ArrayList<AppPassphrase> ();
		}
		
		return appPassphraseList;
	} // End getAllAppConfigs

} // End AppPassphraseController Controller Class

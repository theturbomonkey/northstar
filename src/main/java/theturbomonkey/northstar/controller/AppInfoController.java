package theturbomonkey.northstar.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import theturbomonkey.northstar.bean.AppInfo;
import theturbomonkey.northstar.builder.AppInfoBuilder;

@Controller
@RequestMapping ( value = "/app/service" )
public class AppInfoController 
{
	
	@RequestMapping ( value = "/appinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8"  )
	public @ResponseBody AppInfo getAppInfo ( HttpServletRequest request )
	{
		
		AppInfoBuilder appInfoBuilder = new AppInfoBuilder ();
		return appInfoBuilder.buildAppInfo ( request.getSession().getServletContext () );
	} // End getAppInfo
	
} // End AppInfoController

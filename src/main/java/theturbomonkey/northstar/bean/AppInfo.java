package theturbomonkey.northstar.bean;

public class AppInfo 
{
	public static final String APP_VERSION_NUMBER_META = "Implementation-Version";
	public static final String BUILD_NUMBER_META = "Implementation-Build";
	public static final String PROJECT_URL_META = "Implementation-Url";
	
	private String appVersionNumber;
	private String buildNumber;
	private String projectURL;
	
	public AppInfo ()
	{
	}

	// Setters and Getters - Auto-generated
	public String getAppVersionNumber() {
		return appVersionNumber;
	}

	public void setAppVersionNumber(String appVersionNumber) {
		this.appVersionNumber = appVersionNumber;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	public String getProjectURL() {
		return projectURL;
	}

	public void setProjectURL(String projectURL) {
		this.projectURL = projectURL;
	}
} // End AppInfo Class
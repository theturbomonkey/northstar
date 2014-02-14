package theturbomonkey.northstar.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table ( name = "tblAppConfig")
public class AppConfig 
{
	public static String CONFIG_PROPNAME_ENABLED = "Enabled";
	public static String CONFIG_PROPNAME_EMAIL_ADDRESS = "Email Address";
	public static String CONFIG_PROPNAME_PASSWORD = "Password";
	public static String CONFIG_PROPNAME_OUTBOUND_SMTP = "Outbound SMTP";
	public static String CONFIG_PROPNAME_INBOUND_IMAP = "Inbound IMAP";
	public static String CONFIG_PROPNAME_SUBJECT_PASSPHRASE = "Email Subject Passphrase";
	public static String CONFIG_PROPNAME_POLL_INTERVAL = "Poll Interval (sec)";
	
	// Define each of the JPA column value mappings.
	@Id
	@GeneratedValue ( generator = "increment" )
	@GenericGenerator ( name="increment", strategy = "increment" )
	@Column ( name = "AppConfigID" )
	private Integer appConfigID;
	
	@Column ( name = "AppConfigName", nullable = false, length = 256)
	private String appConfigName;
	
	@Column ( name = "AppConfigType", nullable = false)
	private Short appConfigType;
	
	@Column ( name = "AppConfigOrder", nullable = false)
	private Short appConfigOrder;
	
	@Column ( name = "AppConfigValInt")
	private Integer appConfigValInt;
	
	@Column ( name = "AppConfigValChar", length = 256)
	private String appConfigValChar;
	
	@Column ( name = "AppConfigValDate" )
	private Date appConfigValDate;
	
	@Column ( name = "ConfidentialVal")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean confidentialVal;
	
	@Temporal ( TemporalType.TIMESTAMP )
	@Column ( name = "CreateDate" )
	private Date createDate;
	
	
	public AppConfig ()
	{
	} // End AppConfig Constructor
	
	
	// This ID get method is for Backbone model integration.
	public Integer getId() {
		return appConfigID;
	} // End getId


	public void setId(Integer appConfigID) {
		this.appConfigID = appConfigID;
	} // End setId


	// Setters and Getters - Auto-generated
	public Integer getAppConfigID() {
		return appConfigID;
	}


	public void setAppConfigID(Integer appConfigID) {
		this.appConfigID = appConfigID;
	}


	public String getAppConfigName() {
		return appConfigName;
	}


	public void setAppConfigName(String appConfigName) {
		this.appConfigName = appConfigName;
	}


	public Short getAppConfigType() {
		return appConfigType;
	}


	public void setAppConfigType(Short appConfigType) {
		this.appConfigType = appConfigType;
	}


	public Short getAppConfigOrder() {
		return appConfigOrder;
	}


	public void setAppConfigOrder(Short appConfigOrder) {
		this.appConfigOrder = appConfigOrder;
	}


	public Integer getAppConfigValInt() {
		return appConfigValInt;
	}


	public void setAppConfigValInt(Integer appConfigValInt) {
		this.appConfigValInt = appConfigValInt;
	}


	public String getAppConfigValChar() {
		return appConfigValChar;
	}


	public void setAppConfigValChar(String appConfigValChar) {
		this.appConfigValChar = appConfigValChar;
	}


	public Date getAppConfigValDate() {
		return appConfigValDate;
	}


	public void setAppConfigValDate(Date appConfigValDate) {
		this.appConfigValDate = appConfigValDate;
	}


	public boolean isConfidentialVal() {
		return confidentialVal;
	}


	public void setConfidentialVal(boolean confidentialVal) {
		this.confidentialVal = confidentialVal;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
} // End Route Class

package theturbomonkey.northstar.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table ( name = "tblAppTelemetry" )
public class AppTelemetry 
{
	// Define each of the JPA column value mappings.
	@Id
	@GeneratedValue ( generator = "increment" )
	@GenericGenerator ( name="increment", strategy = "increment" )
	@Column ( name = "TelemetryID" )
	private Integer telemetryID;
	
	@Column ( name = "ExternalIP", nullable = false, length = 256 )
	private String externalIP;
	
	@Column ( name = "EmailsProcessed", nullable = false )
	private Integer emailsProcessed;
	
	@Column ( name = "EmailsResponded", nullable = false )
	private Integer emailsResponded;
	
	public AppTelemetry ()
	{
	} // End Constructor
	
	
	// This ID get method is for Backbone model integration.
	public Integer getId() {
		return telemetryID;
	} // End getId


	public void setId(Integer telemetryID) {
		this.telemetryID = telemetryID;
	} // End setId

	
	// Setters and Getters - Auto-generated
	public Integer getTelemetryID() {
		return telemetryID;
	}

	public void setTelemetryID(Integer telemetryID) {
		this.telemetryID = telemetryID;
	}

	public String getExternalIP() {
		return externalIP;
	}

	public void setExternalIP(String externalIP) {
		this.externalIP = externalIP;
	}

	public Integer getEmailsProcessed() {
		return emailsProcessed;
	}

	public void setEmailsProcessed(Integer emailsProcessed) {
		this.emailsProcessed = emailsProcessed;
	}

	public Integer getEmailsResponded() {
		return emailsResponded;
	}

	public void setEmailsResponded(Integer emailsResponded) {
		this.emailsResponded = emailsResponded;
	}

} // End AppTelemetry Class

/*
 * TelemetryID      INTEGER       AUTO_INCREMENT PRIMARY KEY,
  ExternalIP       VARCHAR(256)  NOT NULL,
  EmailsProcessed  INTEGER       NOT NULL DEFAULT 0,
  EmailsResponded  INTEGER       NOT NULL DEFAULT 0
 */

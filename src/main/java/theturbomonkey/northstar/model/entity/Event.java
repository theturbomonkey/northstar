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
@Table ( name = "tblEvent" )
public class Event 
{
	@Id
	@GeneratedValue ( generator = "increment" )
	@GenericGenerator ( name="increment", strategy = "increment" )
	@Column ( name = "EventID" )
	private Integer eventID;
	
	@Column ( name = "EventTypeID", nullable = false)
	private Short eventTypeID;
	
	@Column ( name = "EventCode", nullable = false, length = 16)
	private String eventCode;
	
	@Column ( name = "EventMessage", nullable = false, length = 2048)
	private String eventMessage;
	
	@Temporal ( TemporalType.TIMESTAMP )
	@Column ( name = "CreateDate" )
	private Date createDate;
	
	@Column ( name = "Acknowledged")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean acknowledged;
	
	public Event ()
	{
	} // End Event Constructor
	
	
	// This ID get method is for Backbone model integration.
	public Integer getId() {
		return eventID;
	} // End getId


	public void setId(Integer eventID) {
		this.eventID = eventID;
	} // End setId

	
	// Setters and Getters - Auto-generated
	public Integer getEventID() {
		return eventID;
	}

	public void setEventID(Integer eventID) {
		this.eventID = eventID;
	}

	public Short getEventTypeID() {
		return eventTypeID;
	}

	public void setEventTypeID(Short eventTypeID) {
		this.eventTypeID = eventTypeID;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getEventMessage() {
		return eventMessage;
	}

	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}
	
} // End Event Class

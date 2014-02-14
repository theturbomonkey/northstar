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

@Entity
@Table ( name = "tblAuthorization" )
public class Authorization 
{
	@Id
	@GeneratedValue ( generator = "increment" )
	@GenericGenerator ( name="increment", strategy = "increment" )
	@Column ( name = "AuthID" )
	private Integer authID;
	
	@Column ( name = "AuthEmail", nullable = false, length = 256)
	private String authEmail;
	
	@Column ( name = "AuthDesc", nullable = false, length = 512)
	private String authDesc;
	
	@Temporal ( TemporalType.TIMESTAMP )
	@Column ( name = "CreateDate" )
	private Date createDate;
	
	public Authorization ()
	{
	} // End Authorization Constructor
	
	
	public Authorization ( String authEmail, String authDesc )
	{
		this.authEmail = authEmail;
		this.authDesc = authDesc;
	} // End Authorization Constructor
	
	
	// This ID get method is for Backbone model integration.
	public Integer getId() {
		return authID;
	} // End getId


	public void setId(Integer authID) {
		this.authID = authID;
	} // End setId

	
	// Setters and Getters - Auto-generated
	public Integer getAuthID() {
		return authID;
	}

	public void setAuthID(Integer authID) {
		this.authID = authID;
	}

	public String getAuthEmail() {
		return authEmail;
	}

	public void setAuthEmail(String authEmail) {
		this.authEmail = authEmail;
	}

	public String getAuthDesc() {
		return authDesc;
	}

	public void setAuthDesc(String authDesc) {
		this.authDesc = authDesc;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	

} // End Authorization Class

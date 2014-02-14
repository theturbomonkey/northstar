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
@Table ( name = "tblAppPassphrase" )
public class AppPassphrase 
{

	// Define each of the JPA column value mappings.
	@Id
	@GeneratedValue ( generator = "increment" )
	@GenericGenerator ( name="increment", strategy = "increment" )
	@Column ( name = "PassphraseID" )
	private Integer passphraseID;
	
	@Column ( name = "Passphrase", nullable = false, length = 512)
	private String passphrase;
	
	@Temporal ( TemporalType.TIMESTAMP )
	@Column ( name = "CreateDate" )
	private Date createDate;
	
	public AppPassphrase ()
	{
	} // End AppPassphrase Constructor
	
	
	// This ID get method is for Backbone model integration.
	public Integer getId() {
		return passphraseID;
	} // End getId


	public void setId(Integer passphraseID) {
		this.passphraseID = passphraseID;
	} // End setId

	
	// Setters and Getters - Auto-generated
	public Integer getPassphraseID() {
		return passphraseID;
	}

	public void setPassphraseID(Integer passphraseID) {
		this.passphraseID = passphraseID;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
		
} // End AppPassphrase Class

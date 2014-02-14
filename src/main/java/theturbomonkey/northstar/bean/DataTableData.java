package theturbomonkey.northstar.bean;

import java.io.Serializable;
import java.util.List;

//****************************************************************************
// Class: DataTableData
/**
 * A JavaScript DataTable component requires the array of row data to be returned
 * as the value of a JSON property named "aaData". This class, when serialized
 * to JSON presents a list of values of a templated data type as the value of 
 * a property named aaData.
 */
//****************************************************************************
public class DataTableData <T> implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	List<T> aaData = null;
	
	public DataTableData ( List<T> aaData )
	{
		this.aaData = aaData;
	} // End DataTableData Constructor
	
	
	public List<T> getaaData ()
	{
		return this.aaData;
	} // End getaaData
	
	
	public void setaaData ( List<T> aaData )
	{
		this.aaData = aaData;
	} // End setaaData
} // End DataTableData Class

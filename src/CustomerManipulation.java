import java.util.*;
import java.sql.*;
import java.io.*;


/**
* 	Class Name: CustomerManipulation
*		Author: Logan C. Urfer
*
* Dependencies: java.util, java.io
*
*	   Purpose: This class is responsible for updating, adding, and deleting tuples
*				from the customer table
*
*  Constructor: Paramaterized Constructor - userIn is used to grab user input from command line,
*				and db_conn is used to communicate with the Oracle DB.
*
*/
public class CustomerManipulation {
	// CustomerID, fname, lname, address, height, sex, dob
	// number    ,string,string  ,string, number, char, date

	// Used for sending and receiving messages from the Oracle DB.
	private dbConnection conn;
	// Used for grabbing user input from command line
	private Scanner user_in;
	
	// Define our sql queries
	private String addCustomerSQL = "INSERT INTO KATUR.customer (CustomerID, " +
									"fName, lName, address, height, sex, dob) " +
									"values (katur.seq_customer.nextval, '<fName>', " +
									"'<lName>', '<addr>', <height>, '<sex>', " +
									"TO_DATE('<date>', 'YYYY-MM-DD'))";
	private String deleteCustomerSQL = "DELETE FROM katur.customer WHERE CustomerID=<#ID>";
	private String updateCustomerSQL = "UPDATE KATUR.Customer set <attr>='<newval>' WHERE customerid=<#ID>";

	/**
	* 	 Name: EmployeeTuple
	*  Params: userIn - Scanner object for grabbing user input
	*			dbConn- dbConnection object for send and recieving messages from an oracle db.
	* Purpose: Saves references to user input scanner and Oracle db connection object.
	*/
	public CustomerManipulation(Scanner user_in, dbConnection conn) {
		this.user_in = user_in;
		this.conn = conn;
	}
	
	/**
	*	 Name: addCustomer
	* Purpose: Adds a tuple to the customer table in the oracle DB. Prompts user for input then 
	*			sends that info in an sql statement.
	*/
	public void addCustomer() {
		System.out.println("Please enter the following details: ");
		System.out.print("First Name: ");
		String fName = grabAndValidateInput().toUpperCase();

		System.out.print("Last Name: ");
		String lName = grabAndValidateInput().toUpperCase();
		
		System.out.print("Address: ");
		String address = grabAndValidateInput().toUpperCase();

		// height sex DOB can be NULL
		System.out.print("Height (Total Inches): ");
		String height = grabAndValidateIntegerInput(0, 3);

		System.out.print("Sex (M, F, or NULL): ");
		String sex = grabAndValidateSexInput().toUpperCase();

		System.out.println("Date of Birth");
		System.out.print("Year (YYYY): ");
		String year = grabAndValidateIntegerInput(4, 4);

		System.out.print("Month (MM): ");
		String month = grabAndValidateIntegerInput(2, 2);

		System.out.print("Day (DD): ");
		String day = grabAndValidateIntegerInput(2, 2);

		String query = addCustomerSQL.replace("<fName>", fName);
		query = query.replace("<lName>", lName);
		query = query.replace("<addr>", address);
		query = query.replace("<height>", height);
		
		if (sex.equals("NULL"))
			query = query.replace("'<sex>'", sex);
		else
			query = query.replace("<sex>", sex);

		query = query.replace("<date>", (year + "-" + month + "-" + day));
		
		if (this.conn.executeQuery(query) == null)
			System.out.println("Inserting new customer didn't work! Please check your date of birth values.");
		else
			System.out.println("1 Customer successfully added!");
	}

	/**
	*	 Name: deleteCustomer
	* Purpose: Deletes a tuple from the customer table by prompting user for id of employee.
	*
	*/
	public void deleteCustomer() {
		System.out.print("Please enter the ID of the customer you are trying to delete: ");
		String customerID = grabAndValidateIntegerInput(1, 10);
		
		int rowsEffected = this.conn.executeUpdate(deleteCustomerSQL.replace("<#ID>", customerID));
		
		if (rowsEffected == 0)
			System.out.println("0 Customers deleted, maybe the CustomerID doesn't exist?");
		else
			System.out.println(rowsEffected + " Employee deleted.");
	}
	
	/**
	*	 Name: updateCustomer
	* Purpose: Updated an customer tuple in the employee table by prompting the user
	*			to select the attribute they would like to change then the value to change to.
	*/
	public void updateCustomer() {
		System.out.print("Please enter the ID # of the customer you are trying to update: ");
		String customerID = grabAndValidateIntegerInput(1, 10);

		System.out.println("Attribute available to change: FName, LName, Address, Height, Sex");
		System.out.print("Please enter the attribute you would like to change: ");
		String attr = grabAndValidateInput().toLowerCase();
		
		System.out.print("Please enter the new value: ");
		String newVal = grabAndValidateInput().toUpperCase();

		String query = updateCustomerSQL.replace("<attr>", attr);
		query = query.replace("<#ID>", customerID);
		
		if (attr.equals("fname") || attr.equals("lname") || attr.equals("address") || attr.equals("sex"))
			query = query.replace("<newval>", newVal);
		else
			query = query.replace("'<newval>'", newVal);
		
		if (this.conn.executeQuery(query) == null)
			System.out.println("Update did not work! Please try again.");
		else
			System.out.println("Customer " + customerID + " updated!");

	}
	
	/**
	*	 Name: grabAndValidateSexInput
	* Purpose: Continually prompts the user for input until the information provided is of
	*			correct format
	*/
	private String grabAndValidateSexInput() {
		String sex = this.user_in.nextLine().toUpperCase();
		
		while (!(sex.equals("M") || sex.equals("F") || sex.equals("NULL"))) {
			System.out.println("Invalid input! Only values M, F, or NULL can be submitted. Please try again.");
			sex = this.user_in.nextLine();
		}

		return sex;
	}

	/**
	* 	 Name: grabAndValidateIntegerInput
	*  Params: minLength - int used to indicate the minimum amount of digits allowed
	*			maxLength - int used to indicate the maximum amount of digits allowed
	* Purpose: Continually prompts the user for input until the information provided is of 
	*			correct format
	*/
	private String grabAndValidateIntegerInput(int minLength, int maxLength) {
		String ret = this.user_in.nextLine();
		
		while (!ret.matches("[0-9]+") || ret.length() < minLength || ret.length() > maxLength) {
			System.out.print("Invalid numerical input, please try again: ");
			ret = this.user_in.nextLine();
		}
		return ret;

	}
	
	/**
	* 	 Name: grabAndValidateInput
	* Purpose: Continually prompts the user for input until the information provided is of 
	*			correct format
	*/
	private String grabAndValidateInput() {
		String ret = this.user_in.nextLine();
		
		while (ret.length() == 0) {
			System.out.print("Invalid input, please try again: ");
			ret = this.user_in.nextLine();
		}
		
		return ret;
	}
}

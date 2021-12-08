import java.io.*;
import java.util.*;
import java.sql.*;


/**
*	Class Name: EmployeeTuple
*		Author: Logan C. Urfer
*
* Dependencies: java.io, java.util, java.sql
*				
*	   Purpose: This class is responsible for update, adding and deleting tuples from the
*				employee table.
*
*  Constructor: Parameterized Constructor - userIn is used to grab user input from command line,
*				and db_conn is used to communicate with the Oracle DB.
*
*/
public class EmployeeTuple {

	// Used for sending and receiving messages from Oracle DB.
	private dbConnection conn;
	// Used for grabbing input from command line
	private Scanner user_in;
	
	// Define our sql querys
	private String deleteEmployeeSQL = "DELETE FROM katur.Employee WHERE EmployeeID=<#ID>";
	private String addEmployeeSQL = "INSERT INTO KATUR.Employee (employeeID, deptid, fname, lname, address, salary, jobtitle, sex) "
									+ "VALUES (KATUR.SEQ_EMPLOYEE.nextval, <deptID>, '<fName>', '<lName>', '<address>', <salary>, '<jobTitle>', '<sex>')";
	private String searchForEmployeeQuery = "SELECT * FROM katur.Employee WHERE EmployeeID=<#ID>";

	private String updateEmployeeQuery = "UPDATE KATUR.Employee SET <attr>='<newval>' WHERE employeeid=<#ID>";
	private String checkIfDeptExistsQuery = "SELECT * FROM katur.Department WHERE DeptID=<#ID>";

	/**
	* 	 Name: EmployeeTuple
	*  Params: userIn - Scanner object for grabbing user input
	*			dbConn- dbConnection object for send and recieving messages from an oracle db.
	* Purpose: Saves references to user input scanner and Oracle db connection object.
	*/
	public EmployeeTuple(Scanner userIn, dbConnection db_conn) {
		this.conn = db_conn;
		this.user_in = userIn;
	}

	/**
	*	 Name: addEmployee
	* Purpose: Adds a tuple to the employee table in the oracle DB. Prompts user for input then 
	*			sends that info in an sql statement.
	*/			
	public void addEmployee() {
		System.out.println("Please enter the following details:");
		
		System.out.print("Department ID (Only numbers allowed in input): ");
		String deptID = grabAndValidateIntegerInput();
		
		// then need to make sure that department ID inserted is a valid id
		while (!checkIfDeptIdExists(deptID)) {
			System.out.print("Department ID does not exist, please try again: ");
			deptID = grabAndValidateIntegerInput();
		}

		System.out.print("First Name: ");
		String fName = grabAndValidateInput().toUpperCase();
		
		System.out.print("Last Name: ");
		String lName = grabAndValidateInput().toUpperCase();
		
		System.out.print("Address (Can be empty): ");
		String address = this.user_in.nextLine().toUpperCase();
		
		System.out.print("Job Title: ");
		String jobTitle = grabAndValidateInput().toUpperCase();
		
		System.out.print("Salary (Only numbers allowed in input): ");
		String salary = grabAndValidateIntegerInput();

		System.out.print("Sex (M, F, or NULL): ");
		String sex = grabAndValidateSexInput().toUpperCase();
		
		String query = addEmployeeSQL.replace("<deptID>", deptID).replace("<fName>", fName).replace("<lName>", lName).replace("<address>", address);
		query = query.replace("<jobTitle>", jobTitle).replace("<salary>", salary);
		
		if (sex.equals("NULL")) {
			query = query.replace("'<sex>'", sex);
		} else
			query = query.replace("<sex>", sex);
		
	
		int rowsEffected = this.conn.executeUpdate(query);
		System.out.println(rowsEffected + " Employee added!");
	}

	/**
	*	 Name: deleteEmployee
	* Purpose: Deletes a tuple from the employee table by prompting user for id of employee.
	*
	*/
	public void deleteEmployee() {
		// prompt the user to enter in the ID of the employee they would like to delete	
		System.out.print("Please enter the ID of the employee you are trying to delete: ");
		String employeeID = grabAndValidateIntegerInput();

		// ********* MIGHT NEED TO DELETE THIS *********
		// because we might not want to delete apptxact
		// might need to delete associated appointments
		String deleteApptXact = "DELETE FROM katur.apptxact WHERE employeeid=<#ID>";
		deleteApptXact = deleteApptXact.replace("<#ID>", employeeID);

		int apptsDeleted = this.conn.executeUpdate(deleteApptXact);
		this.conn.commit();
		// *******************************************

		int rowsEffected = this.conn.executeUpdate(deleteEmployeeSQL.replace("<#ID>", employeeID));
		this.conn.commit();
				
		if (rowsEffected == 0)
			System.out.println("0 Employees deleted, maybe the EmployeeID doesn't exist?");
		else	
			System.out.println(rowsEffected + " Employee deleted");
	}	

	/**
	*	 Name: updateEmployee
	* Purpose: Updated an employee tuple in the employee table by prompting the user
	*			to select the attribute they would like to change then the value to change to.
	*/
	public void updateEmployee() {
		System.out.print("Please enter the ID of the employee whose information you would like to update: ");
		String employeeID = grabAndValidateIntegerInput();
		
		// Check if employee exists. If not, then let user know and return.
		String lookForEmployee = searchForEmployeeQuery.replace("<#ID>", employeeID);
		ResultSet employee = this.conn.executeQuery(lookForEmployee);
		
		try {
			if (!employee.next()) {
				System.out.println("No Employee exists with that ID!");
				return;
			} 
		} catch (SQLException e) {
			System.out.println("L Bruh: Line 92 EmployeeTuple");
			return;
		}

		// Allow user to select from options
		System.out.println("Attributes available to change: DeptID, FName, LName, Address, Salary, JobTitle, sex");
		System.out.print("Please select the attribute you would like to change: ");
		String attr = this.user_in.nextLine().toLowerCase();

		// if the attribute name is invalid, then ask to repeat
		while (!(attr.equals("deptid") || attr.equals("fname") || attr.equals("lname") || attr.equals("sex") || attr.equals("address") || attr.equals("salary") || attr.equals("jobtitle"))) {
			System.out.println("The attribute selected does not exist! Please try again.");
			attr = this.user_in.nextLine().toLowerCase();
		}

		System.out.print("Please enter the new value: ");
		String value = this.user_in.nextLine().toUpperCase();

		String query = "";
		query = updateEmployeeQuery.replace("<attr>", attr);

		if (!(attr.equals("deptid") || attr.equals("salary") || (attr.equals("sex") && value.equals("NULL"))))
			query = query.replace("<newval>", value);
		else
			query = query.replace("'<newval>'", value);
	
		System.out.println(query);

		query = query.replace("<#ID>", employeeID);

		this.conn.executeQuery(query);
	}

	/**
	*	 Name: checkIfDeptIdExists
	*  Params: id - String object that contains the employee id to check
	* Purpose: Checks to see if an the passed employee id exists in the employee table.
	*
	*/
	private boolean checkIfDeptIdExists(String id) {
		String query = checkIfDeptExistsQuery.replace("<#ID>", id); 	
		ResultSet dept = this.conn.executeQuery(query);

		try {
			if (dept.next())
				return true;
		} catch (SQLException e) {
			System.out.println("Error in EmployeeTuple Line 138");
			return false;
		}
		return false;
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
	* Purpose: Continually prompts the user for input until the information provided is of 
	*			correct format
	*/
	private String grabAndValidateIntegerInput() {
		String ret = this.user_in.nextLine();
		
		while (!ret.matches("[0-9]+")) {
			System.out.print("Invalid input, please try again: ");
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

import java.io.*;
import java.util.*;
import java.sql.*;

public class EmployeeTuple {

	// katur.tableName for SQL
	private dbConnection conn;
	private Scanner user_in;
	
	private String deleteEmployeeSQL = "DELETE FROM katur.Employee WHERE EmployeeID=<#ID>";
	private String addEmployeeSQL = "INSERT INTO KATUR.Employee (employeeID, deptid, fname, lname, address, salary, jobtitle, sex) "
									+ "VALUES (KATUR.SEQ_EMPLOYEE.nextval, <deptID>, '<fName>', '<lName>', '<address>', <salary>, '<jobTitle>', '<sex>')";
	private String searchForEmployeeQuery = "SELECT * FROM katur.Employee WHERE EmployeeID=<#ID>";

	private String checkIfDeptExistsQuery = "SELECT * FROM katur.Department WHERE DeptID=<#ID>";

	private String updateEmployeeQuery = "UPDATE KATUR.Employee SET KATUR.Employee.<attr>='<newval>' where KATUR.Employee.EMPLOYEEID=<#ID>";

	public EmployeeTuple(Scanner userIn, dbConnection db_conn) {
		this.conn = db_conn;
		this.user_in = userIn;
	}

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
		String fName = grabAndValidateInput();
		
		System.out.print("Last Name: ");
		String lName = grabAndValidateInput();
		
		System.out.print("Address (Can be empty): ");
		String address = this.user_in.nextLine();
		
		System.out.print("Job Title: ");
		String jobTitle = grabAndValidateInput();
		
		System.out.print("Salary (Only numbers allowed in input): ");
		String salary = grabAndValidateIntegerInput();

		System.out.print("Sex (M, F, or NULL): ");
		String sex = grabAndValidateSexInput();
		
		String query = addEmployeeSQL.replace("<deptID>", deptID).replace("<fName>", fName).replace("<lName>", lName).replace("<address>", address);
		query = query.replace("<jobTitle>", jobTitle).replace("<salary>", salary).replace("<sex>", sex);
		
		int rowsEffected = this.conn.executeUpdate(query);
		System.out.println(rowsEffected + " Employee added!");
	}

	

	public void deleteEmployee() {
		// prompt the user to enter in the ID of the employee they would like to delete	
		System.out.print("Please enter the ID of the employee you are trying to delete: ");
		String employeeID = grabAndValidateIntegerInput();

		int rowsEffected = this.conn.executeUpdate(deleteEmployeeSQL.replace("<#ID>", employeeID));
		if (rowsEffected == 0)
			System.out.println("0 Employees deleted, maybe the EmployeeID doesn't exist?");
		
		System.out.println(rowsEffected + " Employee deleted");
	}	

	public void updateEmployee() {
		// Prompt user for Employee PK
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
		System.out.println("Attributes available to change: DeptID, FName, LName, Address, Salary, JobTitle");
		System.out.print("Please select the attribute you would like to change: ");
		String attr = this.user_in.nextLine().toLowerCase();

		// if the attribute name is invalid, then ask to repeat
		while (!(attr.equals("deptid") || attr.equals("fname") || attr.equals("lname") || attr.equals("address") || attr.equals("salary") || attr.equals("jobtitle"))) {
			System.out.println("The attribute selected does not exist! Please try again.");
			attr = this.user_in.nextLine().toLowerCase();
		}

		// Grab the new value to update with. This could user some error checking.
		// Like if we ask for salary, we then want a string containing only numbers.
		// Same goes for department ID
			// In that case, we would need to check that deptID exists.
			// pain 
		System.out.print("Please enter the new value: ");
		String value = this.user_in.nextLine();

		String query = "";
		
		if (!(attr.equals("deptid") || attr.equals("salary")))
			query = updateEmployeeQuery.replace("<newval>", value);  
		else
			query = updateEmployeeQuery.replace("'<newval>'", value);

		query = query.replace("<#ID>", employeeID);
		query = query.replace("<attr>", attr.toUpperCase());
		System.out.println(query);
	
		this.conn.executeUpdate(updateEmployeeQuery);

	}

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

	private String grabAndValidateSexInput() {
		String sex = this.user_in.nextLine().toUpperCase();
		
		while (!(sex.equals("M") || sex.equals("F") || sex.equals("NULL"))) {
			System.out.println("Invalid input! Only values M, F, or NULL can be submitted. Please try again.");
			sex = this.user_in.nextLine();
		}

		return sex;
	}

	private String grabAndValidateIntegerInput() {
		String ret = this.user_in.nextLine();
		
		while (!ret.matches("[0-9]+")) {
			System.out.print("Invalid input, please try again: ");
			ret = this.user_in.nextLine();
		}
		return ret;

	}

	private String grabAndValidateInput() {
		String ret = this.user_in.nextLine();
		
		while (ret.length() == 0) {
			System.out.print("Invalid input, please try again: ");
			ret = this.user_in.nextLine();
		}
		
		return ret;
	}
}

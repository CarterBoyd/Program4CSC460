import java.util.*;
import java.io.*;

public class CustomerManipulation {
	// CustomerID, fname, lname, address, height, sex, dob
	// number    ,string,string  ,string, number, char, date

	private dbConnection conn;
	private Scanner user_in;
	
	private String addCustomerSQL = "INSERT INTO KATUR.customer (CustomerID, " +
									"fName, lName, address, height, sex, dob) " +
									"values (katur.seq_customer.nextval, '<fName>', " +
									"'<lName>', '<addr>', <height>, '<sex>', " +
									"TO_DATE('<date>', 'YYYY/MM/DD'))";
	private String deleteCustomerSQL = "DELETE FROM katur.customer WHERE CustomerID=<#ID>";
	private String updateCustomerSQL = "";

	public CustomerManipulation(Scanner user_in, dbConnection conn) {
		this.user_in = user_in;
		this.conn = conn;
	}

	public void addCustomer() {
		System.out.println("Please enter the following details: ");
		System.out.print("First Name: ");
		String fName = grabAndValidateInput();

		System.out.print("Last Name: ");
		String lName = grabAndValidateInput();
		
		System.out.print("Address: ");
		String address = grabAndValidateInput();

		// height sex DOB can be NULL
		System.out.print("Height (Total Inches or NULL): ");
		String height = grabAndValidateIntegerInput();

		System.out.print("Sex (M, F, or NULL): ");
		String sex = grabAndValidateSexInput();

		System.out.print("Date (YYYY/MM/DD): ");
		String date = grabAndValidateInput();

		String query = addCustomerSQL.replace("<fName>", fName);
		query = query.replace("<lName>", lName);
		query = query.replace("<addr>", address);
		query = query.replace("<height>", height);
		query = query.replace("<sex>", sex);
		query = query.replace("<date>", date);
		
		int rowsEffected = this.conn.executeUpdate(query);
		System.out.println(rowsEffected + " Customer added!");
	}

	public void deleteCustomer() {
		System.out.print("Please enter the ID of the customer you are trying to delete: ");
		String customerID = grabAndValidateIntegerInput();
		
		int rowsEffected = this.conn.executeUpdate(deleteCustomerSQL.replace("<#ID>", customerID));
		
		if (rowsEffected == 0)
			System.out.println("0 Customers deleted, maybe the CustomerID doesn't exist?");
		else
			System.out.println(rowsEffected + " Employee deleted.");
	}

	public void updateCustomer() {


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

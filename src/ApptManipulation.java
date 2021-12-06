import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.io.*;
import java.sql.*;
import java.util.Scanner;

/*
Author: Raymond Rea
Netid:  raymondprea

Purpose: This file is used to perform database manipulations on 
the AppointmentTransaction database. This file allows the user 
to add a tuple to the database, delete a tuple, or change 
values of a tuple in the database. 
*/
public class ApptManipulation {

    private static String del = "DELETE FROM KATUR.ApptXact WHERE " +
                                "CustomerID = <@> AND StartTime = <#>"; 

    private static String add = "INSERT INTO KATUR.ApptXact " +
                                "VALUES(<!>, TO_DATE(<@>, 'YYYY-MM-DD HH:MM'), <#>, <$>, <%>, <^>, TO_DATE(<&>, 'YYYY-MM-DD HH:MM'), <*>)";

    private static String update = "UPDATE KATUR.ApptXact " +
                                   "SET <@> = <#> WHERE CustomerId = <$> AND StartTime = <&>";
    private static String overlapCheck = "SELECT COUNT(*) FROM KATUR.ApptXact a " +
                                         "WHERE a.EndTime = TO_DATE(<date>, 'YYYY/MM/DD HH:MM')" +
                                         "AND a.CustomerID = <custid> AND ";

    private static final String EMPLOYEE_SEARCH = """
            Select count(*) from katur.apptxact where
                (STARTTIME > TO_DATE(<startdate>,  'YYYY/MM/DD HH:MN') AND STARTTIME < TO_DATE(<enddate>, 'YYYY/MM/DD HH:MN')
                    OR
                (ENDTIME < TO_DATE(<enddate>, 'YYYY/MM/DD HH:MN') AND ENDTIME > (TO_DATE<startdate>, 'YYYY/MM/DD HH:MN'))
                    AND CUSTOMERID = <ID>""";

    private static Scanner input = null;
    private static dbConnection dbConn = null;

    //TODO: if appointment is successful, a new document needs to be added
    // with the customer id and the service type

    //TODO: need to make sure that appointments don't overlap, 
    // can check for this on insert

    //TODO: add endtime where endtime is 1 hr after starttime
    ApptManipulation(Scanner input, dbConnection dbConn) {
        this.input = input;
        this.dbConn = dbConn;
    }

    /*
    This method uses the provided parameters to remove a 
    paticular tuple from the ApptXAct table. 

    Params: none
    Return: none
    */
    public static void deleteAppt() {
        System.out.println("--------------- Delete Appointment ---------------\n");

        System.out.println("CustomerID: ");
        String cusID = input.nextLine();
        System.out.println();

        System.out.println("StartTime: ");
        String st = input.nextLine();
        System.out.println();

        // replacing values in delete statement
        String delStmt = del.replace("<@>", cusID);
        delStmt = delStmt.replace("<#>", st);
        dbConn.executeQuery(delStmt);
    }

    /*
    This method uses the provided parameters to insert a tuple
    into the ApptXAct table.

    Params: none
    Return: none
    */
    public static void addAppt() {
        System.out.println("--------------- Add Appointment ---------------\n");

        System.out.println("DeptID: ");
        String deptID = input.nextLine();
        System.out.println();

        System.out.println("EmployeeID: ");
        String empID = input.nextLine();
        System.out.println();

        System.out.println("CustomerID: ");
        String custID = input.nextLine();
        System.out.println();
        
        System.out.println("StartTime: ");
		String[] results = getDateFromUser();
		String st = results[0] + '-' + results[1] + '-' + results[2] + "- " + results[3] + ':' + results[4];
        System.out.println();

        System.out.println("Cost: ");
        String cost = input.nextLine();
        System.out.println();

        // successful codes: -1 = hasn't started, 0 = unsuccessful, 1 = successful
        // successful is -1 by defualt because the appointment hasn't 
        // started yet
		System.out.println("Was this transaction successful? (1 for yes, 0 for no)");
        String successful = input.nextLine();
		while(!isZeroOrOne(successful))
			successful = input.nextLine();
        System.out.println("EndTime: ");
		String[] endResult = getDateFromUser();
		String et = endResult[0] + '-' + endResult[1] + '-' + endResult[2] + "- " + endResult[3] + ':' + endResult[4];
        System.out.println();

		String type = getType(deptID);
        // replacing values in add statement
        String addStmt = add.replace("<!>", deptID);
        addStmt = addStmt.replace("<@>", empID);
        addStmt = addStmt.replace("<#>", custID);
        addStmt = addStmt.replace("<$>", st);
        addStmt = addStmt.replace("<%>", cost);
        addStmt = addStmt.replace("<^>", successful);
        addStmt = addStmt.replace("<&>", et);
		addStmt = addStmt.replace("<*>", type);
		
        dbConn.executeQuery(addStmt);
		if (successful.equals("1"))
			createDocument(results, deptID, custID, type);
    }

	/**
	 * adds tuple into the document table
	 * @param results the results
	 * @param deptID the department ID
	 * @param custID the customer ID
	 * @param type the type
	 */
	private static void createDocument(String[] results, String deptID, String custID, String type) {
		String year = null;
		switch (type) {
			case "PERMIT", "VEHICLE REGISTRATION" -> year = String.valueOf(Integer.parseInt(results[0]) + 1);
			case "LICENSE" -> year = String.valueOf(Integer.parseInt(results[0]) + 12);
			case "STATE ID" -> year = String.valueOf(Integer.parseInt(results[0]) + 20);
		}
		final String query = "INSERT INTO KATUR.DOCUMENT values (KATUR.SEQ_DOCUMENT.nextval, " + deptID + ", " + custID +
				", TO_DATE(" + results[0] + '-' + results[1] + '-' + results[2] + ", 'YYYY-MM-DD'), TO_DATE(" + year +
				'-' + results[1] + '-' + results[2] + ", 'YYYY-MM-DD')";
		dbConn.executeQuery(query);
	}

	private static boolean isZeroOrOne(String successful) {
		return successful.equals("0") || successful.equals("1");
	}

	private static String getType(String deptID) {
		ResultSet result = dbConn.executeQuery("select DEPTNAME from katur.DEPARTMENT" +
				" where DEPTID = " + deptID);
		try {
			result.next();
			return result.getString(1);
		} catch(SQLException e) {
			return "fucked up";
		}
	}

	/*
	select DEPTNAME from katur.DEPARTMENT
    where DEPTID = deptID
	 */

	public static String[] getDateFromUser() {
		String[] dateArr = new String[5];
		
		System.out.print("Year (YYYY): ");
		String year = grabAndValidateNumericInput(4);
		
		System.out.print("Month (MM): ");
		String month = grabAndValidateNumericInput(2);

		System.out.print("Day (DD): ");
		String day = grabAndValidateNumericInput(2);
		
		System.out.print("Hour (HH): ");
		String hour = grabAndValidateNumericInput(2);
		
		System.out.print("Minute (MM): ");
		String minute = grabAndValidateNumericInput(2);
		
		dateArr[0] = year;
		dateArr[1] = month;
		dateArr[2] = day;
		dateArr[3] = hour;
		dateArr[4] = minute;

		return dateArr;
	}

	public static String grabAndValidateNumericInput(int length) {
		String userInput = input.nextLine();
		while (userInput.length() != length || checkIfNumeric(userInput) == -1) {
			System.out.println("Invalid value, must be a number of length: " + length);
			System.out.print("Please input a new value: ");
			userInput = input.nextLine();
		}
		return userInput;
	}

	public static int checkIfNumeric(String input) {
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			return -1;		
		}
	}

    /*
    This method prompts the user for the primary key of the 
    tuple they want to change, it then asks the user for 
    the attribute they want to change and what the new value 
    should be. The method then uses a database connection to 
    attempt the update

    Params: none
    Return: none
    */
    public static void updateAppt() {
        System.out.println("--------------- Update Appointment ---------------\n");

        System.out.println("Please give a Customer ID and a Start Time of the " + 
        "appointment you would like to change");
        System.out.print("CustomerID: ");
        String custID = input.nextLine();
        System.out.println();
        System.out.print("StartTime: ");
        String st = input.nextLine();
        System.out.println();

        System.out.print("Which attribute would you like to change?");
        System.out.println(" (DepartmentID, EmployeeID, CustomerID, StartTime, Cost, Successful, EndTime)");
        System.out.print("Attribute to change: ");
        String changeAttr = input.nextLine();

        System.out.println("New Value: ");
        String newVal = input.nextLine();

        String updateStmt = update.replace("<@>", changeAttr);
        updateStmt = updateStmt.replace("<#>", newVal);
        updateStmt = updateStmt.replace("<$>", custID);
        updateStmt = updateStmt.replace("<&>", st);

        dbConn.executeQuery(updateStmt);
    }
}

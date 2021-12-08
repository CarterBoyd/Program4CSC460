import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Purpose: This file is used to perform database manipulations on
 * the AppointmentTransaction database. This file allows the user
 * to add a tuple to the database, delete a tuple, or change
 * values of a tuple in the database.
 * Author: Raymond Rea
 * Netid:  raymondprea
 */
public class ApptManipulation {

	private static String del = "DELETE FROM KATUR.ApptXact WHERE " +
								"CustomerID = <@> AND StartTime = <#>";

	private static String add = "INSERT INTO KATUR.ApptXact " +
								"VALUES(<!>, TO_DATE('<@>', 'YYYY-MM-DD'), <#>, <$>, <%>, <^>, TO_DATE('<&>', 'YYYY-MM-DD'))";

	private static String update = "UPDATE KATUR.ApptXact " +
								   "SET <@> = <#> WHERE CustomerId = <$> AND StartTime = <&>";
	private static final int YEAR = 0;
	private static final int MONTH = 1;
	private static final int DAY = 2;

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

	/**
	 * This method uses the provided parameters to remove a
	 * particular tuple from the ApptXAct table.
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

	/**
	 * This method uses the provided parameters to insert a tuple
	 * into the ApptXAct table.
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
		String st = results[YEAR] + '-' + results[MONTH] + '-' + results[DAY];
		System.out.println();

		String type = getType(deptID);
		String cost = "";
		switch (type) {
			case "PERMIT" -> cost = "7";
			case "LICENSE" -> cost = "25";
			case "STATE ID" -> cost = "100";
			case "VEHICLE REGISTRATION" -> cost = "12";
		}
		System.out.println();

		// successful codes: -1 = hasn't started, 0 = unsuccessful, 1 = successful
		// successful is -1 by defualt because the appointment hasn't
		// started yet
		System.out.println("Was this transaction successful? (1 for yes, 0 for no)");
		String successful = input.nextLine();
		
		while(!isZeroOrOne(successful))
			successful = input.nextLine();

		if (successful.equals("1") && (hasOverlaps(st, custID) || hasLicense(custID, st))) {
			System.out.println("Overlap triggered");
			successful = "0";
		}
	
		// replacing values in add statement
		String addStmt = add.replace("<$>", deptID);
		addStmt = addStmt.replace("<#>", empID);
		addStmt = addStmt.replace("<!>", custID);
		addStmt = addStmt.replace("<@>", st);
		addStmt = addStmt.replace("<%>", cost);
		addStmt = addStmt.replace("<^>", successful);
		addStmt = addStmt.replace("<&>", st);

		dbConn.executeQuery(addStmt);

		if (successful.equals("1"))
			createDocument(results, deptID, custID, type);
	}

	/**
	 * since the user can not have multiple licenses here will check to see if the user has an unexpired license
	 *
	 * @param custID the customer that is being checked for multiple licenses
	 * @param st the start date
	 * @return true if the user already has a license, false otherwise
	 *
	 * have not tested yet!!!
	 */
	private static boolean hasLicense(String custID, String st) {
		String query = "select * from katur.document a, katur.department b " +
						"where a.customerid = <somenumber> and a.deptid = b.deptid " +
						"and b.deptname = 'LICENSE' and " +
						"a.issuedate < TO_DATE('<someday>', 'YYYY-MM-DD') " +
						"and a.expirydate > TO_DATE('<somedate>', 'YYYY-MM-DD')";
		
		query = query.replace("<somenumber>", custID);
		query = query.replace("<somedate>", st);
		query = query.replace("<someday>", st);

		ResultSet results = dbConn.executeQuery(query);
		try {
			return results.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Designed to check if the selected appointment overlaps
	 * @param startTime the start time of the appointment
	 * @param cusID the id of the employee, other employees appointments shouldn't matter when looking for overlaps
	 * @return true if there are overlaps, false if there are no overlaps
	 *
	 * @implNote this was created with minimal testing, this query will return results but someone verifies if this is how you will find overlaps
	 */
	private static boolean hasOverlaps(String startTime, String cusID) {
		String query = "SELECT * from katur.apptxact a where a.customerid=<#ID> and a.starttime=TO_DATE('<date>', 'YYYY-MM-DD')";
		query = query.replace("<#ID>", cusID);
		query = query.replace("<date>", startTime);


		ResultSet results = dbConn.executeQuery(query);
		try {
			return results.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * adds tuple into the document table
	 * @param results the results of the start time
	 * @param deptID the department ID
	 * @param custID the customer ID
	 * @param type the type
	 */
	private static void createDocument(String[] results, String deptID, String custID, String type) {
		String year = switch (type) {
			case "VEHICLE REGISTRATION", "PERMIT" -> String.valueOf(Integer.parseInt(results[YEAR]) + 1);
			case "LICENSE" -> String.valueOf(Integer.parseInt(results[YEAR]) + 12);
			case "STATE ID" -> String.valueOf(Integer.parseInt(results[YEAR]) + 20);
			default -> null;
		};

		String query = String.format("INSERT INTO KATUR.DOCUMENT values " +
									"(KATUR.SEQ_DOCUMENT.nextval, %s, %s," +
									"TO_DATE('%s-%s-%s', 'YYYY-MM-DD'), TO_DATE('%s-%s-%s', 'YYYY-MM-DD'))", deptID, custID, results[YEAR], results[MONTH], results[DAY], year, results[MONTH], results[DAY]);
		dbConn.executeQuery(query);
		if (type.equals("VEHICLE REGISTRATION"))
			createVehicle();
	}

	/**
	 * Inserts a vehicle into the vehicles table when a vehicle registration has been added to documents.
	 */
	private static void createVehicle() {
		String query = "INSERT INTO KATUR.VEHICLE VALUES(KATUR.SEQ_DOCUMENT.currval, '<liscence#>', '<make>', '<model>', '<state>')";

		System.out.println("------------- Add Vehicle -------------");
		System.out.print("License Number: ");
		String lNum = input.nextLine();
		System.out.println();

		System.out.print("Make: ");
		String make = input.nextLine().toUpperCase();
		System.out.println();

		System.out.print("Model: ");
		String model = input.nextLine().toUpperCase();
		System.out.println();

		System.out.print("State: ");
		String state = input.nextLine().toUpperCase();
		System.out.println();

		query = query.replace("<liscence#>", lNum);
		query = query.replace("<make>", make);
		query = query.replace("<model>", model);
		query = query.replace("<state>", state);

		dbConn.executeQuery(query);
	}

	/**
	 * Checks if the string provided is a simple 0 or 1 value
	 * @param successful, the string to check
	 * @return is a 0 or 1 boolean/binary value
	 */
	private static boolean isZeroOrOne(String successful) {
		return successful.equals("0") || successful.equals("1");
	}

	/**
	 * Returns the String name of a department, a.k.a. the type of service it renders.
	 * @param deptID, the department id to check against.
	 * @return the department name
	 */
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

	/**
	 * converts a user provided array of date values into a valid date format
	 * @return string in a valid date format
	 */
	public static String[] getDateFromUser() {
		String[] dateArr = new String[5];

		System.out.print("Year (YYYY): ");
		String year = grabAndValidateNumericInput(4);

		System.out.print("Month (MM): ");
		String month = grabAndValidateNumericInput(2);

		System.out.print("Day (DD): ");
		String day = grabAndValidateNumericInput(2);
		dateArr[YEAR] = year;
		dateArr[MONTH] = month;
		dateArr[DAY] = day;

		return dateArr;
	}

	/**
	 * validates that a user input is a numeric value for a given length.
	 * @param length length of input to check against.
	 * @return the user input after validation.
	 */
	public static String grabAndValidateNumericInput(int length) {
		String userInput = input.nextLine();
		while (userInput.length() != length || checkIfNumeric(userInput) == -1) {
			System.out.println("Invalid value, must be a number of length: " + length);
			System.out.print("Please input a new value: ");
			userInput = input.nextLine();
		}
		return userInput;
	}

	/**
	 * Checks if a string is made up of numeric values
	 * @param input string to check against
	 * @return integer value of string, else -1.
	 */
	public static int checkIfNumeric(String input) {
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * This method prompts the user for the primary key of the
	 * tuple they want to change, it then asks the user for
	 * the attribute they want to change and what the new value
	 * should be. The method then uses a database connection to
	 * attempt the update
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
		System.out.println(" (DepartmentID, EmployeeID, CustomerID, StartTime, Cost, Successfully, EndTime)");
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

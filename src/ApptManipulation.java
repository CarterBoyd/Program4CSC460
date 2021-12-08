import java.sql.ResultSet;
import java.sql.SQLException;
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
								"VALUES(<!>, TO_DATE('<@>', 'YYYY-MM-DD'), <#>, <$>, <%>, <^>, TO_DATE('<&>', 'YYYY-MM-DD'))";

	private static String update = "UPDATE KATUR.ApptXact " +
								   "SET <@> = <#> WHERE CustomerId = <$> AND StartTime = <&>";
	private static String overlapCheck = "SELECT COUNT(*) FROM KATUR.ApptXact a " +
										 "WHERE a.EndTime = TO_DATE('<date>', 'YYYY/MM/DD HH24:MI')" +
										 "AND a.CustomerID = <custid> AND ";
	private static final int YEAR = 0;
	private static final int MONTH = 1;
	private static final int DAY = 2;

	private static final String EMPLOYEE_SEARCH = """
			Select count(*) from katur.apptxact where
				(STARTTIME > TO_DATE('<startdate>',  'YYYY/MM/DD HH:MI') AND STARTTIME < TO_DATE('<enddate>', 'YYYY/MM/DD HH24:MI')
					OR
				(ENDTIME < TO_DATE('<enddate>', 'YYYY/MM/DD HH:MI') AND ENDTIME > (TO_DATE('<startdate>', 'YYYY/MM/DD HH24:MI'))
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
		
		/*
		System.out.println("EndTime: ");
		String[] endResult = getDateFromUser();
		String et = endResult[YEAR] + '-' + endResult[MONTH] + '-' + endResult[DAY];
		System.out.println();
		*/

	
		// replacing values in add statement
		String addStmt = add.replace("<$>", deptID);
		addStmt = addStmt.replace("<#>", empID);
		addStmt = addStmt.replace("<!>", custID);
		addStmt = addStmt.replace("<@>", st);
		addStmt = addStmt.replace("<%>", cost);
		addStmt = addStmt.replace("<^>", successful);
		addStmt = addStmt.replace("<&>", st);

		if (hasOverlaps(st, et, custID)) {
			System.out.println("Overlap triggered");
			return;
		}
		
		if (deptID.equals("1") && hasLicense(custID, st)) {
			System.out.println("Another License found");
			return;
		}
		System.out.println(addStmt);
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
		
		boolean blah = true;
	
		try {
			ResultSet results = dbConn.executeQuery(query);
			results.next();
		} catch (SQLException e) {
			blah = false;
		}
		return blah;
	}

	/**
	 * Designed to check if the selected appointment overlaps
	 * @param startTime the start time of the appointment
	 * @param endTime the end time of the appointment
	 * @param cusID the id of the employee, other employees appointments shouldn't matter when looking for overlaps
	 * @return true if there are overlaps, false if there are no overlaps
	 *
	 * @implNote this was created with minimal testing, this query will return results but someone verifies if this is how you will find overlaps
	 */
	private static boolean hasOverlaps(String startTime, String endTime, String cusID) {
		String query = String.format("""
				select * from KATUR.APPTXACT
				    where STARTTIME < TO_DATE('%s', 'YYYY MM DD')
				    and ENDTIME > TO_DATE('%s', 'YYYY MM DD')
				    and CUSTOMERID = %s""", endTime, startTime, cusID); //overlapping should be focussed on employee schedule, so here's a checker
		boolean toRet = true;
		ResultSet results;
		try {
			results = dbConn.executeQuery(query);
			results.next();
			
		} catch (SQLException e) {
			e.printStackTrace();
			toRet = false;
		}
		return toRet;
	}

	/**
	 * adds tuple into the document table
	 * @param results the results of the start time
	 * @param deptID the department ID
	 * @param custID the customer ID
	 * @param type the type
	 */
	private static void createDocument(String[] results, String deptID, String custID, String type) {
		String year = null;
		
		switch (type) {
						case "VEHICLE REGISTRATION":
							year = String.valueOf(Integer.parseInt(results[YEAR]) + 1);
							break;
			case "PERMIT":
							year = String.valueOf(Integer.parseInt(results[YEAR]) + 1);
							break;
			case "LICENSE": 
							year = String.valueOf(Integer.parseInt(results[YEAR]) + 12);
							break;
			case "STATE ID": 
							year = String.valueOf(Integer.parseInt(results[YEAR]) + 20);
							break;
		}
		String query = String.format("INSERT INTO KATUR.DOCUMENT values " +
									"(KATUR.SEQ_DOCUMENT.nextval, %s, %s," +
									"TO_DATE('%s-%s-%s', 'YYYY-MM-DD'), TO_DATE('%s-%s-%s', 'YYYY-MM-DD'))", deptID, custID, results[YEAR], results[MONTH], results[DAY], year, results[MONTH], results[DAY]);
		System.out.println(query);
		dbConn.executeQuery(query);
		if (type.equals("VEHICLE REGISTRATION"))
			createVehicle();
	}

		private static void createVehicle() {
				String query = "INSERT INTO KATUR.VEHICLE VALUES(KATUR.SEQ_DOCUMENT.currval, '<liscence#>', '<make>', '<model>', '<state>')";

				System.out.println("------------- Add Vehicle -------------");
				System.out.print("License Number: ");
				String lNum = input.nextLine();
				System.out.println();

				System.out.print("Make: ");
				String make = input.nextLine();
				System.out.println();

				System.out.print("Model: ");
				String model = input.nextLine();
				System.out.println();

				System.out.print("State: ");
				String state = input.nextLine();
				System.out.println();

				query = query.replace("<liscence#>", lNum);
				query = query.replace("<make>", make);
				query = query.replace("<model>", model);
				query = query.replace("<state>", state);

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

		/*
		System.out.print("Hour (HH): ");
		String hour = grabAndValidateNumericInput(2);

		System.out.print("Minute (MM): ");
		String minute = grabAndValidateNumericInput(2);

		*/
		dateArr[YEAR] = year;
		dateArr[MONTH] = month;
		dateArr[DAY] = day;
		//dateArr[3] = hour;
		//dateArr[4] = minute;

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

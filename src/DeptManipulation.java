import java.util.Scanner;

/**
 * Purpose: This file is used to perform database manipulations on
 * the Department relation. This file allows the user
 * to add a tuple to the database, delete a tuple, or change
 * values of a tuple in the database.
 *
 * Author: Raymond Rea
 * Netid:  raymondprea
 */
public class DeptManipulation {

	private static String add = "INSERT INTO KATUR.Department " +
								"VALUES(KATUR.SEQ_DEPARTMENT.nextval, '<@>', '<#>', <%>)";

	private static String update = "UPDATE KATUR.Department " +
								   "SET <@> = '<#>' WHERE DeptID = <&>";

	private static Scanner input = null;
	private static dbConnection dbConn = null;

	DeptManipulation(Scanner input, dbConnection dbConn) {
		this.input = input;
		this.dbConn = dbConn;
	}

	/**
	 * This method uses the provided user input to remove a
	 * particular tuple from the Department table.
	 */
	public static void deleteDept() {
		System.out.println("--------------- Delete Department ---------------\n");
		// instead of deleting, we will just set active to 0
		System.out.println("DepartmentID: ");
		String deptID = input.nextLine();
		System.out.println();

		// replacing values in delete statement
		String updateStmt = update.replace("<@>", "active").replace("'<#>'", "0").replace("<&>", deptID);
		dbConn.executeQuery(updateStmt);
	}

	/**
	 * This method uses the provided user input to insert a tuple
	 * into the Department table.
	 */
	public static void addDept() {
		System.out.println("--------------- Add Department ---------------\n");

		System.out.println("DepartmentName: ");
		String deptName = input.nextLine().toUpperCase();
		System.out.println();

		System.out.println("DepartmentAddress: ");
		String deptAddr = input.nextLine().toUpperCase();
		System.out.println();

		System.out.println("Active (0-False, 1-True): ");
		String active = input.nextLine();
		while (!(active.equals("1") || active.equals("0"))) {
			System.out.println("Bad active input! Must be 0 or 1.");
			active = input.nextLine();
		}
		System.out.println();

		// replacing values in add statement
		String addStmt = add.replace("<@>", deptName);
		addStmt = addStmt.replace("<#>", deptAddr);
		addStmt = addStmt.replace("<%>", active);

		dbConn.executeQuery(addStmt);
	}

	/**
	 * This method prompts the user for the primary key of the
	 * tuple they want to change, it then asks the user for
	 * the attribute they want to change and what the new value
	 * should be. The method then uses a database connection to
	 * attempt the update
	 */
	public static void updateDept() {
		System.out.println("--------------- Update Department ---------------\n");

		System.out.println("Please give a Department ID of the " +
		"department you would like to change");

		System.out.print("DepartmentID: ");
		String deptID = input.nextLine();
		System.out.println();

		System.out.print("Which attribute would you like to change?");
		System.out.println(" (DeptName, DeptAddress, Active (0 = not active, 1 = active))");
		System.out.print("Attribute to change: ");
		String changeAttr = input.nextLine();

		System.out.println("New Value: ");
		String newVal = input.nextLine().toUpperCase();

		String updateStmt = update.replace("<@>", changeAttr);

		if (changeAttr.equalsIgnoreCase("active")) {
			while (!(newVal.equals("0") || newVal.equals("1"))) {
				System.out.println("Value must be 0 or 1!");
				newVal = input.nextLine().toUpperCase();
			}

			updateStmt = updateStmt.replace("'<#>'", newVal);
		} else
			updateStmt = updateStmt.replace("<#>", newVal);

		updateStmt = updateStmt.replace("<&>", deptID);

		if (dbConn.executeQuery(updateStmt) == null) {
			System.out.println("\n Query didn't work, make sure you entered an correct attribute name.");
		}
   }
}


import java.util.Scanner;
import java.sql.*;
import java.io.*;


/*
Authors: Raymond Rea, Carter Boyd
Netids:  raymondprea
Course: CSc 460
Instructor: Dr. McCann, Justin Do, Sourav Mangla

Purpose: This file provides the basic user interface of the application. The user is given a 
series of options where they can select a query, or select to make manipulations to a relation in the 
database. 

Opperational requirements: A valid Oracle username and password. The JDBC driver needs to be in your classpath
export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}.

Known issues: As of 12/03/21 @ 1624, no known bugs
*/
public class Prog4 {

	// ORACLEDB LOGIN DETAILS
	private static final String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
	private static String username = "user";
	private static String password = "pass";

	// global objects, used to connect to db and
	// perform manipulations on db
	private static dbConnection dbConn = null;
	private static ApptManipulation apptMan = null;
	private static DeptManipulation deptMan = null;
	private static EmployeeTuple emplMan = null;
	private static CustomerManipulation custMan = null;	

	// user input
	private static Scanner input = null;

	// contains queries and methods to execute queries
	private static Queries queries = null;

	/*
	main establishes a connection to the database using a dbConnection object
	and creates objects that will be used to manipulate the different tables

	Params: args
	Return: none
	*/
	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Please provide a username and password");
			return;
		}
		username = args[0];
		password = args[1];

		input = new Scanner(System.in);

		//Create a new dbConnection object that facilitates connection to the oracle db
		dbConn = new dbConnection(oracleURL, username, password);
		//Load the Oracle JDBC Drive
		dbConn.loadOracleJDBCDriver();
		// Establish a connection to the oracle database
		// TODO: should dbConn return an error code, if it was unable to connect?
		dbConn.connect();

		queries = new Queries(dbConn);

		// create objects to be used for manipulationg tables
		apptMan = new ApptManipulation(input, dbConn);
		deptMan = new DeptManipulation(input, dbConn);
	emplMan = new EmployeeTuple(input, dbConn);
	custMan = new CustomerManipulation(input, dbConn);

		displayMenu();     
		getInput();
		dbConn.close();
	}

	/*
	This method displays the different options to the users
	and take their input choice

	Params: none
	Return: none
	*/
	public static void displayMenu() {
		System.out.println();
		System.out.println("Below are the different query options. Enter \"a\", \"b\", \"c\", \"d\", \"e\", \"f\", or \"change\"." +
						   "(Enter option without quotation marks.)\n");
 
		System.out.println("--------------------------------------------- Query Options ---------------------------------------------");
		System.out.println("(a): What is the customer's information whose IDs will expire on a " +
		"given date? (Provide a date in the format of MM/DD/YYYY)\n");
		System.out.println("(b): How many appointments were there last month and how many of " +
		"were successful? (Organized by type of appointment)\n");
		System.out.println("(c): How much money did each department collect in fees? " +
		"(Provide a date in the format of MM/YYYY)\n");
		System.out.println("(d): What are the license numbers and names of owners, given a certain vehicle make and model?\n");
		System.out.println("(e): Display this message again.\n");
		System.out.println("(f): Exit application.\n");
		System.out.println("(g): Update a row in a table/Delete a row from a table/Add a row to a table.\n");
	}

	/*
	This method loops to get user input until a user is ready to exit the
	application

	Params: none
	Return: none
	*/
	public static void getInput() {
		String usrIn = "";
		while (!usrIn.equals("F")) {
			System.out.print("Option: ");
			usrIn = input.nextLine().toUpperCase().replaceAll("\\s", "");
			System.out.println();
			System.out.println(usrIn);
			parseInput(usrIn);
		}
		System.out.println("Goodbye.");
	}

	/*
	This method determines what action to take
	depending on the user's input

	Params: usrIn - the user's input option
	Return: none
	*/
	public static void parseInput(String usrIn) {
		switch(usrIn) {
			case "A":
				System.out.print("Date (MM/DD/YYYY): ");
				String dateA = input.nextLine();
				queries.executeQA(dateA);
				break;
			case "B":
				try {
					queries.executeQB();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case "C":
				System.out.print("Date (MM/YYYY): ");
				String dateB = input.nextLine();
				try {
					queries.executeQC(dateB);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case "D":
				//System.out.print("Input: ");
				//String usrInD = input.nextLine();
				queries.executeQD();
				break;
			case "E":
				displayMenu();
				break;
			case "F":
				break;
			case "G":
				changeTable();
				break;
			case "APPOINTMENTTRANSACTION ADD":
				apptMan.addAppt();
				break;
			case "APPOINTMENTTRANSACTION DELETE":
				apptMan.deleteAppt();
				break;
			case "APPOINTMENTTRANSACTION UPDATE":
				apptMan.updateAppt();
				break;
			case "DEPARTMENT ADD":
				deptMan.addDept();
				break;
			case "DEPARTMENT DELETE":
				deptMan.deleteDept();
				break;
			case "DEPARTMENT UPDATE":
				deptMan.updateDept();
				break;
			case "EMPLOYEE ADD":
				emplMan.addEmployee();
				break;
			case "EMPLOYEE DELETE":
				emplMan.deleteEmployee();
				break;
			case "EMPLOYEE UPDATE":
				emplMan.updateEmployee();
				break;
			case "CUSTOMER ADD":
				custMan.addCustomer();
				break;
			case "CUSTOMER DELETE":
				custMan.deleteCustomer();
				break;
			case "CUSTOMER UPDATE":
				custMan.updateCustomer();
				break;
			default:
				System.out.println("Please provide a valid input.");
				break;
		}
	}

	/*
	This method gets the user's input so
	it can be used to determine which
	table needs to change and what the operation is.

	Params: none
	Return: none
	*/
	public static void changeTable() {
		//TODO: need to add functionality for more tables
		System.out.println("What table would you like to change? The options are: " +
		"AppointmentTransaction, Department, Employee, Customer ...\n");

		System.out.print("Table to change: ");
		String changeTable = input.nextLine().toUpperCase().replaceAll("\\s", "");
		System.out.println();

		System.out.println("Do you want to add, delete, or update?\n");

		System.out.print("Action to perform: ");
		String action = input.nextLine().toUpperCase().replaceAll("\\s", "");
		System.out.println();

		parseInput(changeTable + " " + action);
	}
}


import java.io.*;
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
                                "VALUES(<!>, <@>, <#>, <$>, <%>, <^>, <&>, <*>)";

    private static String update = "UPDATE KATUR.ApptXact " +
                                   "SET <@> = <#> WHERE CustomerId = <$> AND StartTime = <&>";

    private static Scanner input = null;
    private static dbConnection dbConn = null;

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
        String st = input.nextLine();
        System.out.println();

        System.out.println("Cost: ");
        String cost = input.nextLine();
        System.out.println();

        // successful codes: -1 = hasn't started, 0 = unsuccessful, 1 = successful
        // successful is -1 by defualt because the appointment hasn't 
        // started yet
        String successful = "-1";

        System.out.println("EndTime: ");
        String et = input.nextLine();
        System.out.println();

        // replacing values in add statement
        String addStmt = add.replace("<!>", deptID);
        addStmt = addStmt.replace("<@>", empID);
        addStmt = addStmt.replace("<#>", custID);
        addStmt = addStmt.replace("<$>", st);
        addStmt = addStmt.replace("<%>", cost);
        addStmt = addStmt.replace("<^>", successful);
        addStmt = addStmt.replace("<&>", et);

        dbConn.executeQuery(addStmt);
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

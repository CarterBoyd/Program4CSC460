import java.io.*;
import java.util.Scanner;

/*
Author: Raymond Rea
Netid:  raymondprea

Purpose: This file is used to perform database manipulations on 
the Department relation. This file allows the user 
to add a tuple to the database, delete a tuple, or change 
values of a tuple in the database. 
*/
public class DeptManipulation {

    private static String del = "DELETE FROM katur.Department WHERE " +
                                "DeptID = <@>"; 

    private static String add = "INSERT INTO katur.Department " + 
                                "VALUES(<!>, <@>, <#>, <$>, <%>)";

    private static String update = "UPDATE katur.Department " +
                                   "SET <@> = <#> WHERE DeptID = <&>";

    private static Scanner input = null;
    private static dbConnection dbConn = null;

    DeptManipulation(Scanner input, dbConnection dbConn) {
        this.input = input;
        this.dbConn = dbConn;
    }

    /*
    This method uses the provided user input to remove a 
    paticular tuple from the Department table. 

    Params: none
    Return: none
    */
    public static void deleteDept() {
        System.out.println("--------------- Delete Department ---------------\n");
        System.out.println("DepartmentID: ");
        String deptID = input.nextLine();
        System.out.println();

        // replacing values in delete statement
        String delStmt = del.replace("<@>", deptID);
        dbConn.executeQuery(delStmt);
    }

    /*
    This method uses the provided user input to insert a tuple
    into the Department table.

    Params: none
    Return: none
    */
    public static void addDept() {
        System.out.println("--------------- Add Department ---------------\n");

        System.out.println("DeptID: ");
        String deptID = input.nextLine();
        System.out.println();

        System.out.println("DepartmentName: ");
        String deptName = input.nextLine();
        System.out.println();

        System.out.println("DepartmentAddress: ");
        String deptAddr = input.nextLine();
        System.out.println();
        
        System.out.println("ServiceType: ");
        String servType = input.nextLine();
        System.out.println();

        System.out.println("Acitive: ");
        String active = input.nextLine();
        System.out.println();

        // replacing values in add statement
        String addStmt = add.replace("<!>", deptID);
        addStmt = addStmt.replace("<@>", deptName);
        addStmt = addStmt.replace("<#>", deptAddr);
        addStmt = addStmt.replace("<$>", servType);
        addStmt = addStmt.replace("<%>", active);

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
    public static void updateDept() {
        System.out.println("--------------- Update Department ---------------\n");

        System.out.println("Please give a Department ID of the " + 
        "department you would like to change");
        System.out.print("DepartmentID: ");
        String deptID = input.nextLine();
        System.out.println();

        System.out.print("Which attribute would you like to change?");
        System.out.println(" (DeptID, DeptName, DeptAddress, ServiceType, Active (0 = not active, 1 = active))");
        System.out.print("Attribute to change: ");
        String changeAttr = input.nextLine();

        System.out.println("New Value: ");
        String newVal = input.nextLine();

        String updateStmt = update.replace("<@>", changeAttr);
        updateStmt = updateStmt.replace("<#>", newVal);
        updateStmt = updateStmt.replace("<&>", deptID);

        dbConn.executeQuery(updateStmt);
    }
}


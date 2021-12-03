import java.util.Scanner;
import java.io.*;

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

    // user input
    private static Scanner input = null;

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

        // create objects to be used for manipulationg tables
        apptMan = new ApptManipulation(input, dbConn);
        deptMan = new DeptManipulation(input, dbConn);

        displayMenu();        
    }

    /*
    This method displays the different options to the users
    and take their input choice

    Params: none
    Return: none
    */
    public static void displayMenu() {
        System.out.println();
        System.out.println("Below are the different query options. Enter a, b, c, or d.\n");

        System.out.println("Or enter e to display these messages again. Or enter CHANGE " + 
                           "to add/delete/update a relation.\n");
 
        System.out.println("-------------------- Query Options --------------------");
        System.out.println("(a)");
        System.out.println("(b)");
        System.out.println("(c)");
        System.out.println("(d)");

        System.out.print("Option: ");
        String usrOpt = input.nextLine();
        System.out.println();
        System.out.println(usrOpt);

    }
}


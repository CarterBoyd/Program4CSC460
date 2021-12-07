import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Scanner;

/*
This class is used to store the four required queries as strings.
This class uses a dbConnection object to execute the queries 
*/
public class Queries {
    // the four required queries
    private static final String qA = "";
    private static final String qB = "";
    private static final String qC = "";
    private static final String qD = "";
    private static PreparedStatement ps = null;

    private static dbConnection dbConn = null;

    Queries(dbConnection dbConn) {
        this.dbConn = dbConn;
    }

    /*
    This method utilizes a db connection and an input
    date to execute query a

    Params: date - in the form of MM/DD/YYYY
    Return: none
    */
    public static void executeQA(String dateStr) {
        String[] date;
        if (dateStr.contains("/")) {
            date = dateStr.split("/");
            // if the date contains month, day and year and
            // they are the correct amount of characters
            // and the characters are numeric
            if (date.length == 3      && date[0].length() == 2 && 
                date[1].length() == 2 && date[2].length() == 4 && 
                dateValidator(date)) {
                String dateString = date[2] + '-' + date[0] + '-' + date[1];
                System.out.println("this is where query a would be executed with date " + dateStr);
                String query = "select b.CustomerID, b.FName, b.LName, a.issuedate, a.expirydate, c.type from " +
                        "katur.document a, katur.customer b, katur.apptxact c where a.customerid = b.customerid " +
                        "and a.customerid = c.customerid and a.issuedate = c.starttime and a.expirydate = <date>";
                query = query.replace("<date>", dateString);
				dbConn.executeQueryAndPrint(query);
            } else {
                System.out.println("Please provide a date in the correct format");
                return;
            }
        // input date doesn't contain any slashes 
        } else {
            System.out.println("Please provide a date in the correct format");
        }
    }

    /*
    This method utilizes a db connection to execute 
    query b

    Params: none
    Return: none
    */
    public static void executeQB() throws SQLException {
        System.out.println("this is where query b would be executed");
        String dept = "select * from katur.apptxact where type = ? and starttime >= ? and starttime <= ?;";
        ps = dbConn.getConn().prepareStatement(dept, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet answer = null;
        ps.setString(1,  "PERMIT");
        ps.setDate(2, Date.valueOf(YearMonth.now().minusMonths(1).atDay(1).toString()));
        ps.setDate(3, Date.valueOf(YearMonth.now().minusMonths(1).atEndOfMonth().toString()));
        answer = ps.executeQuery();
        int type = 0;
        int succ = 0;
        while (answer.next()) {
            type++;
            if (answer.getInt("successful") > 0) {
                succ++;
            }
        }
        System.out.println("Permits last month: " + type);
        System.out.println("Successful permits: " + succ);
        ps.setString(1,  "LICENSE");
        type = 0;
        succ = 0;
        answer = ps.executeQuery();
        while (answer.next()) {
            type++;
            if (answer.getInt("successful") > 0) {
                succ++;
            }
        }
        System.out.println("Licenses last month: " + type);
        System.out.println("Successful licenses: " + succ);
        ps.setString(1,  "VEHICLE REGISTRATION");
        type = 0;
        succ = 0;
        answer = ps.executeQuery();
        while (answer.next()) {
            type++;
            if (answer.getInt("successful") > 0) {
                succ++;
            }
        }
        System.out.println("Registrations last month: " + type);
        System.out.println("Successful registrations: " + succ);
        type = 0;
        succ = 0;
        ps.setString(1,  "STATE ID");
        answer = ps.executeQuery();
        while (answer.next()) {
            type++;
            if (answer.getInt("successful") > 0) {
                succ++;
            }
        }
        System.out.println("IDs last month: " + type);
        System.out.println("Successful IDs: " + succ);
    }

    /*
    This method utilizes a db connection and an input
    date to execute query c

    Params: date - in the form of MM/YYYY
    Return: none
    */
    public static void executeQC(String dateStr) throws SQLException {
        String[] date;
        if (dateStr.contains("/")) {
            date = dateStr.split("/");
            // if the date contains month, day and year and 
            // they are the correct amount of characters
            // and the characters are numeric
            if (date.length == 2      && date[0].length() == 2 && 
                date[1].length() == 4 && dateValidator(date)) {
                String start = date[1] + '-' + date[0] + '-' + 01;
                int month = Integer.valueOf(date[0]);
                String end;
                if (month == 12) {
                    end = Integer.valueOf(date[1]) + 1 + '-' + String.valueOf(01) + '-' + 01;
                } else {
                    end = date[1] + '-' + month + 1 + '-' + 01;
                }
                System.out.println("this is where query c would be executed with date " + dateStr);
                String query = "select a.deptID, deptName, deptAddress sum(cost) from katur.department a, " +
                        "katur.apptxact b where a.deptID = b.deptID and starttime >= <start> and starttime < <end> " +
                        "group by a.deptID order by sum(cost) desc";
                query = query.replace("<start>", start);
                query = query.replace("<end>", end);
                dbConn.executeQueryAndPrint(query);
            } else {
                System.out.println("Please provide a date in the correct format");
                return;
            }
        // input date doesn't contain any slashes 
        } else {
            System.out.println("Please provide a date in the correct format");
        }
    }

    /*
    This method utilizes a db connection and user input
    date to execute query d

    Params: TODO
    Return: none
    */
    public static void executeQD() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter vehicle make: ");
        String d1 = scan.nextLine().toUpperCase();
        System.out.println("Please enter vehicle model: ");
        String d2 = scan.nextLine().toUpperCase();
        System.out.println();
        String query = String.format("select licensenumber, fname, " +
                "lname from katur.vehicle join katur.document on document.id " +
                "= vehicle.documentid join katur.customer on customer.customerid = " +
                "document.customerid where make = '%s' and model = '%s'", d1, d2);
        dbConn.executeQueryAndPrint(query);
    }

    /*
    This method checks a given date, broken up into an array
    and checks if each part of the date is a numeric value

    Params: date - a String array containig different parts of the
    date
    Return: true, if the whole date is numeric. Otherwise, false
    */
    private static boolean dateValidator(String[] date) {

        //System.out.println("[" + date[0] + ", " + date[1] + ", " + date[2] + "]"); 
        // iterating through the different elements of the date
        for (int i = 0; i < date.length; i++) {
            // if any given element in the date is not numeric, 
            // return false
            try {
                int dateElement = Integer.parseInt(date[i]);
                if (dateElement < 0) {return false;}
            } catch (NumberFormatException e) {
                //System.out.println("date failed");
                return false;
            }
        }
        return true;
    }

}

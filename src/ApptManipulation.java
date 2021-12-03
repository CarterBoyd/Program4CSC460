import java.io.*;

public class ApptManipulation {

    private static String del = "DELETE FROM username.ApptXAct WHERE " +
                                "CustomerID = <@> AND Date = <#>" + 
                                "Time = <$>";

    private static String add = "INSERT INTO username.apptXAct " + 
                                "VALUES(<!>, <@>, <#>, <$>, <%>, <^>, <&>, <*>)";

    //TODO: I made this assuming we won't be using any of the built in
    // Oracle datetime types, just because there are so many different formats
    // I think it may be easier just to add a date and time attribute insted, 
    // where Date will have the format of MM/DD/YYYY, and Time will have the 
    // format of HHMM, using a 24 hour clock

    /*
    This method uses the provided parameters to remove a 
    paticular tuple from the ApptXAct table. 

    Return: none
    */
    public static void deleteAppt(String cusID, String date, String time) {
        // replacing values in delete statement
        String delStmt = del.replace("<@>", cusID);
        delStmt = delStmt.replace("<#>", date);
        delStmt = delStmt.replace("<$>", time);
        // TODO: assuming that Prog4 will have a getDBConn method 
        Prog4.getDBConn().executQuery(delStmt);
    }

    /*
    This method uses the provided parameters to insert a tuple
    into the ApptXAct table.

    Return: none
    */
    public static void addAppt(String cusID, String date, String time, String empID, 
                               String deptID, String cost, String succ, String endTime) {
        // replacing values in add statement
        String addStmt = add.replace("<!>", cusID);
        addStmt = addStmt.replace("<@>", date);
        addStmt = addStmt.replace("<#>", time);
        addStmt = addStmt.replace("<$>", empID);
        addStmt = addStmt.replace("<%>", deptID);
        addStmt = addStmt.replace("<^>", cost);
        addStmt = addStmt.replace("<&>", succ);
        addStmt = addStmt.replace("<*>", endTime);

        Prog4.getDBConn().executeQuery(addStmt);
    }
}

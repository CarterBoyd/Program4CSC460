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
                
                dbConn.executeQuery(qA, dateStr);
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
    public static void executeQB() {
        dbConn.executeQuery(qA, "");
    }

    /*
    This method utilizes a db connection and an input
    date to execute query c

    Params: date - in the form of MM/YYYY
    Return: none
    */
    public static void executeQC(String dateStr) {
        String[] date;
        if (dateStr.contains("/")) {
            date = dateStr.split("/");

            // if the date contains month, day and year and 
            // they are the correct amount of characters
            // and the characters are numeric
            if (date.length == 2      && date[0].length() == 2 && 
                date[1].length() == 4 && dateValidator(date)) {
                
                dbConn.executeQuery(qC, dateStr);
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
    public static void executeQD(String input) {

    }

    /*
    This method checks a given date, broken up into an array
    and checks if each part of the date is a numeric value

    Params: date - a String array containig different parts of the
    date
    Return: true, if the whole date is numeric. Otherwise, false
    */
    private static boolean dateValidator(String[] date) {

        // iterating through the different elements of the date
        for (int i = 0; i < date.length; i++) {
            // if any given element in the date is not numeric, 
            // return false
            try {
                Integer.parseInt(date[i]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

}

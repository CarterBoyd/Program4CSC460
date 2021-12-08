import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Scanner;

/*
This class is used to store the four required queries as strings.
This class uses a dbConnection object to execute the queries 
*/
public class Queries {
	// the four required queries

	private static dbConnection dbConn = null;

	Queries(dbConnection dbConn) {
		Queries.dbConn = dbConn;
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
			if (date.length == 3 && date[0].length() == 2 &&
					date[1].length() == 2 && date[2].length() == 4 &&
					dateValidator(date)) {
				String dateString = date[2] + '-' + date[0] + '-' + date[1];
				String query = "select b.CustomerID, b.FName, b.LName, a.issuedate, a.expirydate, c.type from " +
						"katur.document a, katur.customer b, katur.apptxact c " +
						"where a.customerid = b.customerid and a.customerid = c.customerid " +
						"and a.issuedate = c.starttime and a.expirydate = TO_DATE('<date>', 'YYYY-MM-DD')";
				query = query.replace("<date>", dateString);
				dbConn.executeQueryAndPrint(query);
			} else {
				System.out.println("Please provide a date in the correct format");
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
		String query = "select b.deptname as \"IDType\", sum(case when a.successfully > '0' then 1 else 0 end)" +
				" as \"Successful\", count(*) as \"Total\" from katur.apptxact a, katur.department b where a.starttime " +
				">= TO_DATE('<from>', 'YYYY-MM-DD') and a.starttime <= TO_DATE('<to>', 'YYYY-MM-DD') and a." +
				"deptid = b.deptid group by b.deptname";
		query = query.replace("<from>", YearMonth.now().minusMonths(1).atDay(1).toString());
		query = query.replace("<to>", YearMonth.now().minusMonths(1).atEndOfMonth().toString());
		dbConn.executeQueryAndPrint(query);
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
			if (date.length == 2 && date[0].length() == 2 &&
					date[1].length() == 4 && dateValidator(date)) {
				String start = date[1] + '-' + date[0] + '-' + "01";
				int month = Integer.parseInt(date[0]);
				String end = date[1] + '-' + date[0] + '-';
				end += YearMonth.of(Integer.parseInt(date[1]), month).lengthOfMonth();
				String query = "select DEPARTMENT.deptid, DEPARTMENT.DEPTNAME, DEPARTMENT.DEPTADDRESS, DEPARTMENT.ACTIVE, sum(COST) from KATUR.DEPARTMENT " +
						"join KATUR.APPTXACT on DEPARTMENT.DEPTID = APPTXACT.DEPTID " +
						"where STARTTIME >= TO_DATE('<start>', 'YYYY-MM-DD') " +
						"and ENDTIME <= TO_DATE('<end>', 'YYYY-MM-DD') " +
						"group by DEPARTMENT.DEPTID, DEPTADDRESS, DEPTNAME, ACTIVE " +
						"order by sum(COST) desc";
				query = query.replace("<start>", start);
				query = query.replace("<end>", end);
				dbConn.executeQueryAndPrint(query);
			} else {
				System.out.println("Please provide a date in the correct format");
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
		String query = String.format("select LICENSENUMBER, FNAME, LNAME, address from KATUR.VEHICLE" +
				"    join KATUR.DOCUMENT on DOCUMENT.DOCUMENTID = VEHICLE.DOCUMENTID" +
				"    join KATUR.CUSTOMER on CUSTOMER.CUSTOMERID = DOCUMENT.CUSTOMERID" +
				"    where MAKE = '%s'" +
				"    and MODEL = '%s'", d1, d2);
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
		for(String s : date) {
			// if any given element in the date is not numeric,
			// return false
			try {
				int dateElement = Integer.parseInt(s);
				if (dateElement < 0) {
					return false;
				}
			} catch (NumberFormatException e) {
				//System.out.println("date failed");
				return false;
			}
		}
		return true;
	}

}

import java.sql.*;

/**
* 	Class Name: dbConnection
*		Author: Logan C. Urfer
*	
* Dependencies: java.sql;
*
*	   Purpose: This class is responsible for facilitating a connectionto an oracle DB.
*
*  Constructor: Parameterized Constructor - Initializes our Connection object to null,
*				loads the oracle JDBC driver, and saves the login information for the DB
*				for future use.
*/
public class dbConnection {
	
	// Private vars
	private Connection db_conn;
	// holds the url to the oracle database.
	private final String url;
	// holds the username details.
	private final String user;
	// holds the password details
	private final String pass;
	private Statement stmt;

	/**
	*	 Name: dbConnection
	* Purpose: Saves the login information for the oracle DB and loads the oracle JDBC driver
	*/
	public dbConnection(String url, String user, String pass) {
		this.url = url;
		this.user = user;
		this.pass = pass;
		this.db_conn = null;
		this.stmt = null;
	}

	/**
	*	 Name: loadOracleJDBCDriver
	* Purpose: Loads the oracle jdbc drive required to establish a connection. Make sure to set
	*			your class path. Information is in Prog3.java class
	*/
	public void loadOracleJDBCDriver() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");

		} catch (ClassNotFoundException e) {
			System.err.println("*** ClassNotFound Exception: "
					+ "Unable to load Oracle JDBC Driver. \n");
			System.exit(-1);
		}

	}

	/**
	*	 Name: connect
	* Purpose: Establishes a connection to the oracle DB using the credentials provided in the
	*			constructor.
	*/
	public void connect() {
		try {
			db_conn = DriverManager.getConnection(this.url, this.user, this.pass);
			this.stmt = db_conn.createStatement();
		} catch (SQLException e) {
			System.err.println("*** SQLException: Unable to open JDBC Connection. \n");
			System.exit(-1);
		}
	}
	
	/**
	*	 Name: close
	* Purpose: Closes a connection to the oracle DB.
	*/	
	public void close() {
		try {
			db_conn.close();
		} catch (SQLException e) {
			System.err.println("*** SQLException: Unable to close JDBC Connection. "
					+ "Did you establish the connection first? \n");
		}
	}

	/**
	*	 Name: executeQuery
	*   Param: query A String that represents the query to be run
	*  Return: resultSet A resultSet containing the results from the above query.
	*/
	public ResultSet executeQuery(String query) { 
		ResultSet result = null;
		try {
			result = this.stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Executes a query and prints out its resultset.
	 * @param query, a string representing an SQL command.
	 */
	public void executeQueryAndPrint(String query) {
		ResultSet result;
		try {
			result = this.stmt.executeQuery(query);
						ResultSetMetaData rsmd = result.getMetaData();
						// print column names
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							if (i != rsmd.getColumnCount()) {
								System.out.print(rsmd.getColumnName(i) + " - ");
							} else {
								System.out.print(rsmd.getColumnName(i));
							}
						}
						System.out.println();

						// printing out result tuples
						while (result.next()) {
							for (int i = 1; i <= rsmd.getColumnCount(); i++) {
								if (i != rsmd.getColumnCount()) {
									System.out.print(result.getString(i) + " - ");
								} else {
									System.out.print(result.getString(i));
								}
							}
                            System.out.println();
						}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Executes an update command
	 * @param query command to execute
	 * @return number of rows that were changed by command
	 */
	public int executeUpdate(String query) {
		int rowsEffected = 0;
		try {
			rowsEffected = this.stmt.executeUpdate(query);
		} catch(SQLException e) {
			System.err.println("Unable to execute statement query");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
		return rowsEffected;
	}

	/**
	 * runs a commit operation on the SQL database
	 */
	public void commit() {
		try{
			this.db_conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}

	/**
	 * closes the statement in the DB connection.
	 */
	public void closeStatement() {
		try {
			this.stmt.close();
		} catch (SQLException e) {
			System.out.println("Unable to close statement");
		}

	}
}


// Imports java libraries
import java.sql.*;
import java.util.Scanner;

//The class  declaration
public class EditBooks {

	// The main method declaration
	public static void main(String[] args) {

		// Calls methods to create the database and table
		createDataBase();
		createTable();

		// Initiates variables
		int queryNum;
		boolean isLooping = true;
		Scanner scan = new Scanner(System.in);

		// Calls a method to display all the data for the books
		displayBooks();

		// loops through the code until the user selects 0
		while (isLooping) {

			// Calls a method and stores which query the user would like to execute
			queryNum = selectedQuery(scan);

			// Using the switch statement to execute the appropriate query the user selected
			switch (queryNum) {

			// Executes insert method if user selects 1
			case 1:
				insertBookInfo(scan);
				break;

			// Executes update method if user selects 2
			case 2:
				updateBookInfo(scan);
				break;

			// Executes delete method if user selects 3
			case 3:
				deleteBookInfo(scan);
				break;

			// Executes search method if user selects 4
			case 4:
				searchBookInfo(scan);
				break;

			// breaks the loop if user selects 0
			case 0:
				System.out.println("\nGoodbye.");
				isLooping = false;
				break;

			// If none of the corresponding values were selected outputs a message
			// and loops again
			default:
				System.out.println("\nPlease enter the corresponding number:");
			}
		}
		// closes the scanner object;
		scan.close();
	}

	// A method that creates a Database when called
	private static void createDataBase() {
		try {
			// Makes a connection to the dataBase
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "otheruser",
					"swordfish");

			// Initiates variables
			String newDataBase = "CREATE DATABASE IF NOT EXISTS ebookstore";
			String selectDataBase = "USE ebookstore";

			// Create a direct line to the database for running our queries
			Statement statement = connection.createStatement();

			// Uses the initiated variables to execute an SQL statement to create a database
			statement.execute(newDataBase);
			statement.execute(selectDataBase);

			// Displays an output message
			System.out.println("DataBase created");

			// closes the connections
			statement.close();
			connection.close();

		} catch (SQLException e) {
			// We only want to catch a SQLException - anything else is off-limits for now.
			e.printStackTrace();
		}
	}

	// A method that creates a table
	private static void createTable() {
		try {
			// Makes a connection to the dataBase
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookstore?useSSL=false",
					"otheruser", "swordfish");

			String newTable = "CREATE TABLE books ( id int, Title varchar(50), Author varchar(50), Qty int, PRIMARY KEY (id))";

			// Create a direct line to the database for running our queries
			Statement statement = connection.createStatement();

			// runs an SQL execute statement
			statement.execute(newTable);

			// inserts books into the new table
			statement.executeUpdate("INSERT INTO books VALUES (3001, 'A Tale of Two Cities', 'Charles Dickens', 30)");
			statement.executeUpdate(
					"INSERT INTO books VALUES (3002, 'Harry Potter and the Philosopher''s Stone', 'J.K. Rowling', 40)");
			statement.executeUpdate(
					"INSERT INTO books VALUES (3003, 'The Lion, the Witch and the Wardrobe', 'C. S. Lewis', 25)");
			statement.executeUpdate("INSERT INTO books VALUES (3004, 'The Lord of the Rings', 'J.R.R Tolkien', 37)");
			statement.executeUpdate("INSERT INTO books VALUES (3005, 'Alice in Wonderland', 'Lewis Carroll', 12)");

			// Displays an output message
			System.out.println("Table created\n");

			// closes the connections
			statement.close();
			connection.close();

		} catch (SQLException e) {
			// Catches an exception incase the table has already been made
			System.out.println("Table has alredy been created.\n");
		}
	}

	// A method that asks the user to input what they would wish to search
	private static void searchBookInfo(Scanner scan) {
		try {
			// Initiates variables
			String fieldString = "", dataString = "";

			// asks the user which field they are using to search from
			System.out.println("Enter the field you would wish to search from (id, Title, Author, Qty):");
			fieldString = scan.nextLine();

			// Asks the user what type of data they wish to search
			System.out.println("Enter the data that you will be using to search:");
			dataString = scan.nextLine();

			// Calls a method that searches a SQL query
			searchQuery(fieldString, dataString, scan);

		} catch (Exception e) {
			// only catches scanner exceptions
			e.printStackTrace();
		}
	}

	// A method that uses an SQL query to search for data
	private static void searchQuery(String fieldString, String dataString, Scanner scan) {
		// Initiates variables
		boolean isFieldString;
		int rowsFound = 0;

		// Calls a method to check if the field is a string
		isFieldString = isValidString(fieldString);
		try {

			// Makes a connection to the dataBase
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookstore?useSSL=false",
					"otheruser", "swordfish");

			// Create a direct line to the database for running our queries
			Statement statement = connection.createStatement();
			ResultSet results;

			// if the field is not a string
			// convert the data that will be implemented into an integer type
			if (!isFieldString) {

				int dataInt = Integer.parseInt(dataString);
				// updates a book with the appropriate data type
				results = statement.executeQuery("SELECT * FROM books WHERE " + fieldString + "=" + dataInt);
			} else {
				// updates a book with the appropriate data type
				results = statement
						.executeQuery("SELECT * FROM books WHERE " + fieldString + "=" + "'" + dataString + "'");
			}

			// Loop over the results, printing them all.
			while (results.next()) {
				System.out.println(results.getInt("id") + ", " + results.getString("Title") + ", "
						+ results.getString("Author") + ", " + results.getInt("Qty"));
				// Stores the amount of rows found
				rowsFound++;
			}
			// if zero rows were found output the following message
			// A call the searchBookInfo method
			if (rowsFound == 0) {
				System.out.println("The data you have entered does not appear on the database");
				searchBookInfo(scan);
			}

			// Close up our connections
			statement.close();
			connection.close();

		} catch (SQLException e) {
			// Catches any invalid field inputs and recalls the searchBookInfo method
			System.out.println("The field you have entered does not appear on the database.");
			searchBookInfo(scan);

		} catch (NumberFormatException e) {
			// catches any invalid number format exceptions
			// and recalls the searchBookInfo method
			System.out.println("The data you have entered is invalid.");
			searchBookInfo(scan);
		}
	}

	// A method that asks the user which book they would like to delete
	private static void deleteBookInfo(Scanner scan) {
		try {
			// Initiates variables
			int id;

			System.out.println("Enter the id number of the book you would like to delete:");
			id = scan.nextInt();
			scan.nextLine();

			// Calls a method delete a SQL query
			deleteQuery(id);

		} catch (Exception e) {
			// Catches any invalid inputs and recurse the method
			scan.nextLine();
			System.out.println("\nPlease enter the valid value.");
			deleteBookInfo(scan);
		}
	}

	// A method that uses a SQL query to delete data
	private static void deleteQuery(int id) {
		try {
			// Makes a connection to the dataBase
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookstore?useSSL=false",
					"otheruser", "swordfish");

			// Create a direct line to the database for running our queries
			Statement statement = connection.createStatement();

			// inserts books
			statement.executeUpdate("DELETE FROM books WHERE id=" + id);

			// Calls a method to display all the data for the books
			System.out.println();
			displayBooks();

			// Close up our connections
			statement.close();
			connection.close();

		} catch (SQLException e) {
			// We only want to catch a SQLException - anything else is off-limits for now.
			e.printStackTrace();
		}
	}

	// A method that asks the user to enter the update info
	private static void updateBookInfo(Scanner scan) {
		try {
			// Initiates variables
			int id = 0;
			String updateField = "", updateSet = "";

			// Asks the user which book they would like to update based on the id number
			System.out.println("Which book would you like to Update (Enter its ID):");
			id = scan.nextInt();
			scan.nextLine();

			// Asks which field they would like to update
			System.out.println("Which field would you like to Update (id, Title, Author, Qty):");
			updateField = scan.nextLine();

			// Asks for the new information they would like to set
			System.out.println("What would you like to set the data to be:");
			updateSet = scan.nextLine();

			// Calls method to update the SQL database
			updateQuery(id, updateField, updateSet, scan);

		} catch (NumberFormatException e) {
			// catches any invalid number format exceptions
			// and recalls the searchBookInfo method
			System.out.println("The data you have entered is invalid.");
			updateBookInfo(scan);

		} catch (Exception e) {
			// Catches any invalid inputs and recurse the method
			scan.nextLine();
			System.out.println("\nPlease enter the valid value.");
			updateBookInfo(scan);
		}
	}

	// // A method that uses a SQL query to update data
	private static void updateQuery(int id, String updateField, String updateSet, Scanner scan) {

		// Initiates a variable
		boolean isFieldString;
		int updateSetInt = 0;

		// Calls a method to check if the field
		// and the new data getting updated is a string
		isFieldString = isValidString(updateField);

		try {
			// Makes a connection to the dataBase
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookstore?useSSL=false",
					"otheruser", "swordfish");

			// Create a direct line to the database for running our queries
			Statement statement = connection.createStatement();

			// if the field is not a string
			// convert the data that will be implemented into an integer type
			if (!isFieldString) {
				updateSetInt = Integer.parseInt(updateSet);
				// updates a book with the appropriate data type
				statement.executeUpdate("UPDATE books SET " + updateField + "=" + updateSetInt + " WHERE id=" + id);
			} else {
				// updates a book with the appropriate data type
				statement.executeUpdate(
						"UPDATE books SET " + updateField + "=" + "'" + updateSet + "'" + " WHERE id=" + id);
			}

			// Calls a method to display all the data for the books
			displayBooks();

			// Close up our connections
			statement.close();
			connection.close();

		} catch (SQLException e) {
			// Catches any invalid field inputs and recalls the updateBookInfo method
			System.out.println("\nPlease enter a valid Field.");
			updateBookInfo(scan);
		}
	}

	// A method when called checks to see if the field entered is a string or int
	private static boolean isValidString(String updateField) {
		// if the field entered matches id or Qty return false
		if (updateField.equalsIgnoreCase("id") || updateField.equalsIgnoreCase("Qty")) {

			return false;
		}

		// else return true
		return true;
	}

	// A method that asks the user to enter the Book info
	private static void insertBookInfo(Scanner scan) {
		try {
			// Initiates the variables
			int id = 0, qty = 0;
			String title = "", author = "";

			// Asks the user for the new book id number
			System.out.println("\nEnter the book ID:");
			id = scan.nextInt();
			scan.nextLine();

			// Ask what the title is
			System.out.println("Enter the title of the book");
			title = scan.nextLine();

			// Asks who the autho is
			System.out.println("Enter the author of the book");
			author = scan.nextLine();

			// Asks the quantity in stock
			System.out.println("Enter the quantity of books");
			qty = scan.nextInt();
			scan.nextLine();

			// Calls an SQL insert method
			InsertQuery(id, qty, title, author);

		} catch (Exception e) {
			// Catches any invalid inputs and recurse itself
			scan.nextLine();
			System.out.println("\nPlease enter the valid value.");
			insertBookInfo(scan);
		}
	}

	// A method that uses a SQL query to insert data
	private static void InsertQuery(int id, int qty, String title, String author) {
		try {
			// Makes a connection to the dataBase
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookstore?useSSL=false",
					"otheruser", "swordfish");

			// Create a direct line to the database for running our queries
			Statement statement = connection.createStatement();

			// inserts books
			statement.executeUpdate(
					"INSERT INTO books VALUES (" + id + ", '" + title + "', '" + author + "', " + qty + ")");

			// Calls a method to display all the data for the books
			System.out.println();
			displayBooks();

			// Close up our connections
			statement.close();
			connection.close();

		} catch (SQLException e) {
			// We only want to catch a SQLException - anything else is off-limits for now.
			e.printStackTrace();
		}
	}

	// A method that stores the query the user wishes to execute
	private static int selectedQuery(Scanner scan) {

		// Initiates a variable
		int optionNum = 0;
		try {
			// Outputs a menu of what the user can do
			System.out
					.println("\nWhat would you like to do to the ebookstore Database (Select the corresponding number):"
							+ "\n1. Enter book" + "\n2. Update book" + "\n3. Delete book" + "\n4. Search book"
							+ "\n0. Exit");

			// Stores the input the use entered
			optionNum = scan.nextInt();
			scan.nextLine();

		} catch (Exception e) {
			// Catches any invalid inputs and recurse the method
			scan.nextLine();
			System.out.println("\nPlease enter the valid number.");
			optionNum = selectedQuery(scan);
		}
		// returns the query number selected
		return optionNum;
	}

	// A method that displays the basic information of the books
	private static void displayBooks() {
		try {
			// Makes a connection to the dataBase
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookstore?useSSL=false",
					"otheruser", "swordfish");

			// Create a direct line to the database for running our queries
			Statement statement = connection.createStatement();
			ResultSet results;

			// executeQuery: runs a SELECT statement and returns the results.
			results = statement.executeQuery("SELECT id, title, author, qty FROM books");

			// Loop over the results, printing them all.
			while (results.next()) {
				System.out.println(results.getInt("id") + ", " + results.getString("Title") + ", "
						+ results.getString("Author") + ", " + results.getInt("Qty"));
			}

			// Close up our connections
			results.close();
			statement.close();
			connection.close();

		} catch (SQLException e) {
			// We only want to catch a SQLException - anything else is off-limits for now.
			e.printStackTrace();
		}
	}
}
/*
 * References: https://youtu.be/tybwzry_i_c?si=qzvAoVK2VSQphOUh
 * https://youtu.be/wdz4MRlzOyE?si=Jri0S1cyNjimj-Je
 * https://youtu.be/_tS2gw5l1TY?si=TteI9ZQItI3q4-9f
 */

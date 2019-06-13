import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


/*
export CLASSPATH=$CLASSPATH:mysql-connector-java-8.0.15-bin.jar:.
export APP_JDBC_URL=jdbc:mysql://csc365.toshikuboi.net/cmmcoy
export APP_JDBC_USER=cmmccoy
export APP_JDBC_PW=008506325
*/


public class HotelRes {
	
	static Scanner sc = new Scanner(System.in);
	
    public static void main(String[] args) {

        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

        String jdbcUrl = "jdbc:mysql://csc365.toshikuboi.net/sec03group10";
        String dbUsername = "cmmccoy";
        String dbPassword = "008506325";

        try {
            Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            System.out.print("MySQL Connection created");
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        String command = "";
		
		while(command != "q") {
			printWelcome();
			command = sc.nextLine();
			executeCommand(command);
		}		
    }
    
    
    private static void printWelcome() {
		System.out.println("\nPlease enter a command from the following list\n\n"
				+ "1: Book a room\n"
				+ "2: Cancel reservation\n"
				+ "3: Change reservation\n"
				+ "4: Reservation history\n"
				+ "5: Manager sign-in\n\n"
				+ "q: quit");
	}
	
	private static void executeCommand(String command) {
		if(command.equals("1")) {
			startBooking();
		}
		else if(command.equals("2")) {
			startCancelRes();
		}
		else if(command.equals("3")) {
			startChangeRes();
		}
		else if(command.equals("4")) {
			startResHistory();
		}
		else if(command.equals("5")) {
			startManager();
		}
		else {
			System.out.println("Invalid Selection. Please input a valid command.");
			command = sc.next();
			executeCommand(command);
		}
	}
	
	private static void startBooking() {
		System.out.println("Starts the flow for a user to book a room");
		
		/* The system shall allow booking until the hotel is fully booked.
		 * The system must not allow overbooking.
		 * Each room shall have information about its maximum number of occupants allowed.
		 * The system shall not allow a room to be occupied by guests beyond its capacity.
		 * The system shall allow payment by credit card. Create fake credit card accounts
				in your database. A room shall be reserved only when payment by a credit card
				is approved (do not book a room without payment. Also, do not charge the card
				unless the room can be reserved. I.e. both reservation and the charge must be
				bundled into a transaction.The same should be true for cancellation.).
		 * The system shall allow users to search for availabilities of rooms specifying day
				(checkout and checkin dates), the type of room (single, double, twin, etc), the
				decor, the price range, the number of rooms, and the number of occupants.
		 * The system shall display availability of rooms on each day. For each room,
				display its popularity score (number of days the room has been occupied during
				the previous 180 days divided by 180 (round to two decimal places)), price,
				available or if not, next available date, length, bed type, the number of beds, the
				maximum number of occupancy allowed.
		 * Upon reservation, the system shall display the details of the reservation on the screen.
		 */
	}
	
	private static void startCancelRes() {
		System.out.println("What is your reservation code?\n");
		String code = sc.nextLine();
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Reservations WHERE CODE = ?");
		stmt.setString(1,code);
		ResultSet resultSet = stmt.executeQuery();
		while (resultSet.next()) {
			System.out.println("CODE: " + resultSet.getLong("CODE"));
			System.out.println("ROOM: " + resultSet.getString("Room"));
			System.out.println("CHECK-IN: " + resultSet.getString("CheckIn"));
			System.out.println("CHECK-OUT: " + resultSet.getString("CheckOut"));
			System.out.println("RATE: " + resultSet.getFloat("Rate"));
			System.out.println("LastName: " + resultSet.getString("LastName"));
			System.out.println("FirstName: " + resultSet.getString("FirstName"));
			System.out.println("Adults: " + resultSet.getInt("Adults"));
			System.out.println("Kids: " + resultSet.getInt("Kids"));
		}
		System.out.println("\nAre you sure you want to cancel this reservation?\n\n" 
				+ "1: yes\n"
				+ "2: no\n");
		String decision = sc.nextLine();
		if (decision == "1") {
			PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM Reservations WHERE CODE = ?");
			stmt2.setString(1, code);
			int row = stmt2.executeUpdate();
			System.out.println("Reservation cancelled.")
			System.out.println("CODE: " + resultSet.getLong("CODE"));
			System.out.println("ROOM: " + resultSet.getString("Room"));
			System.out.println("CHECK-IN: " + resultSet.getString("CheckIn"));
			System.out.println("CHECK-OUT: " + resultSet.getString("CheckOut"));
			System.out.println("RATE: " + resultSet.getFloat("Rate"));
			System.out.println("LastName: " + resultSet.getString("LastName"));
			System.out.println("FirstName: " + resultSet.getString("FirstName"));
			System.out.println("Adults: " + resultSet.getInt("Adults"));
			System.out.println("Kids: " + resultSet.getInt("Kids"));
		}
		
		/* Upon the cancellation or change of a reservation, the system shall display the
				details of the cancelled or changed reservation on the screen.
		 * 
		 */
	}

	private static void startChangeRes() {
		System.out.println("Starts the flow for a user to change their reservation");
	}

	private static void startResHistory() {
		System.out.println("Starts the flow for a user to view their reservation history");
		System.out.println("What is your last name?\n");
		String lname = sc.nextLine();
		System.out.println("What is your first name?\n");
		String fname = sc.nextLine();
		PreparedStatement stmt = conn.prepareStatement("SELECT Customers.Room,Customers.CheckIn,Customers.Checkout FROM Customers "
				+ "JOIN Reservations on Reservations.LastName=Customers.LastName and Reservations.FirstName=Customers.FirstName"
				+ " WHERE Customers.FirstName = ? AND Customers.LastName = ?");
		stmt.setString(1, fname);
		stmt.setString(2, lname);
		ResultSet resultSet = stmt.executeQuery();
		while (resultSet.next()) {
			System.out.println("ROOM: " + resultSet.getString("Room"));
			System.out.println("CHECK-IN: " + resultSet.getString("CheckIn"));
			System.out.println("CHECK-OUT: " + resultSet.getString("CheckOut\n"));
		}
	}

	private static void startManager() {
		System.out.println("Allows a manager to sign in to view revenue of year");
	}
}
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/*
export CLASSPATH=$CLASSPATH:mysql-connector-java-8.0.15-bin.jar:.
export APP_JDBC_URL=jdbc:mysql://csc365.toshikuboi.net/cmmccoy
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

        String jdbcUrl = System.getenv("jdbc:mysql://csc365.toshikuboi.net/cmmccoy");
        String dbUsername = System.getenv("cmmccoy");
        String dbPassword = System.getenv("008506325");

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
	}
	
	private static void startCancelRes() {
		System.out.println("Starts the flow for a user to cancel their reservation");
	}

	private static void startChangeRes() {
		System.out.println("Starts the flow for a user to change their reservation");
	}

	private static void startResHistory() {
		System.out.println("Starts the flow for a user to view their reservation history");
	}

	private static void startManager() {
		System.out.println("Allows a manager to sign in to view revenue of year");
	}
}
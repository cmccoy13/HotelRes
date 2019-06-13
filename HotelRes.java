import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class HotelRes {
	
	static Scanner sc = new Scanner(System.in);
	static Connection conn;
	
    public static void main(String[] args) {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

        String jdbcUrl = "jdbc:mysql://csc365.toshikuboi.net/sec03group10";
        String dbUsername = "cmmccoy";
        String dbPassword = "008506325";

        System.out.println(jdbcUrl);
        
        try {
            conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            System.out.print("MySQL Connection created");
            
            String command = "";
    		
    		while(!command.equals("q")) {
    			printWelcome();
    			command = sc.nextLine();
    			executeCommand(command);
    		}
    		
    		conn.close();
        } 
        catch (SQLException e) {
            e.printStackTrace();

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
		else if(command.equals("q")) {
			System.out.println("Quitting");
			System.exit(0);
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
		System.out.println("Enter the credit card number that was used to make the reservation: ");
		int ccNum = sc.nextInt();
		sc.nextLine();
		ArrayList<String> codes = new ArrayList<String>();
		
		try { //get all reservations from that CC			
			
			PreparedStatement prepState = conn.prepareStatement("SELECT * FROM Reservations r JOIN Customers c ON r.FirstName = c.firstname AND r.LastName = c.lastname WHERE c.CC = ?");
			prepState.setInt(1, ccNum);
			ResultSet rs = prepState.executeQuery();
			
			while(rs.next()) {
				System.out.println("Code: " + rs.getString("Code") + ",  Room: " + rs.getString("Room") + ",  Checkin: " + rs.getString("CheckIn") + ",  Checkout: " + rs.getString("Checkout"));
				codes.add(rs.getString("Code"));
			}
			
			System.out.println("\nEnter the code of the reservation you want to cancel: ");
			int code = sc.nextInt();
			sc.nextLine();
			
			if(codes.contains(Integer.toString(code))) { //If the given reservation was made with the given credit card
				try {

					PreparedStatement prepState2 = conn.prepareStatement("DELETE from Reservations WHERE CODE = ?");
					prepState2.setInt(1,  code);
					int success = prepState2.executeUpdate();
					
					if(success == 0)
						System.out.println("\nUnable to cancel reservation.");
					else
						System.out.println("\nReservation deleted.");
				}
				catch(SQLException e) {
					System.out.println(e.getMessage());
				}
			}
			else {
				System.out.println("\nUnable to cancel reservation.");
			}
					
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void startChangeRes() {
		System.out.println("Starts the flow for a user to change their reservation");
	}

	private static void startResHistory() {
		System.out.println("Starts the flow for a user to view their reservation history");
		System.out.println("What is your last name?");
		String lname = sc.nextLine().toUpperCase();
		System.out.println("What is your first name?");
		String fname = sc.nextLine().toUpperCase();
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT r.Room,r.CheckIn,r.CheckOut FROM Reservations r "
					+ " WHERE r.FirstName = ? AND r.LastName = ?");
			stmt.setString(1, "'" + fname + "'");
			stmt.setString(2, "'" + lname + "'");
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				System.out.println("ROOM: " + resultSet.getString("Room"));
				System.out.println("CHECK-IN: " + resultSet.getString("CheckIn"));
				System.out.println("CHECK-OUT: " + resultSet.getString("CheckOut"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void startManager() {
		System.out.println("\nEnter user ID number: ");
		int id = sc.nextInt();
		sc.nextLine();
		
		try { //get all reservations from that CC			
			
			PreparedStatement prepState = conn.prepareStatement("SELECT * FROM Customers WHERE id = ? AND Manager = 1");
			prepState.setInt(1, id);
			ResultSet rs = prepState.executeQuery();

			if(rs.next()) {
				System.out.println("\nWelcome manager");
				System.out.println("\nRevenue for the year, by month\n");
				System.out.println("                           January    February      March       April        May       June        July        August     September    October    November    December          Total");
				
				for(int i = 1; i <= 10; i++) {
					String room = "ERROR";
					
					if(i == 1) {
						room = "'Abscond or bolster'";
					}
					else if (i == 2) {
						room = "'Convoke and sanguine'";
					}
					else if (i == 3) {
						room = "'Frugal not apropos'";
					}
					else if (i == 4) {
						room = "'Harbinger but bequest'";
					}
					else if (i == 5) {
						room = "'Immutable before decorum'";
					}
					else if (i == 6) {
						room = "'Interim but salutary'";
					}
					else if (i == 7) {
						room = "'Mendicant with cryptic'";
					}
					else if (i == 8) {
						room = "'Recluse and defiance'";
					}
					else if (i == 9) {
						room = "'Riddle to exculpate'";
					}
					else if (i == 10) {
						room = "'Thrift and accolade'";
					}
					
					PreparedStatement stmt = conn.prepareStatement("SELECT month(checkin), sum(rate*(DATEDIFF(checkout, checkin))) Rev FROM Reservations rs JOIN Rooms rm ON rs.Room = rm.RoomCode where RoomName = ? GROUP BY month(checkin) ORDER BY MONTH(checkin)");
					stmt.setString(1, room);
					ResultSet roomQ = stmt.executeQuery();
					
					System.out.print(room + ":     ");
					while(roomQ.next()) {
						System.out.print(roomQ.getString("Rev") + "     ");
					}
					
					PreparedStatement total = conn.prepareStatement("SELECT sum(rate*(DATEDIFF(checkout, checkin))) Rev FROM Reservations rs JOIN Rooms rm ON rs.Room = rm.RoomCode where RoomName = ?");
					total.setString(1,  room);
					ResultSet totalQ = total.executeQuery();
					totalQ.next();
					System.out.print("    " + totalQ.getString("Rev"));
					
					
					System.out.print("\n");
				}				
						
			}
			else {
				System.out.println("\nInvalid manager ID");
			}
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void quit() {
		System.out.println("Quitting");
	}
}
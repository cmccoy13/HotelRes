import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class HotelRes {
	
	static Scanner sc = new Scanner(System.in);
	static Connection conn;
	
    public static void main(String[] args) {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

        String jdbcUrl = "jdbc:mysql://csc365.toshikuboi.net/sec03group10";
        String dbUsername = "cmmccoy";
        String dbPassword = "008506325";
        
        try {
            conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            
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
			System.out.println("\n");
			try {
				conn.close();
			}
			catch (SQLException e) {
		            e.printStackTrace();

		    }	
			System.exit(0);
		}
		else {
			System.out.println("Invalid Selection. Please input a valid command.");
			command = sc.next();
			executeCommand(command);
		}
	}
	
	/* The system shall allow booking until the hotel is fully booked.
	 * The system must not allow overbooking.
	 * The system shall not allow a room to be occupied by guests beyond its capacity.
	 * The system shall allow payment by credit card. Create fake credit card accounts
			in your database. A room shall be reserved only when payment by a credit card
			is approved (do not book a room without payment. Also, do not charge the card
			unless the room can be reserved. I.e. both reservation and the charge must be
			bundled into a transaction.The same should be true for cancellation.).
	 * The system shall display availability of rooms on each day. For each room,
			display its popularity score (number of days the room has been occupied during
			the previous 180 days divided by 180 (round to two decimal places)), price,
			available or if not, next available date, length, bed type, the number of beds, the
			maximum number of occupancy allowed.
	 * Upon reservation, the system shall display the details of the reservation on the screen.
	 */
	private static boolean startBooking() {
		int c = 0;
		int rChoice = 0;
		int people = 0;
		long cardNumber = 0;
		float price = 0;
		String tempString = "";
		String sChoice = "";
		String room = "";
		ResultSet available = null;
		ArrayList<String> codes = new ArrayList<String>();
		ArrayList<Integer> caps = new ArrayList<Integer>();
		
		System.out.println("Starts the flow for a user to book a room");
		System.out.println("Please select a search type\n\n"
				+ "1: Search by date\n"
				+ "2: Search by bed type\n"
				+ "3: Search by number of beds\n"
				+ "4: Search by decor\n"
				+ "5: Search by price range\n"
				+ "6: Search by maximum occupants");
		sChoice = sc.nextLine();
		
		if(sChoice.equals("1"))
			available = dateSearch(); //date
		else if(sChoice.equals("2"))
			available = typeSearch(); //bed type
		else if(sChoice.equals("3"))
			available = numberSeach(); //number of beds
		else if(sChoice.equals("4"))
			available = decorSearch(); //decor
		else if(sChoice.equals("5"))
			available = rangeSearch(); //price range
		else if(sChoice.equals("6"))
			available = maxSearch(); //number of occupants
		else {
			System.out.println("Incorret input, try again.");
			return false;
		}
		
		if(available==null)
			return false;
		
		try {
			if(!available.isBeforeFirst()) {
				System.out.println("There are no available rooms fitting your search parameters. We're sorry for the inconvenience.");
				return false;
			}
			
			System.out.println("\nPlease choose which room you would like to reserve:");
			while(available.next()) {
				c++;
				System.out.println(c+") Room: "+available.getString("RoomName")+", Max Occupants: "+available.getInt("maxOcc"));
				codes.add(available.getString("RoomCode"));
				caps.add(available.getInt("maxOcc"));
			}
			
			while(rChoice == 0) {
				tempString = sc.nextLine();
				try {
					rChoice = Integer.parseInt(tempString.substring(0, 1));
					if(rChoice > codes.size() || rChoice < 1) {
						rChoice = 0;
						System.out.println("Please input a listed number:");
					}
				} catch(NumberFormatException e) {
					rChoice = 0;
					System.out.println("Please input a number");
				}
			}
			rChoice--;
			room = codes.get(rChoice);
			
			System.out.println("Room: "+room);
			//if(isAvailable(room)) {
			
			boolean dateless = true;
			Date startDate = null;
			Date endDate = null;
			
			while(dateless) {
				System.out.print("Please enter a check in date (YYYY-[M]M-[D]D): ");
				try {
					startDate=java.sql.Date.valueOf(sc.nextLine());
					dateless = false;
				} catch(IllegalArgumentException e) {
					System.out.println("Incorrect date format");
					dateless = true;
				}
			}
			dateless = true;
			while(dateless) {
				System.out.print("Please enter an check out date (YYYY-[M]M-[D]D): ");
				try {
					endDate=java.sql.Date.valueOf(sc.nextLine());
					dateless = false;
				} catch(IllegalArgumentException e) {
					System.out.println("Incorrect date format");
					dateless = true;
				}
			}
			
			try {
				PreparedStatement getAvail = conn.prepareStatement("select Room from Reservations where Checkout >= ? and CheckIn <= ? and Room = "+room);
				getAvail.setDate(1, startDate);
				getAvail.setDate(2, endDate);
				ResultSet availableRoom = getAvail.executeQuery();
				if(availableRoom.isBeforeFirst()) {
					System.out.println("Unfortunately, this room is not available at these times.\n"
							+ "We're sorry for the inconvenience");
					return false;
				}
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			
			
				int adults = -1;
				while(adults < 1) {
					System.out.println("How many adults in your party? ");
					adults = sc.nextInt();
					sc.nextLine();
				}
				
				int kids = -1;
				while(kids < 0) {
					System.out.println("How many kids in your party? ");
					kids = sc.nextInt();
					sc.nextLine();
				}
				
				people = adults + kids;
				
				if(people > caps.get(rChoice)) {
					System.out.println("Unfortunately, this room cannot accommodate that many people.\n"
							+ "We're sorry for the inconvenience");
					return false;
				}
				
				PreparedStatement roomPrice = conn.prepareStatement("select basePrice*DATEDIFF(?, ?) total from Rooms where RoomCode = ?");
				roomPrice.setDate(1,  endDate);
				roomPrice.setDate(2,  startDate);
				roomPrice.setString(3,  room);
				ResultSet priceQ = roomPrice.executeQuery();
				priceQ.next();
				price = priceQ.getFloat("total");
				
				System.out.println("\nReservation Information...");
				System.out.println("Reservation Dates: " + startDate + " to " + endDate);
				System.out.println("Room: " + room);
				System.out.println("Number of adults: " + adults);
				System.out.println("Number of kids: " + adults);
				System.out.println("Price: " + price);
				
				System.out.println("Proceed to payment (Y/N)?");
				String proceed = sc.nextLine();
				
				while(!(proceed.equalsIgnoreCase("y") || proceed.equalsIgnoreCase("n"))) {
					System.out.println("Please enter a valid selection");
					proceed = sc.nextLine();
				}
				
				if(proceed.equalsIgnoreCase("n")) {
					return false;
				}
				
				System.out.println("Please input your credit card number: ");
				cardNumber = sc.nextLong();
				sc.nextLine();
				PreparedStatement ccCheck = conn.prepareStatement("select CCNum from CreditCards WHERE CCNum = "+cardNumber);
				ResultSet cards = ccCheck.executeQuery();
				
				if(!cards.isBeforeFirst()) {
					//System.out.println("Unfortunately, there is no credit-card matching that number.");
					
					System.out.println("Please enter your first name: ");
					String firstName = sc.nextLine();
					System.out.println("Please enter your last name: ");
					String lastName = sc.nextLine();
					
					PreparedStatement createCC = conn.prepareStatement("INSERT INTO CreditCards (CCNum, FirstName, LastName, Balance) VALUES (?, ?, ?, ?)");
					createCC.setLong(1, cardNumber);
					createCC.setString(2, firstName);
					createCC.setString(3, lastName);
					createCC.setFloat(4, price);
					
					int createdCC = createCC.executeUpdate();
					
					if(createdCC == 0) {
						System.out.println("There was a problem processing your credit card.");
						return false;
					}
					
					PreparedStatement createCustomer = conn.prepareStatement("INSERT INTO Customers (FirstName, LastName, CC) VALUES (?, ?, ?)");
					createCustomer.setString(1, firstName);
					createCustomer.setString(2, lastName);
					createCustomer.setLong(3, cardNumber);
					int createdCustomer = createCustomer.executeUpdate();
					
					if(createdCustomer == 0) {
						System.out.println("There was a problem processing your credit card.");
						return false;
					}
					
					PreparedStatement createRes = conn.prepareStatement("INSERT INTO Reservations (Room, CheckIn, CheckOut, Rate, LastName, FirstName, Adults, Kids) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
					createRes.setString(1, room);
					createRes.setDate(2, startDate);
					createRes.setDate(3, endDate);
					createRes.setFloat(4, price);
					createRes.setString(5, lastName);
					createRes.setString(6, firstName);
					createRes.setInt(7, adults);
					createRes.setInt(8, kids);
					int createdRes = createRes.executeUpdate();
					
					if(createdRes == 0) {
						System.out.println("There was a problem creating your reservation.");
					}
					else {
						System.out.println("Reservation confirmed! We hope you enjoy your stay.");
					}
					
					return false;
				}
				else { //valid credit card
					
					PreparedStatement getName = conn.prepareStatement("SELECT Firstname, lastname FROM Customers WHERE CC = ?");
					getName.setLong(1, cardNumber);
					ResultSet nameQ = getName.executeQuery();
					nameQ.next();
					
					String fname = nameQ.getString("Firstname");
					String lname = nameQ.getString("Lastname");
					
					System.out.println("Welcome back " + fname + "!");
					
					PreparedStatement updateCC = conn.prepareStatement("UPDATE CreditCards SET Balance = Balance + ? WHERE CCNum = ?");
					updateCC.setFloat(1, price);
					updateCC.setLong(2, cardNumber);
					
					int updatedCC = updateCC.executeUpdate();
					
					if(updatedCC == 0) {
						System.out.println("There was a problem processing your credit card.");
						return false;
					}
					
					PreparedStatement createRes = conn.prepareStatement("INSERT INTO Reservations (Room, CheckIn, CheckOut, Rate, LastName, FirstName, Adults, Kids) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
					createRes.setString(1, room);
					createRes.setDate(2, startDate);
					createRes.setDate(3, endDate);
					createRes.setFloat(4, price);
					createRes.setString(5, lname);
					createRes.setString(6, fname);
					createRes.setInt(7, adults);
					createRes.setInt(8, kids);
					int createdRes = createRes.executeUpdate();
					
					if(createdRes == 0) {
						System.out.println("There was a problem creating your reservation.");
						return false;
					}
					else {
						System.out.println("Reservation confirmed! We hope you enjoy your stay.");
					}
					
					return true;
					
				}
			
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	private static boolean isAvailable(String room) {
		boolean dateless = true;
		Date startDate = null;
		Date endDate = null;
		
		while(dateless) {
			System.out.print("Please enter a check in date (YYYY-[M]M-[D]D): ");
			try {
				startDate=java.sql.Date.valueOf(sc.nextLine());
				dateless = false;
			} catch(IllegalArgumentException e) {
				System.out.println("Incorrect date format");
				dateless = true;
			}
		}
		dateless = true;
		while(dateless) {
			System.out.print("Please enter an check out date (YYYY-[M]M-[D]D): ");
			try {
				endDate=java.sql.Date.valueOf(sc.nextLine());
				dateless = false;
			} catch(IllegalArgumentException e) {
				System.out.println("Incorrect date format");
				dateless = true;
			}
		}
		
		try {
			PreparedStatement getAvail = conn.prepareStatement("select Room from Reservations where Checkout >= ? and CheckIn <= ? and Room = "+room);
			getAvail.setDate(1, startDate);
			getAvail.setDate(2, endDate);
			ResultSet available = getAvail.executeQuery();
			if(available.isBeforeFirst()) {
				return false;
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return true;
	}
	
	/* The system shall display availability on each day....
	private static void nextAvailable(Date startDate, Date endDate, String room) {
		PreparedStatement getAvail = null;
		ResultSet available = null;
		
		try {
			getAvail = conn.prepareStatement("select Room from Reservations where Checkout > ? and CheckIn < ? and Room = "+room);
			getAvail.setDate(1, startDate);
			getAvail.setDate(2, endDate);
			available = getAvail.executeQuery();
			if(available.isBeforeFirst()) {
				
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}*/
	
	private static ResultSet dateSearch() {
		boolean dateless = false;
		Date startDate = null;
		Date endDate = null;
		String baseQuery = "select RoomCode, RoomName, Beds, bedType, maxOcc, basePrice, decor from Rooms as r "
				+ "left join (select room from Reservations where CheckOut >= ? and CheckIn <= ?) as b on r.RoomCode = b.Room where b.Room is null;";
		ResultSet available = null;
		
		do {
			System.out.print("Please enter a check in date (YYYY-[M]M-[D]D): ");
			try {
				startDate=java.sql.Date.valueOf(sc.nextLine());
				dateless = false;
			} catch(IllegalArgumentException e) {
				System.out.println("Incorrect date format");
				dateless = true;
			}
		} while(dateless);
		do {
			System.out.print("Pleaes enter an check out date (YYYY-[M]M-[D]D): ");
			try {
				endDate=java.sql.Date.valueOf(sc.nextLine());
				dateless = false;
			} catch(IllegalArgumentException e) {
				System.out.println("Incorrect date format");
				dateless = true;
			}
		} while(dateless);
		
		try {
			PreparedStatement getRooms = conn.prepareStatement(baseQuery);
			getRooms.setDate(1, startDate);
			getRooms.setDate(2, endDate);
			available = getRooms.executeQuery();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return available;
	}
	
	private static ResultSet typeSearch() {
		String tChoice = "";
		String baseQuery = "select RoomCode, RoomName, Beds, bedType, maxOcc, basePrice, decor from Rooms as r ";
		PreparedStatement getRooms = null;
		ResultSet available = null;
		
		System.out.println("Please select the type of bed you prefer: \n\n"
				+ "1: King\n"
				+ "2: Queen\n"
				+ "3: Double");
		tChoice = sc.nextLine();
		
		try {
			if(tChoice.equals("1")) {
				getRooms = conn.prepareStatement(baseQuery+"where bedType = \"'King'\"");
			} else if(tChoice.equals("2")) {
				getRooms = conn.prepareStatement(baseQuery+"where bedType = \"'Queen'\"");
			} else if(tChoice.equals("3")) {
				getRooms = conn.prepareStatement(baseQuery+"where bedType = \"'Double'\"");
			} else {
				System.out.println("Incorrect input, try again.");
				return available;
			}
			
			available = getRooms.executeQuery();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return available;
	}
	
	private static ResultSet numberSeach() {
		String nChoice = "";
		String baseQuery = "select RoomCode, RoomName, Beds, bedType, maxOcc, basePrice, decor from Rooms as r ";
		PreparedStatement getRooms = null;
		ResultSet available = null;
		
		System.out.println("Please select the number of beds you prefer:\n\n"
				+ "1: One bed\n"
				+ "2: Two beds");
		nChoice = sc.nextLine();
		
		try {
			if(nChoice.equals("1")) {
				getRooms = conn.prepareStatement(baseQuery+"where Beds = 1");
			} else if(nChoice.equals("2")) {
				getRooms = conn.prepareStatement(baseQuery+"where Beds = 2");
			} else {
				System.out.println("Incorrect input, try again.");
				return available;
			}
			
			available = getRooms.executeQuery(); 
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return available;
	}
	
	private static ResultSet decorSearch() {
		String dChoice = "";
		String baseQuery = "select RoomCode, RoomName, Beds, bedType, maxOcc, basePrice, decor from Rooms as r ";
		PreparedStatement getRooms = null;
		ResultSet available = null;
		
		System.out.println("Please select the type of decor you prefer:\n\n"
				+ "1: Traditional\n"
				+ "2: Modern\n"
				+ "3: Rustic");
		dChoice = sc.nextLine();
		
		try {
			if (dChoice.equals("1")) {
				getRooms = conn.prepareStatement(baseQuery+"where decor = \"'traditional'\"");
			} else if (dChoice.equals("2")) {
				getRooms = conn.prepareStatement(baseQuery+"where decor = \"'modern'\"");
			} else if (dChoice.equals("3")) {
				getRooms = conn.prepareStatement(baseQuery+"where decor = \"'rustic'\"");
			} else {
				System.out.println("Incorrect input, try again.");
				return available;
			}
			
			available = getRooms.executeQuery();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return available;
	}
	
	private static ResultSet rangeSearch() {
		String rChoice = "";
		String baseQuery = "select RoomCode, RoomName, Beds, bedType, maxOcc, basePrice, decor from Rooms as r ";
		PreparedStatement getRooms = null;
		ResultSet available = null;
		
		System.out.println("Please select your price ceiling:\n\n"
				+ "1: $75\n"
				+ "2: $125\n"
				+ "3: $150\n"
				+ "4: $175\n"
				+ "5: $250");
		rChoice = sc.nextLine();
		
		try {
			if (rChoice.equals("1")) {
				getRooms = conn.prepareStatement(baseQuery+"where basePrice <= 75");
			} else if (rChoice.equals("2")) {
				getRooms = conn.prepareStatement(baseQuery+"where basePrice <= 125");
			} else if (rChoice.equals("3")) {
				getRooms = conn.prepareStatement(baseQuery+"where basePrice <= 150");
			} else if (rChoice.equals("4")) {
				getRooms = conn.prepareStatement(baseQuery+"where basePrice <= 175");
			} else if (rChoice.equals("5")) {
				getRooms = conn.prepareStatement(baseQuery+"where basePrice <= 250");
			} else {
				System.out.println("Incorrect input, try again.");
				return available;
			}
			
			available = getRooms.executeQuery();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return available;
	}
	
	private static ResultSet maxSearch() {
		String mChoice = "";
		String baseQuery = "select RoomCode, RoomName, Beds, bedType, maxOcc, basePrice, decor from Rooms as r ";
		PreparedStatement getRooms = null;
		ResultSet available = null;
		
		System.out.println("Please select size room you need:\n\n"
				+ "1: Two Occupants\n"
				+ "2: Four Occupants");
		mChoice = sc.nextLine();
		
		try {
			if(mChoice.equals("1")) {
				getRooms = conn.prepareStatement(baseQuery+"where maxOcc = 2");
			} else if(mChoice.equals("2")) {
				getRooms = conn.prepareStatement(baseQuery+"where Beds = 4");
			} else {
				System.out.println("Incorrect input, try again.");
				return available;
			}
			
			available = getRooms.executeQuery(); 
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return available;
	}
	
	/* If we need it write a helper function that takes an arraylist.size and returns a choice (use if need to make multiple choices)
	 * private static int *codeChoiceUserErrorCorrector(int arraylist.size);
	 */
	
	private static void startCancelRes() {
		System.out.println("Enter the credit card number that was used to make the reservation: ");
		long ccNum = sc.nextLong();
		sc.nextLine();
		ArrayList<String> codes = new ArrayList<String>();
		
		try { //get all reservations from that CC			
			
			PreparedStatement prepState = conn.prepareStatement("SELECT * FROM Reservations r JOIN Customers c ON r.FirstName = c.firstname AND r.LastName = c.lastname WHERE c.CC = ?");
			prepState.setLong(1, ccNum);
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
		System.out.println("What was your Credit Card Number number?");
		int ccNum = sc.nextInt();
		ArrayList<String> codes = new ArrayList<String>();
		try {
			PreparedStatement prepState = conn.prepareStatement("SELECT * FROM Reservations r JOIN Customers c ON r.FirstName = c.firstname AND r.LastName = c.lastname WHERE c.CC = ?");
			prepState.setInt(1, ccNum);
			ResultSet rs = prepState.executeQuery();
			
			while(rs.next()) {
				System.out.println("Code: " + rs.getString("Code") + ",  Room: " + rs.getString("Room") + ",  Checkin: " + rs.getString("CheckIn") + ",  Checkout: " + rs.getString("Checkout"));
				codes.add(rs.getString("Code"));
			}
			
			System.out.println("\nEnter the code of the reservation you want to change: ");
			try {
			   int code = sc.nextInt();
			   sc.nextLine();
			   if(startBooking()) {
					PreparedStatement rS2 = conn.prepareStatement("DELETE from Reservations WHERE CODE = ?");
					rS2.setInt(1, code);
					rS2.executeUpdate();
				}
			   else {
					System.out.println("Sorry we were not able to change your reservation.");
					return;
				}
			}
			catch (InputMismatchException e) {
				System.out.println("code intered is not one of the codes listed.");
				return;
			}
		} catch (SQLException e) {
			System.out.println("unable to find reservation.");	
		}
		
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
}
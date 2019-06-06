import java.sql.ResultSet;
import java.sql.Statement;
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
    }
}
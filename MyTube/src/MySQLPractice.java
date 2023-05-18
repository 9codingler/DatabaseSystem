import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQLPractice {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            String url = "jdbc:mysql://localhost:3306/MyTube";
            String user = "root";
            String psw = "duftlagltkfwk1";
            Connection con = DriverManager.getConnection(url, user, psw);

            Statement stmt = con.createStatement();
//            stmt.executeUpdate("INSERT INTO `test`.`employee` (`Fname`, `Minit`, `Lname`, `Ssn`, `Bdate`, `Address`, `Sex`, `Salary`, `Dno`) VALUES ('Koo', 'K', 'Mo', '98081', '1998-08-12', '서울시', 'M', '9999999.00', '9');");
            ResultSet rs = stmt.executeQuery("SELECT u_channelName FROM MyAdministrator, MyUser Where admin_num = uadmin_num");

            while(rs.next()) {
                String u_channelName = rs.getString("u_channelName");
                System.out.println("HELLO! 채널명은, " + u_channelName);
            }

            rs.close();
            stmt.close();
            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
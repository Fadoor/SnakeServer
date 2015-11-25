import java.sql.*;

public class Database {
	
	private String Adress;
	private Connection Conn = null;

	public Database(String Adress, String Username, String Password) {
		
		this.Adress = Adress;
		
		try {
			this.Conn = DriverManager.getConnection(Adress, Username, Password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet Query(String SQL) {
		
		try {
			Statement Stmt = this.Conn.createStatement();
			
			ResultSet Response = Stmt.executeQuery(SQL);
			
			return Response;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Boolean Execute(String SQL) {
		
		try {
			Statement Stmt = this.Conn.createStatement();
			
			Boolean Response = Stmt.executeUpdate(SQL) > 0;
			
			Stmt.close();
			
			return Response;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}

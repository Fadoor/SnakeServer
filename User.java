
public class User {
	
	private int Highscore;	
	private String Username;
	private String Password;
	private Boolean IsAdmin;
	
	public User(String Username, String Password, Boolean IsAdmin) {
		
		this.Username = Username;
		this.Password = Password;
		this.IsAdmin = IsAdmin;
		this.Highscore = 0;	
	}
	
	public String GetUsername() {
		
		return this.Username;
	}
	
	public String GetPassword() {
		
		return this.Password;
	}
	
	public int GetHighscore() {
		
		return this.Highscore;
	}
	
	public boolean GetIsAdmin() {
		
		return this.IsAdmin;
	}
}

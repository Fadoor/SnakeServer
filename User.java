
public class User {
	
	private int Highscore;	
	private String Username;
	private String Password;
	private Boolean IsAdmin;
	
	public User(String Username, String Password, int Highscore, Boolean IsAdmin) {
		
		this.Username = Username;
		this.Password = Password;
		this.IsAdmin = IsAdmin;
		this.Highscore = Highscore;	
	}
	
	public String GetUsername() {
		
		return this.Username.toLowerCase();
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

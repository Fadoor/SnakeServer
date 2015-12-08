
public class Game {
	
	private String Name;
	private User Player1;
	private User Player2;
	private int Player1Score;
	private int Player2Score;
	
    public Game(String Name, User Player1, User Player2, int Player1Score, int Player2Score) {
	
    	this.Name = Name;
    	this.Player1 = Player1;
    	this.Player2 = Player2;
    	this.Player1Score = Player1Score;
    	this.Player2Score = Player2Score;
	}

	public User[] GetPlayers() {
		
		return new User[] {this.Player1, this.Player2};
	}
	
	public User GetPlayer2() {
		
		return this.Player2;	
	}
	
	public String GetName() {
		
		return this.Name;
		
	}
	
	public int GetHighscore() {
		
		return Math.max(this.Player1Score, this.Player2Score);
		
	}
	
	public User GetPlayer1() {
		
		return this.Player1;
	}
	
	public int GetPlayer1Score() {
		
		return this.Player1Score;
	}
	
	public int GetPlayer2Score() {
		
		return this.Player2Score;
	}
	
	public boolean HasPlayerByName(String Username) {
		
		if (this.Player2 != null) {
			
			
			if (this.Player2.GetUsername().equals(Username)) {
				
				return true;
			}
			
		}
		
		if (this.Player1 != null) {
		
			if (this.Player1.GetUsername().equals(Username)) {
				
				return true;
			}
		}
		
		return false;
	}
}

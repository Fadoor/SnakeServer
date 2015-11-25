
public class Game {
	
	private User Player1;
	private User Player2;
	
    public Game(User Player1, User Player2) {
	
    	this.Player1 = Player1;
    	this.Player2 = Player2;
	}

	public User[] GetPlayers() {
		
		return new User[] {this.Player1, this.Player2};
	}

}

import java.util.Random;

public class Game {
	
	private String Name;
	private User Player1;
	private User Player2;
	private int Player1Score;
	private int Player2Score;
	private int BallX;
	private int	BallY;
	private int Player1X;
	private int	Player1Y;
	private int Player2X;
	private int Player2Y;
	private String Player1Moves;
	private String Player2Moves;
	public static int Height = 10;
	public static int Width = 20;
	
	
    public Game(String Name, User Player1, User Player2, int Player1Score, int Player2Score) {
	
    	this.Name = Name;
    	this.Player1 = Player1;
    	this.Player2 = Player2;
    	this.Player1Score = Player1Score;
    	this.Player2Score = Player2Score;
	}
    
    public Game(String Name, User Player1, User Player2, int Player1Score, int Player2Score, int BallX, int BallY, int Player1X, int Player1Y, int Player2X, int Player2Y, String Player1Moves, String Player2Moves) {
    	
    	this.Name = Name;
    	this.Player1 = Player1;
    	this.Player2 = Player2;
    	this.Player1Score = Player1Score;
    	this.Player2Score = Player2Score;
    	this.BallX = BallX;
    	this.BallY = BallY;
    	this.Player1X = Player1X;
    	this.Player1Y = Player1Y;
    	this.Player2X = Player2X;
    	this.Player2Y = Player2Y;
    	this.Player1Moves = Player1Moves;
    	this.Player2Moves = Player2Moves;
	}
    
    public void RandomBall() {
    	
    	Random generator = new Random(); 
		int x = generator.nextInt(Game.Width - 2) + 1;
		int y = generator.nextInt(Game.Height - 2) + 1;
		
		BallX = x;
		BallY = y;
    }
    
    public void ResetGame() {
    	
    	RandomBall();
    	
    	this.Player1Score = 0;
    	this.Player2Score = 0;
		this.Player1X = 10;
    	this.Player1Y = 5;
    	this.Player2X = 10;
    	this.Player2Y = 5;
    	this.Player1Moves = null;
    	this.Player2Moves = null;
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
	
	public String GetPlayer1Moves() {
		
		return this.Player1Moves;
	}
	
	public String GetPlayer2Moves() {
		
		return this.Player2Moves;
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
	
	public int[] PlayerMovesToNumbers(String PlayerMoves) {
		
		int x = 0;
		int y = 0;
		
		for (char Letter: PlayerMoves.toCharArray()) {
			
			switch (Letter) {
			
				case 'w':
					
					y ++;
					
					break;
					
				
				case 'a':
					
					x --;
					
					break;
					
				
				case 's':
					
					y --;
	
					break;
	
				
				case 'd':
					
					x ++;
	
					break;
					
			}
		}
		
		return new int[] {x, y};
		
	}
	
	public boolean OutOfBounds(int x, int y) {
		
		if (x > 0 && x < Game.Width && y > 0 && y < Game.Height) {
			
			return false;
		}
		
		return true;
		
	}
	
	public boolean HasBall(int x, int y) {
		
		if (x == this.BallX && y == this.BallY) {
			
			return true;
		}
		
		return false;
	}
	
	public int SimulateGame() {
		
		int[] Player1Move = PlayerMovesToNumbers(this.Player1Moves);
		
		Player1X += Player1Move[0];
		Player1Y += Player1Move[1];
		
		int[] Player2Move = PlayerMovesToNumbers(this.Player2Moves);
		
		Player2X += Player2Move[0];
		Player2Y += Player2Move[1];
		
		boolean Eaten = false;
		
		if (this.HasBall(Player1X, Player1Y)) {
			
			Player1Score ++;
			
			Eaten = true;
		}
		
		if (this.HasBall(Player2X, Player2Y)) {
			
			Player2Score ++;
			
			Eaten = true;
		}
		
		if (this.OutOfBounds(Player1X, Player1Y)) {
			
			return 2;
			
		}
		
		if (this.OutOfBounds(Player2X, Player2Y)) {
			
			return 1;
			
		}
		
		if (Eaten) {
			
			RandomBall(); 
		}
		
		return 0;
		
	}
	
	public String GetUpdateSQL() {
		
		return "UPDATE games SET games.Player1Score = '" + this.Player1Score + "', games.Player2Score = '" + this.Player2Score + "', games.BallX = '" + this.BallX + "', games.BallY = '" + this.BallY + "', games.Player1X = '" + this.Player1X + "', games.Player1Y = '" + this.Player1Y + "', games.Player2X = '" + this.Player2X + "', games.Player2Y = '" + this.Player2Y + "', games.Player1Moves = null, games.Player2Moves = null WHERE games.Name = '" + this.Name + "';";
	}
}

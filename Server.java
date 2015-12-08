import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Server {
	
	private ArrayList<Game> Games = new ArrayList<Game>();
	private Boolean Running = true;
	private Database CurrentDatabase;
	private ServerSocket CurrentSocket;
	
	public Server() {
		
		this.CurrentDatabase = new Database("jdbc:mysql://localhost:3306/snake", "root", "root");
		
		try {
			
			CurrentSocket = new ServerSocket(10800);
			
			(new ClientServerConnection(this)).start(); //Starter en tråd
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Server CurrentServer = new Server();
		
		CurrentServer.StartTui();
	}
	
	public void StartTui() {
		
		User CurrentUser = null;
		
		Scanner scanner = new Scanner(System.in);
		
		while (this.Running) {
			
			System.out.println("Welcome To The Snakepit" + "\n" + "\n" + "Please type Username and Password" + "\n");
			
			
			while (CurrentUser == null) {
			
				System.out.printf("Username: ");
			
				String Username = scanner.nextLine();
				
				System.out.print("\n" + "Password: ");
				
				String Password = scanner.nextLine();
				
				
				CurrentUser = this.GetUserByUsername(Username);
				
				if (this.LogIn(Username, Password) && CurrentUser.GetIsAdmin()) {
					
					System.out.println("\n" + "Welcome To The Snakepit " + CurrentUser.GetUsername());
				}
				
				else {
					
					CurrentUser = null;
					
					System.out.println("\n" + "Invalid Username or Password");
					System.out.println("Please try again \n");
				}
			}
			
			System.out.println("\nAdmin menu:");
			System.out.println("-----------");
			
			System.out.println("1) Create User");
			System.out.println("2) Delete User");
			System.out.println("3) Show all Users");
			System.out.println("4) Show all Games");
			System.out.println("5) Log out\n");
			
			while (CurrentUser != null) {
				
				int UserInput = -1;
			
				try {
					
					UserInput = scanner.nextInt();
					
				}
				catch (InputMismatchException e) {
					
				}
				
				scanner.nextLine();//For at den ikke springer linjen over ved Username og Password
				
				String Username;
				String Password;
				Boolean IsAdmin;
				Boolean result;
				
		        switch (UserInput) {
		        
		            case 1:
		            	System.out.println("Type Username:");
		            	Username = scanner.nextLine();
		            	
		            	if (Username.contains(" ")) {
		            		
		            		System.out.println("Username can not contain spaces\nPlease enter number for action");
		            	}
		            	
		            	else {
		            
			            	System.out.println("Type Password:");
			            	Password = scanner.nextLine();
			            	System.out.println("Make Admin (true/false):");
			            	
			            	IsAdmin = null;
			            	
			            	while (IsAdmin == null) {
			            	
				            	try {
				            	
					            	IsAdmin = scanner.nextBoolean();
					            }
				            	catch (InputMismatchException e) {
				            		
				            		System.out.println("Wrong input, please type true or false");
				            	}
				            	
				            	scanner.nextLine();
			            	}
			            	result = this.CreateUser(Username, Password, IsAdmin);
			            	
			            	if (result) {
			            		
			            		System.out.println("\nCongratulation " + Username + " is created\nPlease enter number for action");
			            	}
			            	else {
			            		
			            		System.out.println("\nFailed, " + Username + " already is taken\nPlease enter number for action");
			            	}
		            	}
		            	
		            break;
		            
		            case 2:
		            	System.out.println("Type Username:");
		            	Username = scanner.nextLine();
		            	if (!CurrentUser.GetUsername().equals(Username)) {
			            		
			            	result = this.DeleteUser(Username);
			            	
			            	if (result) {
			            		
			            		System.out.println("\nSucces " + Username + " is deleted");
			            	}
			            	else {
			            		
			            		System.out.println("\nFailed, " + Username + " not found\nPlease enter number for action");
			            	}
			            	
		            	}
		            	
		            	else {
		            		
		            		System.out.println("You can't delete yourself\nPlease enter number for action");
		            	}
		            
		            break;
		            
		            case 3:
		            	
		            	this.ShowAllUsers();
		            	
		            	System.out.println("\nPlease enter number for action");
		            	
		            break;
		            
		            case 4:
		            	
		            	this.ShowAllGames();
		            	
		            	System.out.println("\nPlease enter number for action");
		            	
		            break;
		            
		            case 5:
		            	
		            	CurrentUser = null;
		            	
		            break;	
		            
		            default:
		            	
		            	System.out.println("\nInvalid input\nPlease enter number for action");
		            
		            break;
		        }
			}
		}
		
		scanner.close();
	}
	
	public Boolean LogIn(String Username, String Password) {
		
		User CurrentUser = this.GetUserByUsername(Username);
		
		if (CurrentUser != null) {
			
			if (CurrentUser.GetPassword().equals(Password)) {
				
				return true;
			}
		}
		
		return false;
	}
	
	public User GetUserByUsername(String Username) {
		
		ResultSet Response = this.CurrentDatabase.Query("SELECT users.Password, users.IsAdmin, users.Highscore FROM users WHERE users.UserName = '" + Username + "';");
		
		if (Response != null) {
			
			try {
				
				if (Response.next()) {
					
					String Password = Response.getString("users.Password");
					Boolean IsAdmin	= (Response.getInt("users.IsAdmin") == 1);
					int Highscore = Response.getInt("users.Highscore");
					
					Response.close();
					
					return new User(Username, Password, Highscore, IsAdmin); //0 og 1 laves om til false/true
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public Game GetGameByUsers(User User1, User User2) {
		
		for (Game CurrentGame : this.Games) {
			
			if (CurrentGame.GetPlayers()[0].equals(User1) && CurrentGame.GetPlayers()[1].equals(User2)) {
				
				return CurrentGame;
			}	
		}
		
		return null;
	}
	
	public Boolean CreateUser(String Username, String Password, Boolean IsAdmin) {
	
		int temp = (IsAdmin) ? 1 : 0; // Midlertig variabel, Hvis IsAdmin er true = 1 og false = 0
		
		String SQL = "INSERT INTO users (users.Username, users.Password, users.IsAdmin) VALUES ('" + Username + "', '" + Password + "', " + temp + ");";
		
		return this.CurrentDatabase.Execute(SQL);	
	}
	
	public Boolean DeleteUser(String Username) {
		
		String SQL = "DELETE FROM users WHERE users.Username = '" + Username + "';";
		
		return this.CurrentDatabase.Execute(SQL);
	}
	
	public void ShowAllUsers() {
			
		String SQL = "SELECT users.Username FROM users ORDER BY users.Username ASC;"; // ASC er A-Z og DESC er Z-A
		
		ResultSet Response = this.CurrentDatabase.Query(SQL);
		
		if (Response != null) {
			
			try {
				while (Response.next()) {
					
					System.out.println(Response.getString("users.Username"));
					
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void ShowAllGames() {
		
		ArrayList<Game> Games = GetGames();
		
		for (Game CurrentGame: Games) {
			
			System.out.println("Game:\n" + CurrentGame.GetName());
		}
	}

	public Boolean CreateGame(String GameName) {
		
		if (!GameName.equals("")) {
		
			String SQL = "INSERT INTO games (games.Name) VALUES ('" + GameName + "');";
			
			return this.CurrentDatabase.Execute(SQL);
		}
		
		return false;
	}
	
	public int GetHighscore() {
		
		ResultSet Response = this.CurrentDatabase.Query("SELECT MAX(users.Highscore) AS Highscore FROM users;");
		
		if (Response != null) {
			
			try {
				
				if (Response.next()) {
					
					int Highscore = Response.getInt("Highscore");
					
					Response.close();
					
					return Highscore;
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return -1;
		
	}
	
	public int GetUserHighscore(String Username) {
		
		User CurrentUser = this.GetUserByUsername(Username);
		
		if (CurrentUser != null) {
		
			return CurrentUser.GetHighscore();
		}
		
		return -1;
	}
	
	public boolean SetUserHighscore(User CurrentUser, int Highscore) {
		
		String SQL = "UPDATE users SET users.Highscore = '" + Highscore + "' WHERE users.Username = '" + CurrentUser.GetUsername() + "';";
	
		return this.CurrentDatabase.Execute(SQL);
	}
	
	public ServerSocket GetSocket() {
		
		return this.CurrentSocket;
	}
	
	public Game GetGameByName(String Name) {
		
		ResultSet Response = this.CurrentDatabase.Query("SELECT games.Name, games.Player1, games.Player2, games.Player1Score, games.Player2Score, games.BallX, games.BallY, games.Player1X, games.Player1Y, games.Player2X, games.Player2Y, games.Player1Moves, games.Player2Moves FROM games WHERE games.Name = '" + Name + "';");
		
		if (Response != null) {
			
			try {
				
				if (Response.next()) {
					
					User Player1 = this.GetUserByUsername(Response.getString("games.Player1"));
					User Player2 = this.GetUserByUsername(Response.getString("games.Player2"));
					int Player1Score = Response.getInt("games.Player1Score");
					int Player2Score = Response.getInt("games.Player2Score");
					int BallX = Response.getInt("games.BallX");
					int BallY = Response.getInt("games.BallY");
					int Player1X = Response.getInt("games.Player1X");
					int Player1Y = Response.getInt("games.Player1Y");
					int Player2X = Response.getInt("games.Player2X");
					int Player2Y = Response.getInt("games.Player2Y");
					String Player1Moves = Response.getString("games.Player1Moves");
					String Player2Moves = Response.getString("games.Player2Moves");
					
					Response.close();
					
					return new Game(Name, Player1, Player2, Player1Score, Player2Score, BallX, BallY, Player1X, Player1Y, Player2X, Player2Y, Player1Moves, Player2Moves);
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public ArrayList<Game> GetGames() {
		
		ResultSet Response = this.CurrentDatabase.Query("SELECT games.Name, games.Player1, games.Player2, games.Player1Score, games.Player2Score FROM games");
		
		if (Response != null) {
			
			ArrayList<Game> Result = new ArrayList<Game>();
			
			try {
				
				while (Response.next()) {
					
					String Name = Response.getString("Games.Name");
					User Player1 = this.GetUserByUsername(Response.getString("Games.Player1"));
					User Player2 = this.GetUserByUsername(Response.getString("Games.Player2"));
					int Player1Score = Response.getInt("Games.Player1Score");
					int Player2Score = Response.getInt("Games.Player2Score");
					
					Result.add(new Game(Name, Player1, Player2, Player1Score, Player2Score));
				}
				
				Response.close();
				
				return Result;
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
		
	}
	
	public boolean SetGamePlayer(Game CurrentGame, User CurrentUser, boolean Player2) {
		
		String SQL;
		
		if (Player2) {
			
			SQL = "UPDATE games SET games.Player2 = '" + CurrentUser.GetUsername() + "' WHERE games.Name = '" + CurrentGame.GetName() + "';";	
		}
		
		else {
			
			SQL = "UPDATE games SET games.Player1 = '" + CurrentUser.GetUsername() + "' WHERE games.Name = '" + CurrentGame.GetName() + "';";
			
		}
		
		return this.CurrentDatabase.Execute(SQL);	
		
	}
	
	public boolean DeleteGame(Game CurrentGame, User Owner) {
		
		String SQL = "DELETE FROM games WHERE games.Name = '" + CurrentGame.GetName() + "' AND games.Player1 = '" + Owner.GetUsername() + "';";
	
		
		return this.CurrentDatabase.Execute(SQL);
	}
	
	public boolean UpdatePlayerMove(Game CurrentGame, User CurrentUser, String PlayerMoves) {
		
		String SQL = "UPDATE games SET games.Player2Moves = '" + PlayerMoves + "' WHERE games.Name = '" + CurrentGame.GetName() + "';";
		
		if (CurrentGame.GetPlayer1().GetUsername().equals(CurrentUser.GetUsername())) {
			
			SQL = "UPDATE games SET games.Player1Moves = '" + PlayerMoves + "' WHERE games.Name = '" + CurrentGame.GetName() + "';";
			
		}
		
		return this.CurrentDatabase.Execute(SQL);
		
	}
	
	public boolean UpdateGame(Game CurrentGame) {
		
		String SQL = CurrentGame.GetUpdateSQL();
		
		return this.CurrentDatabase.Execute(SQL);
	}
	
	public boolean LeaveGame(Game CurrentGame, User CurrentUser) {
		
		String SQL = "UPDATE games SET games.Player2 = null WHERE games.Name = '" + CurrentGame.GetName() + "' and games.Player2 = '" + CurrentUser.GetUsername() + "';";
	
		
		return this.CurrentDatabase.Execute(SQL);
		
	}
	
}

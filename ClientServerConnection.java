import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.*;

public class ClientServerConnection extends Thread {
	
	Server Server;
	
	public ClientServerConnection(Server Server) {
		
		this.Server = Server;
		
	}
	
	public void run() {
		
		Socket clientSocket;
		try {
			
			clientSocket = Server.GetSocket().accept();
			
			(new ClientServerConnection(Server)).start(); //Starter en tr�d
		
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String InputLine;
			String OutputLine;
			
			while ((InputLine = in.readLine()) != null) {
				
				try {
					
					JSONObject Received = new JSONObject(InputLine);
					
					String Method = Received.getString("Method");
					
					Boolean Result;
					JSONObject Response;
					String Username;
						
					switch (Method) {
					
						case "Login":
							
							Username = Received.getString("Username");
							String Password = Received.getString("Password");
							
							Result = Server.LogIn(Username, Password);
							
							Response = new JSONObject();
							
							if (Result) {
								
								Response.put("Username", Server.GetUserByUsername(Username).GetUsername());
								
							}
							
							Response.put("Result", Result);
							
							out.println(Response.toString());
							
							break;
							
							
						case "CreateGame":
							
							Result = false;
							
							String GameName = Received.getString("GameName");
							Username = Received.getString("Username");
							User CurrentUser = this.Server.GetUserByUsername(Username);
							
							if (CurrentUser != null) {
								
								Result = Server.CreateGame(GameName);
								
								if (Result) {
									
									Game CurrentGame = Server.GetGameByName(GameName);
									
									CurrentGame.ResetGame();
									this.Server.UpdateGame(CurrentGame);
									
									if (Server.SetGamePlayer(CurrentGame, CurrentUser, false)) {
										
										Result = true;
										
									}
								}
								
							}
							
							Response = new JSONObject();
							
							Response.put("Result", Result);
							
							out.println(Response.toString());
							
							break;
							
						
						case "UserHighscore":
							
							Username = Received.getString("Username");
							
							CurrentUser = Server.GetUserByUsername(Username);
							
							Response = new JSONObject();
							
							Response.put("Result", CurrentUser.GetHighscore());
							
							out.println(Response.toString());
							
							break;
							
							
						case "GlobalHighscore":
							
							Response = new JSONObject();
							
							Response.put("Result", Server.GetHighscore());
							
							out.println(Response.toString());
							
							break;
							
						
						case "ShowGames":
							
							ArrayList<Game> Games = Server.GetGames();
							
							JSONObject JSONGame;
							
							JSONArray JSONGames = new JSONArray();
							
							for (Game CurrentGame : Games) {
								
								JSONGame = new JSONObject();
								
								JSONGame.put("Name", CurrentGame.GetName());
								JSONGame.put("Highscore", CurrentGame.GetHighscore());
								
								JSONGames.put(JSONGame);
								
							}
							
							Response = new JSONObject();
							
							Response.put("Result", JSONGames);
							
							out.println(Response.toString());
							
							break;
							
						
						case "JoinGame":
							
							GameName = Received.getString("GameName");
							
							Username = Received.getString("Username");
							
							Game CurrentGame = Server.GetGameByName(GameName);
							
							CurrentUser = this.Server.GetUserByUsername(Username);
							
							Result = false;

							Response = new JSONObject();
							
							if (CurrentGame != null && CurrentUser != null) {
								
								if (!(CurrentGame.GetPlayer1().GetUsername().equals(CurrentUser.GetUsername()))) {
									
									if (CurrentGame.GetPlayer2() != null) {
																				
										if (CurrentGame.GetPlayer2().GetUsername().equals(CurrentUser.GetUsername())) {
											
											Result = true;
											
										}
										
									}
									
									else {
										
										Result = this.Server.SetGamePlayer(CurrentGame, CurrentUser, true);
										
									}
									
								}
								
								else {
									
									Result = true;
									
								}
								
							}

							Response.put("Result", Result);
							
							out.println(Response.toString());
							
							break;
							
						case "DeleteGame":
							
							GameName = Received.getString("GameName");
							
							Username = Received.getString("Username");
							
							CurrentGame = this.Server.GetGameByName(GameName);
							
							CurrentUser = this.Server.GetUserByUsername(Username);
							
							Result = false;
							
							if (CurrentGame != null && CurrentUser != null) {
								
								Result = this.Server.DeleteGame(CurrentGame, CurrentUser);
								
							}
							
							Response = new JSONObject();
							
							Response.put("Result", Result);
							
							out.println(Response.toString());
							
							break;
							
						
						case "GameInfo":
							
							GameName = Received.getString("GameName");
							
							Username = Received.getString("Username");
							
							CurrentGame = this.Server.GetGameByName(GameName);
							
							CurrentUser = this.Server.GetUserByUsername(Username);
							
							Result = false;
							
							Response = new JSONObject();
							
							if (CurrentGame != null && CurrentUser != null) {
								
								if (CurrentGame.HasPlayerByName(CurrentUser.GetUsername())) {

									JSONGame = new JSONObject();
									
									JSONGame.put("GameName", CurrentGame.GetName());
									JSONGame.put("Player1Score", CurrentGame.GetPlayer1Score());
									JSONGame.put("Player2Score", CurrentGame.GetPlayer2Score());
									
									if(CurrentGame.GetPlayer2() != null) {
										
										JSONGame.put("Player2", CurrentGame.GetPlayer2().GetUsername());
									
									}
									
									JSONGame.put("Player1", CurrentGame.GetPlayer1().GetUsername());
									
									Response.put("GameInfo", JSONGame);
									
									Result = true;
								
								}
								
							}
							
							Response.put("Result", Result);
							
							out.println(Response.toString());
							
							break;
							
						
						case "LeaveGame":
							
							GameName = Received.getString("GameName");
							
							Username = Received.getString("Username");
							
							CurrentGame = this.Server.GetGameByName(GameName);
							
							CurrentUser = this.Server.GetUserByUsername(Username);
							
							Result = false;
							
							
							if (CurrentGame != null && CurrentUser != null) {
								
								Result = this.Server.LeaveGame(CurrentGame, CurrentUser);
								
								if (Result) {
									
									CurrentGame.ResetGame();
									this.Server.UpdateGame(CurrentGame);
								}
								
							}
							
							Response = new JSONObject();
							
							Response.put("Result", Result);
							
							out.println(Response.toString());
							
							break;
							
							
						case "UserInput":
							
							GameName = Received.getString("GameName");
						
							Username = Received.getString("Username");
							
							String UserInput = Received.getString("UserInput");
							
							CurrentGame = this.Server.GetGameByName(GameName);
							
							CurrentUser = this.Server.GetUserByUsername(Username);
							
							int ResultState = -1;
							
							if (CurrentGame != null && CurrentUser != null && CurrentGame.GetPlayer1() != null && CurrentGame.GetPlayer2() != null) {
								
								if (CurrentGame.HasPlayerByName(CurrentUser.GetUsername())) {
									
									if (Server.UpdatePlayerMove(CurrentGame, CurrentUser, UserInput)) {
										
										CurrentGame = this.Server.GetGameByName(GameName);
																				
										if (CurrentGame.GetPlayer1Moves() != null && CurrentGame.GetPlayer2Moves() != null) {
											
											ResultState = CurrentGame.SimulateGame();
											this.Server.UpdateGame(CurrentGame);
											
											if (ResultState == 1 || ResultState == 2) {
												
												if (CurrentGame.GetPlayer1Score() > CurrentGame.GetPlayer1().GetHighscore()) {
													this.Server.SetUserHighscore(CurrentGame.GetPlayer1(), CurrentGame.GetPlayer1Score());
												}
												
												if (CurrentGame.GetPlayer2Score() > CurrentGame.GetPlayer2().GetHighscore()) {
													this.Server.SetUserHighscore(CurrentGame.GetPlayer2(), CurrentGame.GetPlayer2Score());
												}
												
												CurrentGame.ResetGame();
												this.Server.UpdateGame(CurrentGame);
											}
										}
										else {
											
											ResultState = -2;
										}
																														
									}
									
								}
								
							}
							
							Response = new JSONObject();
							
							Response.put("Result", ResultState);
							
							out.println(Response.toString());
							
							break;
							
							
						
						default:
							
							JSONObject Error = new JSONObject();
							
							Error.put("Error", "Invalid input, please try again");
							
							out.println(Error.toString());
						
						break;
					
					}
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		    }
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;

	}
}


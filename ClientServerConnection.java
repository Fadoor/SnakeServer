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
							
							Response.put("Result", Result);
							
							out.println(Response.toString());
							
							break;
							
							
						case "CreateGame":
							
							String GameName = Received.getString("GameName");
							
							Result = Server.CreateGame(GameName);
							
							Response = new JSONObject();
							
							Response.put("Result", Result);
							
							out.println(Response.toString());
							
							break;
							
						
						case "UserHighscore":
							
							Username = Received.getString("Username");
							
							User CurrentUser = Server.GetUserByUsername(Username);
							
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
							
							if (CurrentGame != null && CurrentUser != null) {
								
								if (CurrentGame.GetPlayer2() == null || CurrentGame.GetPlayer2().GetUsername().equals(CurrentUser.GetUsername())) {
									
									Result = this.Server.SetGamePlayer(CurrentGame, CurrentUser, true);
									
								}
								
							}
							
							Response = new JSONObject();
							
							Response.put("Result", Result);
							
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


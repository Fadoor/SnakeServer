import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
			
			(new ClientServerConnection(Server)).start(); //Starter en tråd
		
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String InputLine;
			String OutputLine;
			
			while ((InputLine = in.readLine()) != null) {
				
				try {
					
					JSONObject Received = new JSONObject(InputLine);
					
					String Method = Received.getString("Method");
						
					switch (Method) {
					
						case "Login":
							
							String Username = Received.getString("Username");
							String Password = Received.getString("Password");
							
							Boolean Result = Server.LogIn(Username, Password);
							
							JSONObject Response = new JSONObject();
							
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


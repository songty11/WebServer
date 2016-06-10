import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.RunnableFuture;

public class WebServer implements Runnable
{
	private int portNum;
	WebServer(int portNum)
	{
		this.portNum = portNum;
	}
	public void run(){
        this.start(this.portNum);
    }
	public void start(int port) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				try {
					clientSocket.setSoTimeout(1500);
				} catch (SocketException e) {
					e.printStackTrace();
				}
				PrintStream out = new PrintStream(clientSocket.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				boolean connection_status = true;
				while (true) {
					try {
						String inputLine;
						StringBuilder sb = new StringBuilder();
						while ((inputLine = in.readLine()) != null) {
							sb.append(inputLine);
							//end of the input stream
							if (inputLine.startsWith("Connection")) {
								if (inputLine.split(" ")[1].equals("close") || inputLine.split(" ")[1].equals("Close")) {
									connection_status = false;
								}
							}
							if (inputLine.equals(""))
								break;
							sb.append("\n");
						}
						clientSocket.setKeepAlive(connection_status);
						if(sb!=null &&sb.length()>0) {
							String inputRequest = sb.toString();
							System.out.print(inputRequest);
							System.out.println("");
							Response res = new Response(inputRequest, out, clientSocket);
							res.give_response();
						}
						else
							break;
						if (!clientSocket.getKeepAlive()) {
							System.out.println("Closed!");
							clientSocket.close();
							break;
						}
					}
					catch (IOException e) {
						System.out.println(e.getMessage());
						clientSocket.close();
						break;
					}
				}
			}
		}
		catch(IOException e) {
			System.out.println("Problem while listening to port number " + port);
			System.out.println(e.getMessage());
		}
	}

}
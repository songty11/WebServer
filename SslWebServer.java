import java.net.*;
import java.io.*;
import java.security.*;
import javax.net.ssl.*;
import java.security.cert.CertificateException;

public class SslWebServer implements Runnable
{
	private static String SERVER_KEY_STORE = "server.jks";
	private int portNum;
	SslWebServer(int portNum)
	{
		this.portNum = portNum;
	}
	public void run(){
        this.start(this.portNum);
    }
	public void start(int port) {
		try{
			System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
			System.setProperty("sun.security.ssl.allowLegacyHelloMessages", "true");
			char[] passwd = "s5468279130".toCharArray();
			SSLContext context = SSLContext.getInstance("TLS");
			KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(SERVER_KEY_STORE),passwd);
			kf.init(ks, passwd);
			context.init(kf.getKeyManagers(), null, null);  
			SSLServerSocketFactory factory = context.getServerSocketFactory();  
	    	SSLServerSocket _socket = (SSLServerSocket)factory.createServerSocket(port);
	    	((SSLServerSocket) _socket).setNeedClientAuth(false);
	    	while(true) {
				SSLSocket clientSocket = (SSLSocket) _socket.accept();
				try{
                    clientSocket.setSoTimeout(1500);
                }
                catch(SocketException e) {
                    e.printStackTrace();
                }
				PrintStream out = new PrintStream(clientSocket.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while(true) {
					try {
						boolean connection_status = true;
						String inputLine;
						StringBuilder sb = new StringBuilder();
						while ((inputLine = in.readLine()) != null) {
							sb.append(inputLine);
							if (inputLine.startsWith("Connection")) {
								if (inputLine.split(" ")[1].equals("close") || inputLine.split(" ")[1].equals("Close")) {
									connection_status = false;
								}
							}
							//end of the input stream
							if (inputLine.equals(""))
								break;
							sb.append("\n");
						}
						clientSocket.setKeepAlive(connection_status);
						if(sb!=null&&sb.length()>0) {
							String inputRequest = sb.toString();
							System.out.print(inputRequest);
							System.out.println("");
							Response res = new Response(inputRequest, out, clientSocket);
							res.give_response();
						}
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
	  	catch (IOException e) {
	    	System.out.println(e.getMessage());
	  	}  
	  	catch(NoSuchAlgorithmException e){
            System.out.println(e.getMessage());
        }
	  	catch(KeyManagementException e)
        {
            System.out.println(e.getMessage());
        }  
        catch(KeyStoreException e){
            System.out.println(e.getMessage());
        }
        catch(CertificateException e){
            System.out.println(e.getMessage());
        }
        catch(UnrecoverableKeyException e){
            System.out.println(e.getMessage());
        }
                                   
	}
}
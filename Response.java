import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Response 
{

	String inputRequest;
	PrintStream out;
	Socket clientSocket;
	Response(String inputRequest, PrintStream out,Socket clientSocket) {
		this.inputRequest = inputRequest;
		this.out = out;
		this.clientSocket = clientSocket;
	}

	public void give_response() {
		//split by new line
		String firstLine = inputRequest.split("\\r?\\n")[0];
		System.out.println(firstLine);
		//split by space
		String[] requestArray = firstLine.split("\\s+");
		try{
			String requestMethod = requestArray[0];
			if (!requestMethod.equals("GET") && !requestMethod.equals("HEAD")) {
			  throw new Exception();
			}
			//example: /redirect.defs, /images/uchicago/logo.png, /foo/bar.html
			String path = "www" + requestArray[1];
			String file_name = requestArray[1].split("\\/")[requestArray[1].split("\\/").length-1];
			String http_protocol = requestArray[2];
			if(file_name.equals("redirect.defs")) {
				out.print(http_protocol+" 404 Not Found\r\n\r\n404 Not Found\r\n\r\n");
				out.flush();
				return ;
			}
			//reading redirect.defs file
			String redirect_URL = is_Redirect(requestArray[1]);
			if (redirect_URL!=null) {
				out.print(http_protocol+" 301 Move Permanently\r\n");
				out.print("Location: ");
				out.print(redirect_URL);
				out.print("\r\n");
				out.print("Connection: keep-alive");
				out.print("\r\n\r\n");
				System.out.print(http_protocol+" 301 Move Permanently\r\n");
				System.out.print("Location: ");
				System.out.print(redirect_URL);
				System.out.print("\r\n");
				System.out.print("Content-Length:");
				System.out.print("\r\n");
				System.out.print("Connection: keep-alive");
				System.out.print("\r\n\r\n");
				out.flush();
				return ;
			}
			else {
				try{
					File file = new File(path);
				    InputStream f = new FileInputStream(path);
					out.print(http_protocol + " 200\r\n");
					out.print("Content-Type:");
					String[] separated_Path = path.split("\\.");
					String ext = separated_Path[separated_Path.length-1];
					System.out.println(path);
					switch (ext) {
					case "html":
						out.print("text/html");
						break;
					case "txt":
						out.print("text/txt");
						break;
					case "jpg":
						out.print("image/jpg");
						break;
					case "png":
						out.print("image/png");
					 	break;
					case "pdf":
						out.print("application/pdf");
						break;
					default:
						throw new FileNotFoundException();
					}
					out.print("\r\n");
					out.print("Connection: keep-alive");
					out.print("\r\n");
					out.print("Content-Length: "+ file.length());
					System.out.println("Connection: keep-alive");
					System.out.println("Content-Length: "+ file.length());
					out.print("\r\n\r\n");
					if (requestMethod.equals("GET")) {
						byte[] buffer = new byte[4096];
				        int n;
				        while ((n=f.read(buffer))>0)
				        	out.write(buffer, 0, n);
					}
					out.flush();
					return ;
				}
				catch (FileNotFoundException x) {
					out.print(http_protocol+" 404 Not Found\r\n\r\n404 Not Found\r\n\r\n");
					out.flush();
				}
			}
		}
		catch(Exception e) {
			out.print("HTTP/1.1 403 Forbidden\r\n\r\nForbidden\r\n\r\n");
			out.flush();
		}
	}
	private static String is_Redirect(String path) {
		try {
			File defs = new File("www/redirect.defs");
			Scanner scanner = new Scanner(defs);
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.split(" ")[0].equals(path)) {
					return line.split(" ")[1];
				}
			}
		} catch (IOException e) {
			return null;
		}
		return null;
	}

}


public class Server {
    public static void main(String[] args) {
       if (args.length != 2) {
           System.err.println("usage: java Server --port=12345 --sslport=12346");
           System.exit(1);
       }
       String[] port_string = args[0].split("=");
       String[] sslport_string = args[1].split("=");

       if ((port_string.length == 2) && (port_string[0].equals("--port")) &&(sslport_string.length==2) && (sslport_string[0].equals("--sslport")))
       {

           int port = Integer.parseInt(port_string[1]);
           int sslport = Integer.parseInt(sslport_string[1]);
            new Thread(new WebServer(port)).start();
            new Thread(new SslWebServer(sslport)).start();
       }
       else
       {
           System.err.println("usage: java Server --port=12345 --sslport=12346");
           System.exit(0);
       }

    }
}

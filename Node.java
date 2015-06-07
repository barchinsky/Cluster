import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.*;
import java.lang.*;
import java.util.concurrent.*;
import java.util.Collections;

class Message{
	private int _msg;

	Message(int msg){
		_msg = msg;
	}

	public int getData(){
		return _msg;
	}
}

class Client implements Runnable{
	private String _host;
	private int _port;
	private Message _msg;
	private Exchanger<Message> _clSrvEx;


	Client(String host, int port,Exchanger<Message> clSrvEx){
		System.out.println("Client()");
		_host = host;
		_port = port;
		_msg = new Message(2);
		_clSrvEx = clSrvEx;
	}

	public void send(Message msg){
		System.out.println("Client::send()");
		try{
			// open socket
			Socket socket = new Socket(_host, _port);

			BufferedReader fromServerMsg = new BufferedReader(
					new InputStreamReader( socket.getInputStream()));
			
			// send Message to server
			PrintWriter toServerMsg = new PrintWriter( socket.getOutputStream());
			toServerMsg.println( Integer.toString( msg.getData() ) ); 
			toServerMsg.flush(); 
			String response;
			while ((response = fromServerMsg.readLine()) != null) {
				System.out.println("Client: response received:" + response);
				write(response);
			} 
		}
		catch(Exception e){
			System.out.println(e.toString());
		}

		System.out.println("Client::~send()");
	}

	void write(String str){
		System.out.println("Client::write()");
		
		try {
			File file = new File("client1");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(str);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Client::~write()");
	}

	public void run(){
		System.out.println("Client::run()");
		this.send(_msg);
		System.out.println("Client::~run()");
	}
}

class Server implements Runnable{ 
	private String _host;
	private int _port;
	private Exchanger<Message> _clSrvEx;

	Server(int port, Exchanger<Message> clSrvEx){
		System.out.println("Server()");
		_port = port;
		clSrvEx = _clSrvEx;
	}

	public void run(){ 
		System.out.println("Server::run()");
		try {
			ServerSocket listen = new ServerSocket(_port); 
			System.out.println("Server started on port "+_port+"\nPress ctr+C to stop server.\n");
			while (true) {
				System.out.println( "Connection waiting ...");
				Socket socket = listen.accept();
				// чекати клієнта, створити вхідний і вихідний
				// потоки для обміну даними з клієнтом
				BufferedReader fromClientMsg = new BufferedReader( 
						new InputStreamReader( socket.getInputStream() ) );
				PrintWriter toClientMsg = new PrintWriter( socket.getOutputStream() );
				
				// read msg from client
				String buf = fromClientMsg.readLine();
				// читати рядки файлу і відправляти їх клієнту 
				System.out.println( "Server: Data received:" + buf);
				toClientMsg.println("Server:ok!");
				break;
			}
		}
		catch (Exception e) { // повідомляти про будь-яке виключення
			System.err.println(e.toString()); 
		}

		System.out.println("Server::~run()");
	}
}

public class Node{
	public static void main(String args[]){
		System.out.println("Node()");

		try{

			if(args.length != 2){
				System.out.println("Usage:\n java Node host port\n");
				return;
			}

			String host = args[0];
			int port = Integer.parseInt(args[1]);

			System.out.println("Node: host="+host);
			System.out.println("Node: port="+Integer.toString(port));

			Exchanger<Message> clSrvEx;
			Client cl = new Client(host,port,clSrvEx);
			Server srv = new Server(port,clSrvEx);

			new Thread(srv).start();
			new Thread(cl).start();
		}
		catch(Exception e){
			System.out.println("Node: Exception caught.");
		}

		System.out.println("~Node()");
	}
}
package sockets;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.FileHandler;

/*
	Sender will send part of the data to another node.
*/
public class Sender implements Runnable{
	private String _host;
	private int _port;
	private Exchanger<String> _msgEx;
	private String _msg;
	private Exchanger<String> _senderToMainEx;
	Logger _logger;
	SimpleFormatter _formatter;
	FileHandler _fh;


	public Sender(String host, int port,Exchanger<String> senderToMainEx,Exchanger<String> msgEx,String id){
		System.out.println("Sender()");

		try{

			_senderToMainEx = senderToMainEx; // send data to main Thread
			_port = port;
			_host = host;
			_msgEx = msgEx;
			_logger = Logger.getLogger("Sender"+id);
			_fh = new FileHandler(id+".log");
			_logger.addHandler(_fh);
			_formatter = new SimpleFormatter();
			_fh.setFormatter(_formatter); 
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		System.out.println("~Sender()");
	}

	public void send(){
		System.out.println("Sender::send()");
		try{
			// open socket
			Socket socket = new Socket(_host, _port);

			
			
			// Process will wait untill input data received from exchanger
			// System.out.println("Sender: waiting for input msg...");

			_msg = _msgEx.exchange(null); // get input data to send

			System.out.println("Sender: msg to send:"+_msg);
			_logger.info("Sender: msg to send:"+_msg);
			
			// Send data to right node
			PrintWriter toServerMsg = new PrintWriter( socket.getOutputStream());
			BufferedReader fromServerMsg = new BufferedReader(
					new InputStreamReader( socket.getInputStream() ) );

			toServerMsg.println( _msg ); 
			toServerMsg.flush(); 

			// System.out.println("Message sent.");

			String response = "";
			String buf;

			while ( (buf = fromServerMsg.readLine() ) != null) {
				// System.out.println("w");
				response = buf;
				System.out.println("Sender: response received:" + response);
				_logger.info("Sender: response received:" + response);
			}
			// System.out.println("Sender: response saved:");
			System.out.println("Sender: response saved:" + response);
			_logger.info("Sender: response saved:" + response);
			// Exchange result from  right node with main process
			_senderToMainEx.exchange(response);
		}
		catch(Exception e){
			System.out.println(e.toString());
		}

		System.out.println("Sender::~send()");
	}

	public void run(){
		System.out.println("Sender::run()");
		System.out.println("Sender:: send to: port ="+_port + ", host="+_host);

		this.send();

		System.exit(1);

		System.out.println("Sender::~run()");
	}
}

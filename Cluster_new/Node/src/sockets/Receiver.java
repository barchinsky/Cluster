package sockets;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
/*
	Receiver receive data sent by another node.
*/
public class Receiver implements Runnable{ 
	private String _host;
	private int _port;
	private Exchanger<String> _receiverToMainEx;
	private Exchanger<String> _mainToReceiverEx;

	public Receiver(int port, Exchanger<String> receiverToMainEx, Exchanger<String> mainToReceiverEx){
//		System.out.println("Receiver()");
		_port = port;
		_receiverToMainEx = receiverToMainEx; // Exchanger beetween main server process
		_mainToReceiverEx = mainToReceiverEx;
	}

	public void listen(){
//		System.out.println("Receiver::listen()");
		try {
                    ServerSocket listen = new ServerSocket(_port); 
//                    System.out.println("Receiver started on port "+_port+"\nPress ctr+C to stop Receiver.\n");
                    while (true) {
                            System.out.println( "Connection waiting ...");
                            Socket socket = listen.accept();
                            // чекати клієнта, створити вхідний і вихідний
                            // потоки для обміну даними з клієнтом
                            BufferedReader fromClientMsg = new BufferedReader( 
                                            new InputStreamReader( socket.getInputStream() ) );
                            // read msg from client
                            String inputMsg = fromClientMsg.readLine();
//                            System.out.println( "Receiver: Data received:" + inputMsg);

                            // send data to processing
                            _receiverToMainEx.exchange(inputMsg);

                            // Get calculation results from calculation engine
                            String out = _mainToReceiverEx.exchange(null);

//                            System.out.println("Receiver:: send to client:"+out);
                            
                            PrintWriter toClientMsg = new PrintWriter( socket.getOutputStream() );
                            toClientMsg.println(out);
                            toClientMsg.close();
                            // break;
			}
		}
		catch (Exception e) { // повідомляти про будь-яке виключення
			System.err.println(e.toString()); 
		}

//		System.out.println("Receiver::~listen()");
	}

	public void run(){ 
//		System.out.println("Receiver::run()");
		
		this.listen();

//		System.out.println("Receiver::~run()");
	}
}
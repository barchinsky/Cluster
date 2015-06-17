import java.util.ArrayList;
import java.util.*;
import java.lang.*;
import java.util.Collections;
import java.util.concurrent.*;
// import sockets.Receiver;
import sockets.Sender;



/*
	InputNode provide  data input and exchange beetween another nodes
	InputNode send 1/4 data to right/left node and 2/4 to left/right node
	Other 1/4 part of data calculate byself
	Waits for response from right/left node and send results to left/right node
*/

public class InputNode{
	public static void main(String args[]){
		System.out.println("InputNode()");

		try{

			if(args.length != 2){
				System.out.println("Usage:\n java InputNode leftNodePort rightNodePort\n");
				return;
			}

			// String host = args[0];
			// int recvPort = Integer.parseInt( args[0] );
			int leftNodePort = Integer.parseInt( args[0] );
			int rightNodePort = Integer.parseInt( args[1] );

			String host = "localhost";
			// // int recvPort = 51001;
			// // int leftNodePort = 51000;
			// // int rightNodePort = 51002;

			// System.out.println("Node: host="+host);
			// System.out.println("Node: port="+Integer.toString(port));

			Exchanger<String> senderToMainEx = new Exchanger<String>(); // keep data received from neighbour nodes after operation finished
			Exchanger<String> rightResultEx = new Exchanger<String>(); // keep  calculation results from right node
			Exchanger<String> rightDataEx = new Exchanger<String>(); // input data for right node
			Exchanger<String> bufEx = new Exchanger<String>();
			Exchanger<String> resultEx = new Exchanger<String>();

			Sender senderRight = new Sender(host, rightNodePort, rightResultEx, rightDataEx,"0");
			// Sender taskToLeftNodeSender = new Sender(host, leftNodePort, senderToMainEx);
			Sender resultToLeftNodeSender = new Sender(host, leftNodePort, bufEx ,resultEx,"1"); // send catenated data to OutputNode

			new Thread(senderRight).start();
			new Thread(resultToLeftNodeSender).start();
			// new Thread(senderLeft).start();

			// Exchange input data to sender
			rightDataEx.exchange("Raw data from InputNode.");
			
			String rightNodeProcessedData = rightResultEx.exchange(null);
			System.out.println("rightNodeProcessedData="+rightNodeProcessedData);

			// Catenate inputNode and Right node results
			//...
			// End

			resultEx.exchange(rightNodeProcessedData); // trigger data sending to OutputNode

		}
		catch(Exception e){
			System.out.println("Node: Exception caught."+e.toString());
		}

		System.out.println("~InputNode()");
	}
}
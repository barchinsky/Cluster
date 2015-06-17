import java.util.ArrayList;
import java.util.*;
import java.lang.*;
import java.util.Collections;
import java.util.concurrent.*;
import sockets.*;

/*
	CalculationNode perform calculation on data.
	Send result data back to sender.
*/

public class CalculationNode{
	public static void main(String args[]){
		System.out.println("Node()");

		try{

			if(args.length != 1){
				System.out.println("Usage:\n java CalculationNode port\n");
				return;
			}

			// String host = args[0];
			int recvPort = Integer.parseInt( args[0] );
			System.out.println("Node: recvPort="+Integer.toString(recvPort));

			Exchanger<String> receiverToMainEx = new Exchanger<String>();
			Exchanger<String> mainToReceiverEx = new Exchanger<String>();

			Receiver rcvr = new Receiver(recvPort, receiverToMainEx, mainToReceiverEx);

			new Thread(rcvr).start();

			while(true){
				String inputMsg = receiverToMainEx.exchange(null);

				// SOME CALCULATIONS START
				// ...
				
				System.out.println("CalculationNode:: Input data:"+inputMsg);
				String result = inputMsg+"Processed.";
				
				// SOME CALCULATIONS END

				mainToReceiverEx.exchange(result);
			}
		}
		catch(Exception e){
			System.out.println("CalculationNode: Exception caught."+e.toString());
		}

		System.out.println("~CalculationNode()");
	}
}
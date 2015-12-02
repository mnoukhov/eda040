package skeleton.client;

import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		if (args.length!=4) {
			System.out.println("Syntax: JPEGHTTPClient <address1> <port1> <address2> <port2>");
			System.exit(1);
		}
        String[] addressPort1 = Arrays.copyOfRange(args,0,2);
        String[] addressPort2 = Arrays.copyOfRange(args, 3, 4);

        new Client(addressPort1, addressPort2);
	}
}

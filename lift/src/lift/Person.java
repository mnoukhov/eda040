package lift;

import java.util.Math;
import se.lth.cs.realtime.RTError;

class Person extends Thread {
	int startFloor;
	int exitFloor;

	Person(int totalFloors, Lift lift) {
		super();
		startFloor = (int) Math.random()*totalFloors
		endFloor = (startFloor + 1 + (int)Math.random()*(totalFloors - 1)) % totalFloors
	}

	public void run() {
		
	}
}
			

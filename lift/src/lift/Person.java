package lift;

import java.util.Math;
import se.lth.cs.realtime.RTError;
import lift.Shared;

class Person extends Thread {
	int startFloor;
	int exitFloor;
	Shared shared;

	Person(int totalFloors, Shared shared) {
		super();
		startFloor = (int) Math.random()*totalFloors
		exitFloor = (startFloor + 1 + (int)Math.random()*(totalFloors - 1)) % totalFloors
		this.shared = shared
	}

	public void run() {
		shared.requestFloor(startFloor)
		try {
			while (shared.getFloor() != startFloor
					|| !(shared.isSpace())) wait();
		} catch (InterruptedException e) {
			throw new RTError("Person interrupted: " + e);
		}

		shared.enterLift(exitFloor);
		
		try {
			while (shared.getFloor() != exitFloor) wait();
        } catch (InterruptedException e) {
            throw new RTError("Person interrupted: " + e);
        }

		shared.exitLift();
	}
}
			

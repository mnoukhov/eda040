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
		int delay = 1000*((int)(Math.random()*46.0))

		sleep(delay);

		shared.requestFloor(startFloor)

		//TODO: don't get on lift if going wrong direction
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
			

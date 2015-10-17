package lift;

import se.lth.cs.realtime.RTError;

import java.lang.Math;

class Person extends Thread {
	int startFloor;
	int exitFloor;
	Shared shared;

	Person(Shared shared, int totalFloors) {
		super();
		startFloor = (int) (Math.random()*totalFloors);
		exitFloor = (startFloor + 1 + (int)(Math.random()*(totalFloors - 1))) % totalFloors;
		this.shared = shared;
        System.out.println("Person created");
	}

	public void run() {
		int delay = 1000*((int)(Math.random()*4.0));

		try {
			sleep(delay);
		} catch (InterruptedException e) {
			throw new RTError("Person sleep interrupted " + e);
		}

		shared.takeLift(startFloor, exitFloor);
//		shared.requestFloor(startFloor);
//        shared.waitToEnter(startFloor);
//		shared.enterLift(exitFloor);
//		shared.waitToExit(exitFloor);
//		shared.exitLift();
	}
}
			

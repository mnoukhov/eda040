package lift;

import se.lth.cs.realtime.RTError;

class Lift extends Thread {
	Shared shared;
    LiftView lv;

	Lift (Shared shared, LiftView lv) {
		super();
		this.shared = shared;
        this.lv = lv;
	}

	public void run() {
        int currentFloor = 0;
        int nextFloor;
		while (true) {
			shared.waitToMoveLift();
			shared.chooseDestinationFloor();
			nextFloor = shared.moveToNext();
            lv.moveLift(currentFloor, nextFloor);
		}
	}
}


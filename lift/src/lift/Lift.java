package lift;

import se.lth.cs.realtime.RTError;
import lift.Shared;

class Lift extends Thread {
	Shared shared;

	Lift (Shared s) {
		super();
		shared = s;	
	}

	public void run() {
		while (true) {
			try {
				while (shared.isEntering() 
						|| shared.isExiting() 
						|| shared.isEmpty()) {
					wait();
				}
			} catch (InterruptedException e) {
				throw new RTError("Lift interrupted: " + e);
			}
			shared.getNextFloor();
			shared.moveToNext();
		}
	}
}


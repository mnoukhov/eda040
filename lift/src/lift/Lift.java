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
			shared.chooseNextFloor();
			shared.moveToNext();
			lv.moveLift(shared.getFloor(), shared.getNextFloor());
		}
	}
}


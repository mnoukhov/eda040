/**
 * TODO 
 * protect requestFloor from single person requesting multiple times
 * double check that waitEntry/Exit no race condition between methods
 * 
 * MAYBEDO
 * move liftView methods into Person + Lift so they are non-blocking
 * make lift stop before reaching top (modify getNextFloor)
 * make exit/enterLift a method that returns boolean of whether the person succeeded, use that to determine whether they should keep waiting
 */

package lift;

import lift.LiftView;

class Shared {
	int maxCapacity;
	int totalFloors;
	int currentFloor = 0; 		
	int nextFloor = 0;			
	int direction = 1; 	// 1 for up, -1 for down
	int load = 0;		// number of people in elevator
	int[] waitEntry;	// how many people on a floor waiting for the elevator
	int[] waitExit;		// how many people want to get off at the floor
	LiftView lv;		// used to draw the lift

	Shared(int totalFloors, int maxCapacity, LiftView lv) {
		this.totalFloors = totalFloors;
		this.maxCapacity = maxCapacity;
		this.lv = lv;
		waitEntry = new int[totalFloors];
		waitExit = new int[totalFloors];

		// initialize waitExit/Entry to 0
		for (int i; i < totalFloors; i++) {
			waitEntry[i] = 0;
			waitExit[i] = 0;
		}
	}

	synchronized int getFloor() { return currentFloor; }

	synchronized void requestFloor(int startFloor) {
		waitEntry[startFloor] += 1;	
	}

	synchronized void enterLift(int exitFloor) {
		waitExit[exitFloor] += 1;
		load += 1;
		lv.drawLift(currentFloor, load);
		lv.drawLevel(currentFloor, waitEntry[currentFloor]);
	}

	synchronized void exitLift() {
		waitEntry[currentFloor] -= 1;
		waitExit[currentFloor] -= 1;
		load -= 1;
		lv.drawLift(currentFloor, load);
		lv.drawLevel(currentFloor, waitEntry[currentFloor]);
		notify();
	}

	synchronized boolean isSpace() {
		return load < maxCapacity;
	}

	synchronized boolean isEntering() {
		return load < maxCapacity && (waitEntry[currentFloor] > 0);
	}

	synchronized boolean isExiting() {
		return waitExit[currentFloor] > 0;
	}

	synchronized boolean isEmpty() {
		// no one in elevator
		// no one waiting
		for (int i : waitEntry) {
			if (i != 0) {
				return false;
			}
		}

		for (int i : waitExit) {
			if (i != 0) {
				return false;
			}
		}
		return true;
	}

	synchronized void moveToNext() {
		int oldFloor = currentFloor;
		currentFloor += direction;
		lv.moveLift(oldFloor, currentFloor);
		notifyAll();
	}

	synchronized void getNextFloor() {
		if (direction == 1) {
			if (currentFloor < totalFloors - 1) {
				nextFloor = totalFloors - 1;
			} else {
				nextFloor = 0;
			}
		} else {
			if (currentFloor > 0) {
				nextFloor = 0;
			} else {
				nextFloor = totalFloors - 1;
			}
		}
	}
}

/**
 * TODO
 * initialize waitExit/Entry to 0 in constructor
 * protect requestFloor from single person requesting multiple times
 * double check that waitEntry/Exit no race condition between methods
 * 
 * MAYBEDO
 * make lift stop before reaching top (modify getNextFloor)
 * make exit/enterLift a method that returns boolean of whether the person succeeded, use that to determine whether they should keep waiting
 */

package lift;

import se.lth.cs.realtime.RTError;

final static int MAX_CAPACITY;
final static int TOTAL_FLOORS;

class Shared {
	int currentFloor = 0; 		
	int nextFloor = 0;			
	int direction = 1; 	// 1 for up, -1 for down
	int load = 0;		// number of people in elevator
	int[] waitEntry;	// how many people on a floor waiting for the elevator
	int[] waitExit;		// how many people want to get off at the floor

	Shared(int totalFloors, int maxCapacity) {
		MAX_CAPACITY = max_capacity;
		TOTAL_FLOORS = total_floors;
		waitEntry = new boolean[totalFloors];
		waitExit = new int[totalFloors];
		// initialize waitExit/Entry to 0
	}

	synchronized int getFloor() { return currentFloor; }

	synchronized int getNextFloor() { return nextFloor; }

	synchronized int getCapacity() { return capacity; }

	synchronized void requestFloor(int startFloor) {
		waitEntry[startFloor] += 1;	
	}

	synchronized void enterLift(int exitFloor) {
		waitExit[exitFloor] += 1;
		load += 1
	}

	synchronized void exitLift() {
		waitEntry[currentFloor] -= 1;
		waitExit[currentFloor] -= 1;
		load -= 1;
		notify();
	}

	synchronized boolean isSpace() {
		return load < MAX_CAPACITY;
	}

	synchronized boolean isEntering() {
		return load < MAX_CAPACITY && (waitEntry[currentFloor] > 0);
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
		currentFloor += direction;
		notifyAll();
	}

	synchronized void getNextFloor() {
		if (direction == 1) {
			if (currentFloor < TOTAL_FLOORS - 1) {
				nextFloor = TOTAL_FLOORS - 1;
			} else {
				nextFloor = 0;
			}
		} else {
			if (currentFloor > 0) {
				nextFloor = 0;
			} else {
				nextFloor = TOTAL_FLOORS - 1;
			}
		}
	}
}

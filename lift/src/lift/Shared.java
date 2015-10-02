/**
 * TODO
 * initialize waitExit/Entry to 0 in constructor
 * protect requestFloor from single person requesting multiple times
 * double check that waitEntry/Exit no race condition between methods
 */

package lift;

import se.lth.cs.realtime.RTError;

final static int MAX_CAPACITY;
final static int TOTAL_FLOORS;

class Shared {
	int currentFloor = 0; 		
	int nextFloor = 1;			
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

	synchronized void moveUp() {
		currentFloor += 1;
		currentDirecion = 1;
		notifyAll();
	}

	synchronized void moveDown() {
		currentFloor -= 1;
		currentDirection = 0;
		notifyAll();
	}
}

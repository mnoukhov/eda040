/**
 * TODO 
 * lift move concurrency
 * MAYBEDO
 * protect requestFloor from single person requesting multiple times
 * don't get on lift if going wrong direction
 * make lift stop before reaching top (modify getNextFloor)
 * make exit/enterLift a method that returns boolean of whether the person succeeded, use that to determine whether they should keep waiting
 */

package lift;

import se.lth.cs.realtime.RTError;

class Shared {
	int maxCapacity;
	int totalFloors;
	int currentFloor = 0; 		
	int destinationFloor = 0;
	int direction = 1; 	// 1 for up, -1 for down
	int load = 0;		// number of people in elevator
	int[] waitEntry;	// how many people on a floor waiting for the elevator
	int[] waitExit;		// how many people want to get off at the floor
	LiftView lv;		// used to draw the lift

	Shared(int totalFloors, int maxCapacity, LiftView lv) {
        this.lv = lv;
		this.totalFloors = totalFloors;
		this.maxCapacity = maxCapacity;
		waitEntry = new int[totalFloors];
		waitExit = new int[totalFloors];

		// initialize waitExit/Entry to 0
		for (int i = 0; i < totalFloors; i++) {
			waitEntry[i] = 0;
			waitExit[i] = 0;
		}
	}

    private synchronized boolean isSpace() {
        return load < maxCapacity;
    }

    private synchronized boolean isEntering() {
        return (load < maxCapacity) && (waitEntry[currentFloor] > 0);
    }

    private synchronized boolean isExiting() {
        return waitExit[currentFloor] > 0;
    }

    private synchronized boolean isEmpty() {
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

    // Person
//	synchronized void requestFloor(int startFloor) {
    synchronized void takeLift(int startFloor, int exitFloor) {
		waitEntry[startFloor] += 1;	
		lv.drawLevel(startFloor, waitEntry[startFloor]);
        System.out.println("request");
		notifyAll();
//	}
//
//	synchronized void waitToEnter(int startFloor) {
		try {
			while (currentFloor != startFloor || !isSpace() || isExiting()) {
				wait();
			}
		} catch (InterruptedException e) {
			throw new RTError("Person interrupted: " + e);
		}
//	}
//
//	synchronized void enterLift(int exitFloor) {
        waitEntry[currentFloor] -= 1;
		waitExit[exitFloor] += 1;
		load += 1;
		lv.drawLift(currentFloor, load);
		lv.drawLevel(currentFloor, waitEntry[currentFloor]);
        System.out.println("enter");
        notifyAll();
//	}

//	synchronized void waitToExit (int exitFloor) {
		try {
			while (currentFloor != exitFloor) wait();
		} catch (InterruptedException e) {
			throw new RTError("Person interrupted: " + e);
		}
//	}

//	synchronized void exitLift() {
		waitExit[currentFloor] -= 1;
		load -= 1;
		lv.drawLift(currentFloor, load);
		notifyAll();
	}

    synchronized void waitToMoveLift() {
		notifyAll();
        System.out.println("lift should wait");
        System.out.println("at floor " + currentFloor);
        try {
            while (isEntering() || isExiting() || isEmpty()) {
                System.out.println("lift waiting");
                wait();
            }
        } catch (InterruptedException e) {
            throw new RTError("Lift interrupted: " + e);
        }
        System.out.println("lift stopped waiting");
    }

	synchronized int moveToNext() {
		currentFloor += direction;
        return currentFloor;
	}

	synchronized void chooseDestinationFloor() {
		if (direction == 1) {
			if (currentFloor < totalFloors - 1) {
				destinationFloor = totalFloors - 1;
			} else {
				destinationFloor = 0;
                direction = -1;
			}
		} else {
			if (currentFloor > 0) {
				destinationFloor = 0;
			} else {
				destinationFloor = totalFloors - 1;
                direction = 1;
			}
		}
	}
}

package lift;

import se.lth.cs.realtime.RTError;

final static int MAX_CAPACITY;
final static int TOTAL_FLOORS;

class Shared {
	int currentFloor; 		
	int currentDirection;
	int capacity;			// free number of spaces in elevator
	boolean[] floorRequests;

	Shared(int totalFloors, int maxCapacity) {
		MAX_CAPACITY = max_capacity;
		TOTAL_FLOORS = total_floors;
		floorRequests = new boolean[totalFloors];
	}

	synchronized getFloor() {}

	synchronized getCapacity() {}

	synchronized getDirection() {}

	synchronized enterPerson(Person p) {}

	synchronized exitPerson(Person p) {}

	synchronized requestFloor(int floor) {}
}

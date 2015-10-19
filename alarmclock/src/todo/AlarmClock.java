/*
 * TODO: 
 * alarm setting
 * alarm ringing
 * mutex to stop time from moving when it is being changed
 * move all shared data into its own file
 * 	time/alarm should be accessed via methods get, set, increment
 * 	those methods should be wrapped with a mutex
 * 
 * bonus: 
 * change alarm clock from thread to regular
 * change Calendar to division
 *
 */

package todo;

import java.util.Calendar;

import done.*;
import se.lth.cs.realtime.semaphore.Semaphore;

public class AlarmClock extends Thread {
    Shared shared;
	private static ClockInput	input;
	private static ClockOutput	output;
    private static Semaphore sem;


	public AlarmClock(ClockInput i, ClockOutput o) {
		input = i;
		output = o;
        sem = i.getSemaphoreInstance();
        shared = new Shared();
	}

	// The AlarmClock thread is started by the simulator. No
	// need to start it by yourself, if you do you will get
	// an IllegalThreadStateException. The implementation
	// below is a simple alarm clock thread that beeps upon 
	// each keypress. To be modified in the lab.
	public void run() {
		Controller c = new Controller(shared, input);
		TimeDisplay t = new TimeDisplay(shared, output);
		c.start();
		t.start();
	}
	
	public class Controller extends Thread {	
		Shared s;
        ClockInput input;

		public Controller(Shared s, ClockInput input) {
            this.s = s;
            this.input = input;
		}
		
		public void run() {
			while (true){
                sem.take();
				int choice = input.getChoice();
				if (choice == ClockInput.SET_TIME) {
                    s.setTime(input.getValue());
				} else if (choice == ClockInput.SET_ALARM) {
                    s.setAlarm(input.getValue());
                } else {
                    s.setAlarmOn(input.getAlarmFlag());
                }
                s.setAlarmRingingOff();
			}
		}
	}
	
	public class TimeDisplay extends Thread {
        Shared s;
        ClockOutput output;

		public TimeDisplay(Shared s, ClockOutput o) {
            this.s = s;
            this.output = o;
		}
		
		public void run() {
            s.setTime(currentTime());

			while(true) {
				output.showTime(s.incrementTime());
                s.maybeRingAlarm();

                if (s.isAlarmRinging()) {
                    output.doAlarm();
                }

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {/* Continue termination...*/};
			}
		}
        private int currentTime() {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());

            return c.get(Calendar.HOUR_OF_DAY)*10000 + c.get(Calendar.MINUTE)*100 + c.get(Calendar.SECOND);
        }
	}
}

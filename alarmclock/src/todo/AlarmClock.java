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
import se.lth.cs.realtime.semaphore.MutexSem;

public class AlarmClock extends Thread {

	private static ClockInput	input;
	private static ClockOutput	output;
	private static Semaphore	sem; 
	private int					time;
	private Semaphore			timeMutex;
	private int					alarm;
	private Semaphore			alarmMutex;
	

	public AlarmClock(ClockInput i, ClockOutput o) {
		input = i;
		output = o;
		sem = input.getSemaphoreInstance();
		timeMutex = new MutexSem();
		alarmMutex = new MutexSem();
	}

	// The AlarmClock thread is started by the simulator. No
	// need to start it by yourself, if you do you will get
	// an IllegalThreadStateException. The implementation
	// below is a simple alarm clock thread that beeps upon 
	// each keypress. To be modified in the lab.
	public void run() {
		Controller c = new Controller();
		TimeDisplay t = new TimeDisplay();
		c.start();
		t.start();
	}
	
	public class Controller extends Thread {	
		
		public Controller() {			
		}
		
		public void run() {
			while (true){
				sem.take();
				
				int choice = input.getChoice();
				if (choice == ClockInput.SET_TIME){
					int newTime = input.getValue();
					timeMutex.take();
					time = newTime;
					timeMutex.give();
				}
			}
		}
	}
	
	public class TimeDisplay extends Thread {
				
		public TimeDisplay() {
		}
		
		public void run() {
			timeMutex.take();
			time = setInitalTime();
			timeMutex.give();
			
			while(true) {
				timeMutex.take();
				time = increment(time);
				output.showTime(time);
				timeMutex.give();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {/* Continue termination...*/};
			}
		}
		
		private int setInitalTime() {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			
			return c.get(Calendar.HOUR_OF_DAY)*10000 + c.get(Calendar.MINUTE)*100 + c.get(Calendar.SECOND);	
		}
		
		private int increment(int hhmmss) {
			int seconds = hhmmss % 100;
			hhmmss /= 100;
			int minutes = hhmmss % 100;
			hhmmss /= 100;
			int hours = hhmmss % 100;
			
			seconds += 1;
			if (seconds >= 60) {
				minutes += 1;
				seconds -= 60;
			}
			
			if (minutes >= 60) {
				hours += 1;
				minutes -= 60;
			}
			
			if (hours >= 24) {
				hours -= 24;
			}
			
			return hours*10000 + minutes*100 + seconds;
		}
	}
}

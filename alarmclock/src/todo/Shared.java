package todo;

import se.lth.cs.realtime.semaphore.MutexSem;
import se.lth.cs.realtime.semaphore.Semaphore;

public class Shared {
    int                 time;
    Semaphore           timeMutex;
    int		            alarm;
    Semaphore           alarmMutex;
    boolean             alarmOn;
    Semaphore           alarmOnMutex;
    int                 alarmRinging;
    Semaphore           alarmRingingMutex;


    public Shared() {
        timeMutex = new MutexSem();
        alarmMutex = new MutexSem();
        alarmOnMutex = new MutexSem();
        alarmRingingMutex = new MutexSem();
        alarmOn = false;
        alarmRinging = 0;
    }

    public void setAlarm(int t) {
        alarmMutex.take();
        alarm = t;
        alarmMutex.give();
    }

    public void setAlarmOn(boolean isOn) {
        alarmOnMutex.take();
        alarmOn = isOn;
        alarmOnMutex.give();
    }

    public void maybeRingAlarm() {
        alarmRingingMutex.take();
        alarmOnMutex.take();
        alarmMutex.take();
        timeMutex.take();

        if (alarmRinging > 0) {
           alarmRinging--;
        } else if (alarmOn && time == alarm) {
           alarmRinging = 20;
        }

        timeMutex.give();
        alarmMutex.give();
        alarmOnMutex.give();
        alarmRingingMutex.give();
    }

    public void setAlarmRingingOff() {
        alarmRingingMutex.take();
        alarmRinging = 0;
        alarmRingingMutex.give();
    }

    public boolean isAlarmRinging() {
        boolean ret;

        alarmRingingMutex.take();
        ret = (alarmRinging > 0);
        alarmRingingMutex.give();

        return ret;
    }


    public void setTime(int t) {
        timeMutex.take();
        time = t;
        timeMutex.give();
    }

    public int incrementTime() {
        timeMutex.take();
        time = incrementTime(time);
        timeMutex.give();
        return time;
    }

    private int incrementTime(int hhmmss) {
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

package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;


public class SpinController extends PeriodicThread {
    int mode = SpinEvent.SPIN_OFF;
    int direction = AbstractWashingMachine.SPIN_RIGHT;
    long slowSpinChangeTime = -1;
    AbstractWashingMachine mach;


	public SpinController(AbstractWashingMachine mach, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
        this.mach = mach;
	}

	public void perform() {
	    SpinEvent req = (SpinEvent) this.mailbox.tryFetch();

        if (req != null) {
            mode = req.getMode();
        }

        if (mode == SpinEvent.SPIN_SLOW) {
            long now = System.currentTimeMillis();
            if (slowSpinChangeTime == -1) {
                slowSpinChangeTime = now + 60*getPeriod();
            } else if (now >= slowSpinChangeTime) {
                if (direction == AbstractWashingMachine.SPIN_RIGHT)
                    direction = AbstractWashingMachine.SPIN_LEFT;
                else
                    direction = AbstractWashingMachine.SPIN_RIGHT;

                slowSpinChangeTime = now + 60*getPeriod();
            }
            mach.setSpin(direction);
        } else if (mode == SpinEvent.SPIN_FAST && Double.compare(mach.getWaterLevel(), 0.0) == 0) {
            mach.setSpin(AbstractWashingMachine.SPIN_FAST);
            slowSpinChangeTime = -1;
        } else {
            mach.setSpin(AbstractWashingMachine.SPIN_OFF);
            slowSpinChangeTime = -1;
        }
    }
}

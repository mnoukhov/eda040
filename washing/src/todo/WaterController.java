package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;


public class WaterController extends PeriodicThread {
    int mode = WaterEvent.WATER_IDLE;
    double goalLevel;
    AbstractWashingMachine mach;
    RTThread src;

	public WaterController(AbstractWashingMachine mach, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
        this.mach = mach;
	}

	public void perform() {
        WaterEvent req = (WaterEvent) this.mailbox.tryFetch();

        if (req != null) {
            mode = req.getMode();
            goalLevel = req.getLevel();
            src = (RTThread) req.getSource();
        }

        System.out.println("mode: " + mode);
        System.out.println("goalLevel: " + goalLevel);
        System.out.println("level: " + mach.getWaterLevel());

        if (mode == WaterEvent.WATER_DRAIN && Double.compare(mach.getWaterLevel(), 0.0) > 0)  {
            mach.setDrain(true);
            mach.setFill(false);
        } else if (mode == WaterEvent.WATER_FILL && Double.compare(goalLevel, mach.getWaterLevel()) > 0) {
            mach.setDrain(false);
            mach.setFill(true);
        } else {
            if (mode != WaterEvent.WATER_IDLE) {
                AckEvent ack = new AckEvent(this);
                src.putEvent(ack);
                mode = WaterEvent.WATER_IDLE;
            }
            mach.setDrain(false);
            mach.setFill(false);
        }
	}
}

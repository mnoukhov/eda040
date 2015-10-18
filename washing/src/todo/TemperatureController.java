package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;
import se.lth.cs.realtime.event.RTEvent;

//TODO: get last message

public class TemperatureController extends PeriodicThread {
    AbstractWashingMachine mach;
    double goalTemp;
    int mode = TemperatureEvent.TEMP_IDLE;
    RTThread src;

	public TemperatureController(AbstractWashingMachine mach, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
        this.mach = mach;
	}

	public void perform() {
        TemperatureEvent req = (TemperatureEvent) this.mailbox.tryFetch();
        if (req != null) {
            goalTemp = req.getTemperature();
            mode = req.getMode();
            src = (RTThread) req.getSource();
        }

        if (mode != TemperatureEvent.TEMP_IDLE
                && Double.compare(mach.getTemperature(), goalTemp) < 0
                && Double.compare(mach.getWaterLevel(), 0.0) > 0) {
            mach.setHeating(true);
        } else {
            if (mode == TemperatureEvent.TEMP_SET) {
                AckEvent ack = new AckEvent(this);
                src.putEvent(ack);
                mode = TemperatureEvent.TEMP_KEEP;
            }
            mach.setHeating(false);
        }
	}
}

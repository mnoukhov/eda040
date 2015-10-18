package todo;

import done.*;

public class WashingController implements ButtonListener {
    WashingProgram1 wp1;
    WashingProgram2 wp2;
	WashingProgram3 wp3;
    TemperatureController tc;
    WaterController wc;
    SpinController sc;
    WashingProgram current;

    public WashingController(AbstractWashingMachine theMachine, double theSpeed) {
        tc = new TemperatureController(theMachine, theSpeed);
        wc = new WaterController(theMachine, theSpeed);
        sc = new SpinController(theMachine, theSpeed);
        wp1 = new WashingProgram1(theMachine, theSpeed, tc, wc, sc);
        wp2 = new WashingProgram2(theMachine, theSpeed, tc, wc, sc);
        wp3 = new WashingProgram3(theMachine, theSpeed, tc, wc, sc);
        current = null;
    }

    public void processButton(int theButton) {
        if (current == null) {
            tc.start();
            wc.start();
            sc.start();
        } else {
            current.interrupt();
        }

		switch (theButton) {
            case 0: current = null;
                tc.interrupt();
                wc.interrupt();
                sc.interrupt();
                break;
            case 1: current = wp1;
                break;
            case 2: current = wp2;
                break;
            case 3: current = wp3;
                break;
            default:
                break;
        }

        if (current != null) {
            current.start();
        }
    }
}

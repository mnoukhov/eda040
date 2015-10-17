package todo;

import done.*;

public class WashingController implements ButtonListener {
//    WashingProgram1 wp1;
//    WashingProgram2 wp2;
	WashingProgram3 wp3;
    TemperatureController tc;
    WaterController wc;
    SpinController sc;

    public WashingController(AbstractWashingMachine theMachine, double theSpeed) {
        tc = new TemperatureController(theMachine, theSpeed);
        wc = new WaterController(theMachine, theSpeed);
        sc = new SpinController(theMachine, theSpeed);
//        wp1 = new WashingProgram1(theMachine, theSpeed, tc, wc, sc);
//        wp2 = new WashingProgram2(theMachine, theSpeed, tc, wc, sc);
        wp3 = new WashingProgram3(theMachine, theSpeed, tc, wc, sc);
    }

    public void processButton(int theButton) {
		switch (theButton) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                wp3.start();
                break;
            default:
                break;
        }
    }
}

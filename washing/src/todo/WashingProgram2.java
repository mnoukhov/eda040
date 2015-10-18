/*
 * Real-time and concurrent programming course, laboratory 3
 * Department of Computer Science, Lund Institute of Technology
 *
 * PP 980812 Created
 * PP 990924 Revised
 */

package todo;

import done.AbstractWashingMachine;

/**
 * Program 3 of washing machine. Does the following:
 * <UL>
 *   <LI>Switches off heating
 *   <LI>Switches off spin
 *   <LI>Pumps out water
 *   <LI>Unlocks the hatch.
 * </UL>
 */
class WashingProgram2 extends WashingProgram {

	// ------------------------------------------------------------- CONSTRUCTOR

	/**
	 * @param   mach             The washing machine to control
	 * @param   speed            Simulation speed
	 * @param   tempController   The TemperatureController to use
	 * @param   waterController  The WaterController to use
	 * @param   spinController   The SpinController to use
	 */
	public WashingProgram2(AbstractWashingMachine mach,
                           double speed,
                           TemperatureController tempController,
                           WaterController waterController,
                           SpinController spinController) {
		super(mach, speed, tempController, waterController, spinController);
	}

	// ---------------------------------------------------------- PUBLIC METHODS

	/**
	 * This method contains the actual code for the washing program. Executed
	 * when the start() method is called.
	 */
	protected void wash() throws InterruptedException {
        System.out.println("start 2");

        System.out.println("lock");
        myMachine.setLock(true);

        System.out.println("fill");
        myWaterController.putEvent(new WaterEvent(this,
                WaterEvent.WATER_FILL,
                0.95));
        mailbox.doFetch(); // Wait for Ack

        // Switch of temp regulation
        System.out.println("temp");
        myTempController.putEvent(new TemperatureEvent(this,
                TemperatureEvent.TEMP_SET,
                40.0));
        mailbox.doFetch(); // Wait for Ack

        System.out.println("spin");
        mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));
        waitMinutes(15);
        mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));

        // Switch of temp regulation
        System.out.println("temp");
        myTempController.putEvent(new TemperatureEvent(this,
                TemperatureEvent.TEMP_SET,
                90.0));
        mailbox.doFetch(); // Wait for Ack

        waitMinutes(30);

        myTempController.putEvent(new TemperatureEvent(this,
                TemperatureEvent.TEMP_IDLE,
                0.0));

        // Drain
        System.out.println("drain");
        myWaterController.putEvent(new WaterEvent(this,
                WaterEvent.WATER_DRAIN,
                0.0));
        mailbox.doFetch(); // Wait for Ack

        for (int i = 0; i < 5; i++) {
            System.out.println("rinse");
            myWaterController.putEvent(new WaterEvent(this,
                    WaterEvent.WATER_FILL,
                    0.9));
            mailbox.doFetch(); // Wait for Ack
            waitMinutes(2);
            myWaterController.putEvent(new WaterEvent(this,
                    WaterEvent.WATER_DRAIN,
                    0.0));
            mailbox.doFetch(); // Wait for Ack
        }

        System.out.println("spin");
        mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_FAST));
        waitMinutes(5);
        mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));

        // Unlock
        System.out.println("unlock");
        myMachine.setLock(false);

        System.out.println("done");
	}
}

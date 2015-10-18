/*
 * Real-time and concurrent programming course, laboratory 3
 * Department of Computer Science, Lund Institute of Technology
 *
 * PP 980812 Created
 * PP 990924 Revised
 */

package todo;

import done.*;

/**
 * Program 3 of washing machine. Does the following:
 * <UL>
 *   <LI>Switches off heating
 *   <LI>Switches off spin
 *   <LI>Pumps out water
 *   <LI>Unlocks the hatch.
 * </UL>
 */
class WashingProgram3 extends WashingProgram {

	// ------------------------------------------------------------- CONSTRUCTOR

	/**
	 * @param   mach             The washing machine to control
	 * @param   speed            Simulation speed
	 * @param   tempController   The TemperatureController to use
	 * @param   waterController  The WaterController to use
	 * @param   spinController   The SpinController to use
	 */
	public WashingProgram3(AbstractWashingMachine mach,
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
        System.out.println("start");

		// Switch of temp regulation
        System.out.println("temp");
		myTempController.putEvent(new TemperatureEvent(this,
                TemperatureEvent.TEMP_IDLE,
                0.0));


		// Switch off spin
        System.out.println("spin");
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));

		// Drain
        System.out.println("drain");
		myWaterController.putEvent(new WaterEvent(this,
                WaterEvent.WATER_DRAIN,
                0.0));
		mailbox.doFetch(); // Wait for Ack

		// Set water regulation to idle => drain pump stops
        System.out.println("finish drain");
		myWaterController.putEvent(new WaterEvent(this,
                WaterEvent.WATER_IDLE,
                0.0));

		// Unlock
        System.out.println("unlock");
		myMachine.setLock(false);

        System.out.println("done");
	}
}

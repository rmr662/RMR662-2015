package org.usfirst.frc.team662.robot;

// This class's run method is called during autonomous mode.
public class Autonomous {

	static final int AUTONOMOUS_SWITCH = 1;

	static final int LIFT_BIN_SWITCH = 0;
	static final int LIFT_TOTE_SWITCH = 0;

	static Drive d;
	static AbsolutePositioningManipulator m;

	// Given a drive and manipulator object to control
	static void run(Drive dr, AbsolutePositioningManipulator ma) {
		d = dr;
		m = ma;

		/*DigitalInput liftBinSwitch = new DigitalInput(LIFT_BIN_SWITCH);
		DigitalInput liftToteSwitch = new DigitalInput(LIFT_TOTE_SWITCH);*/

		//DigitalInput autonomousSwitch = new DigitalInput(AUTONOMOUS_SWITCH);
		System.out.println("Inside Auto");
		//if (autonomousSwitch.get()) {
		System.out.println("Auto switch pressed");
		liftEither();
		//}

		/*if (liftToteSwitch.get() && liftBinSwitch.get()) {
			liftBoth();
		} else if (liftToteSwitch.get()) {
			liftTote();
		} else if (liftBinSwitch.get()) {
			liftBin();
		}*/

	}

	public static void liftEither() {
		d.moveStraightTime(1., .4);
		m.liftToteAutonomous();
		//m.liftToteAutonomous();
		d.moveStraightTime(2, -.8);
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < 2000) {
			//do nothing
		}
		m.dropToteAutonomous();
		d.moveStraightTime(1., -.25);
	}

	public static void liftBoth() {

		d.moveStraightTime(0.5, .2);
		m.liftToteAutonomous();
		m.liftToteAutonomous();
		d.moveStraightTime(0.5, -.2);
		d.strafe(.2, .2);
		d.moveStraightTime(0.5, .2);
		m.liftToteAutonomous();
		m.liftToteAutonomous();
		d.moveStraightTime(2, .5);

	}

	public static void liftBin() {
		d.moveStraightTime(.5, .2);
		m.liftToteAutonomous();
		m.liftToteAutonomous();
		d.moveStraightTime(.5, .2);
		m.dropToteAutonomous();
		d.moveStraightTime(.5, -.2);
	}

	public static void liftTote() {
		d.moveStraightTime(.5, .2);
		m.liftToteAutonomous();
		d.moveStraightTime(.5, .2);
		m.dropToteAutonomous();
		m.dropToteAutonomous();
		d.moveStraightTime(.5, -.2);
	}

}

package org.usfirst.frc.team662.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;

public class AbsolutePositioningManipulator extends Component {

	final int LIFT_ENCODER_A = 18;
	final int LIFT_ENCODER_B = 19;
	final int BRAKE_PORT_ENG = 0;
	final int BRAKE_PORT_REL = 1;
	final int LIMIT_SWITCH_PORT = 2;
	final int UPPER_SWITCH_PORT = 3;
	final int MOTOR_PORT = 0;
	final int PCM_ID = 1;
	final int LIGHT_PORT = 0;

	final double SLOW_MULT = -.4;
	final double FAST_MULT = -.7;
	final double AUTO_LIFT_SPEED = -.4;
	final double DEAD_ZONE = .2;

	final int[] HOOK_POSITIONS = { 0, 1800, 3600, 5400, 7200, 9000 };
	final int LED_DELAY = 2000;

	NegativeEncoder liftEncoder;
	DigitalInput limitSwitch, upperSwitch;
	Talon liftMotor;
	DoubleSolenoid airBrake;
	Relay lights;
	Joystick xbox;
	Robot bot;

	boolean dropping, lifting, aligning, blinking, startPressed, oldSwitchVal, thruStartBtnCheck, startBlinking, blue;

	long time;

	int desiredVal;
	long upperSwitched;

	public AbsolutePositioningManipulator(Robot robot) {
		bot = robot;
		liftEncoder = new NegativeEncoder(LIFT_ENCODER_A, LIFT_ENCODER_B);
		limitSwitch = new DigitalInput(LIMIT_SWITCH_PORT);
		upperSwitch = new DigitalInput(UPPER_SWITCH_PORT);
		liftMotor = new Talon(MOTOR_PORT);
		airBrake = new DoubleSolenoid(PCM_ID, BRAKE_PORT_ENG, BRAKE_PORT_REL);
		lights = new Relay(LIGHT_PORT, Relay.Direction.kBoth);
		xbox = new Joystick(XboxMap.DRIVE_CONTROLLER);
		oldSwitchVal = false;
		dropping = false;
		lifting = false;
		aligning = false;
		blinking = false;
		startPressed = false;
		thruStartBtnCheck = true;
		startBlinking = false;
		blue = false;

		time = System.currentTimeMillis();
		upperSwitched = System.currentTimeMillis() - 100;

		desiredVal = 0;
	}

	public void update() {
		/*if (thruStartBtnCheck == true && xbox.getRawButton(XboxMap.B)) {
			thruStartBtnCheck = false;
			startBlinking = !startBlinking;
		} else {
			thruStartBtnCheck = true;
		}
		*/
		if (xbox.getRawButton(XboxMap.B)) {
			lights.set(Relay.Value.kReverse);
		} else if (xbox.getRawButton(XboxMap.X)) {
			lights.set(Relay.Value.kOff);
		} else if (xbox.getRawButton(XboxMap.Y)) {
			lights.set(Relay.Value.kForward);
		}
		//lights.set(Relay.Value.kReverse);
		//double liftVal = -xbox.getRawAxis(XboxMap.LEFT_JOY_HORIZ);
		double liftVal = 0;
		if (xbox.getRawButton(XboxMap.RB)){
			liftVal = .5;
		} else if (xbox.getRawButton(XboxMap.LB)){
			liftVal = -.5;
		}
		/*double liftValFoSho = -xbox.getRawAxis(XboxMap.RIGHT_JOY_HORIZ);
		if (Math.abs(liftValFoSho) > DEAD_ZONE) {
			setBrake(false);

			if (xbox.getRawButton(XboxMap.A)) {

				moveMotor(FAST_MULT * liftValFoSho);
			} else {
				moveMotor(SLOW_MULT * liftValFoSho);
			}*/
		    if (Math.abs(liftVal) > DEAD_ZONE && (upperSwitch.get() || liftVal > 0)) {
			setBrake(false);

			if (xbox.getRawButton(XboxMap.A)) {

				moveMotor(FAST_MULT * liftVal);
			} else {
				moveMotor(SLOW_MULT * liftVal);
			}

		} else {
			moveMotor(0);
			setBrake(true);
		}

		/*
				double liftVal = -xbox.getRawAxis(XboxMap.LEFT_JOY_HORIZ);

				System.out.println("Upper switched = " + upperSwitched);
				if (Math.abs(liftVal) > DEAD_ZONE && (-1 * upperSwitched + System.currentTimeMillis() > 300 || liftVal < 0)) {
					setBrake(false);
					if (oldSwitchVal != upperSwitch.get()) {
						System.out.println("Top Switched");
					}
					oldSwitchVal = upperSwitch.get();
					//if ((!upperSwitch.get() && liftVal < 0) || !!upperSwitch.get()) {
					moveMotor(SLOW_MULT * liftVal);
					//}
					lifting = false;
					dropping = false;
					aligning = false;
				} else if (xbox.getRawButton(XboxMap.A) || lifting) {
					setBrake(false);
					//System.out.println("A pressed");
					//lifting = true;
					// This code exists
					dropping = false;
					aligning = false;
					liftTote();
					} else if (xbox.getRawButton(XboxMap.B) || dropping) {
					setBrake(false);
					//System.out.println("B pressed");
					lifting = false;
					//dropping = true;
					// This tote exists in drop tote
					aligning = false;
					dropTote();
					} else if (xbox.getRawButton(XboxMap.RB) || aligning) {
					setBrake(false);
					//System.out.println("RB pressed");
					lifting = false;
					dropping = false;
					//aligning = true;
					// This code exists in align.
					alignTopToBottom();
					} else if (xbox.getRawButton(XboxMap.X)) {
					//System.out.println("X pressed");
					liftEncoder.reset();
					} else if (xbox.getRawButton(XboxMap.Y)) {
					//System.out.println("Y pressed");
					System.out.println(liftEncoder.get());
					System.out.println(!limitSwitch.get());
					System.out.println(!upperSwitch.get());
					} else if (xbox.getRawButton(XboxMap.START)) {
					startPressed = true;
					} else if (!xbox.getRawButton(XboxMap.START) && startPressed) {
					startPressed = false;
					blinking = !blinking;
					}

				else {
					moveMotor(0);
					setBrake(true);
				}
				if (!limitSwitch.get()) {
					liftEncoder.reset();
				}
				if (!upperSwitch.get() && (lifting || xbox.getRawAxis(XboxMap.LEFT_JOY_HORIZ) > 0)) {
					System.out.println("Condition to enter breaking = " + (!upperSwitch.get() && (lifting || xbox.getRawAxis(XboxMap.LEFT_JOY_HORIZ) > 0)));

					System.out.println("switched? = " + !upperSwitch.get());
					upperSwitched = System.currentTimeMillis();
					moveMotor(0);
					setBrake(false);
					lifting = false;
				}
		*/
		/*updateLED(blinking);*/

	}

	public void setBrake(boolean on) {
		if (on) {
			airBrake.set(DoubleSolenoid.Value.kForward);
		} else {
			airBrake.set(DoubleSolenoid.Value.kReverse);
		}
	}

	public void dropTote() {

		// Initial condition to find the value it wants to go to
		if (!dropping) {
			desiredVal = 0;
			int encVal = liftEncoder.get();
			for (int i = 0; i < HOOK_POSITIONS.length - 1; i++) {
				if (encVal > HOOK_POSITIONS[i] && encVal < HOOK_POSITIONS[i + 1]) {
					desiredVal = HOOK_POSITIONS[i];
				}
			}
			dropping = true;
		} else {
			if (liftEncoder.get() < desiredVal) {
				//Stop motor
				moveMotor(0);
				dropping = false;
			} else {
				moveMotor(AUTO_LIFT_SPEED);
				// Move motor
			}
		}

	}

	public void liftTote() {
		setBrake(false);
		if (!lifting) {
			desiredVal = 0;
			int encVal = liftEncoder.get();
			for (int i = 0; i < HOOK_POSITIONS.length - 1; i++) {
				if (encVal > HOOK_POSITIONS[i] && encVal < HOOK_POSITIONS[i + 1]) {
					desiredVal = HOOK_POSITIONS[i + 1];
				}
			}
			System.out.println("Desired value: " + desiredVal);
			lifting = true;
		} else {
			if (liftEncoder.get() > desiredVal) {
				//Stop motor
				moveMotor(0);
				lifting = false;
			} else {
				moveMotor(-1 * AUTO_LIFT_SPEED);
				// Move motor
			}
		}
	}

	public void moveMotor(double speed) {
		liftMotor.set(speed);
	}

	public void alignTopToBottom() {
		if (!limitSwitch.get()) {
			moveMotor(0);
			aligning = false;
		} else {
			if (liftEncoder.get() < 0) {
				moveMotor(AUTO_LIFT_SPEED);
			} else {
				moveMotor(-AUTO_LIFT_SPEED);
			}
		}
	}

	public void liftToteAutonomous() {
		setBrake(false);
		liftEncoder.reset();
		while ((liftEncoder.get() < 800) && bot.isAutonomous()) {
			System.out.println("lift encoder: " + liftEncoder.get());
			moveMotor(-1 * AUTO_LIFT_SPEED);
		}
		moveMotor(0);
	}

	public void dropToteAutonomous() {
		liftEncoder.reset();
		while ((liftEncoder.get() > -900) && bot.isAutonomous()) {
			System.out.println("lift encoder: " + liftEncoder.get());
			moveMotor(AUTO_LIFT_SPEED);
		}
		moveMotor(0);
	}

	public void updateLED() {
		if (System.currentTimeMillis() - time > LED_DELAY) {
			lights.set(Relay.Value.kForward);
			time = System.currentTimeMillis();
		} else {
			lights.set(Relay.Value.kReverse);
		}
	}

}
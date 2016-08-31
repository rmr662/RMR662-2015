package org.usfirst.frc.team662.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

public class Manipulator extends Component {

	//All ports are constants.
	public static final int LIFT_ENCODER_A = 18;
	public static final int LIFT_ENCODER_B = 19;
	public static final int BRAKE_PORT = 0;
	public static final int LIFT_PORT = 0;
	public static final int SWITCH_PORT = 0;

	//Generic constants.
	public static final double BRAKE_VAL = 0.5;
	public static final double DEAD_ZONE = 0.15;
	public static final double LIFT_MULT = (-.4);
	public static final double LIFT_SPEED = 0.5;
	public static final int TICKS_PER_REVOLUTION = -13460;
	public static final double CIRCUMFERENCE = 26 * 12; // Inches, Rough estimate.
	public static final double DISTANCE_PER_TICK = CIRCUMFERENCE / TICKS_PER_REVOLUTION;
	public static final int TARGET_DISTANCE = 10; //Arbitrary filler value.

	//Booleans for various purposes.
	public boolean motorOn;
	public boolean isYPressed;
	public boolean isXPressed;
	public boolean isAPressed;
	public boolean isBPressed;
	public boolean goneThru = false;

	double liftVal;
	double startDist;

	//Hardware components.
	Talon liftMotor;
	Solenoid airBrake;
	Joystick xbox;
	DigitalInput liftSwitch;
	Encoder liftEncoder;
	Joystick.RumbleType RumbleType;

	public Manipulator() {
		xbox = new Joystick(XboxMap.MANIP_CONTROLLER);
		//Motor to control the conveyer belt.
		liftMotor = new Talon(LIFT_PORT);
		airBrake = new Solenoid(BRAKE_PORT);

		isAPressed = false;
		isBPressed = false;
		isXPressed = false;
		isYPressed = false;

		airBrake.set(false);
		//liftVal is value inputed by the left joystick.
		liftVal = 0.0;
		liftMotor.setSafetyEnabled(true);
		liftMotor.setExpiration(.5);
		liftSwitch = new DigitalInput(SWITCH_PORT);
		//motorOn keeps track of whether the lift motor is on or not.
		motorOn = false;
		liftEncoder = new Encoder(LIFT_ENCODER_A, LIFT_ENCODER_B);
		liftEncoder.reset();
	}

	public void liftTote() {
		//Sets the distance per tick.
		liftEncoder.setDistancePerPulse(DISTANCE_PER_TICK);

		//This lets the robot know to stop when it lifts the crate a certain amount in the air.
		if (liftEncoder.getDistance() >= TARGET_DISTANCE + startDist && motorOn) {
			//This stops the motor, setting it to 0.
			liftMotor.set(0.0);
			motorOn = false;
			//airBrake.set(true);
			//Sets the controller to massage your hands when the motor is moving.
			xbox.setRumble(RumbleType.kLeftRumble, 0); //The rumbles are fine.
			xbox.setRumble(RumbleType.kRightRumble, 0);
		}

		//By pressing the 'A' button, it turns the motor on.
		if (!motorOn) {
			motorOn = true;
			startDist = liftEncoder.getDistance();
			//airBrake.set(false);
			//If the robot is lifting a tote, it'll RumbleType the controller.
			liftMotor.set(LIFT_SPEED);
			xbox.setRumble(Joystick.RumbleType.kLeftRumble, 1);
			xbox.setRumble(Joystick.RumbleType.kRightRumble, 1);
		}
	}

	// Drops <toteNumber> totes
	public void dropTote(int toteNumber) {
		//Begins to drops the tote.
		liftMotor.set(-LIFT_SPEED);
		//drops the tote if the tote was moved passed the set distance.
		if (liftEncoder.getDistance() <= -(toteNumber * TARGET_DISTANCE) + startDist) {
			liftMotor.set(0.0);
			// Stops the drop tote method from running again without a button
			isXPressed = false;
			isYPressed = false;
		}
	}

	//prints the number of ticks that have been ticked when 'B' is pressed.
	public void printEncoder() {
		if (xbox.getRawButton(XboxMap.B)) {
			System.out.println(-liftEncoder.get());
		}
	}

	// Supposed to align the top hook to the bottom of the mast
	public void autoMove() {
		//Automatically moves the motor when the limit switch has NOT been reached.
		if (liftSwitch.get()) {
			liftMotor.set(0);
			isBPressed = false;
			liftEncoder.reset();
		} else if (isBPressed) {
			//Move at a constant speed.
			liftMotor.set(LIFT_SPEED);
		}
	}

	// Moves the chain <distance> in inches. Not used right now because we don't trust it.
	/*public void moveChain(int distance) {
		int startLiftVal = 0;
		int startClicks = 0;
		double totalClicks = 0;
		double changeInClicks = (distance * TICKS_PER_REVOLUTION) / 312;
		startLiftVal = -liftEncoder.get();

		//checks for First iteration
		if (goneThru == false) {

			startClicks = -liftEncoder.get();
			totalClicks = startClicks + changeInClicks;
			goneThru = true;
		}

		//checks to see if motor has gone chosen value
		if (totalClicks - startLiftVal == startClicks) {
			totalClicks = startClicks;
			liftMotor.set(0);
			goneThru = false;
			//Every time that someone clicks, when totalclicks changes, the robot goes through first iteration again
		} else {
			liftMotor.set(LIFT_SPEED);
		}

	}*/

	// This method is called in a loop
	public void update() {

		liftVal = xbox.getRawAxis(XboxMap.LEFT_JOY_VERT);
		// This code calls automove, disabled right now
		/*if (xbox.getRawButton(XboxMap.A) || isAPressed) {
			isAPressed = true;
			isXPressed = false;
			isYPressed = false;
			autoMove();
		}*/

		/*
		if (xbox.getRawButton(XboxMap.A)) {
			liftEncoder.reset();
		}
		*/
		//printEncoder();

		// Sees if the joystick has passed the deadzone
		if (Math.abs(liftVal) > DEAD_ZONE) {
			isAPressed = false;
			isBPressed = false;
			isXPressed = false;
			isYPressed = false;
			liftMotor.set(LIFT_MULT * liftVal);
		}//Drops all totes when 'X' is pressed.
		else if (xbox.getRawButton(XboxMap.X) || isXPressed) {
			if (!isXPressed) {
				startDist = liftEncoder.getDistance();
			}
			isXPressed = true;
			isYPressed = false;
			isAPressed = false;
			isBPressed = false;
			dropTote(3);
		} else if (xbox.getRawButton(XboxMap.Y) || isYPressed) {
			if (!isYPressed) {
				startDist = liftEncoder.getDistance();
			}
			isYPressed = true;
			isXPressed = false;
			dropTote(1);
		} else if (xbox.getRawButton(XboxMap.B) || isBPressed) {
			isBPressed = true;
			isYPressed = false;
			isAPressed = false;
			isXPressed = false;
			liftTote();

		} else {
			liftMotor.set(0);
		}

		/* Air brake not 
		if(xbox.getRawAxis(XboxMap.RIGHT_TRIGGER) >= BRAKE_VAL){
			airBrake.set(true); 
		} 
		else{
			airBrake.set(false); 
		}
		 */
	}
}

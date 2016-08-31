package org.usfirst.frc.team662.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;

public class Drive extends Component {
	// Motor Ports
	public static final int FL_PORT = 5;
	public static final int FR_PORT = 3;
	public static final int BL_PORT = 4;
	public static final int BR_PORT = 6;

	// Motor multipliers
	public static final double TL = .66;
	public static final double TR = -.66;
	public static final double BL = .66;
	public static final double BR = -.66;

	// Joystick dead zone
	public static final double DEAD_ZONE = .2;

	// Motor expiration date
	public static final double EXPIRATION_DATE = .5;

	// Wheel constants
	public static final double RADIUS_OF_WHEEL = 3; // inches
	public static final int TICKS_PER_REVOLUTION = 360; // ticks per rotation of
														// encoder

	public static final int[][] ENCODER_PORTS = { { 16, 17 }, // FL
			{ 14, 15 }, // FR
			{ 12, 13 }, // RL
			{ 10, 11 } }; // RR

	// Values for motors
	double rotational, horizontal, forward;

	Robot bot;

	CANTalon fLMotor;
	CANTalon fRMotor;
	CANTalon bLMotor;
	CANTalon bRMotor;

	Joystick stick;
	Encoder[] encoders;

	public Drive(Robot robot) {
		bot = robot;
		System.out.println("Drive constructor");
		fLMotor = new CANTalon(FL_PORT);
		fRMotor = new CANTalon(FR_PORT);
		bLMotor = new CANTalon(BL_PORT);
		bRMotor = new CANTalon(BR_PORT);

		// Enable safety controls on all the motors.
		//		fLMotor.setSafetyEnabled(true);
		//		fRMotor.setSafetyEnabled(true);
		//		bLMotor.setSafetyEnabled(true);
		//		bRMotor.setSafetyEnabled(true);

		/*fLMotor.setExpiration(EXPIRATION_DATE);
		fRMotor.setExpiration(EXPIRATION_DATE);
		bLMotor.setExpiration(EXPIRATION_DATE);
		bRMotor.setExpiration(EXPIRATION_DATE);*/

		encoders = new Encoder[4];

		// Create all encoders and set their distance
		for (int i = 0; i < 4; i++) {
			encoders[i] = new Encoder(ENCODER_PORTS[i][0], ENCODER_PORTS[i][1]);
			// Formula sets distance per pulse to the circumfrence over the ticks in one revolution of the wheels
			encoders[i].setDistancePerPulse((2 * RADIUS_OF_WHEEL * Math.PI) / TICKS_PER_REVOLUTION);
		}

		rotational = 0;
		horizontal = 0;
		forward = 0;

		stick = new Joystick(XboxMap.DRIVE_CONTROLLER);
	}

	public void update() {

		forward = stick.getRawAxis(XboxMap.LEFT_JOY_VERT);
		horizontal = -stick.getRawAxis(XboxMap.LEFT_JOY_HORIZ);
		rotational = -stick.getRawAxis(XboxMap.RIGHT_JOY_HORIZ);

		//Debug information.
		/*System.out.println("Left Vert: " + forward);
		System.out.println("Left Horiz: " + horizontal);
		System.out.println("Right Vert: " + rotational);
		*/
		if (Math.abs(rotational) < DEAD_ZONE) {
			rotational = 0;
		}
		if (Math.abs(horizontal) < DEAD_ZONE) {
			horizontal = 0;
		}
		if (Math.abs(forward) < DEAD_ZONE) {
			forward = 0;
		}

		if (stick.getRawButton(XboxMap.A)) {
			System.out.println("Setting FL: " + (TL * (horizontal + rotational + forward)));
			System.out.println("Setting FR: " + (TR * (horizontal - rotational - forward)));
			System.out.println("Setting BL: " + (BL * (horizontal + rotational - forward)));
			System.out.println("Setting BR: " + (BR * (horizontal - rotational + forward)));
		}

		move(forward, horizontal, rotational);

	}

	public void move(double forward, double horizontal, double rotational) {
		//System.out.println("Move : TL " + TL * (horizontal + rotational + forward));
		fLMotor.set(TL * (horizontal + rotational + forward));
		fRMotor.set(TR * (horizontal - rotational - forward));
		bLMotor.set(BL * (horizontal + rotational - forward));
		bRMotor.set(BR * (horizontal - rotational + forward));

		//System.out.println("Get: fL:fR:bL:bR " + fLMotor.get() + ":" + fRMotor.get() + ":" + bLMotor.get() + ":" + bRMotor.get());
	}

	// Moves robot straight dist inches
	public void moveStraight(int dist) {
		resetEncoders();

		// While loops are ok as long as this is only called from autonomous
		if (dist > 0) {
			while (getDistance() < dist) {
				move(1, 0, 0);
			}
		} else {
			while (getDistance() > dist) {
				move(-1, 0, 0);
			}
		}
		// This is the same code as above, but Will wrote this because he likes it more
		/*
		 * while ((dist < 0 && getDistance() > dist) || (dist > 0 && getDistance() < dist)) {
		 *  	move(0, 0, dist / Math.abs(dist)); 
		 * }
		 */
		// Stops the motors after the distance has been traveled
		move(0, 0, 0);
	}

	public void moveAuto(double speed) {
		//Really the right rear
		fLMotor.set(TL * speed * .5 / .75);
		//left rear
		fRMotor.set(TR * speed * .5 / .75);
		//front right
		bLMotor.set(BL * speed * .5 / .75);
		//front left
		bRMotor.set(BR * speed * .5 / .75);
	}

	public void moveStraightTime(double time, double speed) {
		System.out.println("Entering moveStraightTime: " + time);
		long startTime = System.currentTimeMillis();
		long currTime = 0;

		while ((currTime - startTime) < (time * 1000) && bot.isAutonomous()) {
			//System.out.println("looping " + speed + " " + (currTime - startTime));
			moveAuto(speed);
			currTime = System.currentTimeMillis();
		}
		System.out.println("Setting to 0");
		move(0, 0, 0);
	}

	// This method strafes for <seconds> in the direction and speed of dir. Dir should be between -1 & 1
	public void strafe(double seconds, double dir) {

		long t = System.currentTimeMillis();
		long currTime = 0;

		while (currTime < seconds) {
			move(0, dir, 0);
			currTime = (System.currentTimeMillis() - t) / 1000;
		}

		// Stop it afterward
		move(0, 0, 0);

	}

	// Gets the average distance of all 4 wheels
	public double getDistance() {
		double sum = 0;
		for (int i = 0; i < 4; i++) {
			sum += encoders[i].getDistance();
		}
		return sum / 4;
	}

	// resets encoders
	public void resetEncoders() {
		for (int i = 0; i < 4; i++) {
			encoders[i].reset();
		}
	}
}

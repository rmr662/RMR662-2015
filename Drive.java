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
	public static final double TL = .5;
	public static final double TR = -.5;
	public static final double BL = .5;
	public static final double BR = -.5;

	public static final double DEAD_ZONE = .1;
	public static final double EXPIRATION_DATE = .5;
	public static final double RADIUS_OF_WHEEL = 3; // inches
	public static final int TICKS_PER_REVOLUTION = 360; // ticks per rotation of
														// encoder

	public static final int[][] ENCODER_PORTS = { { 16, 17 }, // FL
			{ 14, 15 }, // FR
			{ 12, 13 }, // RL
			{ 10, 11 } }; // RR

	double a, b, c;

	CANTalon fLMotor;
	CANTalon fRMotor;
	CANTalon bLMotor;
	CANTalon bRMotor;

	Joystick stick;
	Encoder[] encoders;

	public Drive() {
		fLMotor = new CANTalon(FL_PORT);
		fRMotor = new CANTalon(FR_PORT);
		bLMotor = new CANTalon(BL_PORT);
		bRMotor = new CANTalon(BR_PORT);

		fLMotor.setSafetyEnabled(true);
		fRMotor.setSafetyEnabled(true);
		bLMotor.setSafetyEnabled(true);
		bRMotor.setSafetyEnabled(true);

		fLMotor.setExpiration(EXPIRATION_DATE);
		fRMotor.setExpiration(EXPIRATION_DATE);
		bLMotor.setExpiration(EXPIRATION_DATE);
		bRMotor.setExpiration(EXPIRATION_DATE);

		encoders = new Encoder[4];

		for (int i = 0; i < 4; i++) {
			encoders[i] = new Encoder(ENCODER_PORTS[i][0], ENCODER_PORTS[i][1]);
			encoders[i].setDistancePerPulse((2 * RADIUS_OF_WHEEL * Math.PI)
					/ TICKS_PER_REVOLUTION);
		}

		stick = new Joystick(0);
	}

	public void update() {
		// double speed = 0;

		c = stick.getRawAxis(XboxMap.LEFT_JOY_VERT);
		b = -stick.getRawAxis(XboxMap.LEFT_JOY_HORIZ);
		a = -stick.getRawAxis(XboxMap.RIGHT_JOY_HORIZ);

		if (Math.abs(a) < DEAD_ZONE) {
			a = 0;
		}

		if (Math.abs(b) < DEAD_ZONE) {
			b = 0;
		}
		if (Math.abs(c) < DEAD_ZONE) {
			c = 0;
		}

		move(a, b, c);

	}

	public void move(double a, double b, double c) {
		fLMotor.set(TL * (b + a + c));
		fRMotor.set(TR * (b - a - c));
		bLMotor.set(BL * (b + a - c));
		bRMotor.set(BR * (b - a + c));
	}

	public void moveStraight(int dist) {
		resetEncoders();

		if (dist > 0) {
			while (getDistance() < dist) {
				move(0, 0, 1);
			}
		} else {
			while (getDistance() > dist) {
				move(0, 0, -1);
			}
		}

		/*
		 * while ((dist < 0 && getDistance() > dist) || (dist > 0 &&
		 * getDistance() < dist) ) { move(0, 0, dist / Math.abs(dist)); }
		 */
		move(0, 0, 0);
	}

	public void strafe(int seconds, double dir) {

		long t = System.currentTimeMillis();
		long currTime = 0;

		while (currTime < seconds) {
			move(0, dir, 0);
			currTime = (System.currentTimeMillis() - t) / 1000;
		}

		move(0, 0, 0);

	}

	public double getDistance() {
		double sum = 0;
		for (int i = 0; i < 4; i++) {
			sum += encoders[i].getDistance();
		}
		return sum / 4;
	}

	public void resetEncoders() {
		for (int i = 0; i < 4; i++) {
			encoders[i].reset();
		}
	}
}
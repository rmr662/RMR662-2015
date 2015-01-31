package org.usfirst.frc.team662.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Solenoid;

public class Manipulator extends Component {
	
	public static final int BRAKE_PORT = 0;
	public static final int LIFT_PORT = 0;
	public static final double BRAKE_VAL = 0.5;
	public static final double DEAD_ZONE = 0.1;
	public static final double LIFT_MULT = -(1 / 3.0);
	
	Talon liftMotor;
	Solenoid airBrake;
	Joystick xbox;
	
	double liftVal;
	
	public Manipulator() {
		xbox = new Joystick(0);
		liftMotor = new Talon(LIFT_PORT);
		//airBrake = new Solenoid(BRAKE_PORT);
		
		//airBrake.set(false);
		liftVal = 0.0;
		liftMotor.setSafetyEnabled(true);
		liftMotor.setExpiration(.5);
		
	}
	
	public void update() {
		liftVal = xbox.getRawAxis(XboxMap.LEFT_JOY_VERT);
	
		if(Math.abs(liftVal) < DEAD_ZONE){
			liftVal = 0;
		}
		
		liftMotor.set(LIFT_MULT * liftVal);
		
		/*if(xbox.getRawAxis(XboxMap.RIGHT_TRIGGER) <= BRAKE_VAL){
			airBrake.set(true);
		}
		else{
			airBrake.set(false);
		}*/
	}

}

package org.usfirst.frc.team662.robot;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot {

	final int DRIVE_INDEX = 0;
	final int MANIP_INDEX = 1;
	final int NUM_COMPONENTS = 2;
	
	Component [] parts;
	
	Joystick xbox;
	
    public Robot() {
    	
    	parts = new Component[NUM_COMPONENTS];
    	
    	//parts[0] = new Drive();
    	parts[1] = new Manipulator();
    	
    }

    public void autonomous() {
    	Autonomous.run((Drive)parts[DRIVE_INDEX], (Manipulator)parts[MANIP_INDEX]);
    }


    public void operatorControl() {
        
    	while (isEnabled() && isOperatorControl()) {
    		for (int c = 0; c < parts.length; c++) {
    			if (parts[c] != null)
    			{
    				parts[c].update();
    			}
    		
    		}
    	}
    	
    }

    public void test() {
    }
}

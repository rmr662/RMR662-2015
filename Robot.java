// Package name, MUST NOT CHANGE. If changed, can't be pushed to robot.
package org.usfirst.frc.team662.robot;

// Base class for robots
import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot {

	// Constants for the parts array
	final int DRIVE_INDEX = 0;
	final int MANIP_INDEX = 1;
	final int NUM_COMPONENTS = 2;

	// Array of parts. It's an array in case we want to add things like an LED controller
    	Component [] parts;

    public Robot() {

    	parts = new Component[NUM_COMPONENTS];

	// Comment out either of these to disable parts of the robot
    	//parts[DRIVE_INDEX] = new Drive();
    	parts[MANIP_INDEX] = new Manipulator();

    }

    // This method gives a Drive object and a Manipulator object to control
    public void autonomous() {
    	Autonomous.run((Drive)parts[DRIVE_INDEX], (Manipulator)parts[MANIP_INDEX]);
    }

	// Main control method of the robot
    public void operatorControl() {

	// Continuous loop for operator control. Don't change this
    	while (isEnabled() && isOperatorControl()) {

		// Cycles through the parts, updating them all
    		for (int c = 0; c < parts.length; c++) {
			// This block allows for disabling parts of the robot without breaking the code
    			if (parts[c] != null)
    			{
    				parts[c].update();
    			}

    		}
    	}

    } // End of operatorControl

	// This method isn't used
    public void test() {
    }

} // End class Robot

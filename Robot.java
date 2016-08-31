// Package name, MUST NOT CHANGE. If changed, can't be pushed to robot.
package org.usfirst.frc.team662.robot;

// Base class for robots

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot {

	final int COMPRESSOR_SWITCH_PORT = 0;
	// Constants for the parts array
	final int DRIVE_INDEX = 0;
	final int MANIP_INDEX = 1;
	final int NUM_COMPONENTS = 2;
	final int hi = 2;

	// Array of parts. It's an array in case we want to add things like an LED
	// controller
	Component[] parts;
	Compressor airComp;
	DigitalInput compressorSwitch;

	public Robot() {
		/*
				parts = new Component[NUM_COMPONENTS];

				// Comment out either of these to disable parts of the robot
				parts[DRIVE_INDEX] = new Drive();
				//parts[MANIP_INDEX] = new Manipulator();
				parts[MANIP_INDEX] = new AbsolutePositioningManipulator();

				airComp = new Compressor(1);
				compressorSwitch = new DigitalInput(COMPRESSOR_SWITCH_PORT);
				//airComp.setClosedLoopControl(true);
				 * */
		//moved down to robot init
	}

	// This method gives a Drive object and a Manipulator object to control
	public void autonomous() {
		System.out.println("Entering Auto");
		Autonomous.run((Drive) parts[DRIVE_INDEX], (AbsolutePositioningManipulator) parts[MANIP_INDEX]);
	}

	public void robotInit() {
		/*airComp.setClosedLoopControl(true);
		airComp.start();*/

		parts = new Component[NUM_COMPONENTS];

		// Comment out either of these to disable parts of the robot
		parts[DRIVE_INDEX] = new Drive(this);
		//parts[MANIP_INDEX] = new Manipulator();
		parts[MANIP_INDEX] = new AbsolutePositioningManipulator(this);

		airComp = new Compressor(1);
		compressorSwitch = new DigitalInput(COMPRESSOR_SWITCH_PORT);
		//airComp.setClosedLoopControl(true);
	}

	// Main control method of the robot
	public void operatorControl() {

		// Continuous loop for operator control. Don't change this
		while (isEnabled() && isOperatorControl()) {
			try {
				if (!compressorSwitch.get() /*&& !airComp.getPressureSwitchValue()*/) {
					airComp.start();
				} else {
					airComp.stop();
				}
			} catch (Exception e) {

			}

			// Cycles through the parts, updating them all
			for (int c = 0; c < parts.length; c++) {
				// This block allows for disabling parts of the robot without
				// breaking the code
				if (parts[c] != null) {
					parts[c].update();
				}

			}
		}

	} // End of operatorControl

	// This method isn't used
	public void test() {
	}

} // End class Robot

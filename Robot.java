
package org.usfirst.frc.team662;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot {

	Drive d;
	Manipulator m;
	Joystick xbox;
	
    public Robot() {
    	
    }

    public void autonomous() {
    	Autonomous.run(d, m);
    }

    public void operatorControl() {
        
    }

    public void test() {
    }
}

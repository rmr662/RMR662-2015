package org.usfirst.frc.team662.robot;

import edu.wpi.first.wpilibj.Encoder;

public class NegativeEncoder extends Encoder {

	public NegativeEncoder(int aChannel, int bChannel) {
		super(aChannel, bChannel);
		// TODO Auto-generated constructor stub
	}

	public int get() {
		return -1 * super.get();
	}

}

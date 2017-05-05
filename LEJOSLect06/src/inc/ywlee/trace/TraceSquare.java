package inc.ywlee.trace;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class TraceSquare {

	EV3 ev3Brick;
	Keys btns;

	EV3LargeRegulatedMotor leftMotor, rightMotor;
	Wheel leftWheel, rightWheel;

	// Chassis information
	double wheelDiameter = 5.6;
	double offsetN = 6.0;
	double travelDistance = 30.0;

	Chassis chassis;
	MovePilot movePilot;

	public TraceSquare() {
		ev3Brick = (EV3) BrickFinder.getLocal();
		btns = ev3Brick.getKeys();
		LCD.drawString("Press any key", 0, 0);
		btns.waitForAnyPress();

		leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
		leftWheel = WheeledChassis.modelWheel(leftMotor, wheelDiameter).offset(-offsetN);
		rightWheel = WheeledChassis.modelWheel(rightMotor, wheelDiameter).offset(offsetN);

		chassis = new WheeledChassis(new Wheel[] { leftWheel, rightWheel }, WheeledChassis.TYPE_DIFFERENTIAL);

		movePilot = new MovePilot(chassis);
		
		followSquare();
		
		while (movePilot.isMoving()) {
			if(btns.getButtons()==Keys.ID_ESCAPE){
				movePilot.stop();
			}			
		}
		btns.waitForAnyPress();
	}

	public static void main(String[] args) {
		// 2017-1st semester lecture :Àç´É³ª´®ºÀ»ç
		new TraceSquare();
	}
	
	public void followSquare(){
		for (int i = 0; i < 4; i++) {
			movePilot.travel(travelDistance);
			movePilot.rotate(90.0);			
		}
	}

}

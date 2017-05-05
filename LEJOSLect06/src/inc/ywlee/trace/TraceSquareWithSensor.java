package inc.ywlee.trace;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;

public class TraceSquareWithSensor {

	EV3 ev3Brick;
	Keys btns;

	EV3LargeRegulatedMotor leftMotor, rightMotor;
	Wheel leftWheel, rightWheel;

	EV3GyroSensor ev3GyroSensor;
	EV3UltrasonicSensor ev3UltrasonicSensor;
	SampleProvider gyroSample, usSample;
	float[] gyroValue, usValue;

	// Chassis information
	double wheelDiameter = 5.6;
	double offsetN = 6.0;
	double travelDistance = 100.0;

	Chassis chassis;
	MovePilot movePilot;

	public TraceSquareWithSensor() {
		ev3Brick = (EV3) BrickFinder.getLocal();
		btns = ev3Brick.getKeys();
		LCD.drawString("Press any key", 0, 0);

		ev3GyroSensor = new EV3GyroSensor(SensorPort.S4);
		gyroSample = ev3GyroSensor.getAngleMode();
		gyroValue = new float[gyroSample.sampleSize()];
		LCD.drawString("Gyro Sensor Setting", 0, 1);

		ev3UltrasonicSensor = new EV3UltrasonicSensor(SensorPort.S2);
		usSample = ev3UltrasonicSensor.getDistanceMode();
		usValue = new float[usSample.sampleSize()];
		LCD.drawString("us Sensor setting", 0, 2);

		btns.waitForAnyPress();

		leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
		leftWheel = WheeledChassis.modelWheel(leftMotor, wheelDiameter).offset(-offsetN);
		rightWheel = WheeledChassis.modelWheel(rightMotor, wheelDiameter).offset(offsetN);

		chassis = new WheeledChassis(new Wheel[] { leftWheel, rightWheel }, WheeledChassis.TYPE_DIFFERENTIAL);

		movePilot = new MovePilot(chassis);

		LCD.clear();

		followSquare();

		while (movePilot.isMoving()) {
			if (btns.getButtons() == Keys.ID_ESCAPE) {
				movePilot.stop();
			}
		}
		btns.waitForAnyPress();

		leftMotor.close();
		rightMotor.close();
		ev3GyroSensor.close();
		ev3UltrasonicSensor.close();
	}

	public static void main(String[] args) {
		// 2017-1st semester lecture :Àç´É³ª´®ºÀ»ç
		new TraceSquareWithSensor();
	}

	public void followSquare() {
		for (int i = 0; i < 4; i++) {
			// read sensor value
			

			movePilot.travel(travelDistance);  
			movePilot.addMoveListener(new MoveListener() {
				
				@Override
				public void moveStopped(Move event, MoveProvider mp) {
					// TODO Auto-generated method stub
					LCD.drawString("Stopped!! ", 0, 3);
					
				}
				
				@Override
				public void moveStarted(Move event, MoveProvider mp) {
					// TODO Auto-generated method stub
					
					gyroSample.fetchSample(gyroValue, 0);
					usSample.fetchSample(usValue, 0);

					if (usValue[0] < 0.2) {
						stop();
					}

					LCD.drawString("Heading : " + gyroValue[0], 0, 1);
					LCD.drawString("Distance: " + usValue[0], 0, 2);
					
				}
			});
			movePilot.rotate(90.0);
		}
	}

	public void stop() {
		leftMotor.stop();
		rightMotor.stop();

	}

}

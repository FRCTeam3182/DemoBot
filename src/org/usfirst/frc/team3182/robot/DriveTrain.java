/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *  
 *                          The DriveTrain								*
 * 																		*
 * This class is the thread for the drivetrain without shifters			*
 * 																		*
 * 																		*
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * **/

package org.usfirst.frc.team3182.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;

public class DriveTrain implements Runnable {


	private final DriverStation driverStation;

    private RobotDrive drive = null;  

    private ArrayList<Encoder> encoders = new ArrayList<Encoder>();

	// General direction commands (start at unmoving)
	private volatile double xCommand = 0; // 0 for unmoving, 1 for full strafe right, -1 for strafe left
	private volatile double yCommand = 0; // 0 for unmoving, 1 for full forward, -1 for backward
	private volatile double rotationCommand = 0; // 0 for unmoving, 1 for full clockwise, -1 counterclockwise

	// Joysticks
	private final Joystick joystickLeft;
    private final Joystick joystickRight;
	private volatile boolean joystickStateCommand = true; // false to disable joysticks

	// Deadzone and smoothing
	private double ySmooth = 0; // for making joystick output a linear function between P and 1 and -P to -1
	private double xSmooth = 0;
	private double rotationSmooth = 0;
	private final double P = 0.10; // dead zone of joysticks for drive is between -P and P
	private final double rotationP = 10; // dead zone for the joystick's rotation (in degrees)
    

	public DriveTrain() {

		// Initializing everything
		driverStation = DriverStation.getInstance();

		// Instantiate

		// Joysticks
        joystickLeft = new Joystick(0);
        joystickRight = new Joystick(1);
		//	Joystick turnJoystick = new Joystick(0);


        drive = new RobotDrive(0, 1, 2, 3);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        drive.setSafetyEnabled(false);

	}

	public void run() {
        while (true) {
            if (driverStation.isEnabled()) {
                /*=================================================================
                -Makes sure joystick will not work at �P% throttle, P is declared above
                -smoothVarRight/Left are output variables from a function
                that calculates how much to power the motors
                -Full throttle always outputs a 1 (full power)
                -While joystick is in deadzone, a 0 is output
                 =================================================================*/

                //Deadzone
                if (yCommand < P && yCommand > (-P)) {
                    yCommand = 0;
                }
                if (xCommand < P && xCommand > (-P)) {
                    xCommand = 0;
                }

               //Smoothing
               //If the x is positive and passed the deadzone (joystick is moved to the right)
               if (xCommand >= P) {
                   xSmooth = ((1 / (1 - P)) * xCommand + (1 - (1 / (1 - P))));
                }
                // If the x is negative and passed the deadzone (joystick is moved to the left)
                if (xCommand <= (-P)) {
                    xSmooth = ((1 / (1 - P)) * xCommand - (1 - (1 / (1 - P))));
                }
                // If the y is positive and passed the deadzone (joystick is moved to the up)
                if (yCommand >= P) {
                   ySmooth = ((1 / (1 - P)) * yCommand + (1 - (1 / (1 - P))));
                }
                // If the y is negative and passed the deadzone (joystick is moved to the down)
                if (yCommand <= (-P)) {
                    ySmooth = ((1 / (1 - P)) * yCommand - (1 - (1 / (1 - P))));
                }
                // If the rotation is positive and passed the deadzone (joystick is twisted clockwise)
                if (rotationCommand >= rotationP) {
                    rotationSmooth = ((1 / (1 - rotationP)) * yCommand + (1 - (1 / (1 - P))));
               }
                // If the rotation is negative and passed the deadzone (joystick is twisted counterclockwise)
                if (yCommand <= rotationP) {
                   rotationSmooth = ((1 / (1 - rotationP)) * yCommand - (1 - (1 / (1 - P))));
                }


                moveDriveTrain(joystickLeft, joystickRight);
            }
            driveToDashboard();
            Timer.delay(.05); //100ms delay
        }
	}

	public synchronized void setXCommand(double x) {
		this.xCommand = x;
	}
	
	public synchronized void setYCommand(double y) {
		this.yCommand = y;
	}
	
	public synchronized void setJoystickStateCommand(boolean joystickStateCommand) {
		this.joystickStateCommand = joystickStateCommand;
	}



    public void moveDriveTrain(GenericHID left, GenericHID right){
        drive.tankDrive(left, right);
    }

	private void driveToDashboard() {
		SmartDashboard.putNumber("Raw x Axis", xCommand);
		SmartDashboard.putNumber("Raw y Axis", yCommand);
		SmartDashboard.putNumber("Raw rotation", rotationCommand);
		SmartDashboard.putNumber("Smooth Var x Axis", xSmooth);
		SmartDashboard.putNumber("Smooth Var y Axis", ySmooth);
		SmartDashboard.putNumber("Smooth Var rotation", rotationSmooth);
		SmartDashboard.putBoolean("Joystick state", joystickStateCommand);

        SmartDashboard.putNumber("Encoder (top left)", encoders.get(0).getRate());
        SmartDashboard.putNumber("Encoder (bottom left)", encoders.get(1).getRate());
        SmartDashboard.putNumber("Encoder (top right)", encoders.get(2).getRate());
        SmartDashboard.putNumber("Encoder (bottom right)", encoders.get(3).getRate());       
	}
	
	public void testDriveTrain()
	{
		while(true)
		{
			moveDriveTrain(0, 0.3, 0, 0);
			driveToDashboard();
			double sum = 0;
			for(Encoder e : encoders)
			{
				sum += e.getRate();	
			}
			double avg = sum / 4.0;
			for(Encoder e : encoders)
			{
				double percentError = Math.abs(e.getRate() - avg)/avg * 100; 
				SmartDashboard.putNumber("Encoder "+encoders.indexOf(e) +" Percent Error", percentError);
			}
			
		}
	}

    public Joystick getJoystick() {
        return driveJoystick;
    }
}



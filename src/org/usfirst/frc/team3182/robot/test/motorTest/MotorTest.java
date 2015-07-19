package org.usfirst.frc.team3182.robot.test.motorTest;

import edu.wpi.first.wpilibj.Joystick;

public class MotorTest {

    private Joystick driveJoystick;


    public MotorTest(Joystick driveJoystick) {
        this.driveJoystick = driveJoystick;
    }

    public void initiateTest(){
        DriveTrainThread driveTrainVar = new DriveTrainThread(driveJoystick);
        new Thread(driveTrainVar, "DriveTrainTest").start();

    }

    public void killThreads(){

    }
}

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.NeutralOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class Elevator extends SubsystemParent implements Reportable {
  private final TalonFX elevatorMotor;

  private double desiredPosition = 0.0;
  private double maxHeight = 1;
  private double minHeight = 0.5;
  private boolean enabled =  false;
  private MotionMagicVoltage motionMagicVoltage;
  private final NeutralOut neutralRequest = new NeutralOut();

  public Elevator(int motorID){
    elevatorMotor = new TalonFX(motorID);
    motionMagicVoltage = new MotionMagicVoltage(0);

    setMotorConfigs();

    CommandScheduler.getInstance().registerSubsystem(this);
    
  }

  public void setMotorConfigs(){
        TalonFXConfiguration config = new TalonFXConfiguration();

        //TODO: Tune PID
        config.Slot0.kP = 0.05;


        StatusCode statusCode = elevatorMotor.getConfigurator().apply(config);
        if (!statusCode.isOK()){
            DriverStation.reportError("Could not apply motor configs, " + statusCode.getDescription(), true);
        }
  }

  @Override
  public void periodic() {
    if (!enabled) {
      return;
    }
    if (getPosition() <= minHeight){
      desiredPosition = maxHeight;
    } else if(getPosition() >= maxHeight) {
      desiredPosition = minHeight;
    }
    elevatorMotor.setControl(motionMagicVoltage.withPosition(desiredPosition));
  }

  public Command startCommand(){
    return Commands.runOnce(() -> setEnabled(true));
  }
  public Command stopCommand(){
    return Commands.runOnce(()-> setEnabled(false));
  }

  public void setEnabled(boolean enabled){
    this.enabled = enabled;
    if(!enabled){
      elevatorMotor.setControl(neutralRequest);
    } 
  }

  public void setTargetPosition(double position){
    desiredPosition = position;
  }

  public double getPosition(){
    return elevatorMotor.getPosition().getValueAsDouble();
  }

  public double getTargetPosition(){
    return desiredPosition;
  }

  public boolean atSpeed(){
    return getPosition() > getTargetPosition();
  }

  @Override
  public void initShuffleboard(LOG_LEVEL priority) {}

  @Override
  public void stop() {
    enabled = false;
  }
}
package frc.robot.subsystems;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.NeutralOut;
import com.ctre.phoenix6.controls.VelocityTorqueCurrentFOC;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
 
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Flywheel extends SubsystemParent {
    private final TalonFX flyWheelMotor;
    private final TalonFXConfigurator flyWheelConfigurator;

    private final VelocityVoltage velocityRequestFlywheel = new VelocityVoltage(0);

    public TalonFXConfiguration motorConfigs;

    private boolean enabled = false;
    // private boolean velocityControl = true;

    private double desiredVoltage = 0;
    private final VelocityVoltage velocityRequest = new VelocityVoltage(0);
    private final NeutralOut brakeRequest = new NeutralOut();

    public Flywheel(int deviceID){
        flyWheelMotor = new TalonFX(deviceID);
        flyWheelConfigurator = flyWheelMotor.getConfigurator();
        velocityRequest.Slot = 0;
        motorConfigs = new TalonFXConfiguration();
        configureMotor(motorConfigs);
    }

    public void configureMotor(TalonFXConfiguration motorConfigs) {
        flyWheelConfigurator.refresh(motorConfigs);
        // TODO change all of these values
        motorConfigs.Feedback.FeedbackSensorSource = FeedbackSensorSourceValue.RotorSensor;
        motorConfigs.Voltage.PeakForwardVoltage = 11.5;
        motorConfigs.Voltage.PeakReverseVoltage = -11.5;
        motorConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        // motorConfigs.MotorOutput.DutyCycleNeutralDeadband = RollerConstants.kNeutralDeadband;
        motorConfigs.CurrentLimits.SupplyCurrentLimit = 40;
        motorConfigs.CurrentLimits.SupplyCurrentLimitEnable = false;
        motorConfigs.CurrentLimits.StatorCurrentLimit = 100;
        motorConfigs.CurrentLimits.StatorCurrentLimitEnable = true;
        motorConfigs.Audio.AllowMusicDurDisable = true;
 
        flyWheelConfigurator.apply(motorConfigs);
        // TODO add logging for errors

        // StatusCode response = rollerConfigurator.apply(motorConfigs);
        // if (!response.isOK())
        //     DriverStation.reportError("Could not apply motor configs, error code:" + response.toString(), new Error().getStackTrace());
        // rollerConfiguratorRight.refresh(motorConfigs);

        // StatusCode responseRight = rollerConfiguratorRight.apply(motorConfigs);

        // if (!responseRight.isOK())
        // DriverStation.reportError("Could not apply motor configs, error code:" + responseRight.toString(), new Error().getStackTrace());
    }

    @Override
    public void periodic(){
        if (!enabled) return;
        flyWheelMotor.setVoltage(desiredVoltage);
    }

    public Command startCommand() {
        return null;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        
        if (enabled) {
            flyWheelMotor.setControl(velocityRequest);
        } else {
            desiredVoltage = 0.0;
            flyWheelMotor.setControl(brakeRequest);
        }
    }

    private void setVelocity(double velocity){
        velocityRequestFlywheel.Velocity = velocity;
    }

    public double getTargetVelocity() {
        return velocityRequestFlywheel.Velocity;
    }

    private void setVoltage(double volt) {
        desiredVoltage = volt;
    }

    public Command setEnabledCommand(boolean enabled) {
        return Commands.runOnce(() -> setEnabled(enabled));
    }

    public Command setVelocityCommand(double velocity){
        return Commands.runOnce(() -> setVelocity(velocity));
    }

    public Command setVoltageCommand(double voltage){
        return Commands.runOnce(() -> setVoltage(voltage));
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }


}

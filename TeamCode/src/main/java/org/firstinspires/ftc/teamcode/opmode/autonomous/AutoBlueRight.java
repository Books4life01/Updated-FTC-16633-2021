package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.opmode.control.Pipeline_Target_Detect;
import org.firstinspires.ftc.teamcode.util.BaseRobot;
import org.firstinspires.ftc.teamcode.util.fields.PositionFields;
import org.firstinspires.ftc.teamcode.util.helpers.Printer;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
//alignmnt: left on box lines up with hole


@Config
@Autonomous
public class AutoBlueRight extends LinearOpMode {
    // Instance of Robot Class
    BaseRobot robot = new BaseRobot(telemetry);

    // todo TESTING DISTANCE
    public static double distanceToMove = 1;

    // Delay ElapsedTime
    private final ElapsedTime runtime = new ElapsedTime();
    public static double begginingDelay = 1;

    // OpenCV
    WebcamName webcamName;
    OpenCvCamera camera;
    Pipeline_Target_Detect myPipeline;
    double zone = 3;
    double xPos = -1;

    // Instance of a Printing Class for Telemetry
    Printer printer;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize telemetry
        printer = new Printer(telemetry);
        // Tell User to Wait For Start (to allow motors to instantiate)
        printer.print("Wait for Start!");

        // Initialize Hardware
        robot.init(hardwareMap, true);

        // Signal that robot is ready to run
        printer.print("Ready to start!");

        // Wait for User to Start

        // Clear Previous Telemetry
        printer.load();
        waitForStart();

        // Init Camera
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName);
        myPipeline = new Pipeline_Target_Detect(20, 40);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
                camera.setPipeline(myPipeline);
            }

            @Override
            public void onError(int errorCode) {
            }
        });


        // Wait for Camera to Start Up and Detect
        delay(2);
        xPos = myPipeline.getXPos();
        printer.addInfo("XPos: ", xPos);
        // Read Detection
        if (xPos < 100) {
            zone = 1;
        } else if (xPos > 200) {
            zone = 3;
        } else {
            zone = 2;
        }

        // Close Camera
        camera.stopStreaming();
        camera.closeCameraDevice();

        // Print Detection to Drivers
        printer.addInfo("XPos: ", xPos);
        printer.addInfo("Zone: ", zone);
        printer.load();

        // Autonomous Movements
        movement();
    }

    // Main Function that runs before the zone functions
    private void movement() {
        robot.reset();
        // Movement before the hub
        delay(begginingDelay);
        drive(8);//move forrward right before the barcode
        delay(0.5);
        turn(90);
        delay(0.5);
        drive(14);//turn and move to before the wall
        delay(0.5);
        turn(-90);
        delay(0.5);
        drive(23.4);//turn and move past barcode
        delay(0.5);
        turn(90);
        delay(.5);
        drive(13, .5);
        delay(0.5);
        drive(-28, .7);//turn and move to carousel
        depositBlock();
        delay(2);
        drive(26.5);
        robot.reset();
        //add parking and carousel
       turn(95);
        delay(0.5);
        drive(11);

    }

    // Functions for the different zones
    private void depositBlock()
    {
        robot.bucket.setPosition(PositionFields.BUCKET_HOLDING);
        delay(1);
        // Depositing Movement
        if (zone == 1) {
            robot.slider.setTargetPosition(PositionFields.LOW);
            robot.slider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.slider.setPower(1);
            delay(2);
        } else if (zone == 2) {
            drive(.5);
            robot.slider.setTargetPosition(PositionFields.MIDDLE);
            robot.slider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.slider.setPower(1);
            delay(2);
        } else {
            robot.slider.setTargetPosition(PositionFields.TOP);
            robot.slider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.slider.setPower(1);
            delay(2);
        }
        robot.bucket.setPosition(PositionFields.BUCKET_OUTTAKE);
    }


    public void delay(double t) { // Imitates the Arduino delay function
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < t)) {
        }
    }

    // Functions with default speed and timeoutS
    public void drive(double distance) {
        robot.drive(distance, 1, 5);
    }
    public void drive(double distance, double speed) {
        robot.drive(distance, speed, 5);
    }

    public void turn(double angle) {

        robot.pointTurnDegrees(1, angle, 1.5);

    }
    public void turn(double angle, double speed) {

        robot.pointTurnDegrees(speed, angle, 1.5);

    }


}

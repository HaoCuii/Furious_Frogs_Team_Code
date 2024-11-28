package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;//importing libraries
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "FrogDriveSoloQ", group= "TeleOp")
public class FrogDriveSoloQ extends OpMode {
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private Servo leftIn, rightIn, wrist, arm, claw;
    private DcMotor horSlide, vertSlideL, vertSlideR, intake;
    //Bigger number - intake is lower
    Gamepad currentGamepad1;
    Gamepad previousGamepad1;
    private static ElapsedTime timer = new ElapsedTime();
    private static final float inDown = 0.67F;
    private static final float inUp = 0.2F;
    private static final float inTransfer = 0.32F;
    private static final float inWait = 0.36F; //this is the position where the intake arm waits for the outtake arm to go down
    //Bigger number - arm is lower
    private static final float armOut = 0.17F;
    private static final float armTransfer = 0.46F;
    private static final float armInit = 0.3F;
    private static final float armWait = 0.2F;
    //Bigger number - claw is open
    private static final float clawClose = 0.6F;
    private static final float clawOpen = 0.31F;
    //Bigger number - wrist goes out
    private static final float wristTransfer = 0.2F;
    private static final float wristOut = 0.7F;
    private TouchSensor hortouch;
    private TouchSensor vertouch;
    boolean squareSequence = false;
    boolean circleSequence = false;
    boolean triangleSequence = false;
    boolean resethor = false;
    boolean resetver = false;

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "front_left");
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontRight = hardwareMap.get(DcMotor.class, "front_right");
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        backLeft = hardwareMap.get(DcMotor.class, "back_left");
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        backRight = hardwareMap.get(DcMotor.class, "back_right");
        backRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftIn = hardwareMap.get(Servo.class, "leftin");
        leftIn.setDirection(Servo.Direction.FORWARD);
        leftIn.setPosition(inUp);

        rightIn = hardwareMap.get(Servo.class, "rightin");
        rightIn.setDirection(Servo.Direction.FORWARD);
        rightIn.setPosition(inUp);

        arm = hardwareMap.get(Servo.class, "outarm");
        arm.setDirection(Servo.Direction.FORWARD);
        arm.setPosition(armInit);

        wrist = hardwareMap.get(Servo.class, "wrist");
        wrist.setDirection(Servo.Direction.FORWARD);
        wrist.setPosition(wristOut);


        claw = hardwareMap.get(Servo.class, "claw");
        claw.setDirection(Servo.Direction.FORWARD);
        claw.setPosition(clawClose);

        horSlide = hardwareMap.get(DcMotor.class, "righthor");
        horSlide.setDirection(DcMotor.Direction.REVERSE);
        horSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        horSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        vertSlideL = hardwareMap.get(DcMotor.class, "leftvertical");
        vertSlideL.setDirection(DcMotor.Direction.FORWARD);
        vertSlideL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vertSlideL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        vertSlideL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        vertSlideR = hardwareMap.get(DcMotor.class, "rightvertical");
        vertSlideR.setDirection(DcMotor.Direction.REVERSE);
        vertSlideR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vertSlideR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        vertSlideR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotor.Direction.FORWARD);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        currentGamepad1 = new Gamepad();
        previousGamepad1 = new Gamepad();
        hortouch = hardwareMap.get(TouchSensor.class, "hortouch");
        vertouch = hardwareMap.get(TouchSensor.class, "vertouch");


    }
    public void drive() {

        double driveX = 0;
        double driveY = 0;
        double RightTurn = gamepad1.right_trigger;
        double LeftTurn = gamepad1.left_trigger;
        double slowvar = 0.5;
        if (Math.abs(gamepad1.left_stick_x) > 0.4) {
            driveX = gamepad1.left_stick_x;
        } else if (Math.abs(gamepad1.left_stick_x) <= 0.4) {
            driveX = slowvar * gamepad1.left_stick_x;
        }
        if (Math.abs(gamepad1.left_stick_y) > 0.4) {
            driveY = -gamepad1.left_stick_y;

        } else if (Math.abs(gamepad1.left_stick_y) <= 0.4) {
            driveY = slowvar * -gamepad1.left_stick_y;
        }


        double rotate = 0.7 * (RightTurn - LeftTurn);
        double angle = Math.atan2(driveY, driveX) - Math.PI / 4;//caculate output
        double magnitude = Math.hypot(driveX, driveY) - 0.1;//MIGHT BREAK CODE IDK CONSTANT VALUE
        double frontLeftPower = magnitude * Math.cos(angle) + rotate;
        double frontRightPower = magnitude * Math.sin(angle) - rotate;
        double backLeftPower = magnitude * Math.sin(angle) + rotate;
        double backRightPower = magnitude * Math.cos(angle) - rotate;
        double maxPower = Math.max(Math.abs(frontLeftPower), Math.max(Math.abs(frontRightPower),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));
        if (maxPower > 1.0) {
            frontLeftPower /= maxPower;
            frontRightPower /= maxPower;
            backLeftPower /= maxPower;
            backRightPower /= maxPower;
        }
        frontLeft.setPower(frontLeftPower);//giving output
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }
    public void manualTake() {
        //manual controls
        // Store previous state
        previousGamepad1.copy(currentGamepad1);
        // Update current state with the latest gamepad data
        currentGamepad1.copy(gamepad1);
        if (currentGamepad1.right_bumper && !previousGamepad1.right_bumper) { //Intake
            if (intake.getPower() < 0.2) {
                intake.setPower(0.8);
            } else if (intake.getPower() > 0.2 && currentGamepad1.right_bumper && !previousGamepad1.right_bumper) {
                intake.setPower(0);
            }
        }
        if (currentGamepad1.left_bumper && !previousGamepad1.left_bumper) {
            if (intake.getPower() > -0.2) {
                intake.setPower(-0.7); // Reverse
            } else {
                intake.setPower(0); // Stop
            }
        }
        if (currentGamepad1.cross && !previousGamepad1.cross) { // Arm down
            if (leftIn.getPosition() > 0.5) {
                // Set servo positions to ArmDwn
                leftIn.setPosition(inWait);
                rightIn.setPosition(inWait);
            } else if ((leftIn.getPosition() < 0.5) && Math.abs(horSlide.getCurrentPosition()) > 300) {
                // Set servo positions to ArmUp
                leftIn.setPosition(inDown);
                rightIn.setPosition(inDown);
            }
        }
        if (hortouch.isPressed()) { //Horizontal touch sensor detection

            horSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            horSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        }
        if (vertouch.isPressed()) { //Reset vertical encoders
            vertSlideL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            vertSlideL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            vertSlideR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            vertSlideR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        if (currentGamepad1.square && !previousGamepad1.square) {
            //reset vipers
            claw.setPosition(clawOpen);
            leftIn.setPosition(inWait);
            rightIn.setPosition(inWait);
            squareSequence = true;
            resethor=true;
            resetver=true;
            timer.reset();
        }
        if (squareSequence){
            if (timer.seconds()>2.0){
                arm.setPosition(armTransfer);
                wrist.setPosition(wristTransfer);
            }else if (timer.seconds()>4.0){
                leftIn.setPosition(inTransfer);
                rightIn.setPosition(inTransfer);
                squareSequence=false;
            }
        }
        if (resethor){
            if (!hortouch.isPressed()){
                horSlide.setPower(-1);
            }else{
                horSlide.setPower(0);
                resethor=false;
            }
        } else {
            horSlide.setPower(gamepad1.right_stick_x);
        }
        if (resetver){
            if (!vertouch.isPressed()){
                vertSlideL.setPower(-0.88);
                vertSlideR.setPower(-0.88);
            } else {
                vertSlideL.setPower(0);
                vertSlideR.setPower(0);
                resetver=false;
            }
        } else {
            vertSlideL.setPower(gamepad1.right_stick_y);
            vertSlideR.setPower(gamepad1.right_stick_y);
        }
        if (currentGamepad1.circle && !previousGamepad1.circle){
            claw.setPosition(clawClose);
            circleSequence=true;
            timer.reset();
        }
        if (circleSequence){
            if(timer.seconds()>2.0){
                leftIn.setPosition(inWait);
                rightIn.setPosition(inWait);
            } else if (timer.seconds()>4.0) {
                arm.setPosition(armWait);
                wrist.setPosition(wristOut);
                circleSequence=false;
            }

        }
        if (currentGamepad1.triangle && !previousGamepad1.triangle){
            arm.setPosition(armOut);
            triangleSequence=true;
            timer.reset();
        }
        if (triangleSequence){
            if (timer.seconds()>2.0){
                claw.setPosition(clawOpen);
            }
        }
    }
    public void Telemetry() {
        telemetry.addData("Vertical Right Slide Pos", vertSlideR.getCurrentPosition());
        telemetry.addData("Vertical left Slide Pos", vertSlideL.getCurrentPosition());
        telemetry.addData("Horizontal Slide Pos", horSlide.getCurrentPosition());
        if (hortouch.isPressed()) {
            telemetry.addLine("touch joe");
        }
        if (vertouch.isPressed()) {
            telemetry.addLine("touch chuck");
        }
        if (leftIn.getPosition() == inTransfer) {
            telemetry.addLine("Intake Up");
        } else if (leftIn.getPosition() == inDown){
            telemetry.addLine("Intake down");
        }
    }
    @Override
    public void loop () {
        drive();
        manualTake();
        Telemetry();
    }
}

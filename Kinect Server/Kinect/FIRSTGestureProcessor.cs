/////////////////////////////////////////////////////////////////////////
//
// This module contains the Gesture Processing code for the 
// FRC 2012 Kinect Server
//
// Copyright (c) FIRST 2011. All Rights Reserved.							  
// Open Source Software - may be modified and shared by FRC teams. The code   
// must be accompanied by, and comply with the terms of, the license found at
// \FRC Kinect Server\License_for_KinectServer_code.txt which complies
// with the Microsoft Kinect for Windows SDK (Beta) 
// License Agreement: http://kinectforwindows.org/download/EULA.htm
//
/////////////////////////////////////////////////////////////////////////

using System;
using System.Collections.Generic;
using System.Text;
using System.Linq;
using Microsoft.Research.Kinect.Nui;
using Edu.FIRST.WPI.Kinect.KinectServer.Networking.WritableElements;

namespace Edu.FIRST.WPI.Kinect.KinectServer.Kinect
{
    public class KinectButtons {
    }

    /// <summary>
    /// Process the FIRST standard gestures:
    ///  - Left arm inclination from 0: Joystick 0 Y axis, from -128 to 127
    ///  - Right arm inclination from 0: Joystick 1 Y axis, from -128 to 127
    ///  
    ///  - Buttons:
    ///     - Head:
    ///         - Tilted to the right: Btn0
    ///         - Tilted to the left: Btn1        
    ///     - Right leg:
    ///         - Out (extended to the right): Btn2
    ///         - Forward: Btn4
    ///         - Backward: Btn5
    ///     - Left leg:
    ///         - Out (extended to the left): Btn3
    ///         - Forward: Btn6
    ///         - Backward: Btn7
    ///         
    /// Button values the same for both joysticks.
    /// </summary>
    class FIRSTGestureProcessor: IGestureProcessor
    {
        public const double Z_PLANE_TOLERANCE = 0.3;

        public const double ARM_MAX_ANGLE = 105;
        public const double ARM_MIN_ANGLE = -90;

        delegate bool CheckAngle(double angle);

        CheckAngle IsLegForward = x => x < -110;
        CheckAngle IsLegBackward = x => x > -80;
        CheckAngle IsLegOut = x => x > -75;
        CheckAngle IsHeadLeft = x => x > 98;
        CheckAngle IsHeadRight = x => x < 82;

        System.IO.TextWriter log = null;

        /// <summary>
        /// Processes a skeleton into joystick data using the default FIRST gestures.
        /// </summary>
        /// <param name="joy">Vector of Joysticks to put the result in</param>
        /// <param name="skeleton">The skeleton to process</param>
        public void ProcessGestures(Networking.WritableElements.WritableJoystick[] joy, Microsoft.Research.Kinect.Nui.SkeletonData skeleton)
        {

            if (log == null)
                log = new System.IO.StreamWriter(@"./log.txt");

            // Check edge cases
            if (joy == null || joy.Length < 2 || joy[0] == null || joy[1] == null)
                return;

            sbyte[] leftAxis = new sbyte[6];
            sbyte[] rightAxis = new sbyte[6];
            sbyte[] nullAxis = new sbyte[6];
            ushort buttons = 0;

            
        ushort ENABLE = (ushort)WritableJoystick.Buttons.Btn1;
        ushort ABORT = (ushort)WritableJoystick.Buttons.Btn2;
        ushort DRIVE_ENABLED = (ushort)WritableJoystick.Buttons.Btn3;
        ushort PICKUP_IN = (ushort)WritableJoystick.Buttons.Btn4;
        ushort SUCK = (ushort)WritableJoystick.Buttons.Btn5;
        ushort SHOOT = (ushort)WritableJoystick.Buttons.Btn6;


            double unitsPerMeter = 1;

        //PUT IN THE KINECT LOOP
        Joint head = skeleton.Joints[JointID.Head];
        double headX = head.Position.X;
        double headY = head.Position.Y;
        double headZ = head.Position.Z;
        Joint shoulder = skeleton.Joints[JointID.ShoulderLeft];
        double shoulderY = shoulder.Position.Y;
        double shoulderZ = shoulder.Position.Z;
        Joint leftHand = skeleton.Joints[JointID.WristLeft];
        Joint rightHand = skeleton.Joints[JointID.WristRight];
        double leftX = leftHand.Position.X;
        double rightX = rightHand.Position.X;
        double leftY = leftHand.Position.Y;
        double rightY = rightHand.Position.Y;
        double leftZ = leftHand.Position.Z;
        double rightZ = rightHand.Position.Z;
        Joint leftKnee = skeleton.Joints[JointID.KneeLeft];
        Joint rightKnee = skeleton.Joints[JointID.KneeRight];

        log.WriteLine("----------------------------");

        log.WriteLine("headX: " + headX + ", headY: " + headY + ", headZ: " + headZ);
        log.WriteLine("shoulderY: " + shoulderY + ", shoulderZ: " + shoulderZ);
        log.WriteLine("leftX: " + leftX + ", leftY: " + leftY + ", leftZ: " + leftZ);
        log.WriteLine("rightX: " + rightX + ", rightY: " + rightY + ", rightZ: " + rightZ);
        log.WriteLine("leftFootY: " + leftKnee.Position.Y + ", rightFootY: " + rightKnee.Position.Y);

        log.WriteLine("> > > > > < < < < <");

        //KP added:
        bool facepalm = false;
        double facepalmXTol = .2 * unitsPerMeter;
        double facepalmYTol = .3 * unitsPerMeter;
        double facepalmZTol = .3 * unitsPerMeter;
        if (Math.Abs(headX - rightX) < facepalmXTol &&
            Math.Abs(headY - rightY - .07) < facepalmYTol &&
            Math.Abs(headZ - rightZ) < facepalmZTol)
        {

            buttons |= ENABLE;
            log.WriteLine("ENABLE");
            facepalm = true;

        }


        double driveDeadband = .2*unitsPerMeter;
        if (!facepalm)
        {
            double dzL = leftZ - shoulderZ;
            double dzR = rightZ - shoulderZ;
            double dyL = leftY - shoulderY;
            double dyR = rightY - shoulderY;


            double powL = Math.Atan(dyL / dzL) * 2 / Math.PI;
            double powR = Math.Atan(dyR / dzR) *2 /Math.PI;

                double joystickL = CoerceToRange(powL,
                                                -0.7,
                                                 0.7,
                                                -127,
                                                 128);
	            double joystickR = CoerceToRange(powR,
                                                -0.7,
                                                 0.7,
                                                -127,
                                                 128);
	
            leftAxis[(uint)WritableJoystick.Axis.Y] = (sbyte) -joystickL;
            rightAxis[(uint)WritableJoystick.Axis.Y] = (sbyte) -joystickR;

            double lenArmsSqd = .3*.3;
            if(/*Math.Abs(powR) > .7 || Math.Abs(rightZ - headZ) > driveDeadband ||
	        Math.Abs(leftZ - headZ) > driveDeadband */
                dzL*dzL + dyL*dyL > lenArmsSqd && dzR*dzR + dyR*dyR > lenArmsSqd) {
	            buttons |= DRIVE_ENABLED;
            }
            log.WriteLine("DRIVE ENABLED");
        }



        if(headY < 0/* || noTargetsFound */) {
            buttons |= ABORT;
            log.WriteLine("ABORT");
        }


        double footMoveTolerance = .12*unitsPerMeter;
        if(Math.Abs(leftKnee.Position.Y - rightKnee.Position.Y) > footMoveTolerance) {
	
	        buttons |= PICKUP_IN;
            log.WriteLine("PICKUP IN");
	
        }

        double crossTol = .05*unitsPerMeter;
        if(rightHand.Position.X < leftHand.Position.X + crossTol) {
	
	        buttons |= SUCK;
            log.WriteLine("SUCK");
        }

        //Liz's net armspan = 1.7 m (w/ hands)
        double armSpan = 1*unitsPerMeter;
        if(Math.Abs(rightHand.Position.X - leftHand.Position.X) > armSpan) {
	
	        buttons |= SHOOT;
            log.WriteLine("SHOOT");
        }

        //ONLY PUT SOMEWHERE IF YOU FEEL LIKE IT
        /*

        drive
	        arms forward, then up and down

        estop
	        sit

        kinect enable
	        facepalm

        pickup in/out
	        leg up/down

        suck
	        cross arms

        shoot
	        Arms to side

        */




        /*
        if (Math.Math.Abs(leftY - shoulderY) < 0.1 && Math.Math.Abs(rightY - shoulderY) < 0.1) {
            if (leftZ - shoulderZ > 0.5)
                buttons |= 1;
            else if (Math.Math.Abs(leftZ - shoulderZ) < 0.1 && Math.Math.Abs(rightZ - shoulderZ) < 0.1)
                buttons |= 2;
        } else if (leftY > (headY+.1) && rightY > headY) {
            buttons |= 0;
        }
        */




        

            joy[0].Set(leftAxis, buttons);
            joy[1].Set(rightAxis, buttons);
        }


        /*

        //OH, and this may want to go in the drive code, in some mode or another
        //but, idk how much it would really help...

        double gyroFrac = Math.Math.Abs(balanceGyro.getAngle())/30;
        driveVel *= (gyroFrac*2 + 1);
         */

        /// <summary>
        /// Converts units from radians to degrees.
        /// </summary>
        /// <param name="rad">A value in radians.</param>
        /// <returns>The given value in degrees.</returns>
        private double RadToDeg(double rad)
        {
            return (rad * 180 ) / Math.PI;
        }

        /// <summary>
        /// Calculates the XY plane tangent between the given vectors.
        /// </summary>
        /// <param name="origin">The first point.</param>
        /// <param name="measured">The second point.</param>
        /// <param name="mirrored">Whether or not to invert the X axis.</param>
        /// <returns></returns>
        private double AngleXY(Vector origin, Vector measured, bool mirrored = false)
        {
            return Math.Atan2(measured.Y - origin.Y, (mirrored) ? (origin.X - measured.X) : (measured.X - origin.X));
        }

        /// <summary>
        /// Calculates the YZ plane tangent between the given vectors.
        /// </summary>
        /// <param name="origin">The first point.</param>
        /// <param name="measured">The second point.</param>
        /// <param name="mirrored">Whether or not to invert the Z axis.</param>
        /// <returns></returns>
        private double AngleYZ(Vector origin, Vector measured, bool mirrored = false)
        {
            return Math.Atan2(measured.Y - origin.Y, (mirrored) ? (origin.Z - measured.Z) : (measured.Z - origin.Z));
        }

        /// <summary>
        /// Determines whether the given points lie in the same XY plane along the Z axis.
        /// </summary>
        /// <param name="origin">The first point.</param>
        /// <param name="measured">The second point.</param>
        /// <param name="tolerance">The acceptable tolerance between the XY planes of the given points.</param>
        /// <returns>Whether or not the given points are close enough along the Z axis.</returns>
        private bool InSameZPlane(Vector origin, Vector measured, double tolerance)
        {
            return Math.Abs(measured.Z - origin.Z) < tolerance;
        }

        /// <summary>
        /// Converts an input value in the given input range into an output value along the given
        /// output range.
        /// 
        /// If the result would be outside of the given output range, it is constrained to the 
        /// output range.
        /// </summary>
        /// <param name="input">An input value within the given input range.</param>
        /// <param name="inputMin">The minimum expected input value.</param>
        /// <param name="inputMax">The maximum expected input value.</param>
        /// <param name="outputMin">The minimum expected output value.</param>
        /// <param name="outputMax">The maximum expected output value.</param>
        /// <returns>An output value within the given output range proportional to the input.</returns>
        private double CoerceToRange(double input, double inputMin, double inputMax, double outputMin, double outputMax)
        {
            // Determine the center of the input range
            double inputCenter = Math.Abs(inputMax - inputMin) / 2 + inputMin;
            double outputCenter = Math.Abs(outputMax - outputMin) / 2 + outputMin;

            // Scale the input range to the output range
            double scale = (outputMax - outputMin) / (inputMax - inputMin);

            // Apply the transformation
            double result = (input + -inputCenter) * scale + outputCenter;

            // Constrain to the result range
            return Math.Max(Math.Min(result, outputMax), outputMin);
        }
    }
}
/////////////////////////////////////////////////////////////////////////
// Copyright (c) FIRST 2011. All Rights Reserved.							  
// Open Source Software - may be modified and shared by FRC teams. The code   
// must be accompanied by, and comply with the terms of, the license found at
// \FRC Kinect Server\License_for_KinectServer_code.txt which complies
// with the Microsoft Kinect for Windows SDK (Beta) 
// License Agreement: http://kinectforwindows.org/download/EULA.htm
/////////////////////////////////////////////////////////////////////////

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Edu.FIRST.WPI.Kinect.KinectServer.Networking.WritableElements;
using Microsoft.Research.Kinect.Nui;

namespace Edu.FIRST.WPI.Kinect.KinectServer.Kinect
{
    interface IGestureProcessor
    {
        /// <summary>
        /// Updates the given joysticks with axis and button values determined by the 
        /// given skeleton.
        /// </summary>
        /// <param name="joy">The joysticks to update.</param>
        /// <param name="skeleton">The skeleton to process.</param>
        void ProcessGestures(WritableJoystick[] joy, SkeletonData skeleton);
    }
}

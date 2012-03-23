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
using Edu.FIRST.WPI.Kinect.KinectServer.Networking.Serialization;
using System.IO;

namespace Edu.FIRST.WPI.Kinect.KinectServer.Networking.WritableElements
{
    class WritableTrackingStates : IBinaryWritable
    {
        protected byte[] m_trackingStates;

        public WritableTrackingStates(byte[] trackingStates)
        {
            if (trackingStates == null)
                throw new ArgumentException("Array argument must not be null");

            m_trackingStates = trackingStates;
        }

        public void Serialize(NetworkOrderBinaryWriter writer)
        {
            foreach (byte b in m_trackingStates)
            {
                writer.Write(b);
            }
        }

        public void Set(byte[] trackingStates)
        {
            if (trackingStates == null)
                throw new ArgumentException("Array argument must not be null");

            m_trackingStates = trackingStates;
        }
    }
}

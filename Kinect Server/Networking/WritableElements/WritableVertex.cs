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
using System.IO;
using Edu.FIRST.WPI.Kinect.KinectServer.Networking.Serialization;

namespace Edu.FIRST.WPI.Kinect.KinectServer.Networking.WritableElements
{
    class WritableVertex : IBinaryWritable
    {
        protected float m_x;
        protected float m_y;
        protected float m_z;

        public WritableVertex(float x, float y, float z)
        {
            m_x = x;
            m_y = y;
            m_z = z;
        }

        public void Serialize(NetworkOrderBinaryWriter writer)
        {
            writer.Write(m_x);
            writer.Write(m_y);
            writer.Write(m_z);
        }

        public void Set(float x, float y, float z)
        {
            m_x = x;
            m_y = y;
            m_z = z;
        }
    }
}

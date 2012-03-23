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
    class WritableFloorClipPlane : WritableVertex, IBinaryWritable
    {
        protected float m_w;

        public WritableFloorClipPlane(float x, float y, float z, float w)
            : base(x, y, z)
        {
            m_w = w;
        }

        public new void Serialize(NetworkOrderBinaryWriter writer)
        {
            base.Serialize(writer);
            writer.Write(m_w);
        }

        public void Set(float x, float y, float z, float w)
        {
            base.Set(x, y, z);
            m_w = w;
        }
    }
}

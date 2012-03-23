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

namespace Edu.FIRST.WPI.Kinect.KinectServer.Networking.Serialization
{
    class BinaryWritableContainer : List<IBinaryWritable>, IBinaryWritable
    {
        public void Serialize(NetworkOrderBinaryWriter writer)
        {
            foreach (IBinaryWritable element in this)
            {
                element.Serialize(writer);
            }
        }
    }
}

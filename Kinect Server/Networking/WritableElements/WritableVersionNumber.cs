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
    class WritableVersionNumber : IBinaryWritable
    {
        protected String m_version;
       
        public WritableVersionNumber(String versionNumber)
        {
            m_version = versionNumber;

            if (m_version.Length > 11)
                throw new InvalidDataException("Version string must be 11 characters long.");

            while (m_version.Length < 11)
                m_version = m_version + ' ';
        }

        public void Serialize(NetworkOrderBinaryWriter writer)
        {
            writer.Write(m_version);
        }

        public void Set(String versionNumber)
        {
            m_version = versionNumber;

            if (m_version.Length > 11)
                throw new InvalidDataException("Version string must be 11 characters long.");

            while (m_version.Length < 11)
                m_version = m_version + ' ';
        }
    }
}

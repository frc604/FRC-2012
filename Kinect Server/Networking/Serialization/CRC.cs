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
    class CRC : IBinaryWritable
    {
        protected IBinaryWritable m_dataSource;

        public CRC(IBinaryWritable dataSource)
        {
            m_dataSource = dataSource;
        }

        public void Serialize(NetworkOrderBinaryWriter writer)
        {
            MemoryStream tempBuffer = new MemoryStream();
            NetworkOrderBinaryWriter tempWriter = new NetworkOrderBinaryWriter(tempBuffer);

            m_dataSource.Serialize(tempWriter);

            // Read the stream, compute CRC, and add 4 bytes
            // We do nothing for now.
            tempWriter.Write((byte)0);
            tempWriter.Write((byte)0);
            tempWriter.Write((byte)0);
            tempWriter.Write((byte)0);

            writer.BaseStream.Write(tempBuffer.GetBuffer(), 0, (int) tempBuffer.Length);
        }
    }
}

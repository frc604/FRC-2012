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

namespace Edu.FIRST.WPI.Kinect.KinectServer.Networking
{
    class TaggedBundle : BinaryWritableContainer, IBinaryWritable
    {
        protected byte m_tagID;

        public TaggedBundle(byte tagID)
        {
            m_tagID = tagID;
        }

        public new void Serialize(NetworkOrderBinaryWriter writer)
        {
            MemoryStream tempBuffer = new MemoryStream();
            NetworkOrderBinaryWriter tempWriter = new NetworkOrderBinaryWriter(tempBuffer);

            // The tag counts in the length measurement, so buffer it first
            tempWriter.Write(m_tagID);

            foreach(IBinaryWritable bw in this){
                bw.Serialize(tempWriter);
            }

            // Write the length, then everything else (the tag, then the data)
            writer.Write((byte)tempBuffer.Length);
            writer.BaseStream.Write(tempBuffer.GetBuffer(), 0, (int) tempBuffer.Length);
        }
    }
}

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
    /// <summary>
    /// A byte which can be serialized to a network packet.
    /// </summary>
    class WritableByte : IBinaryWritable
    {
        protected byte m_value;

        /// <summary>
        /// Creates a new WritableByte with the given value.
        /// </summary>
        /// <param name="players"></param>
        public WritableByte(byte players)
        {
            m_value = players;
        }

        /// <summary>
        /// Serializes this WritableByte
        /// </summary>
        /// <param name="writer"></param>
        public void Serialize(NetworkOrderBinaryWriter writer)
        {
            writer.Write(m_value);
        }

        /// <summary>
        /// Updates the value of this WritableByte
        /// </summary>
        /// <param name="value">The new value.</param>
        public void Set(byte value)
        {
            m_value = value;
        }

        /// <summary>
        /// Gets the value of this WritableByte
        /// </summary>
        public byte Get()
        {
            return m_value;
        }
    }
}

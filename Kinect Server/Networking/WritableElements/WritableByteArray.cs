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
    /// <summary>
    /// Represents an array of bytes which can be written to the network.
    /// </summary>
    class WritableByteArray : IBinaryWritable
    {
        protected byte[] m_bytes;

        /// <summary>
        /// Creates a new WritableArray with the given data.
        /// </summary>
        /// <param name="data">The data this WritableArray should contain. Must have a length less than or equal to 255.</param>
        public WritableByteArray(byte[] data)
        {
            if (data != null && data.Length > 255)
                throw new ArgumentException("UserBytes data is limited to 255 bytes in length.");

            m_bytes = data;
        }

        /// <summary>
        /// Writes this WritableByteArray to the given network stream.
        /// </summary>
        /// <param name="writer">The network stream to write to.</param>
        public void Serialize(NetworkOrderBinaryWriter writer)
        {
            if (m_bytes != null)
            {
                writer.BaseStream.Write(m_bytes, 0, m_bytes.Length);
            }
        }

        /// <summary>
        /// Updates the value of this WritableByteArray.
        /// </summary>
        /// <param name="data">The new value to assume.</param>
        public void Set(byte[] data)
        {
            if (data != null && data.Length > 255)
                throw new ArgumentException("UserBytes data is limited to 255 bytes in length.");

            m_bytes = data;
        }
    }
}

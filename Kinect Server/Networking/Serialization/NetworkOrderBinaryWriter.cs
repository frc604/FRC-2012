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
    /// <summary>
    /// Writes out data types in binary, big-endian format to a stream.
    /// </summary>
    class NetworkOrderBinaryWriter
    {
        public Stream BaseStream;

        public NetworkOrderBinaryWriter(Stream output)
        {
            BaseStream = output;
        }

        public void Write(float value)
        {
            flipEndiannessAndWrite(BitConverter.GetBytes(value));
        }

        public void Write(uint value)
        {
            flipEndiannessAndWrite(BitConverter.GetBytes(value));
        }

        public void Write(ushort value)
        {
            flipEndiannessAndWrite(BitConverter.GetBytes(value));
        }

        public void Write(byte value)
        {
            BaseStream.Write(new byte[] { value }, 0, 1);
        }

        public void Write(sbyte value)
        {
            Write((byte)value);
        }

        public void Write(String value)
        {
            byte[] bytes = ASCIIEncoding.UTF8.GetBytes(value);
            BaseStream.Write(bytes, 0, bytes.Length);
        }

        protected void flipEndiannessAndWrite(byte[] data)
        {
            for (int i = data.Length - 1; i >= 0; i--)
            {
                BaseStream.Write(data, i, 1);
            }
        }
    }
}

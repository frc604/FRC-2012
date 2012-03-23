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
    class WritableVertices : IBinaryWritable
    {
        protected WritableVertex[] m_vertices;

        public WritableVertices(WritableVertex[] vertices)
        {
            if (vertices == null)
                throw new ArgumentException("Argument must not be null");

            m_vertices = vertices;
        }

        public void Serialize(NetworkOrderBinaryWriter writer)
        {
            foreach (WritableVertex v in m_vertices)
            {
                v.Serialize(writer);
            }
        }

        public void Set(WritableVertex[] vertices)
        {
            if (vertices == null)
                throw new ArgumentException("Argument must not be null");

            m_vertices = vertices;
        }
    }
}

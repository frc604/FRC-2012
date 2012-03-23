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
    class WritableJoystick : IBinaryWritable
    {
        public enum Axis
        {
            X,
            Y,
            Z,
            Twist,
            Throttle,
            Custom
        }

        public enum Buttons
        {
            Btn1 = 1,
            Btn2 = 2,
            Btn3 = 4,
            Btn4 = 8,
            Btn5 = 16,
            Btn6 = 32,
            Btn7 = 64,
            Btn8 = 128,
            Btn9 = 256,
            Btn10 = 512,
            Btn11 = 1024,
            Btn12 = 2048,
        };

        protected sbyte[] m_axis;
        protected ushort m_buttons;

        public WritableJoystick(sbyte[] axis, ushort buttons)
        {
            m_axis = axis;
            m_buttons = buttons;
        }

        public void Serialize(NetworkOrderBinaryWriter writer)
        {
            foreach (sbyte b in m_axis)
            {
                writer.Write(b);
            }
            writer.Write(m_buttons);
        }

        public void Set(sbyte[] axis, ushort buttons)
        {
            if (axis == null)
                throw new ArgumentException("Array argument must not be null");

            m_axis = axis;
            m_buttons = buttons;
        }

        public sbyte[] getAxis()
        {
            sbyte[] copy = new sbyte[m_axis.Length];
            m_axis.CopyTo(copy, 0);
            return copy;
        }

        public ushort getButtons()
        {
            return m_buttons;
        }
    }
}

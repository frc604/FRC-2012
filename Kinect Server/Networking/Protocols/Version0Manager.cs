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
using System.Threading;
using System.Net;
using System.Net.Sockets;
using Edu.FIRST.WPI.Kinect.KinectServer.Networking.Serialization;
using Edu.FIRST.WPI.Kinect.KinectServer.Kinect;
using Microsoft.Research.Kinect.Nui;
using Edu.FIRST.WPI.Kinect.KinectServer.Networking.WritableElements;
using Edu.FIRST.WPI.Kinect.KinectServer;

namespace Edu.FIRST.WPI.Kinect.KinectServer.Networking.Protocols
{
    class Version0Manager : ISkeletonProcessor
    {
        protected const int HEARTBEAT_PERIOD_MS = 450;
        protected String m_hostname;
        protected int m_port;
        protected String m_kinectVersion;
        protected String m_kinectStatus;
        protected UdpClient m_udpClient;
        protected Version0 m_version0Packet;
        protected Timer m_heartbeatTimer;
        protected bool m_started = false;
        protected IGestureProcessor m_gestureProcessor;

        /// <summary>
        /// Manages sending Kinect information as UDP to the specified hostname and port
        /// at least once a second.
        /// 
        /// Uses the FIRST gesture processor to process gestures into joysticks.
        /// </summary>
        /// <param name="kinectVersion">The version string to report to the Driver Station.</param>
        /// <param name="hostname">The destination hostname.</param>
        /// <param name="port">The destination port number.</param>
        public Version0Manager(String kinectVersion, String hostname, int port)
        {
            m_kinectVersion = kinectVersion;
            m_kinectStatus = "No Kinect";
            m_hostname = hostname;
            m_port = port;
            m_udpClient = new UdpClient();
            m_version0Packet = new Version0();
            m_heartbeatTimer = new Timer(this.heartBeatExpired);
            m_heartbeatTimer.Change(HEARTBEAT_PERIOD_MS, HEARTBEAT_PERIOD_MS);
            m_started = true;
            m_gestureProcessor = new FIRSTGestureProcessor();
        }

        /// <summary>
        /// Manages sending Kinect information as UDP to the specified hostname and port
        /// at least once a second.
        /// 
        /// Uses the given gesture processor to process gestures into joysticks.
        /// </summary>
        /// <param name="gp">The IGestureProcessor to use to set joystick values.</param>
        /// <param name="kinectVersion">The version string to report to the Driver Station.</param>
        /// <param name="hostname">The destination hostname.</param>
        /// <param name="port">The destination port number.</param>
        public Version0Manager(IGestureProcessor gp, String kinectVersion, String hostname, int port)
            : this(kinectVersion,
                   hostname, 
                   port)
        {
            m_gestureProcessor = gp;
        }

        /// <summary>
        /// Closes this Version0Manager's associated UDP port and stops the heartbeat.
        /// </summary>
        public void Close()
        {
            m_heartbeatTimer.Change(Timeout.Infinite, Timeout.Infinite);
            m_udpClient.Close();
            m_started = false;
        }

        /// <summary>
        /// Sets the status string being transmitted as part of the packet based on the given status
        /// </summary>
        /// <param name="status">The current Kinect status</param>
        public void SetKinectStatus(MainWindow.ErrorCondition status)
        {
            switch (status)
            {
                case MainWindow.ErrorCondition.None:
                    m_kinectStatus = m_kinectVersion;
                    break;
                case MainWindow.ErrorCondition.NoKinect:
                    m_kinectStatus = "No Kinect";
                    break;
                case MainWindow.ErrorCondition.NotReady:
                    m_kinectStatus = "Not Ready";
                    break;
                case MainWindow.ErrorCondition.NoPower:
                    m_kinectStatus = "No Power";
                    break;
                case MainWindow.ErrorCondition.KinectAppConflict:
                    m_kinectStatus = "In Use";
                    break;
                case MainWindow.ErrorCondition.EngConflict:
                    m_kinectStatus = "Eng In Use";
                    break;
                default:
                    break;
            }
        }

        /// <summary>
        /// Processes the given skeleton and updates the network packet data structure, then
        /// sends the resulting packet.
        /// </summary>
        /// <param name="frame">The Kinect skeleton to process.</param>
        public void ProcessSkeleton(SkeletonFrame frame)
        {
            sbyte[] nullAxis = new sbyte[6];

            // Do stuff here
            lock (m_version0Packet)
            {
                m_version0Packet.PlayerCount.Set((byte)KinectUtils.CountTrackedSkeletons(frame.Skeletons));

                m_version0Packet.Flags.Set(0);      //Flags only accessible in the C++ API??
                m_version0Packet.FloorClipPlane.Set(frame.FloorClipPlane.X,
                                                   frame.FloorClipPlane.Y,
                                                   frame.FloorClipPlane.Z,
                                                   frame.FloorClipPlane.W);
                m_version0Packet.GravityNormal.Set(frame.NormalToGravity.X,
                                                   frame.NormalToGravity.Y,
                                                   frame.NormalToGravity.Z);

                // Get the best skeleton
                SkeletonData s = KinectUtils.SelectBestSkeleton(frame.Skeletons);
               
                m_version0Packet.Quality.Set((byte)s.Quality);
                m_version0Packet.CenterOfMass.Set(s.Position.X,
                                                  s.Position.Y,
                                                  s.Position.Z);
                m_version0Packet.SkeletonTrackState.Set((uint) s.TrackingState);

                // Loop through joints; get tracking states and positions
                byte[] trackingStates = new byte[20];
                WritableVertex[] vertices = new WritableVertex[20];

                for (uint i = 0; i < s.Joints.Count; i++)
                {
                    Joint j = s.Joints[(JointID)i];

                    trackingStates[i] = (byte)(j.TrackingState);
                    vertices[i] = new WritableVertex(j.Position.X,
                                                     j.Position.Y,
                                                     j.Position.Z);
                }
                m_version0Packet.SkeletonTrackingStates.Set(trackingStates);
                m_version0Packet.SkeletonJoints.Set(vertices);

                // Update Joysticks
                WritableJoystick[] sticks = new WritableJoystick[2] { 
                    m_version0Packet.Joystick1, 
                    m_version0Packet.Joystick2
                };
                if (m_version0Packet.PlayerCount.Get() != 0)        //Only process and send valid data if a player is detected
                {
                    m_gestureProcessor.ProcessGestures(sticks, s);
                }
                else
                {
                    m_version0Packet.Joystick1.Set(nullAxis, (ushort)WritableJoystick.Buttons.Btn2);
                    m_version0Packet.Joystick2.Set(nullAxis, (ushort)WritableJoystick.Buttons.Btn2);
                }
            }

            send();
            m_heartbeatTimer.Change(HEARTBEAT_PERIOD_MS, HEARTBEAT_PERIOD_MS);
        }

        /// <summary>
        /// Called when the heart beat timer expires and a packet must be sent.
        /// </summary>
        /// <param name="stateinfo"></param>
        private void heartBeatExpired(Object stateinfo)
        {
            sbyte[] nullAxis = new sbyte[6];

            m_version0Packet.PlayerCount.Set(0);
            m_version0Packet.Joystick1.Set(nullAxis, 0);
            m_version0Packet.Joystick2.Set(nullAxis, 0);
            send();
        }

        /// <summary>
        /// Cuts a new UDP packet and sends it to the Driver Station.
        /// 
        /// If a Kinect is not present, the version number is set to "No Kinect".
        /// Otherwise, the originally specified Kinect version string is reported.
        /// </summary>
        private void send()
        {
            if (!m_started)
                throw new InvalidOperationException("This Version0Manager has been closed and is no longer usable.");

            MemoryStream udpbuffer = new MemoryStream();
            NetworkOrderBinaryWriter writer = new NetworkOrderBinaryWriter(udpbuffer);

            lock (m_version0Packet)
            {
                m_version0Packet.VersionNumber.Set(m_kinectStatus);
                m_version0Packet.Serialize(writer);
            }

            m_udpClient.Send(udpbuffer.GetBuffer(), (int) udpbuffer.Length, m_hostname, m_port);
        }
    }
}

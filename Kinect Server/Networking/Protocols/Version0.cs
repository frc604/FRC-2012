/////////////////////////////////////////////////////////////////////////
// Copyright (c) FIRST 2011. All Rights Reserved.							  
// Open Source Software - may be modified and shared by FRC teams. The code   
// must be accompanied by, and comply with the terms of, the license found at
// \FRC Kinect Server\License_for_KinectServer_code.txt which complies
// with the Microsoft Kinect for Windows SDK (Beta) 
// License Agreement: http://kinectforwindows.org/download/EULA.htm
/////////////////////////////////////////////////////////////////////////

using System;
using System.IO;
using Edu.FIRST.WPI.Kinect.KinectServer.Networking.Serialization;
using Edu.FIRST.WPI.Kinect.KinectServer.Networking.WritableElements;

namespace Edu.FIRST.WPI.Kinect.KinectServer.Networking.Protocols
{
    class Version0 : IBinaryWritable
    {
        protected IBinaryWritable m_packet;

        // Header components
        public WritableVersionNumber VersionNumber;
        public WritableByte PlayerCount;
        public WritableUInt Flags;
        public WritableFloorClipPlane FloorClipPlane;
        public WritableVertex GravityNormal;

        // Skeleton Extra 
        public WritableTrackingStates SkeletonTrackingStates;
        public WritableVertex CenterOfMass;
        public WritableUInt Quality;
        public WritableUInt SkeletonTrackState;

        // Skeleton
        public WritableVertices SkeletonJoints;

        // Joysticks
        public WritableJoystick Joystick1;
        public WritableJoystick Joystick2;

        // User data
        public WritableByteArray UserBytes;

        public Version0()
        {
            BinaryWritableContainer m_packetContainer = new BinaryWritableContainer();
            TaggedBundle m_kinectHeader;
            TaggedBundle m_skeletonExtra;
            TaggedBundle m_skeletonVertices;
            TaggedBundle m_kinectJoystick;
            TaggedBundle m_kinectCustom;

            m_kinectHeader = new TaggedBundle(19);
            m_kinectHeader.Add((VersionNumber = new WritableVersionNumber("01.02.03.04")));
            m_kinectHeader.Add((PlayerCount = new WritableByte(0)));
            m_kinectHeader.Add((Flags = new WritableUInt(0)));    // flags
            m_kinectHeader.Add((FloorClipPlane = new WritableFloorClipPlane(0.1f, 0.1f, 0.1f, 0.1f)));
            m_kinectHeader.Add((GravityNormal = new WritableVertex(0.0f, 0.0f, 0.0f)));  // gravity normal
            m_packetContainer.Add(m_kinectHeader);

            byte[] trackingStates = new byte[20];
            for (uint i = 0; i < trackingStates.Length; i++)
            {
                trackingStates[i] = (byte) Microsoft.Research.Kinect.Nui.JointTrackingState.Tracked;
            }

            m_skeletonExtra = new TaggedBundle(20);
            m_skeletonExtra.Add((SkeletonTrackingStates = new WritableTrackingStates(trackingStates))); // for joints
            m_skeletonExtra.Add((CenterOfMass = new WritableVertex(0.1f, 0.1f, 0.1f))); // center of mass
            m_skeletonExtra.Add((Quality = new WritableUInt(0))); // Quality
            m_skeletonExtra.Add((SkeletonTrackState = new WritableUInt((uint)Microsoft.Research.Kinect.Nui.SkeletonTrackingState.Tracked))); // TrackState
            m_packetContainer.Add(m_skeletonExtra);

            WritableVertex[] vertices = new WritableVertex[20];
            for (uint i = 0; i < vertices.Length; i++)
            {
                vertices[i] = new WritableVertex(0.0f, 0.33f, 2f);
            }

            m_skeletonVertices = new TaggedBundle(21);
            m_skeletonVertices.Add((SkeletonJoints = new WritableVertices(vertices)));
            m_packetContainer.Add(m_skeletonVertices);

            m_kinectJoystick = new TaggedBundle(24);
            m_kinectJoystick.Add((Joystick1 = new WritableJoystick(new sbyte[6], 0)));
            m_kinectJoystick.Add((Joystick2 = new WritableJoystick(new sbyte[6], 0)));
            m_packetContainer.Add(m_kinectJoystick);

            m_kinectCustom = new TaggedBundle(25);
            m_kinectCustom.Add((UserBytes = new WritableByteArray(null)));
            m_packetContainer.Add(m_kinectCustom);

            // compute CRC
            // this simply adds four 0 bytes for now
            m_packet = new CRC(m_packetContainer);
        }

        public void Serialize(NetworkOrderBinaryWriter writer)
        {
            m_packet.Serialize(writer);
        }
    }
}

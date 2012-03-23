/////////////////////////////////////////////////////////////////////////
// Copyright (c) FIRST 2011. All Rights Reserved.							  
// Open Source Software - may be modified and shared by FRC teams. The code   
// must be accompanied by, and comply with the terms of, the license found at
// \FRC Kinect Server\License_for_KinectServer_code.txt which complies
// with the Microsoft Kinect for Windows SDK (Beta) 
// License Agreement: http://kinectforwindows.org/download/EULA.htm
/////////////////////////////////////////////////////////////////////////

using System;
using System.Diagnostics;
using System.Linq;
using System.Windows;
using System.Windows.Documents;
using System.Windows.Input;
using Microsoft.Research.Kinect.Nui;
using Microsoft.Samples.Kinect.WpfViewers;
using System.Runtime.InteropServices;
using Edu.FIRST.WPI.Kinect.KinectServer.Kinect;
using Edu.FIRST.WPI.Kinect.KinectServer.Networking;
using Edu.FIRST.WPI.Kinect.KinectServer.Networking.Protocols;
using System.IO;

namespace Edu.FIRST.WPI.Kinect.KinectServer
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {

        public MainWindow()
        {
            InitializeComponent();
        }

        public bool Minimal = true;
        public bool LogFPS = false;
        public bool RenderColor = false;

        public Runtime Kinect
        {
            get
            {
                return _Kinect;
            }
            set
            {
                if (_Kinect != null)
                {
                    UninitializeKinectServices(_Kinect);
                }
                _Kinect = value;
                if (_Kinect != null)
                {
                    InitializeKinectServices(_Kinect);
                }
            }
        }

        public enum ErrorCondition
        {
            None,
            NoPower,
            NoKinect,
            NotReady,
            KinectAppConflict,
            EngConflict,
        }

        #region Private state
        RuntimeOptions runtimeOptions;
        Version0Manager manager;
        Process[] otherServers;
        Runtime _Kinect;
        DateTime lastTime = DateTime.MaxValue;
        int frameRate = 0;
        int lastFrames = 0;
        int totalFrames = 0; 
        StreamWriter fpsLog;

        #endregion Private state


        #region Window events
        private void Window_Loaded(object sender, EventArgs e)
        {
            //Set process priority to BelowNormal to avoid affecting DS behavior on CPU limited systems.
            Process.GetCurrentProcess().PriorityClass = ProcessPriorityClass.BelowNormal;

            //Kill other instances of the Kinect Server if found. Only one copy can access the Kinect at a time
            otherServers = System.Diagnostics.Process.GetProcessesByName("KinectServer");
            foreach (System.Diagnostics.Process killThis in otherServers)
            {
                if (killThis.Id != Process.GetCurrentProcess().Id)
                {
                    killThis.Kill();
                }
                System.Threading.Thread.Sleep(500); //wait to allow any instance using a Kinect to release it
            }

            if (LogFPS)
            {
                fpsLog = new StreamWriter(Environment.GetFolderPath(Environment.SpecialFolder.CommonDocuments) + "\\FRC\\KinectFPS.txt", false);
            }

            manager = new Version0Manager("01.07.12.00", "localhost", 1155);
           
            KinectStart();

            //If the GUI is enabled, initialize it
            if (!Minimal)
            {
                AddKinectViewer(Kinect);
                UpdateUIBasedOnKinectCount();
            }
        }

        private void Window_Closed(object sender, EventArgs e)
        {
            CleanUpAllKinectViewers();
            if (LogFPS)
                fpsLog.Close();

            KinectStop();
        }
        #endregion Window events

        #region Kinect management

        /// <summary>
        /// Function called whenever the application wants to display the current status of the Kinect device or application
        /// Currently sets the version string being sent out in the Kinect packets, additional methods could be added here
        /// </summary>
        /// <param name="errorCondition">The status to be communicated. Statuses are members of the ErrorCondition enum</param>
        private void ShowStatus(ErrorCondition errorCondition)
        {
            manager.SetKinectStatus(errorCondition);
        }

        /// <summary>
        /// Initializes a Kinect by opening the appropriate streams, and setting an error condition if appropriate.
        /// </summary>
        /// <param name="runtime">The Kinect to initialize</param>
        private void InitializeKinectServices(Runtime runtime)
        {
            bool skeletalViewerAvailable = IsSkeletalViewerAvailable;

            runtimeOptions = skeletalViewerAvailable ?
                                     RuntimeOptions.UseDepthAndPlayerIndex | RuntimeOptions.UseSkeletalTracking | RuntimeOptions.UseColor
                                     : RuntimeOptions.UseDepth | RuntimeOptions.UseColor;

            try
            {
                runtime.Initialize(runtimeOptions);
            }
            catch (COMException comException)
            {
                if (comException.ErrorCode == -2147220947)  //Runtime is being used by another app.
                {
                    runtime = null;
                    ShowStatus(ErrorCondition.KinectAppConflict);
                    return;
                }
                else
                {
                    throw comException;
                }
            }

            if (runtimeOptions.HasFlag(RuntimeOptions.UseSkeletalTracking))
            {
                Kinect.SkeletonEngine.TransformSmooth = true;
                Kinect.SkeletonFrameReady += new EventHandler<SkeletonFrameReadyEventArgs>(SkeletonsReady);
            }

        }

        /// <summary>
        /// Method to uninitialize a Kinect. Closes all streams and removes event handlers
        /// </summary>
        /// <param name="runtime">The Kinect to be uninitialized</param>
        private void UninitializeKinectServices(Runtime runtime)
        {
            runtime.Uninitialize();
            runtime.SkeletonFrameReady -= new EventHandler<SkeletonFrameReadyEventArgs>(SkeletonsReady);
        }

        /// <summary>
        /// Method to be called to start using a Kinect device. Calls other methods to start and initialize the Kinect,
        /// registers Event Handlers and sets status if appropriate.
        /// </summary>
        private void KinectStart()
        {
            KinectDiscovery();

            //listen to any status change for Kinects
            Runtime.Kinects.StatusChanged += new EventHandler<StatusChangedEventArgs>(Kinects_StatusChanged);

            if (Kinect == null)
            {
                if (Runtime.Kinects.Count == 0)
                {
                    ShowStatus(ErrorCondition.NoKinect);
                }
                else
                {
                    if (Runtime.Kinects[0].Status == KinectStatus.NotPowered)
                    {
                        ShowStatus(ErrorCondition.NoPower);
                    }
                }
            }
            else if (!runtimeOptions.HasFlag(RuntimeOptions.UseSkeletalTracking))
                ShowStatus(ErrorCondition.EngConflict);
            else
                ShowStatus(ErrorCondition.None);
        }

        /// <summary>
        /// Method to be called to stop using the Kinect. Removes event handler and sets Kinect to Null.
        /// </summary>
        private void KinectStop()
        {
            Runtime.Kinects.StatusChanged -= new EventHandler<StatusChangedEventArgs>(Kinects_StatusChanged);
            if (Kinect != null)
            {
                Kinect = null;
            }
        }

        /// <summary>
        /// Method to discover and initialize a Kinect attached to the PC
        /// </summary>
        private void KinectDiscovery()
        {
            //loop through all the Kinects attached to this PC, and start the first that is connected without an error.
            foreach (Runtime kinect in Runtime.Kinects)
            {
                if (kinect.Status == KinectStatus.Connected)
                {
                    if (Kinect == null)
                    {
                        Kinect = kinect;
                        return;
                    }
                }
            }
        }

        /// <summary>
        /// Event handler to be called when the Kinect status changes. Initializes or Uninitializes the Kinect as appropriate,
        /// updates the GUI if necessary, and sets the status
        /// </summary>
        /// <param name="sender">Sender</param>
        /// <param name="e">New Status</param>
        private void Kinects_StatusChanged(object sender, StatusChangedEventArgs e)
        {
            switch (e.Status)
            {
                case KinectStatus.Connected:
                    if (Kinect == null)
                    {
                        Kinect = e.KinectRuntime; //if Runtime.Init() fails due to an AppDeviceConflict, this property will be null after return.
                        if (Kinect == null)
                            ShowStatus(ErrorCondition.KinectAppConflict);
                        else
                            ShowStatus(ErrorCondition.None);
                        CleanUpAllKinectViewers();
                        if (!Minimal)
                            AddKinectViewer(Kinect);
                    }
                    break;
                case KinectStatus.Disconnected:
                    if (Kinect == e.KinectRuntime)
                    {
                        Kinect = null;
                    }
                    ShowStatus(ErrorCondition.NoKinect);
                    RemoveKinectViewer(e.KinectRuntime);
                    break;
                case KinectStatus.NotReady:
                    ShowStatus(ErrorCondition.NotReady);
                    break;
                case KinectStatus.NotPowered:
                    if (Kinect == e.KinectRuntime)
                    {
                        Kinect = null;
                    }
                    ShowStatus(ErrorCondition.NoPower);
                    if (!Minimal)
                        DisableOrAddKinectViewer(e.KinectRuntime);
                    break;
                default:
                    throw new Exception("Unhandled Status: " + e.Status);
            }
            UpdateUIBasedOnKinectCount();
        }

        /// <summary>
        /// Checks if the Kinect is started. Returns True if the Kinect is started
        /// </summary>
        private bool IsKinectStarted
        {
            get { return Kinect != null; }
        }

        /// <summary>
        /// Checks if the Skeletal Viewer is currently available. Returns True if the Skeletal Engine is not in use
        /// </summary>
        private bool IsSkeletalViewerAvailable
        {
            get { return Runtime.Kinects.All(k => k.SkeletonEngine == null); }
        }
        #endregion Kinect management

        /// <summary>
        /// Event handler to be called when a Skeleton Frame is ready. Optionally calculates Frame Rate, then processes the skeleton frame using the manager object.
        /// </summary>
        /// <param name="sender">Sender</param>
        /// <param name="e">Skeleton Frame</param>
        void SkeletonsReady(object sender, SkeletonFrameReadyEventArgs e)
        {
            SkeletonFrame skeletonFrame = e.SkeletonFrame;

            //KinectSDK TODO: This nullcheck shouldn't be required. 
            //Unfortunately, this version of the Kinect Runtime will continue to fire some skeletonFrameReady events after the Kinect USB is unplugged.
            if (skeletonFrame == null)
            {
                return;
            }

            if (LogFPS)
                CalculateFrameRate();

            manager.ProcessSkeleton(skeletonFrame);
        }

        /// <summary>
        /// Method to calculate and log the frame rate. Called every time a new frame is processed, logs the number of frames
        /// processed once every second.
        /// </summary>
        private void CalculateFrameRate()
        {
            ++totalFrames;

            DateTime cur = DateTime.Now;
            if (lastTime == DateTime.MaxValue || cur.Subtract(lastTime) > TimeSpan.FromSeconds(1))
            {
                frameRate = totalFrames - lastFrames;
                lastFrames = totalFrames;
                lastTime = cur;
                fpsLog.WriteLine(frameRate);
                fpsLog.Flush();
            }
        }

        /// <summary>
        /// Update the visibility of the status messages based on min/maxKinectCount and the number of Kinects
        ///  that are connected to the system.
        /// </summary>
        private void UpdateUIBasedOnKinectCount()
        {
            switch (Runtime.Kinects.Count)
            {
                case 0:
                    insertKinectSensor.Visibility = System.Windows.Visibility.Visible;
                    break;
                default:
                    insertKinectSensor.Visibility = System.Windows.Visibility.Collapsed;
                    break;
            }
            foreach (UIElement element in viewerHolder.Items)
            {
                var kinectViewer = element as KinectDiagnosticViewer;
                if (kinectViewer != null)
                {
                    kinectViewer.UpdateUi();
                }
            }
        }

        #region KinectViewer Utilities
        /// <summary>
        /// Method to add a KinectViewer to the UI for a given Kinect
        /// </summary>
        /// <param name="runtime">Kinect to use for the viewer</param>
        private void AddKinectViewer(Runtime runtime)
        {
            if(runtime != null)
            {
                var kinectViewer = new KinectDiagnosticViewer(RenderColor);
                kinectViewer.RuntimeOptions = runtimeOptions;
                kinectViewer.Kinect = runtime;
                viewerHolder.Items.Add(kinectViewer);
            }
        }

        /// <summary>
        /// Method to remove the KinectViewer for a given Kinect, if it exists
        /// </summary>
        /// <param name="runtime">The Kinect</param>
        private void RemoveKinectViewer(Runtime runtime)
        {
            var foundViewer = FindViewer(runtime);

            if (foundViewer != null)
            {
                foundViewer.Kinect = null;
                viewerHolder.Items.Remove(foundViewer);
            }
        }

        /// <summary>
        /// Method that adds a KinectViewer for a Kinect if none exists, disables the KinectViewer
        /// if one already exists
        /// </summary>
        /// <param name="runtime">The Kinect</param>
        private void DisableOrAddKinectViewer(Runtime runtime)
        {
            var foundViewer = FindViewer(runtime);

            if (foundViewer != null)
            {
                runtime.Uninitialize();
            }
            else
            {
                AddKinectViewer(runtime);
            }
        }

        /// <summary>
        /// Removes all KinectViewers from the UI
        /// </summary>
        private void CleanUpAllKinectViewers()
        {
            foreach (object item in viewerHolder.Items)
            {
                KinectDiagnosticViewer kinectViewer = item as KinectDiagnosticViewer;
                kinectViewer.Kinect = null;
            }
            viewerHolder.Items.Clear();
        }

        /// <summary>
        /// Finds the KinectViewer associated with a given Kinect. Returns Null if no KinectViewer exists for that Kinect
        /// </summary>
        /// <param name="runtime">The Kinect</param>
        /// <returns>The KinectViewer associated with the Kinect</returns>
        private KinectDiagnosticViewer FindViewer(Runtime runtime)
        {
            // Return the Viewer associated with the runtime.
            return (from v in viewerHolder.Items.OfType<KinectDiagnosticViewer>() where Object.ReferenceEquals(v.Kinect, runtime) select v).FirstOrDefault();
        }

        #endregion KinectViewer Utilities
    }
}

/////////////////////////////////////////////////////////////////////////
// Copyright (c) FIRST 2011. All Rights Reserved.							  
// Open Source Software - may be modified and shared by FRC teams. The code   
// must be accompanied by, and comply with the terms of, the license found at
// \FRC Kinect Server\License_for_KinectServer_code.txt which complies
// with the Microsoft Kinect for Windows SDK (Beta) 
// License Agreement: http://kinectforwindows.org/download/EULA.htm
/////////////////////////////////////////////////////////////////////////

using System;
using System.Configuration;
using System.Windows;

namespace Edu.FIRST.WPI.Kinect.KinectServer
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        private void Application_Startup(object sender, StartupEventArgs e)
        {
            var mainWindow = new Edu.FIRST.WPI.Kinect.KinectServer.MainWindow();

            mainWindow.ShowInTaskbar = false;
            mainWindow.ShowActivated = false;

            foreach (String s in e.Args)
            {
                switch (s)
                {
                    case "-debug":
                        mainWindow.Minimal = false;
                        mainWindow.ShowInTaskbar = true;
                        mainWindow.ShowActivated = true;
                        break;
                    case "-logFPS":
                        mainWindow.LogFPS = true;
                        break;
                    case "-color":
                        mainWindow.RenderColor = true;
                        break;
                    default:
                        break;
                }
            }
            mainWindow.Show();
            if(mainWindow.Minimal)
                MainWindow.Visibility = Visibility.Hidden;
        }
    }
}
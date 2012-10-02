#!/bin/bash

#You just lost the game. That's what you get for looking inside a shell script.

# Anyway... If this doesn't work, try the following
# 1. Use a *nix machine
# 2. chmod +x
# 3. Be lazy and make the jar with an IDE
# 4. cry


# build everything
echo " - Building .java files"
SRC_PATH=TcpCommunicator/src
PATH_TO_STUFF=com/_604robotics/robot2012/vision
mkdir tempbin
javac -d tempbin/ -sourcepath $SRC_PATH $SRC_PATH/$PATH_TO_STUFF/VisionProcessing.java $SRC_PATH/$PATH_TO_STUFF/config/Configger.java 2>&1 | sed "s/^/   javac: /g"



#create manifest files to indicate the main classes
echo "Main-Class: $PATH_TO_STUFF/VisionProcessing" > tmpVisionManifest
echo "Main-Class: $PATH_TO_STUFF/config/Configger" > tmpConfiggerManifest



#jar everything
if [ -d visionJars ]
then
echo " - directory visionJars/ already exists. Placing vision.jar and Configger.jar there."
else
echo " - directory visionJars/ does not exist. Creating the directory and then creating vision.jar and Configger.jar there."
mkdir visionJars
fi

cd tempbin
jar cfm ../visionJars/vision.jar ../tmpVisionManifest *
jar cfm ../visionJars/Configger.jar ../tmpConfiggerManifest *
cd ..




#delete temporary files/directories
echo " - Cleaning up!"
rm -r tempbin
rm tmpVisionManifest
rm tmpConfiggerManifest

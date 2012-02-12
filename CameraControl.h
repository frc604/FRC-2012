#include "CameraData.cpp"
#include "HomebrewTracking.h"

#define CAMERA_IP "10.6.4.11"

#define HALF_IMAGE_WIDTH 320
#define HALF_HORIZONTAL_FOV 0.4102

#define HALF_IMAGE_HEIGHT 240
#define VERTICAL_SCALE_FACTOR 1

static int CameraProcess() {
	static double rad2deg = 180.0 / 3.141592653589793238;
	static float hScaleFactor = HALF_IMAGE_WIDTH / tan(HALF_HORIZONTAL_FOV);
	
	AxisCamera &axisCamera = AxisCamera::GetInstance(CAMERA_IP);
	CameraData &cameraData = CameraData::GetInstance();
	
	ColorImage *image = new ColorImage(IMAQ_IMAGE_HSL);
	bool *success;
	ParticleAnalysisReport *target;
	
	Timer *second = new Timer();
	int fps = 0;
	
	int totalFrames = 0;
	
	second->Start();
	
	while(CameraData::GetInstance().enabled) {
		if(axisCamera.IsFreshImage()) {
			axisCamera.GetImage(image);
			FindRectangles(image, target, success);
			
			if(*success) {
				cameraData.centerX = target->center_mass_x;
				cameraData.centerY = target->center_mass_y;
				cameraData.offsetAngleX = atan(target->center_mass_x - HALF_IMAGE_WIDTH / hScaleFactor) * rad2deg; // TODO: Test!
				cameraData.offsetAngleY = 0; // TODO: Implement!
				//cameraData.offsetAngleY = atan(target->center_mass_y - HALF_IMAGE_HEIGHT / VERTICAL_SCALE_FACTOR);
			} else {
				cameraData.inRange = false;
			}
			
			totalFrames++;
			
			CameraData::GetInstance().frames = totalFrames;
			
			if(second->Get() >= 1) {
				CameraData::GetInstance().fps = fps;
				
				fps = 0;
				second->Reset();
			}
			
			fps++;
			
			if(CameraData::GetInstance().log) {
				printf("???");
				char buffer[128];
				sprintf(buffer, "snapshot-%f.jpg", Timer::GetFPGATimestamp());
				imaqWriteJPEGFile(image->GetImaqImage(), buffer, 1000, NULL);
			}
		}
	}
	
	second->Stop();
	
	return 0;
}

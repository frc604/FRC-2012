#include <nivision.h>
#include <vector>
using namespace std;

#define THRESHOLD_MIN_HUE 59
#define THRESHOLD_MAX_HUE 202
#define THRESHOLD_MIN_SATURATION 12
#define THRESHOLD_MAX_SATURATION 255
#define THRESHOLD_MIN_LUMINANCE 117
#define THRESHOLD_MAX_LUMINANCE 210

void FindRectangles(ColorImage *image, ParticleAnalysisReport_struct *target, bool *success) {
	BinaryImage *binImage = image->ThresholdHSL(THRESHOLD_MIN_HUE, THRESHOLD_MAX_HUE, THRESHOLD_MIN_SATURATION, THRESHOLD_MAX_SATURATION, THRESHOLD_MIN_LUMINANCE, THRESHOLD_MAX_LUMINANCE);
	binImage->ConvexHull(true);
	binImage->RemoveSmallObjects(true, 3);
	
	if(binImage->GetNumberParticles() != 0) {
		*success = true;
		
		vector<ParticleAnalysisReport_struct> *results = binImage->GetOrderedParticleAnalysisReports();
		*target = results->front();
	} else {
		*success = false;
	}
}

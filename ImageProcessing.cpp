 
//**************************************************************************
//* WARNING: This file was automatically generated.  Any changes you make  *
//*          to this file will be lost if you generate the file again.     *
//**************************************************************************
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <nivision.h>
//#include <nimachinevision.h>
//#include <windows.h>

// If you call Machine Vision functions in your script, add NIMachineVision.c to the project.

#define IVA_MAX_BUFFERS 10

#define VisionErrChk(Function) {if (!(Function)) {success = 0; goto Error;}}

typedef enum IVA_ResultType_Enum {IVA_NUMERIC, IVA_BOOLEAN, IVA_STRING} IVA_ResultType;

typedef union IVA_ResultValue_Struct    // A result in Vision Assistant can be of type double, BOOL or string.
{
    double numVal;
    bool   boolVal;
    char*  strVal;
} IVA_ResultValue;

typedef struct IVA_Result_Struct
{
    IVA_ResultType  type;           // Result type
    IVA_ResultValue resultVal;      // Result value
} IVA_Result;

typedef struct IVA_StepResultsStruct
{
    int         numResults;         // number of results created by the step
    IVA_Result* results;            // array of results
} IVA_StepResults;

typedef struct IVA_Data_Struct
{
    Image* buffers[IVA_MAX_BUFFERS];            // Vision Assistant Image Buffers
    IVA_StepResults* stepResults;              // Array of step results
    int numSteps;                               // Number of steps allocated in the stepResults array
    CoordinateSystem *baseCoordinateSystems;    // Base Coordinate Systems
    CoordinateSystem *MeasurementSystems;       // Measurement Coordinate Systems
    int numCoordSys;                            // Number of coordinate systems
} IVA_Data;



static IVA_Data* IVA_InitData(int numSteps, int numCoordSys);
static int IVA_DisposeData(IVA_Data* ivaData);
static int IVA_PushBuffer(IVA_Data* ivaData, Image* image, int bufferNumber);
static int IVA_DisposeStepResults(IVA_Data* ivaData, int stepIndex);
static int IVA_CLRExtractHue(Image* image);
static int IVA_MaskFromROI(Image* image,
                                    ROI*   roi,
                                    int    invertMask,
                                    int    extractRegion);
static int IVA_ParticleFilter(Image* image,
                                       int pParameter[],
                                       float plower[],
                                       float pUpper[],
                                       int pCalibrated[],
                                       int pExclude[],
                                       int criteriaCount,
                                       int rejectMatches,
                                       int connectivity);
static int IVA_Particle(Image* image,
                                 int connectivity,
                                 int pPixelMeasurements[],
                                 int numPixelMeasurements,
                                 int pCalibratedMeasurements[],
                                 int numCalibratedMeasurements,
                                 IVA_Data* ivaData,
                                 int stepIndex);

static IVA_Data* IVA_ProcessImage(Image *image)
{
	int success = 1;
    IVA_Data *ivaData;
    ROI *roi;
    int pParameter[1] = {32};
    float plower[1] = {9.10000038146973};
    float pUpper[1] = {9.89999961853027};
    int pCalibrated[1] = {0};
    int pExclude[1] = {0};
    int pParameter1[1] = {53};
    float plower1[1] = {1.5};
    float pUpper1[1] = {2.09999990463257};
    int pCalibrated1[1] = {0};
    int pExclude1[1] = {0};
    int pKernel[9] = {1,1,1,1,1,1,1,1,1};
    StructuringElement structElem;
    int i;
    int pParameter2[1] = {35};
    float plower2[1] = {0};
    float pUpper2[1] = {10};
    int pCalibrated2[1] = {0};
    int pExclude2[1] = {0};
    int pPixelMeasurements[81] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,
        16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,35,36,
        37,38,39,41,42,43,45,46,48,49,50,51,53,54,55,56,58,59,60,61,
        62,63,64,65,66,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,
        83,84,85,86,87,88};
    int *pCalibratedMeasurements = 0;

    // Initializes internal data (buffers and array of points for caliper measurements)
    VisionErrChk(ivaData = IVA_InitData(10, 0));

	VisionErrChk(IVA_CLRExtractHue(image));

    // Creates a new, empty region of interest.
    VisionErrChk(roi = imaqCreateROI());

    // Creates a new rectangle ROI contour and adds the rectangle to the provided ROI.
    VisionErrChk(imaqAddRectContour(roi, imaqMakeRect(7, 13, 472, 644)));

    //-------------------------------------------------------------------//
    //                           Mask from ROI                           //
    //-------------------------------------------------------------------//

	VisionErrChk(IVA_MaskFromROI(image, roi, FALSE, TRUE));

    // Cleans up resources associated with the object
    imaqDispose(roi);

    //-------------------------------------------------------------------//
    //                          Manual Threshold                         //
    //-------------------------------------------------------------------//

    // Thresholds the image.
    VisionErrChk(imaqThreshold(image, image, 128, 255, TRUE, 1));

	VisionErrChk(IVA_ParticleFilter(image, pParameter, plower, pUpper, 
		pCalibrated, pExclude, 1, TRUE, FALSE));

	VisionErrChk(IVA_ParticleFilter(image, pParameter1, plower1, pUpper1, 
		pCalibrated1, pExclude1, 1, FALSE, TRUE));

    //-------------------------------------------------------------------//
    //                  Advanced Morphology: Convex Hull                 //
    //-------------------------------------------------------------------//

    // Computes the convex envelope for each labeled particle in the source image.
    VisionErrChk(imaqConvexHull(image, image, TRUE));

    //-------------------------------------------------------------------//
    //                         Image Buffer: Push                        //
    //-------------------------------------------------------------------//

    VisionErrChk(IVA_PushBuffer(ivaData, image, 1));

    //-------------------------------------------------------------------//
    //                          Basic Morphology                         //
    //-------------------------------------------------------------------//

    // Sets the structuring element.
    structElem.matrixCols = 3;
    structElem.matrixRows = 3;
    structElem.hexa = FALSE;
    structElem.kernel = pKernel;

    // Applies multiple morphological transformation to the binary image.
    for (i = 0 ; i < 2 ; i++)
    {
        VisionErrChk(imaqMorphology(image, image, IMAQ_ERODE, &structElem));
    }

	VisionErrChk(IVA_ParticleFilter(image, pParameter2, plower2, pUpper2, 
		pCalibrated2, pExclude2, 1, TRUE, TRUE));

	VisionErrChk(IVA_Particle(image, TRUE, pPixelMeasurements, 81, 
		pCalibratedMeasurements, 0, ivaData, 9));

    // Releases the memory allocated in the IVA_Data structure.
    //IVA_DisposeData(ivaData);

    return ivaData;

Error:
	return ivaData;
}

////////////////////////////////////////////////////////////////////////////////
//
// Function Name: IVA_CLRExtractHue
//
// Description  : Extracts the hue plane from a color image.
//
// Parameters   : image  - Input image
//
// Return Value : success
//
////////////////////////////////////////////////////////////////////////////////
static int IVA_CLRExtractHue(Image* image)
{
    int success = 1;
    Image* plane;


    //-------------------------------------------------------------------//
    //                         Extract Color Plane                       //
    //-------------------------------------------------------------------//

    // Creates an 8 bit image that contains the extracted plane.
    VisionErrChk(plane = imaqCreateImage(IMAQ_IMAGE_U8, 7));

    // Extracts the hue plane
    VisionErrChk(imaqExtractColorPlanes(image, IMAQ_HSL, plane, NULL, NULL));

    // Copies the color plane in the main image.
    VisionErrChk(imaqDuplicate(image, plane));

Error:
    imaqDispose(plane);

    return success;
}


////////////////////////////////////////////////////////////////////////////////
//
// Function Name: IVA_MaskFromROI
//
// Description  : Copies the source image to the destination image in the
//                following manner: If a pixel in the mask has a value of 0,
//                the function sets the corresponding source pixel to 0.
//                Otherwise the function copies the corresponding source pixel
//                to the destination image.
//
// Parameters   : image          -  Input image
//                roi            -  Region of interest
//                invertMask     -  Inverts the mask
//                extractRegion  -  Extracts masked region
//
// Return Value : On success, this function returns a non-zero value.
//                On failure, this function returns 0.
//                To get extended error information, call imaqGetLastError().
//
////////////////////////////////////////////////////////////////////////////////
static int IVA_MaskFromROI(Image* image,
                                    ROI*   roi,
                                    int    invertMask,
                                    int    extractRegion)
{
    int success = 1;
    Image* imageMask;
    PixelValue pixValue;
    Rect roiBoundingBox;


    // Creates the image mask.
    VisionErrChk(imageMask = imaqCreateImage(IMAQ_IMAGE_U8, 7));

    // Transforms the region of interest into a mask image.
    VisionErrChk(imaqROIToMask(imageMask, roi, 255, image, NULL));

    if (invertMask)
    {
        pixValue.grayscale = 255;

        // Inverts the mask image.
        VisionErrChk(imaqXorConstant(imageMask, imageMask, pixValue));
    }

    // Masks the input image using the mask image we just created.
    VisionErrChk(imaqMask(image, image, imageMask));

    if (extractRegion)
    {
        // Gets the bounding box for the region of interest.
        VisionErrChk(imaqGetROIBoundingBox(roi, &roiBoundingBox));

        // Extracts the bounding box.
        VisionErrChk(imaqScale(image, image, 1, 1, IMAQ_SCALE_SMALLER, roiBoundingBox));
    }

Error:
    imaqDispose(imageMask);

    return success;
}


////////////////////////////////////////////////////////////////////////////////
//
// Function Name: IVA_ParticleFilter
//
// Description  : Filters particles based on their morphological measurements.
//
// Parameters   : image          -  Input image
//                pParameter     -  Morphological measurement that the function
//                                  uses for filtering.
//                plower         -  Lower bound of the criteria range.
//                pUpper         -  Upper bound of the criteria range.
//                pCalibrated    -  Whether to take a calibrated measurement or not.
//                pExclude       -  TRUE indicates that a match occurs when the
//                                  value is outside the criteria range.
//                criteriaCount  -  number of particle filter criteria.
//                rejectMatches  -  Set this parameter to TRUE to transfer only
//                                  those particles that do not meet all the criteria.
//                                  Set this parameter to FALSE to transfer only those
//                                  particles that meet all the criteria to the destination.
//                connectivity   -  Set this parameter to 1 to use connectivity-8
//                                  to determine whether particles are touching.
//                                  Set this parameter to 0 to use connectivity-4
//                                  to determine whether particles are touching.
//
// Return Value : success
//
////////////////////////////////////////////////////////////////////////////////
static int IVA_ParticleFilter(Image* image,
                                       int pParameter[],
                                       float plower[],
                                       float pUpper[],
                                       int pCalibrated[],
                                       int pExclude[],
                                       int criteriaCount,
                                       int rejectMatches,
                                       int connectivity)
{
    int success = 1;
    ParticleFilterCriteria2* particleCriteria = NULL;
    int i;
    ParticleFilterOptions particleFilterOptions;
    int numParticles;


    //-------------------------------------------------------------------//
    //                          Particle Filter                          //
    //-------------------------------------------------------------------//

    if (criteriaCount > 0)
    {
        // Fill in the ParticleFilterCriteria2 structure.
        particleCriteria = (ParticleFilterCriteria2*)malloc(criteriaCount * sizeof(ParticleFilterCriteria2));

        for (i = 0 ; i < criteriaCount ; i++)
        {
            particleCriteria[i].parameter = (MeasurementType) pParameter[i];
            particleCriteria[i].lower = plower[i];
            particleCriteria[i].upper = pUpper[i];
            particleCriteria[i].calibrated = pCalibrated[i];
            particleCriteria[i].exclude = pExclude[i];
        }
        
        particleFilterOptions.rejectMatches = rejectMatches;
        particleFilterOptions.rejectBorder = 0;
        particleFilterOptions.connectivity8 = connectivity;
        
        // Filters particles based on their morphological measurements.
        VisionErrChk(imaqParticleFilter3(image, image, particleCriteria, criteriaCount, &particleFilterOptions, NULL, &numParticles));
    }

Error:
    free(particleCriteria);

    return success;
}


////////////////////////////////////////////////////////////////////////////////
//
// Function Name: IVA_Particle
//
// Description  : Computes the number of particles detected in a binary image and
//                a 2D array of requested measurements about the particle.
//
// Parameters   : image                      -  Input image
//                connectivity               -  Set this parameter to 1 to use
//                                              connectivity-8 to determine
//                                              whether particles are touching.
//                                              Set this parameter to 0 to use
//                                              connectivity-4 to determine
//                                              whether particles are touching.
//                pixelMeasurements          -  Array of measuremnets parameters
//                numPixelMeasurements       -  Number of elements in the array
//                calibratedMeasurements     -  Array of measuremnets parameters
//                numCalibratedMeasurements  -  Number of elements in the array
//                ivaData                    -  Internal Data structure
//                stepIndex                  -  Step index (index at which to store
//                                              the results in the resuts array)
//
// Return Value : success
//
////////////////////////////////////////////////////////////////////////////////
static int IVA_Particle(Image* image,
                                 int connectivity,
                                 int pPixelMeasurements[],
                                 int numPixelMeasurements,
                                 int pCalibratedMeasurements[],
                                 int numCalibratedMeasurements,
                                 IVA_Data* ivaData,
                                 int stepIndex)
{
    int success = 1;
    int numParticles;
    double* pixelMeasurements = NULL;
    double* calibratedMeasurements = NULL;
    unsigned int visionInfo;
    IVA_Result* particleResults;
    int i;
    int j;
    double centerOfMassX;
    double centerOfMassY;


    //-------------------------------------------------------------------//
    //                         Particle Analysis                         //
    //-------------------------------------------------------------------//

    // Counts the number of particles in the image.
    VisionErrChk(imaqCountParticles(image, connectivity, &numParticles));

    // Allocate the arrays for the measurements.
    pixelMeasurements = (double*)malloc(numParticles * numPixelMeasurements * sizeof(double));
    calibratedMeasurements = (double*)malloc(numParticles * numCalibratedMeasurements * sizeof(double));

    // Delete all the results of this step (from a previous iteration)
    IVA_DisposeStepResults(ivaData, stepIndex);

    // Check if the image is calibrated.
    VisionErrChk(imaqGetVisionInfoTypes(image, &visionInfo));

    // If the image is calibrated, we also need to log the calibrated position (x and y)
    ivaData->stepResults[stepIndex].numResults = (visionInfo & IMAQ_VISIONINFO_CALIBRATION ?
                                                  numParticles * 4 + 1 : numParticles * 2 + 1);
    ivaData->stepResults[stepIndex].results = (IVA_Result*) malloc (sizeof(IVA_Result) * ivaData->stepResults[stepIndex].numResults);
    
    particleResults = ivaData->stepResults[stepIndex].results;

    particleResults->type = IVA_NUMERIC;
    particleResults->resultVal.numVal = numParticles;
    particleResults++;
    
    for (i = 0 ; i < numParticles ; i++)
    {
        // Computes the requested pixel measurements about the particle.
        for (j = 0 ; j < numPixelMeasurements ; j++)
        {
            VisionErrChk(imaqMeasureParticle(image, i, FALSE, (MeasurementType) pPixelMeasurements[j], &pixelMeasurements[i*numPixelMeasurements + j]));
        }

        // Computes the requested calibrated measurements about the particle.
        for (j = 0 ; j < numCalibratedMeasurements ; j++)
        {
            VisionErrChk(imaqMeasureParticle(image, i, TRUE, (MeasurementType) pCalibratedMeasurements[j], &calibratedMeasurements[i*numCalibratedMeasurements + j]));
        }
        
        particleResults->type = IVA_NUMERIC;
        VisionErrChk(imaqMeasureParticle(image, i, FALSE, IMAQ_MT_CENTER_OF_MASS_X, &centerOfMassX));
        particleResults->resultVal.numVal = centerOfMassX;
        particleResults++;

        particleResults->type = IVA_NUMERIC;
        VisionErrChk(imaqMeasureParticle(image, i, FALSE, IMAQ_MT_CENTER_OF_MASS_Y, &centerOfMassY));
        particleResults->resultVal.numVal = centerOfMassY;
        particleResults++;

        if (visionInfo & IMAQ_VISIONINFO_CALIBRATION)
        {
            particleResults->type = IVA_NUMERIC;
            VisionErrChk(imaqMeasureParticle(image, i, TRUE, IMAQ_MT_CENTER_OF_MASS_X, &centerOfMassX));
            particleResults->resultVal.numVal = centerOfMassX;
            particleResults++;

            particleResults->type = IVA_NUMERIC;
            VisionErrChk(imaqMeasureParticle(image, i, TRUE, IMAQ_MT_CENTER_OF_MASS_Y, &centerOfMassY));
            particleResults->resultVal.numVal = centerOfMassY;
            particleResults++;
        }
    }

Error:
    free(pixelMeasurements);
    free(calibratedMeasurements);

    return success;
}


////////////////////////////////////////////////////////////////////////////////
//
// Function Name: IVA_InitData
//
// Description  : Initializes data for buffer management and results.
//
// Parameters   : # of steps
//                # of coordinate systems
//
// Return Value : success
//
////////////////////////////////////////////////////////////////////////////////
static IVA_Data* IVA_InitData(int numSteps, int numCoordSys)
{
    int success = 1;
    IVA_Data* ivaData = NULL;
    int i;


    // Allocate the data structure.
    VisionErrChk(ivaData = (IVA_Data*)malloc(sizeof (IVA_Data)));

    // Initializes the image pointers to NULL.
    for (i = 0 ; i < IVA_MAX_BUFFERS ; i++)
        ivaData->buffers[i] = NULL;

    // Initializes the steo results array to numSteps elements.
    ivaData->numSteps = numSteps;

    ivaData->stepResults = (IVA_StepResults*)malloc(ivaData->numSteps * sizeof(IVA_StepResults));
    for (i = 0 ; i < numSteps ; i++)
    {
        ivaData->stepResults[i].numResults = 0;
        ivaData->stepResults[i].results = NULL;
    }

    // Create the coordinate systems
	ivaData->baseCoordinateSystems = NULL;
	ivaData->MeasurementSystems = NULL;
	if (numCoordSys)
	{
		ivaData->baseCoordinateSystems = (CoordinateSystem*)malloc(sizeof(CoordinateSystem) * numCoordSys);
		ivaData->MeasurementSystems = (CoordinateSystem*)malloc(sizeof(CoordinateSystem) * numCoordSys);
	}

    ivaData->numCoordSys = numCoordSys;

Error:
    return ivaData;
}


////////////////////////////////////////////////////////////////////////////////
//
// Function Name: IVA_DisposeData
//
// Description  : Releases the memory allocated in the IVA_Data structure
//
// Parameters   : ivaData  -  Internal data structure
//
// Return Value : success
//
////////////////////////////////////////////////////////////////////////////////
static int IVA_DisposeData(IVA_Data* ivaData)
{
    int i;


    // Releases the memory allocated for the image buffers.
    for (i = 0 ; i < IVA_MAX_BUFFERS ; i++)
        imaqDispose(ivaData->buffers[i]);

    // Releases the memory allocated for the array of measurements.
    for (i = 0 ; i < ivaData->numSteps ; i++)
        IVA_DisposeStepResults(ivaData, i);

    free(ivaData->stepResults);

    // Dispose of coordinate systems
    if (ivaData->numCoordSys)
    {
        free(ivaData->baseCoordinateSystems);
        free(ivaData->MeasurementSystems);
    }

    free(ivaData);

    return TRUE;
}


////////////////////////////////////////////////////////////////////////////////
//
// Function Name: IVA_PushBuffer
//
// Description  : Stores an image in a buffer
//
// Parameters   : ivaData       -  Internal data structure
//                image         -  image
//                bufferNumber  -  Buffer index
//
// Return Value : success
//
////////////////////////////////////////////////////////////////////////////////
static int IVA_PushBuffer(IVA_Data* ivaData, Image* image, int bufferNumber)
{
    int success = 1;
    ImageType imageType;


    // Release the previous image that was contained in the buffer
    VisionErrChk(imaqDispose(ivaData->buffers[bufferNumber]));

    // Creates an image buffer of the same type of the source image.
    VisionErrChk(imaqGetImageType(image, &imageType));
    VisionErrChk(ivaData->buffers[bufferNumber] = imaqCreateImage(imageType, 7));

    // Copies the image in the buffer.
    VisionErrChk(imaqDuplicate(ivaData->buffers[bufferNumber], image));

Error:
    return success;
}


////////////////////////////////////////////////////////////////////////////////
//
// Function Name: IVA_DisposeStepResults
//
// Description  : Dispose of the results of a specific step.
//
// Parameters   : ivaData    -  Internal data structure
//                stepIndex  -  step index
//
// Return Value : success
//
////////////////////////////////////////////////////////////////////////////////
static int IVA_DisposeStepResults(IVA_Data* ivaData, int stepIndex)
{
    int i;

    
    for (i = 0 ; i < ivaData->stepResults[stepIndex].numResults ; i++)
    {
        if (ivaData->stepResults[stepIndex].results[i].type == IVA_STRING)
            free(ivaData->stepResults[stepIndex].results[i].resultVal.strVal);
    }

    free(ivaData->stepResults[stepIndex].results);

    return TRUE;
}



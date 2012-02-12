#include "WPILib.h"

#define TURRET_POWER_MOD -1

class TurretOutput : public PIDOutput {
	Victor *turretMotor;
	
	public:
		float lastOutput;
		
		TurretOutput(Victor *tM) {
			turretMotor = tM;
			lastOutput = 0;
		}
		
		virtual void PIDWrite(float output) {
			output *= TURRET_POWER_MOD;
			lastOutput = output;
			turretMotor->Set(output);
		}
};

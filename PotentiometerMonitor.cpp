#include "WPILib.h"

class PotentiometerMonitor : public PIDSource {
	AnalogChannel* analogPotentiometer;
	
	public:
		PotentiometerMonitor(AnalogChannel *aP) {
			analogPotentiometer = aP;
		}
		
		float PotentiometerToDegrees(float voltage) {
			if(voltage <= 0.168)
				return (voltage - 0.09) / 0.078 * -80 + 170;
			else
				return (voltage - 0.168) / 0.149 * -55 + 90;
		}

		virtual double PIDGet() {
			return PotentiometerToDegrees(analogPotentiometer->GetVoltage());
		}
};

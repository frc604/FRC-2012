class CameraData {
	public:
		bool enabled;
		bool inRange;
		float centerX;
		float centerY;
		float offsetAngleX;
		float offsetAngleY;
		int frames;
		int fps;
		bool log;
		static CameraData& GetInstance() {
			static CameraData instance;
			return instance;
		}
	private:
		CameraData() {
			enabled = false;
			inRange = false;
			centerX = 0;
			centerY = 0;
			offsetAngleX = 0;
			offsetAngleY = 0;
			log = false;
		}
};

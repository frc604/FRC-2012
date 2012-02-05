class CameraData {
	public:
		bool enabled;
		double centerX;
		double centerY;
		int frames;
		int fps;
		static CameraData& GetInstance() {
			static CameraData instance;
			return instance;
		}
	private:
		CameraData() {
			
		}
};

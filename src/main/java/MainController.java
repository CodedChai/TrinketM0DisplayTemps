
public class MainController {

	SerialController serialController;
	SensorOutput sensorOutput;

	public static void main(String[] args){

		MainController mainController = new MainController();


		mainController.run(  );

	}

	public void run(){
		init();

		while(true){
			try{
				execute();
			} catch ( Exception e ){
				e.printStackTrace();
			}
		}
	}

	public void execute() throws Exception{
		sensorOutput.updateSensors();
		Thread.sleep( 300 );

		String cpuTemp = getFormattedTemperature(sensorOutput.getCPUTemperature());
		String gpuTemp = getFormattedTemperature(sensorOutput.getGPUTemperature());

		serialController.sendData( cpuTemp, gpuTemp );


	}


	// TODO: Don't make this a shitty hack lmao
	public String getFormattedTemperature(String temperature){
		return temperature.substring( 0, 2 );
	}

	public void init(){
		serialController = SerialController.getInstance();
		sensorOutput = SensorOutput.getInstance();

		serialController.initPort();
		sensorOutput.updateSensors();
	}


}

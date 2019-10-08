import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import com.fazecast.jSerialComm.*;

import java.util.List;


public class SensorOutput {

    public static Components components;
    protected static SensorOutput sensorOutput;


    public static SensorOutput getInstance(){
        if(sensorOutput == null){
            sensorOutput = new SensorOutput();
        }

        return sensorOutput;
    }

    /*
    This needs to be called to initialize the process and everytime between reads. The initial run takes a little bit.
     */
    public void updateSensors(){
        components = JSensors.get.components();
    }

    public String getCPUTemperature(){

        List<Cpu> cpus = components.cpus;
        StringBuilder stringBuilder = new StringBuilder(  );

        for (final Cpu cpu : cpus) {
            System.out.println("Found CPU component: " + cpu.name);
            if (cpu.sensors != null) {
                System.out.println("Sensors: ");

                //Print temperatures
                List<Temperature> temps = cpu.sensors.temperatures;
                for (final Temperature temp : temps) {
                    if( temp.name.contains( "Package" )){

                        String tempOutput = temp.value.toString();

                        stringBuilder.append(tempOutput);
                    }
                    System.out.println(temp.name + ": " + temp.value + " C");
                }

            }
        }
        return stringBuilder.toString();
    }

    public String getGPUTemperature(){

        List<Gpu> gpus = components.gpus;
        StringBuilder stringBuilder = new StringBuilder(  );

        for(final Gpu gpu : gpus){
            System.out.println("Found GPU component: " + gpu.name);
            if (gpu.sensors != null) {
                System.out.println("Sensors: ");

                //Print temperatures
                List<Temperature> temps = gpu.sensors.temperatures;
                for (final Temperature temp : temps) {
                    String tempOutput = temp.value.toString();

                    stringBuilder.append(tempOutput);
                    System.out.println(temp.name + ": " + temp.value + " C");
                }

            }
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args){
        System.out.println("Initializing... Please wait.");
        Components components = JSensors.get.components();

        while (true) {
            List<Cpu> cpus = components.cpus;
            List<Gpu> gpus = components.gpus;

            for (final Cpu cpu : cpus) {
                System.out.println("Found CPU component: " + cpu.name);
                if (cpu.sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = cpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                    }

                }
            }

            for(final Gpu gpu : gpus){
                System.out.println("Found GPU component: " + gpu.name);
                if (gpu.sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = gpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                        sendToTrinket(temp.value + "");
                    }

                }
            }

            try{
                components = JSensors.get.components();
                Thread.sleep(1000);
            } catch(Exception e ){
                e.printStackTrace();
            }
        }
    }


    public static void sendToTrinket(String temperature) {
        SerialPort[] serialPorts = SerialPort.getCommPorts();

        for (SerialPort sp : serialPorts) {
            System.out.println(sp.getSystemPortName());
            System.out.println(sp.getDescriptivePortName());
        }


        SerialPort serialPort = SerialPort.getCommPort("COM3");
        serialPort.setComPortParameters(9600, 8, 1, 0); // default connection settings for Arduino
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written

        if (serialPort.openPort()) {
            System.out.println("Successfully opened port.");
        } else {
            System.out.println("Failed to open port. Exiting.");
            return;
        }
        try {
            for (Integer i = 0; i < 4; ++i) {
                serialPort.getOutputStream().write(i.byteValue());
                serialPort.getOutputStream().flush();
                System.out.println("Sent number: " + i);
                Thread.sleep(100);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Well this is awkward...");
        }

        if (serialPort.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Failed to close port :(");
            return;
        }
    }

}

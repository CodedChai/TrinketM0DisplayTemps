import com.fazecast.jSerialComm.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;


public class SerialController {

    protected static SerialController serialController;

    protected static SerialPort serialPort = null;

    public static SerialController getInstance(){
        if(serialController == null){
            serialController = new SerialController();
        }

        return serialController;
    }

    public void closePort(SerialPort port) {
        port.closePort();
    }


    public void scanPortsAndGetArduino() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();

        Arrays.stream( serialPorts ).forEach( sp-> System.out.println(sp.getDescriptivePortName()) );

        while ( serialPort == null ) {
            for(SerialPort port: serialPorts){
                if(initPort(port)){
                    break;
                }
            }
        }

    }

    public void init() {
        scanPortsAndGetArduino();
    }

    public boolean isArduino(SerialPort port){
        try {

            String readValue = Character.toString((char) port.getInputStream().read() );

            System.out.println( "Read: '" + readValue + "' from port");

            return "~".equalsIgnoreCase( readValue );

        } catch ( Exception e ){
            System.out.println( port.getDescriptivePortName() + " was available but is not the Arduino." );
            return false;
        }
    }

    public boolean initPort(SerialPort port) {

        port.setComPortParameters(9600, 8, 1, 0); // default connection settings for Arduino
        port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written

        if (port.openPort()) {
            System.out.println("Successfully opened port: " + port.getDescriptivePortName());
            if(isArduino( port )){
                System.out.println( "Connecting to arduino on port: " + port.getDescriptivePortName()  );
                serialPort = port;
                return true;
            }

            closePort(port);
        }

        System.out.println("Failed to open port. Exiting.");
        return false;
    }

    protected void sendData(String dataToSend) {

        try {
            int dataToSendLength = dataToSend.getBytes().length;

            byte[] bytesToSend = new byte[ dataToSendLength + 1];
            for(int i = 0; i < dataToSendLength; i++){
                bytesToSend[i] = dataToSend.getBytes()[i];
            }

            bytesToSend[dataToSendLength] = '\n';


            serialPort.getOutputStream().write(bytesToSend);
            serialPort.getOutputStream().flush();
            System.out.println("Sent data: " + dataToSend);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send data, will send next time.");
        }
    }


    public void sendData(String... dataToSend){
        if(dataToSend.length > 1){
            sendData(Arrays.stream(dataToSend).collect( Collectors.joining("") ));
        } else {
            sendData(dataToSend);

        }


    }

}

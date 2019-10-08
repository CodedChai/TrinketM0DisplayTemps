import com.fazecast.jSerialComm.*;

import java.util.Arrays;
import java.util.stream.Collectors;


public class SerialController {

    protected static SerialController serialController;

    protected static SerialPort serialPort;

    public static SerialController getInstance(){
        if(serialController == null){
            serialController = new SerialController();
        }

        return serialController;
    }

    public void closePort() {
        serialPort.closePort();
    }

    public SerialPort initPort() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();

        for (SerialPort sp : serialPorts) {
            System.out.println(sp.getSystemPortName());
            System.out.println(sp.getDescriptivePortName());
        }


        serialPort = SerialPort.getCommPort("COM5");
        serialPort.setComPortParameters(9600, 8, 1, 0); // default connection settings for Arduino
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written

        if (serialPort.openPort()) {
            System.out.println("Successfully opened port.");
        } else {
            System.out.println("Failed to open port. Exiting.");
            return null;
        }

        return serialPort;
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
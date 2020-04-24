import com.fazecast.jSerialComm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class SerialController {

    protected static SerialController serialController;

    protected static List<SerialPort> activePorts = new ArrayList <>();

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
        List <SerialPort> serialPorts = new ArrayList <>(Arrays.asList( SerialPort.getCommPorts() ));

        serialPorts.forEach( sp-> System.out.println(sp.getDescriptivePortName()) );

        for(SerialPort port: serialPorts){
            initPort(port);

            try {
                Thread.sleep( 100 );
            }catch ( Exception e ){
                e.printStackTrace();
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

    public void initPort(SerialPort port) {

        port.setComPortParameters(9600, 8, 1, 0); // default connection settings for Arduino
        port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written

        if (port.openPort()) {
            System.out.println("Successfully opened port: " + port.getDescriptivePortName());
            if(isArduino( port )){
                System.out.println( "Connecting to arduino on port: " + port.getDescriptivePortName() );
                activePorts.add( port );
                System.out.println( port.getDescriptivePortName() + " added as an active port." );
                System.out.println( "There are '" + activePorts.size() + "' currently active port(s)." );
            } else {
                closePort(port);
            }
        } else {
            System.out.println("Failed to open port '"+ port.getDescriptivePortName() + "'. Ignoring.");
        }

    }

    protected void sendData(String dataToSend) {

        try {
            int dataToSendLength = dataToSend.getBytes().length;

            byte[] bytesToSend = new byte[ dataToSendLength + 1];
            for(int i = 0; i < dataToSendLength; i++){
                bytesToSend[i] = dataToSend.getBytes()[i];
            }

            bytesToSend[dataToSendLength] = '\n';

            activePorts.parallelStream().forEach( p -> sendAndFlushData(p, bytesToSend) );

            System.out.println("Sent data: " + dataToSend);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send data, will send next time.");
        }
    }

    private static void sendAndFlushData(SerialPort port, byte[] bytesToSend){
        try {
            port.getOutputStream().write(bytesToSend);
            port.getOutputStream().flush();
        } catch ( Exception e ){
            e.printStackTrace();

            System.out.println( "Removing port '" + port.getDescriptivePortName() + "' from list of active ports" );
            activePorts.remove( port );
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

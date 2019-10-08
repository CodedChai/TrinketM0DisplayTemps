import com.fazecast.jSerialComm.*;


public class JSSCTest {

    public static void main(String[] args) throws  Exception {

       // SerialPort sp = initPort();

      //  while(true){
            SerialPort sp = initPort();
            if(sp == null){
                System.out.println("No port detected, shutting down.");
                return;
            }
            sendData(sp);
            Thread.sleep(1500);
            closePort(sp);
       // }
    }

    static void closePort(SerialPort sp){
        sp.closePort();
    }

    static SerialPort initPort(){
        SerialPort[] serialPorts = SerialPort.getCommPorts();

        for (SerialPort sp : serialPorts) {
            System.out.println(sp.getSystemPortName());
            System.out.println(sp.getDescriptivePortName());
        }


        SerialPort serialPort = SerialPort.getCommPort("COM5");
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

    static void sendData( SerialPort sp){

        try {
            int output = 69;
            for (Integer i = 0; i < 15; ++i) {
                String st = output + "";
                output++;

                sp.getOutputStream().write( st.getBytes());
                sp.getOutputStream().write('\n');
                sp.getOutputStream().flush();
                System.out.println("Sent number: " + i);
                Thread.sleep(100);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Well this is awkward...");
        }

    }
}

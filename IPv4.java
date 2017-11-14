package project5;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;

/*  Oscar Alcaraz
	CS 380 Networks
	Project 5
*/

public class IPv4 {

    private static int packetSize = 2;
    private static String recievedResponse = "empty";

    private static final int VERSION = 4;
    private static final int H_LEN = 5;
    private static final int TOS = 0;
    private static final byte INDENT = 0;
    private static final int FLAG = 2;
    private static final byte OFFSET = 0;
    private static final int TTL = 50;
    private static final int PROTOCOL = 17; //UDP
    
    // Google address
    private static final byte[] SOURCE_ADDR = {(byte) 172, (byte) 217, 11, 78};
    
    // server address
    private static byte[] DESTINTION_ADDR;

    public IPv4(Socket socket) {
    	
        DESTINTION_ADDR = socket.getInetAddress().getAddress();
    }

    public byte[] generateUDPPacket(int size, byte[] udp) {
    	
        int length = udp.length + 20;
        byte[] packet = new byte[length];

        packet[0] = (VERSION * 16) + H_LEN;
        packet[1] = TOS;
        packet[2] = (byte) ((length >>> 8) & 0xFF);
        packet[3] = (byte) (length & 0xFF);
        packet[4] = INDENT;
        packet[5] = INDENT;
        packet[6] = (byte) (FLAG * 32);
        packet[7] = OFFSET;
        packet[8] = TTL;
        packet[9] = PROTOCOL;

        int count = 0; 
        for(int i = 12; i < 16; ++i) {
        	
            packet[i] = SOURCE_ADDR[count++];
        }
                
        count = 0; 
        for(int k = 16; k < 20; ++k) {
        	
            packet[k] = DESTINTION_ADDR[count++];
        }

        byte[] checkSum = getCheckSum(packet);
        packet[10] = checkSum[0];
        packet[11] = checkSum[1];

        // generate Data
        count = 0;
        
        for(int i = 20; i < packet.length; ++i) {
        	
            packet[i] = udp[count++];
        }

        return  packet;
    }


    // Returns the checksum as a byte array
    // so that it can then be placed in the proper
    // indecies in the packet
    private byte[] getCheckSum(byte[] packet) {
    	
        short checkSum = checkSum(packet);
        
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(checkSum);
        
        return buffer.array();
    }

    // Will calculate the check sum for the packet
    public short checkSum(byte[] b) {
    	
        long sum = 0;
        int length = b.length;
        int i = 0;
        long highVal;
        long lowVal;
        long value;

        while(length > 1){
        	
            //gets the two halves of the whole byte and adds to the sum
            highVal = ((b[i] << 8) & 0xFF00);
            lowVal = ((b[i + 1]) & 0x00FF);
            
            value = highVal | lowVal;
            sum += value;

            //check for the overflow
            if ((sum & 0xFFFF0000) > 0) {
            	
                sum = sum & 0xFFFF;
                sum += 1;
            }

            //iterates
            i += 2;
            length -= 2;
            
        }
        
        //leftover bits
        if(length > 0){
        	
            sum += (b[i] << 8 & 0xFF00);
            
            if ((sum & 0xFFFF0000) > 0) {
            	
                sum = sum & 0xFFFF;
                sum += 1;
            }
        }

        sum = ~sum;
        sum = sum & 0xFFFF;
        
        return (short)sum;
    }
    

    // Builds the packet with the proper values,
    // calculates the check sum, and returns the packet
    public byte[] generateHandShake() {

        int length = 4 + 20;
        byte[] packet = new byte[length];

        packet[0] = (VERSION * 16) + H_LEN;
        packet[1] = TOS;
        packet[2] = (byte) (length >> 8);
        packet[3] = (byte) length;
        packet[4] = INDENT;
        packet[5] = INDENT;
        packet[6] = (byte) (FLAG * 32);
        packet[7] = OFFSET;
        packet[8] = TTL;
        packet[9] = PROTOCOL;

        int count = 0;      
        for(int i = 12; i < 16; ++i) {
        	
            packet[i] = SOURCE_ADDR[count++];
        }
        
        count = 0;        
        for(int k = 16; k < 20; ++k) {
        	
            packet[k] = DESTINTION_ADDR[count++];
        }

        byte[] checkSum = getCheckSum(packet);
        packet[10] = checkSum[0];
        packet[11] = checkSum[1];

        packet[20] = (byte)0xDE;
        packet[21] = (byte)0xAD;
        packet[22] = (byte)0xBE;
        packet[23] = (byte)0xEF;

        return packet;
    }

    public byte[] getSourceAddr() {
    	
        return SOURCE_ADDR;
    }

    public byte[] getDestintionAddr() {
    	
        return DESTINTION_ADDR;
    }
}
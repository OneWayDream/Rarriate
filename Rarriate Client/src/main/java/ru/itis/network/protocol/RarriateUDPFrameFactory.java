package ru.itis.network.protocol;

import lombok.*;
import ru.itis.exceptions.IncorrectFCSException;
import ru.itis.exceptions.UDPFrameFactoryException;
import ru.itis.protocol.UDPFrame;
import ru.itis.protocol.UDPFrameFactory;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;


// Message Content : [PR][SFD][TYPE][L][DATA][FCS]
// PR (preamble) - preamble for chat frames (11011100); [8 bit]
// SFD (start frame delimiter) - SFD for char frames (10101010); [8 bit]
// TYPE - type of frame content; [8 bit]
// LENGTH - data length; [16 bit]
// DATA - frame data; [0 - 65536 byte]
// FCS (frame check sequence) - frame check for chat frames; [8 bit]
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class RarriateUDPFrameFactory implements UDPFrameFactory {

    protected byte pr;
    protected byte sdf;
    protected int maxLength;
    protected int maxTypeValue;
    protected int minTypeValue;

    @Override
    public UDPFrame createUDPFrame(int messageType, Object ... messageContent){
        return new UDPFrame(this, messageType, messageContent);
    }

    @Override
    public UDPFrame readUDPFrame (DatagramChannel datagramChannel) throws UDPFrameFactoryException, IncorrectFCSException {
        try{
            UDPFrame result = null;
            ByteBuffer buffer = ByteBuffer.allocate(maxLength + 6);
//            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
//            datagramChannel.socket().receive(packet);
            datagramChannel.receive(buffer);

            byte[] recvData = buffer.array();
            short dataLength = ByteBuffer.wrap(new byte[]{recvData[3], recvData[4]}).getShort();
            recvData = Arrays.copyOfRange(recvData, 0, dataLength + 6);

//            System.out.println(recvData.length);
//            System.out.println(Arrays.toString(recvData));

            int currentSum = 0;
            for (int i = 0; i < recvData.length - 1; i++){
                currentSum+=recvData[i];
            }
            currentSum = currentSum - recvData[3] - recvData[4] + dataLength;
            byte fcs = (byte) (currentSum%3 << 6 | currentSum%5 << 3 | currentSum%7);

//            System.out.println(fcs);
//            System.out.println(recvData[recvData.length-1]);

            if (recvData[recvData.length-1] == fcs){
                if ((recvData[0]==pr)&&(recvData[1]==sdf)){
                    byte type = recvData[2];

//                    System.out.println(type);
//
//                    System.out.println(Arrays.toString(Arrays.copyOfRange(recvData, 5, recvData.length - 1)));

                    ByteArrayInputStream byteStream = new ByteArrayInputStream(Arrays.copyOfRange(recvData, 5, recvData.length - 1));
                    ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(byteStream));
                    Object[] objects;
                    switch (type){
                        case 0:
                            objects = new Object[3];
                            objects[0] = in.readObject(); // Client id
                            objects[1] = in.readObject(); // new X coordinates
                            objects[2] = in.readObject(); // new Y coordinates
                            break;
                        case 1:
                            objects = new Object[4];
                            objects[0] = in.readObject(); // Message id
                            objects[1] = in.readObject(); // Player nickname
                            objects[2] = in.readObject(); // new X coordinates
                            objects[3] = in.readObject(); // new Y coordinates
                            break;
                        default:
                            objects = new Object[0];
                            break;
                    }
                    in.close();
                    byteStream.close();
                    result = new UDPFrame(this, type, objects);
                }
                return result;
            } else {
                throw new IncorrectFCSException();
            }
        } catch (IOException ex) {
            throw new UDPFrameFactoryException("Cannot read UDP frame.", ex);
        } catch (ClassNotFoundException ex){
            throw new UDPFrameFactoryException("Unknown class in frame content", ex);
        }
    }

    @Override
    public void writeUDPFrame (DatagramChannel datagramChannel, UDPFrame udpFrame, InetSocketAddress address) throws UDPFrameFactoryException {
        try{
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(3 + maxLength);
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
//            out.write(pr);
//            out.write(sdf);
//            out.write(udpFrame.getType());
            for (int i = 0; i < udpFrame.getContent().length; i++){
                out.writeObject(udpFrame.getContent()[i]);
            }
            out.flush();
            byte[] sendBuf = byteStream.toByteArray();

//            System.out.println(Arrays.toString(sendBuf));

            int length = sendBuf.length;
            int currentSum = pr + sdf + udpFrame.getType() + length;
            ByteBuffer lengthBuffer = ByteBuffer.allocate(2);
            lengthBuffer.putShort((short) length);
            lengthBuffer.flip();
            byte[] sendBufWithFcs = new byte[sendBuf.length+6];
            sendBufWithFcs[0] = pr;
            sendBufWithFcs[1] = sdf;
            sendBufWithFcs[2] = (byte) udpFrame.getType();
            sendBufWithFcs[3] = lengthBuffer.get();
            sendBufWithFcs[4] = lengthBuffer.get();
            for (int i = 0; i < sendBuf.length; i++){
                currentSum+=sendBuf[i];
                sendBufWithFcs[i+5] = sendBuf[i];
            }
            byte fcs = (byte) (currentSum%3 << 6 | currentSum%5 << 3 | currentSum%7);
            sendBufWithFcs[sendBufWithFcs.length-1] = fcs;

            datagramChannel.send(ByteBuffer.wrap(sendBufWithFcs), address);
            out.close();
        } catch (UnknownHostException ex) {
            throw new UDPFrameFactoryException("Unknown host.", ex);
        } catch (IOException ex) {
            throw new UDPFrameFactoryException("Cannot send UDP message", ex);
        }
    }

}

package ru.itis.network.protocol;

import lombok.*;
import ru.itis.exceptions.IncorrectFCSException;
import ru.itis.exceptions.TCPFrameFactoryException;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.TCPFrameFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;


// Message Content : [PR][SFD][TYPE][L][DATA][FCS]
// PR (preamble) - preamble for chat frames (11011100); [8 bit]
// SFD (start frame delimiter) - SFD for char frames (10101010); [8 bit]
// TYPE - type of frame content : [8 bit]
// L (length) - frame data length; [16 bit]
// DATA - frame data; [0 - 65536 byte]
// FCS (frame check sequence) - frame check for chat frames; [8 bit]
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class RarriateTCPFrameFactory implements TCPFrameFactory {

    protected byte pr;
    protected byte sdf;
    protected int maxLength;
    protected int maxTypeValue;
    protected int minTypeValue;

    @Override
    public TCPFrame createTCPFrame(int messageType, Object... messageContent) {
        return new TCPFrame(this, messageType, messageContent);
    }

    @Override
    public TCPFrame readTCPFrame(SocketChannel serverTCPSocket) throws TCPFrameFactoryException, IncorrectFCSException {
        try{
            TCPFrame result = null;
            InputStream in = serverTCPSocket.socket().getInputStream();
            byte framePr = (byte) in.read();
            byte frameSdf = (byte) in.read();
            if ((framePr == pr)&&(frameSdf == sdf)){
                byte type = (byte) in.read();
                int dataLength = ByteBuffer.wrap(new byte[]{(byte) in.read(), (byte) in.read()}).getShort();
                byte[] recvData = new byte[dataLength + 1];
                for (int i = 0; i < dataLength + 1; i++){
                    recvData[i] = (byte) in.read();
                }
                int currentSum = pr + sdf + type + dataLength;
                for (int i = 0; i < dataLength; i++){
                    currentSum+=recvData[i];
                }
                byte fcs = (byte) (currentSum%3 << 6 | currentSum%5 << 3 | currentSum%7);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(Arrays.copyOfRange(recvData, 0, recvData.length));
                ObjectInputStream inObject = new ObjectInputStream(new BufferedInputStream(byteStream));
                Object[] objects;

                //System.out.println(Arrays.toString(recvData));

                if (recvData[recvData.length-1] == fcs){
                    switch (type){
                        //TODO switch options for different types
                        case 1:
                            objects = new Object[3];
                            objects[0] = inObject.readObject(); //Message id
                            objects[1] = inObject.readObject(); //Client udp get address
                            objects[2] = inObject.readObject(); //Player data
                            break;
                        case 2:
                            objects = new Object[5];
                            objects[0] = inObject.readObject(); //Message id
                            objects[1] = inObject.readObject(); //Client id
                            objects[2] = inObject.readObject(); //Server udp get address
                            objects[3] = inObject.readObject(); //Server id
                            objects[4] = inObject.readObject(); //World
                            break;
                        default:
                            objects = new Object[0];
                            break;
                    }
                } else {
                    throw new IncorrectFCSException(inObject.readObject());
                }
                inObject.close();
                byteStream.close();
                result = new TCPFrame(this, type, objects);
            }
            //in.close(); - Not close!!!!!!!
            return result;
        } catch (IOException ex) {
            throw new TCPFrameFactoryException("Cannot read TCP frame.", ex);
        } catch (ClassNotFoundException ex){
            throw new TCPFrameFactoryException("Unknown class in frame content", ex);
        }
    }

    @Override
    public void writeTCPFrame(SocketChannel serverTCPSocket, TCPFrame tcpFrame) throws TCPFrameFactoryException {
        try{
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(maxLength);
            ObjectOutputStream outObject = new ObjectOutputStream(new BufferedOutputStream(byteStream));
            for (int i = 0; i < tcpFrame.getContent().length; i++){
                outObject.writeObject(tcpFrame.getContent()[i]);
            }
            outObject.flush();
            outObject.close();
            byte[] sendBuf = byteStream.toByteArray();
            int currentSum = pr + sdf + tcpFrame.getType() + sendBuf.length;
            for (int i = 0; i < sendBuf.length; i++){
                currentSum+=sendBuf[i];
            }
            byte fcs = (byte) (currentSum%3 << 6 | currentSum%5 << 3 | currentSum%7);
            OutputStream out = serverTCPSocket.socket().getOutputStream();
            out.write(pr);
            out.write(sdf);

            System.out.println(tcpFrame.getType());

            out.write(tcpFrame.getType());

            ByteBuffer byteBuffer = ByteBuffer.allocate(2);
            byteBuffer.putShort((short) sendBuf.length);
            out.write(byteBuffer.array());

            out.write(sendBuf);
            out.write(fcs);
            out.flush();
            //out.close(); - Not close!!!!!!!!!!
        }catch (IOException ex) {
            throw new TCPFrameFactoryException("Cannot send TCP message", ex);
        }
    }
}

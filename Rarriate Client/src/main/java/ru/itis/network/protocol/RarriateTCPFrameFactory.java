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
    public TCPFrame readTCPFrame(SocketChannel channel) throws TCPFrameFactoryException, IncorrectFCSException {
        TCPFrame result = null;
        try{
            ByteBuffer serviceBytesBuffer = ByteBuffer.allocate(5);
            channel.read(serviceBytesBuffer);
            serviceBytesBuffer.flip();
            byte framePr = serviceBytesBuffer.get();
            byte frameSdf = serviceBytesBuffer.get();
            if ((framePr == pr)&&(frameSdf == sdf)){
                byte type = serviceBytesBuffer.get();
                int dataLength = serviceBytesBuffer.getShort();
                ByteBuffer recvDataBuffer = ByteBuffer.allocate(dataLength + 1);
                channel.read(recvDataBuffer);
                byte[] recvData = recvDataBuffer.array();
                int currentSum = pr + sdf + type + dataLength;
                for (int i = 0; i < dataLength; i++){
                    currentSum+=recvData[i];
                }
                byte fcs = (byte) (currentSum%3 << 6 | currentSum%5 << 3 | currentSum%7);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(Arrays.copyOfRange(recvData, 0, recvData.length));
                ObjectInputStream inObject = new ObjectInputStream(new BufferedInputStream(byteStream));
                Object[] objects;

                if (recvData[recvData.length-1] == fcs){
                    switch (type){
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
                        case 3:
                            objects = new Object[0]; //Info-frame
                            break;
                        case 4:
                            objects = new Object[2];
                            objects[0] = inObject.readObject(); //Message id
                            objects[1] = inObject.readObject(); //Player data
                            break;
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            objects = new Object[2];
                            objects[0] = inObject.readObject(); //Message id
                            objects[1] = inObject.readObject(); //AbstractBlock data (5-6 - break block, 7-8 - place block)
                            break;
                        case 9:
                            objects = new Object[2];
                            objects[0] = inObject.readObject(); //Message id
                            objects[1] = inObject.readObject(); //Message-text
                            break;
                        case 10:
                            objects = new Object[3];
                            objects[0] = inObject.readObject(); //Message id
                            objects[1] = inObject.readObject(); //Sender-nickname
                            objects[2] = inObject.readObject(); //Message-text
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
            return result;
        } catch (IOException ex) {
            throw new TCPFrameFactoryException("Cannot read TCP frame.", ex);
        } catch (ClassNotFoundException ex){
            throw new TCPFrameFactoryException("Unknown class in frame content", ex);
        }
    }

    @Override
    public void writeTCPFrame(SocketChannel channel, TCPFrame tcpFrame) throws TCPFrameFactoryException {
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

            ByteBuffer sendData = ByteBuffer.allocate(5 + sendBuf.length + 1);
            sendData.put(pr);
            sendData.put(sdf);
            sendData.put((byte) tcpFrame.getType());
            sendData.putShort((short) sendBuf.length);
            sendData.put(sendBuf);
            sendData.put(fcs);
            sendData.flip();
            channel.write(sendData);

        }catch (IOException ex) {
            throw new TCPFrameFactoryException("Cannot send TCP message", ex);
        }
    }
}

package ru.itis.protocol;


import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TCPFrame {

    protected RarriateTCPFrameFactory tcpFrameFactory;
    protected int type;
    protected Object[] content;

    public TCPFrame (RarriateTCPFrameFactory tcpFrameFactory, int type, Object ... content){
        this.tcpFrameFactory = tcpFrameFactory;
        this.type = type;
        this.content = content;
    }

}

package ru.itis.protocol;


import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UDPFrame {

    protected UDPFrameFactory udpFrameFactory;
    protected int type;
    protected Object[] content;

    public UDPFrame (UDPFrameFactory udpFrameFactory, int type, Object ... content){
        this.udpFrameFactory = udpFrameFactory;
        this.type = type;
        this.content = content;
    }

}

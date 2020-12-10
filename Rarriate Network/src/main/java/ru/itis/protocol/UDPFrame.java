package ru.itis.protocol;


import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UDPFrame {

    protected RarriateUDPFrameFactory udpFrameFactory;
    protected int type;
    protected Object[] content;

    public UDPFrame (RarriateUDPFrameFactory udpFrameFactory, int type, Object ... content){
        this.udpFrameFactory = udpFrameFactory;
        this.type = type;
        this.content = content;
    }

}

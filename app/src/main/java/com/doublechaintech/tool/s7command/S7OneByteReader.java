package com.doublechaintech.tool.s7command;

import com.github.s7connector.api.DaveArea;
import com.github.s7connector.api.S7Connector;
import com.github.s7connector.api.factory.S7ConnectorFactory;

import java.io.IOException;

public class S7OneByteReader {
    private static S7Connector connectWith(Param param) {
        return S7ConnectorFactory
                .buildTCPConnector()

                .withHost(param.getIp())
                .withPort(param.getPort())
                .withRack(0) //optional
                .withSlot(0) //optional
                .build();
    }
    private static byte readData(Param param) throws IOException {
        try{
            S7Connector connector = connectWith(param);
            byte[] bs = connector.read(DaveArea.DB, param.getDbBock(), 1, 0);
            connector.close();
            return bs[0];
        }catch (Exception e){
            throw new IllegalArgumentException("发生错误，链接参数:" + param.toString());
        }

    }

    public static byte readOneByte() throws IOException {
        Param p=Param.createFromArgs();
        byte value = readData(p);
        return value;

    }
}

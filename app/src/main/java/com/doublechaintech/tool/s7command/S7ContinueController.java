package com.doublechaintech.tool.s7command;

import java.io.IOException;

public class S7ContinueController extends ContinueController{

    public boolean continueToGo(){
        try {
            byte value=S7OneByteReader.readOneByte();
            return value==1;
        } catch (Exception e) {
            System.out.println("请检查链接: " +e.getMessage());
            //System.exit(-100);
            return true;
        }
    }

}

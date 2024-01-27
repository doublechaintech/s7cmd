package com.doublechaintech.tool.s7command;

import com.github.s7connector.api.DaveArea;
import com.github.s7connector.api.S7Connector;
import com.github.s7connector.api.factory.S7ConnectorFactory;

import java.io.IOException;
import java.util.Arrays;
import java.io.IOException;
import java.util.Arrays;

class TestCase{

    protected String name;
    protected int startAreaNumber;
    protected int endAreaNumber;
    protected int size;
    protected boolean fail;
    protected String errorMessage;


    public TestCase(String name, int startAreaNumber, int endAreaNumber, int size) {
        this.name = name;
        this.startAreaNumber = startAreaNumber;
        this.endAreaNumber = endAreaNumber;
        this.size = size;
        this.fail=false;
    }


}

public class S7ConnectorTest {

    public static  long testWriteBlock(S7Connector connector,int startAreaNumber, int endAreaNumber, int size){

        long start=System.currentTimeMillis();
        byte[] bs = connector.read(DaveArea.DB, startAreaNumber, size, 0);

        for(int i=startAreaNumber;i<=endAreaNumber;i++){
            connector.write(DaveArea.DB, i, 0, bs);
        }
        long end=System.currentTimeMillis();
        return end-start;

    }
    public static  long testReadBlock(S7Connector connector,int startAreaNumber, int endAreaNumber, int size){

        long start=System.currentTimeMillis();

        for(int i=startAreaNumber;i<=endAreaNumber;i++){
            //connector.write(DaveArea.DB, i, 0, bs);
            connector.read(DaveArea.DB, i, size, 0);
        }
        long end=System.currentTimeMillis();
        return end-start;

    }
    public static  boolean testOneRound(S7Connector connector){
        TestCase [] testCases=new TestCase[]{
                new TestCase("阀门DB1",5001,5039,60),
                new TestCase("阀门DB2",5101,5139,60),
                new TestCase("阀门DB3",5201,5239,60),

                new TestCase("模拟量DB1",3001,3016,60),
                new TestCase("模拟量DB2",3101,3111,60),
                new TestCase("模拟量DB3",3201,3217,60),

                new TestCase("配方DB",6501,6539,8556),
                new TestCase("报告DB",2991,2998,12556)

        };





        Arrays.asList(testCases).forEach(t->{
            try{
                long time=testReadBlock(connector,t.startAreaNumber,t.endAreaNumber,t.size);
                int count = t.endAreaNumber-t.startAreaNumber+1;
                String mesg=String.format("%s\t%d\t%d\t%d\t%d\t%d\t%d(ms)",
                        t.name,t.startAreaNumber,t.endAreaNumber,t.size,count,time,(time/count));
                log(mesg);
            }catch (Exception e){
                t.fail=true;
                t.errorMessage=e.getMessage();
                log(t.name+"\t"+e.getMessage());
            }


        });

        if(Arrays.asList(testCases).stream().filter(t->t.fail).findAny().isPresent()){

            return false;

        }
        return true;


    }
    public static void main4(String []args) throws IOException {


        if(args.length<1){
            log("use like java -jar s7test <count>");
            return ;
        }
        Integer runCount=Integer.parseInt(args[0]);


        S7Connector connector =
                S7ConnectorFactory
                        .buildTCPConnector()
                        //.withHost("192.168.50.1")
                        //.withPort(7777)
                        .withHost("192.168.50.5")
                        .withPort(102)
                        //.withType(1) //optional
                        .withRack(0) //optional
                        .withSlot(0) //optional
                        .build();


        int completeCount=0;
        for(int i=0;i<runCount;i++){
            boolean needToContinue = testOneRound(connector);
            if(!needToContinue){
                completeCount=i;
                break;
            }
        }

        log(String.format("Planned Count:  %s, Complete Count: %s",runCount,completeCount));




        //Write to DB100 10 bytes


        //Close connection
        connector.close();
    }

    private static void log(String mesg) {


        System.out.println(mesg);

    }

    public static void main(String []args) throws IOException {

        S7Connector connector =
                S7ConnectorFactory
                        .buildTCPConnector()
                        .withHost("iot.doublechaintech.com")
                        .withPort(6899)
                        //.withType(1) //optional
                        .withRack(0) //optional
                        .withSlot(0) //optional
                        .build();

        //Read from DB100 10 bytes
        byte[] bs = connector.read(DaveArea.DB, 6501, 8556, 0);

        for(int i=0;i<bs.length;i++){
            System.out.printf("%02X ",bs[i]);
        }
        System.out.println();
        //Set some bytes
        //bs[0] = 0x00;
        long start=System.currentTimeMillis();
        for(int i=0;i<1;i++){
            connector.write(DaveArea.DB, 6501, 0, bs);
        }
        System.out.println("used time: "+(System.currentTimeMillis()-start));




        //Write to DB100 10 bytes


        //Close connection
        connector.close();
    }




}
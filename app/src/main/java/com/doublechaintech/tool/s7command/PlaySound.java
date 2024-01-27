package com.doublechaintech.tool.s7command;

import javax.sound.sampled.*;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioInputStream;
//import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class PlaySound {
    private final int BUFFER_SIZE = 12000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;

    public boolean isStopping() {
        return stopping;
    }

    public void setStopping(boolean stopping) {
        this.stopping = stopping;
    }

    private boolean stopping;

    /**
     * @param filename the name of the file that is going to be played
     */
    public void playSound(String filename) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        String strFilename = filename;
        initToPlay(strFilename);
        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            if(isStopping()){
                break;
            }
            nBytesRead = audioStream.read(abData, 0, abData.length);
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }
    public void playSoundWithSupplier(String filename,ContinueController continueController) throws UnsupportedAudioFileException, IOException, LineUnavailableException {


        String strFilename = filename;
        initToPlay(strFilename);
        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (true) {
            if(!continueController.continueToGo()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            nBytesRead = audioStream.read(abData, 0, abData.length);
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
                continue;
            }
            sourceLine.drain();
            sourceLine.close();
            initToPlay(strFilename);
        }



    }

    private void initToPlay(String strFilename) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        soundFile = new File(strFilename);
        audioStream = AudioSystem.getAudioInputStream(soundFile);
        audioFormat = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open(audioFormat);
        sourceLine.start();
    }
    static String currentDirectory(){
        return System.getProperty("user.dir");
    }
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        AudioFileFormat.Type[] list = AudioSystem.getAudioFileTypes();
        StringBuilder supportedFormat = new StringBuilder("Supported formats:");
        for (AudioFileFormat.Type type : list) {
            supportedFormat.append(", " + type.toString());
        }
        System.out.println(supportedFormat.toString());
        System.out.println(System.getProperty("user.dir"));
        //System.getProperty("user.dir")

        PlaySound playsound = new PlaySound();
        ContinueController continueController=new S7ContinueController();
        playsound.playSoundWithSupplier(currentDirectory()+"/alert.wav",continueController);
//      ContinueController continueController=new ContinueController();
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    playsound.playSoundWithSupplier(currentDirectory()+"/alert.wav",continueController);
//                } catch (UnsupportedAudioFileException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                } catch (LineUnavailableException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }.start();

//        while(true){
//            Thread.sleep(10000);
//            continueController.setStopping(true);
//
//            Thread.sleep(10000);
//            continueController.setStopping(false);
//
//        }










    }
}

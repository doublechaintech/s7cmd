package com.doublechaintech.tool.s7command;

public class ContinueController {

    public void init(){

    }

    public boolean isStopping() {
        return stopping;
    }

    public void setStopping(boolean stopping) {
        this.stopping = stopping;
    }

    private boolean stopping=false;
    public boolean continueToGo(){
        return !isStopping();
    }


}

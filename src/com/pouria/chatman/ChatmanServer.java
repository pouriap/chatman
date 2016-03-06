/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pouria.chatman;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 *
 * @author pouriap
 */
//Output: STDOUT
//Input:  STDIN
public class ChatmanServer extends Chatman {
    
    public ChatmanServer(){
        super(MOD_SERVER);
    }
    
    @Override
    public void start(){
        //establishes the input and output streams as a server
        writer = new PrintWriter(new OutputStreamWriter(System.out), true);
        gui.setLabelStatus(gui.l.getString("server_running"));

        th = new InputReaderTh();
        inputReaderThread = new Thread(th);
        inputReaderThread.start();
        
    }


}

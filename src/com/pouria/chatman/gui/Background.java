/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pouria.chatman.gui;

import com.google.common.io.Files;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author pouriap
 */
public class Background {
    
    ArrayList<String> backgrounds = new ArrayList<String>();
    private String current;
    private final String bgFolder = "backgrounds";
    private final String defaultBg = "batman.jpg";
    
    private Background(){
        File bgsPath;
        String [] _backgrounds;
        
        try{
            bgsPath = new File(bgFolder);
            
            //first add from background folder
            if(bgsPath.exists()){
                _backgrounds = bgsPath.list(new JpegFileFilter());
                if(_backgrounds.length > 0){
                    backgrounds.addAll(Arrays.asList(_backgrounds));
                }
            }

            //then initialize current bg
            current = ChatmanConfig.getInstance().get("background-image");

            
        }catch(Exception e){
            //i know. i just don't care
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
    
    public void next(){     
        if(backgrounds.isEmpty())
            return;
        
        int nextIndex = backgrounds.indexOf(current) + 1;
        current = (nextIndex < backgrounds.size())? backgrounds.get(nextIndex) : backgrounds.get(0);
        ChatmanConfig.getInstance().set("background-image", current);
    }

    public String getCurrent(){
        File currentBgFile = new File(bgFolder + "/" + current);
        if(!currentBgFile.exists())
            current = defaultBg;
        
        return current;
    }

    public URL getCurrentURL(){
        File currentBgFile = new File(bgFolder + "/" + current);
        if(!currentBgFile.exists()){
            current = defaultBg;
            return getClass().getResource("/resources/bg/" + defaultBg);
        }
        
        try{
            return currentBgFile.toURI().toURL();
        }catch(MalformedURLException e){
            return getClass().getResource("/resources/bg/" + defaultBg);
        }
    }
    
    
    
    //Singleton stuff
    public static Background getInstance() {
        return BackgroundHolder.INSTANCE;
    }
    
    
    private static class BackgroundHolder {

        private static final Background INSTANCE = new Background();
    }
    
    //only accepts jpeg files
    private class JpegFileFilter implements FilenameFilter{
        
        @Override
        public boolean accept(File dir, String fileName){
            String ext = Files.getFileExtension(fileName);
            if(ext.equals("jpg") || ext.equals("png"))
                return true;
            
            return false;
        }
    }
    
}
/*
 * Copyright (C) 2016 Pouria Pirhadi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.pouria.chatman.gui;

import com.pouria.chatman.Helper;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author pouriap
 * 
 * a dialog that pops up and makes a bleep
 * we don't use some of it's functions in this application
 */
public class PopupDialog extends JDialog{

	private final int os;
    private AudioInputStream audioStream;
    private Clip clip;
	
	public PopupDialog(){
		os = Helper.getInstance().getOS();
	}
	
    //plays the bleep
    public void playSound(){
		
        try{ 
			//linux sucks so...
			if(os == Helper.getInstance().OS_WIN){
				audioStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/resources/notification.wav"));
				clip = AudioSystem.getClip();
			}
			else{
				BufferedInputStream srcStream = new BufferedInputStream(getClass().getResourceAsStream("/resources/notification.wav")); 
				audioStream = AudioSystem.getAudioInputStream(srcStream);
				AudioFormat format = audioStream.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				clip = (Clip)AudioSystem.getLine(info);
			}

			clip.open(audioStream);
			clip.start();

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, Helper.getInstance().getStr("audio_play_fail") + e.getMessage());
        }
    }

    //shows the popup dialog in bottom-right corner of the screen and hides window decorations of it
    public void showPopup(){
		
		if(this.isDisplayable()){
			this.setVisible(true);
			return;
		}
		
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        this.setLocation(
                (screenWidth - this.getWidth()) - 20, 
                (screenHeight - this.getHeight()) - 100
        );
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setVisible(true);
    }
    
    //fancy version of the previous function
    public void showPopup(int x, int y, boolean undecorated){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        this.setLocation(
                (screenWidth - this.getWidth()) - 20, 
                (screenHeight - this.getHeight()) - 100
        );
        this.setUndecorated(undecorated);
        this.setAlwaysOnTop(true);
        this.setVisible(true);
    }
    
    //gues what?
    public void hidePopup(){
        this.setVisible(false);
    }
    
    /*
    //useless
    public void closePopup(){
        try{
            if(this.audioStream != null)
                this.audioStream.close();
            if(this.clip != null)
                this.clip.close();
        }catch(Exception e){
            ChatFrame.getInstance().message(Helper.getInstance().getStr("audio_close_fail") + e.getMessage());
        }
        finally{
            this.dispose();
        }
        
    }
    */
    
}
    



    


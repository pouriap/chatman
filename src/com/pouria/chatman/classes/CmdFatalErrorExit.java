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
package com.pouria.chatman.classes;

import com.pouria.chatman.CMHelper;
import com.pouria.chatman.gui.ChatFrame;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JOptionPane;

/**
 * shows an error and exits
 * @author pouriap
 */
public class CmdFatalErrorExit implements Command{
    String message;
	ChatFrame gui;
	Exception exception;
    
    public CmdFatalErrorExit(String message, Exception exception){
        this.message = message;
		this.exception = exception;
		this.gui = ChatFrame.getInstance();
    }
    
    @Override
    public void execute(){
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String stackTrace = sw.toString();
		
		CMHelper.getInstance().log("Fatal Error: " + message + "\nStack Trace:\n" + stackTrace); 
		
		message += "\nThis is a fatal error, exitting application.";
		message += "\nSee the logs for more info.";
        JOptionPane.showMessageDialog(null, message, "Fatal Error!", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
    }
	
    
}
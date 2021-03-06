/*
 * Copyright (c) 2020. Pouria Pirhadi
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.pouria.chatman.commands;

import com.pouria.chatman.CMHelper;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author pouriap
 * 
 * get one or more Command objects and runs their Execute() method in a thread-safe manner
 */
public class CmdInvokeLater implements Command{
	
	//if we do invokeAndWait GUI elements freeze. vaghti message ha gif haye ziad
	//dare hamashoon freeze mishan ta message ferestade beshe
	
    private final Command[] innerCommands;
	private final boolean sync;
    
    public CmdInvokeLater(Command c){
        innerCommands = new Command[]{c};
		sync = false;
    }
    
    public CmdInvokeLater(Command[] c){
        innerCommands = c;
		sync = false;
    }
	
	public CmdInvokeLater(Command c, boolean synchronous){
		innerCommands = new Command[]{c};
		sync = synchronous;
	}
    
    @Override
    public void execute(){
		
		//don't use invokelater if we're on event dispatch thread as suggested by JAVA docs
		if(SwingUtilities.isEventDispatchThread()){
			for(Command command: innerCommands){
				command.execute();
			}
		}

		if(sync){
			try{
				SwingUtilities.invokeAndWait(() -> {
					for(Command command: innerCommands){
						command.execute();
					}
				});
			}catch(Exception e){
				//this is for debug
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				String stackTrace = sw.toString();
				CMHelper.getInstance().log("invokeAndWait Failed" + "\nStack Trace:\n" + stackTrace);
				//if wait fails then do normal invokeAndWait
				SwingUtilities.invokeLater(() -> {
					for(Command command: innerCommands){
						command.execute();
					}
				});
			}
		}
		else{
			SwingUtilities.invokeLater(() -> {
				for(Command command: innerCommands){
					command.execute();
				}
			});
		}
    }
}

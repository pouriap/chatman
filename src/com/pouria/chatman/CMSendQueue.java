/*
 * Copyright (C) 2020 pouriap
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
package com.pouria.chatman;

import com.pouria.chatman.classes.CmdShowMessage;
import com.pouria.chatman.classes.CmdInvokeLater;
import com.pouria.chatman.gui.ChatFrame;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author pouriap
 */
public class CMSendQueue {
	
	private final ConcurrentLinkedQueue<CMMessage> queue = new ConcurrentLinkedQueue<CMMessage>();
	private Thread processThread = new Thread();
	private final Runnable r;
	private final int CONNECT_COOLDOWN = 1000 * 30;	//30 sec
	private long lastConnectTime = 0;
	
	public CMSendQueue(){
		r = () -> {
			//ta zamani ke chizi dar queue has edame bede
			while(!queue.isEmpty()){
				//avvalin message ra befrest
				CMMessage firstMessage = queue.peek();
				MessageHandler sender = new MessageHandler(CMMessage.DIR_OUT);
				sender.handle(firstMessage);
				//agar ferestade shod az saf dar biar va boro baadi
				if(firstMessage.getStatus() == CMMessage.STATUS_SENT){
					queue.poll();
					continue;
				}
				//agar nashod connect sho
				else{
					boolean connectFail = !connectWithCooldown();
					//agar connect nashod hamaro 'unsent' kon va processing ro motevaghef kon chon faide nadare
					if(connectFail){
						//add them to gui because meessages are added to gui int messageHandler whicih we don't use here
						for(CMMessage message : queue){
							//add them to conversation without sending
							message.setStatus(CMMessage.STATUS_SENDFAIL);
							message.setIsOurMessage(true);
							ChatFrame.getInstance().getChatmanInstance().addToAllMessages(message);
							(new CmdInvokeLater(new CmdShowMessage(message), true)).execute();
						}
						return;
					}
					else{
						//agar connect shod dobare az avval befrest
						//khodesh ettefagh miofte chon tooye while() hastim
						//nokte: har vagh serveri peida mishavad CMSendQueue() call mishavad
						//vali chon ma khodeman alan inja hastim oon call barmigarde va hamin edame peida mikone
					}
				}
			}
		};
	}
	
	private boolean connectWithCooldown(){
		long time = System.currentTimeMillis();
		if(time - lastConnectTime > CONNECT_COOLDOWN){
			boolean success = ChatFrame.getInstance().getChatmanInstance().getClient().connect();
			lastConnectTime = time;
			return success;
		}
		return false;
	}
	
	public void add(CMMessage object){
		queue.add(object);
	}
	
	public CMMessage peek(){
		return queue.peek();
	}
	
	public CMMessage poll(){
		return queue.poll();
	}
	
	public boolean isEmpty(){
		return queue.isEmpty();
	}
	
	public void process(){
		if(!processThread.isAlive()){
			processThread = new Thread(r, "CM-SendQeue-Processor");
			processThread.start();
		}
	}
	
}
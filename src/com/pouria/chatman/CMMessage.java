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

import io.undertow.server.handlers.form.FormData;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author pouriap
 */
public class CMMessage {

	private int type;
	private String content;
	private String sender;
	private long time;
	private int status = STATUS_NOTSENT;
	private boolean isOurMessage = false;
	private boolean isDisplayed = false;
	private boolean isSaved = false;


	public static final int TYPE_BADMESSAGE = 0;
	public static final int TYPE_TEXT = 1;
	public static final int TYPE_SHUTDOWN = 2;
	public static final int TYPE_ABORT_SHUTDOWN = 3;
	public static final int TYPE_FILE = 4;
	public static final int TYPE_SHOWGUI = 5;
	public static final int TYPE_PING = 6;
	
	public static final int DIR_IN = 0;
	public static final int DIR_OUT = 1;
	
	public static final int STATUS_NOTSENT = 0;
	public static final int STATUS_SENT = 1;
	public static final int STATUS_SENDFAIL = 2;
	
	public CMMessage(int type, String content, String sender, long time){
		this.type = type;
		this.content = content;
		this.sender = sender;
		this.time = time;
	}
	
	public CMMessage(int type, String content, String sender){
		this.type = type;
		this.content = content;
		this.sender = sender;
		this.time = CMHelper.getInstance().getTime();
	}
	
	public CMMessage(FormData postData){
		
		try{
			//if normal message
			if(postData.contains("message")){
				FormData.FormValue messageValue = postData.get("message").getFirst();
				String message = messageValue.getValue();
				//throws exception if JSON is curropt
				JSONObject json = new JSONObject(message);
				this.type = json.getInt("type");
				this.content = json.getString("content");
				this.sender = json.getString("sender");
				this.time = json.getLong("time");
			}
			//if file upload
			else if(postData.contains("data")){
				FormData.FormValue formFile = postData.get("data").getFirst();
				if(formFile.isFileItem()){
					String filePath = formFile.getFileItem().getFile().toAbsolutePath().toString();
					String fileName = formFile.getFileName();
					//we store filename in 'sender' field hehe
					this.type = TYPE_FILE;
					//we have to store file name somewhere
					this.content = filePath + "**" + fileName;
					this.sender = CMHelper.getInstance().getStr("file_recieved");
					this.time = CMHelper.getInstance().getTime();
				}
				else{
					throw new Exception("bad file message");
				}
			}
			//if bad message
			else{
				throw new Exception();
			}

		}catch(JSONException e){
			//create a 'bad message' instance as our message because the original one is lost
			this.type = TYPE_BADMESSAGE;
			this.content = "bad json syntax";
			this.sender = "unknown";
			this.time = CMHelper.getInstance().getTime();
			
		}catch(Exception e){
			//create a 'bad message' instance as our message because the original one is lost
			this.type = TYPE_BADMESSAGE;
			this.content = "bad message";
			this.sender = "unknown";
			this.time = CMHelper.getInstance().getTime();	
		}
	}
	
	public String getAsJsonString(){
		JSONObject json = new JSONObject();
		json.put("type", this.type);
		json.put("content", this.content);
		json.put("sender", this.sender);
		json.put("time", this.time);
		return json.toString();
	}
	
	public int getType(){
		return this.type;
	}
	
	public String getContent(){
		return this.content;
	}
	
	public void setContent(String content){
		this.content = content;
	}
	
	//formats content for being displayed in textAreaIncoming
	public String getDisplayableContent(){
		
		String t = content;
		Date d = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String _time = dateFormat.format(d);
	
		if(this.type == TYPE_TEXT){
			//parse web links 
			t = t.replaceAll("((http|https)://[^\\s]*)\\s?", "<a style='color:#dee3e9;font-weight:bold;' href='$1'><u>$1</u></a> ");
			//parse emoticons
			String url = getClass().getResource("/resources/emoticons_large/").toString();
			t = t.replaceAll("src=\"[^\"]*emoticons_large\\/([^\"]*\\.gif)\"", "src=\"" + url + "$1\"");
		}
		else if(this.type == TYPE_FILE){
			File file = new File(t);
			String path = file.getAbsolutePath();
			String name = file.getName();
			//parse file linke
			t = "<a style='color:#dee3e9;font-weight:bold;' href='file://"+path+"'><u>"+name+"</u></a>";
		}
		else if(this.type == TYPE_SHUTDOWN){
			t = "[SHUTDOWN COMMAND]";
		}
		else if(this.type == TYPE_BADMESSAGE){
			t = "BAD MESSAGE";
		}
		else if(this.type == TYPE_ABORT_SHUTDOWN){
			t = "[ABORT SHUTDOWN COMMAND]";
		}
		else{
			//other message types don't have a display
			return "";
		}
		String color = (status==STATUS_SENDFAIL)? "red" : "white";
		String senderName = (isOurMessage)? "You" : sender;
		
		//each message is a div
		t = "<div style='padding:5px;'><span style='color:"+color+"' class='time'>["+_time+"]  |  </span><b style='font-size:14px;color:"+color+"'>" + senderName + ":</b> " + t + "</div>";
        return t;
	}
	
	public String getSender(){
		return this.sender;
	}
	
	public void setIsOurMessage(boolean b){
		this.isOurMessage = b;
	}
	
	public boolean isOurMessage(){
		return this.isOurMessage;
	}
	
	public long getTime(){
		return this.time;
	}
	
	public int getStatus(){
		return this.status;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	
	public boolean isSaved() {
		return isSaved;
	}

	public void setIsSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	
}
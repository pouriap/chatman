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

package com.pouria.chatman.messages;

import com.pouria.chatman.CMHelper;
import com.pouria.chatman.enums.CMType;
import com.pouria.chatman.gui.ChatFrame;
import org.json.JSONException;
import org.json.JSONObject;

public class PingMessage extends HiddenMessage{

	private final String senderTheme;

	private PingMessage(Direction direction, String senderTheme){
		super(direction);
		this.senderTheme = senderTheme;
	}

	public static PingMessage getNew(Direction direction, String senderTheme){
		return new PingMessage(direction, senderTheme);
	}

	public static PingMessage getNewOutgoing(){
		String senderTheme = ChatFrame.getInstance().getCurrentTheme().getFileName();
		return new PingMessage(Direction.OUT, senderTheme);
	}

	public static PingMessage getNewIncoming(JSONObject json) throws JSONException {
		String senderTheme = json.getString("sender_theme");
		return new PingMessage(Direction.IN, senderTheme);
	}

	@Override
	public CMType getType(){
		return CMType.PING;
	}

	@Override
	public String getAsJSONString() {
		JSONObject json = new JSONObject();
		json.put("type", CMType.PING);
		json.put("sender_theme", senderTheme);
		return json.toString();
	}

	@Override
	public void doOnReceive(){
		CMHelper.getInstance().log("ping received");
	}

	@Override
	public void doOnSend(){
		if(getStatus() == Status.SENT){
			CMHelper.getInstance().log("ping sent successfully");
		}
		else{
			CMHelper.getInstance().log("ping send failed");
		}
	}

}

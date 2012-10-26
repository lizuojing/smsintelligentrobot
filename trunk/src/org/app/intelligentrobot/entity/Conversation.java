package org.app.intelligentrobot.entity;

public class Conversation {
	private int smsid;
	private String pnum;
	private String sendcontent;
	private String sendtime;
	private String receivecontent;
	private String receivetime;
	private int type;

	public int getSmsid() {
		return smsid;
	}

	public void setSmsid(int smsid) {
		this.smsid = smsid;
	}

	public String getSendcontent() {
		return sendcontent;
	}

	public void setSendcontent(String sendcontent) {
		this.sendcontent = sendcontent;
	}

	public String getReceivecontent() {
		return receivecontent;
	}

	public void setReceivecontent(String receivecontent) {
		this.receivecontent = receivecontent;
	}

	public String getPnum() {
		return pnum;
	}

	public void setPnum(String pnum) {
		this.pnum = pnum;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getReceivetime() {
		return receivetime;
	}

	public void setReceivetime(String receivetime) {
		this.receivetime = receivetime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}

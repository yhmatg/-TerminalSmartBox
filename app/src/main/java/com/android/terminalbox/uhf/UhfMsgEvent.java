package com.android.terminalbox.uhf;

public class UhfMsgEvent<T> {
	private String type;
	private T data;

	public UhfMsgEvent() {
	}

	public UhfMsgEvent(String type) {
		this.type = type;
	}

	public UhfMsgEvent(String type, T data) {
		super();
		this.data = data;
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
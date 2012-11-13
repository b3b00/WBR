package com.beboo.wifibackupandrestore;

import java.io.File;

public class FileItem {

	String date;
	File file;
	
	public FileItem(File file, String date) {
		super();
		this.date = date;
		this.file = file;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	
}

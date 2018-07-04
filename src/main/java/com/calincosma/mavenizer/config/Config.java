package com.calincosma.mavenizer.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
	
	private List<FolderConfig> folders = new ArrayList<>();
	
	public void add(FolderConfig folderConfig) {
		folders.add(folderConfig);
	}
	
	public List<FolderConfig> getFolders() {
		return folders;
	}
	
	public void setFolders(List<FolderConfig> folders) {
		this.folders = folders;
	}
}

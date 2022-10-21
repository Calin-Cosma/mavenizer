package com.calincosma.mavenizer;

import com.calincosma.jargs.Arg;

import java.io.File;

public class Args {

	@Arg(value = "-d")
	private File folder;

	@Arg(value = "-g")
	private String group;

	@Arg(value = "-v")
	private String version;

	public File getFolder() {
		return folder;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}

package com.calincosma.mavenizer;

import com.calincosma.jargs.Arg;

import java.io.File;

public class Args {

	@Arg(value = "-f")
	private File file;

	@Arg(value = "-g")
	private String group;

	@Arg(value = "-v")
	private String version;


}

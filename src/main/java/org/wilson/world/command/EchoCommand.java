package org.wilson.world.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class EchoCommand extends AbstractCommand {
	public EchoCommand() {
		super();
		this.setName("echo");
		this.setDescription("Show what user just entered");
	}
	
	@Override
	public String execute(CommandLine cmd) {
		StringBuilder sb = new StringBuilder();
		String [] args = cmd.getArgs();
		for(int i = 0; i < args.length; i++) {
			sb.append(args[i]);
			if(i != args.length - 1) {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}

	@Override
	public Options buildOptions(Options options) {
		return options;
	}

	@Override
	public String getUsage() {
		return this.getName() + " args";
	}

}

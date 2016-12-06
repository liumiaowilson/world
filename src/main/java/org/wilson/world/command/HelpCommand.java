package org.wilson.world.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.wilson.world.exception.DataException;
import org.wilson.world.manager.CommandManager;
import org.wilson.world.util.TableBuilder;

public class HelpCommand extends AbstractCommand {
	private static final Logger logger = Logger.getLogger(HelpCommand.class);
	
	public HelpCommand() {
		super();
		this.setName("help");
		this.setDescription("Get help information");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String execute(CommandLine cmd) {
		List<String> argList = cmd.getArgList();
		if(argList.isEmpty()) {
			//print all commands
			return this.printAllCommands();
		}
		else {
			//print single help
			return this.printSingleHelp(argList);
		}
	}
	
	private String printAllCommands() {
		List<Command> commands = CommandManager.getInstance().getCommands();
		Collections.sort(commands, new Comparator<Command>(){

			@Override
			public int compare(Command o1, Command o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		
		TableBuilder tb = new TableBuilder();
		tb.addRow("Command Name", "Description");
		tb.addRow("------------", "-----------");
		for(Command command : commands) {
			tb.addRow(command.getName(), command.getDescription());
		}
		
		return tb.toString();
	}
	
	private String printSingleHelp(List<String> argList) {
		String commandName = argList.get(0);
		Command command = CommandManager.getInstance().getCommand(commandName);
		if(command == null) {
			throw new DataException("No help found for [" + commandName + "].");
		}
		
		String help = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(baos, true);
			printHelp(pw, command);
			help = baos.toString();
			baos.close();
		}
		catch(Exception e) {
			logger.error(e);
			throw new DataException(e.getMessage());
		}
		
		return help;
	}
	
	private void printHelp(PrintWriter pw, Command command) {
		int width = 120;
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(width);
		formatter.printWrapped(pw, width, "Name: " + command.getName());
		formatter.printWrapped(pw, width, "Description: " + command.getDescription());
		formatter.printUsage(pw, width, command.getUsage(), command.getOptions());
		formatter.printOptions(pw, width, command.getOptions(), formatter.getLeftPadding(), formatter.getDescPadding());
	}

	@Override
	public Options buildOptions(Options options) {
		return new Options();
	}
}

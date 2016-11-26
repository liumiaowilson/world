package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.wilson.world.command.AbstractCommand;
import org.wilson.world.command.Command;
import org.wilson.world.command.EchoCommand;
import org.wilson.world.command.HelpCommand;
import org.wilson.world.exception.DataException;
import org.wilson.world.java.JavaExtensionListener;

public class CommandManager implements JavaExtensionListener<AbstractCommand> {
	private static CommandManager instance;
	
	private Map<String, Command> commands = new HashMap<String, Command>();
	
	private CommandLineParser parser;
	
	private CommandManager() {
		parser = new BasicParser();
		
		ExtManager.getInstance().addJavaExtensionListener(this);
		
		this.loadSystemCommands();
	}
	
	private void loadSystemCommands() {
		this.addCommand(new EchoCommand());
		this.addCommand(new HelpCommand());
	}
	
	public void addCommand(Command command) {
		if(command != null && command.getName() != null) {
			this.commands.put(command.getName(), command);
		}
	}
	
	public void removeCommand(Command command) {
		if(command != null && command.getName() != null) {
			this.commands.remove(command.getName());
		}
	}
	
	public static CommandManager getInstance() {
		if(instance == null) {
			instance = new CommandManager();
		}
		
		return instance;
	}

	@Override
	public Class<AbstractCommand> getExtensionClass() {
		return AbstractCommand.class;
	}

	@Override
	public void created(AbstractCommand t) {
		this.addCommand(t);
	}

	@Override
	public void removed(AbstractCommand t) {
		this.removeCommand(t);
	}
	
	public List<Command> getCommands() {
		return new ArrayList<Command>(this.commands.values());
	}
	
	public List<String> getCommandNames() {
		return new ArrayList<String>(this.commands.keySet());
	}
	
	public Command getCommand(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.commands.get(name);
	}
	
	public String execute(String line) throws Exception {
		if(StringUtils.isBlank(line)) {
			return null;
		}
		
		line = line.trim();
		
		String [] items = line.split(" ");
		List<String> argList = new ArrayList<String>();
		String commandName = items[0].trim();
		for(int i = 1; i < items.length; i++) {
			argList.add(items[i].trim());
		}
		String [] args = argList.toArray(new String[0]);
		
		Command command = this.getCommand(commandName);
		if(command == null) {
			throw new DataException("No command named [" + commandName + "] is found.");
		}
		
		Options options = command.getOptions();
		CommandLine cmd = parser.parse(options, args);
		String ret = command.execute(cmd);
		
		return ret;
	}
}

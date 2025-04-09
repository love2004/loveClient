package me.love2004.porn.command.commands;

import me.love2004.porn.loveClient;
import me.love2004.porn.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("commands");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("You can use following commands: ");
        for (Command command : loveClient.commandManager.getCommands()) {
            HelpCommand.sendMessage(loveClient.commandManager.getPrefix() + command.getName());
        }
    }
}


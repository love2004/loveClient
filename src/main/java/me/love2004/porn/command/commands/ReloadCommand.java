package me.love2004.porn.command.commands;

import me.love2004.porn.loveClient;
import me.love2004.porn.command.Command;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        loveClient.reload();
    }
}


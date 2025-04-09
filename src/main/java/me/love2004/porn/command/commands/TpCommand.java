package me.love2004.porn.command.commands;

import me.love2004.porn.command.Command;

public class TpCommand
        extends Command {
    public TpCommand() {
        super("tp", new String[]{"<x>", "<y>","<z>"});
    }

    @Override
    public void execute(String[] commands) {
        int x=0;
        int y=0;
        int z=0;

        mc.player.setPosition(x,y,z);
    }
}


package me.love2004.porn.command.commands;

import me.love2004.porn.loveClient;
import me.love2004.porn.command.Command;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigCommand
        extends Command {
    public ConfigCommand() {
        super("config", new String[]{"<save/load>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            ConfigCommand.sendMessage("You`ll find the config files in your gameProfile directory under phobos/config");
            return;
        }
        if (commands.length == 2) {
            if ("list".equals(commands[0])) {
                String configs = "Configs: ";
                File file = new File("phobos/");
                List<File> directories = Arrays.stream(file.listFiles()).filter(File::isDirectory).filter(f -> !f.getName().equals("util")).collect(Collectors.toList());
                StringBuilder builder = new StringBuilder(configs);
                for (File file1 : directories) {
                    builder.append(file1.getName() + ", ");
                }
                configs = builder.toString();
                ConfigCommand.sendMessage("\u00a7a" + configs);
            } else {
                ConfigCommand.sendMessage("\u00a7cNot a valid command... Possible usage: <list>");
            }
        }
        if (commands.length >= 3) {
            switch (commands[0]) {
                case "save": {
                    loveClient.configManager.saveConfig(commands[1]);
                    ConfigCommand.sendMessage("\u00a7aConfig has been saved.");
                    break;
                }
                case "load": {
                    loveClient.moduleManager.onUnload();
                    loveClient.configManager.loadConfig(commands[1]);
                    loveClient.moduleManager.onLoad();
                    ConfigCommand.sendMessage("\u00a7aConfig has been loaded.");
                    break;
                }
                default: {
                    ConfigCommand.sendMessage("\u00a7cNot a valid command... Possible usage: <save/load>");
                }
            }
        }
    }
}


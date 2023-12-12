package com.github.minemaniauk.minemaniamenus.command.type;

import com.github.minemaniauk.minemaniamenus.User;
import com.github.minemaniauk.minemaniamenus.command.BaseCommandType;
import com.github.minemaniauk.minemaniamenus.command.CommandStatus;
import com.github.minemaniauk.minemaniamenus.command.CommandSuggestions;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

public class MainMenu extends BaseCommandType {

    @Override
    public String getName() {
        return "mainmenu";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        return null;
    }
}

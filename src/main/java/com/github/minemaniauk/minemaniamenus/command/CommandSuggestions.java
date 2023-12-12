package com.github.minemaniauk.minemaniamenus.command;

import com.github.minemaniauk.minemaniamenus.PlayerUtility;
import com.github.minemaniauk.minemaniamenus.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Represents the tab suggestions for a command.
 */
public class CommandSuggestions {

    private final List<List<String>> data = new ArrayList<>();
    private boolean isContinuous = false;

    /**
     * Used to append the next suggestion list.
     *
     * @param list Instance of a list.
     * @return This instance.
     */
    public CommandSuggestions append(List<String> list) {
        this.data.add(list);
        return this;
    }

    /**
     * Used to append the next suggestion list.
     *
     * @param list Instance of a string array.
     * @return This instance.
     */
    public CommandSuggestions append(String[] list) {
        this.data.add(new ArrayList<>(Arrays.stream(list).toList()));
        return this;
    }

    /**
     * Append something to the first tab item.
     *
     * @param string The string.
     */
    public void appendBase(String string) {
        if (this.data.isEmpty()) {
            this.data.add(new ArrayList<>(Arrays.stream(new String[]{string}).toList()));
        }

        this.data.get(0).add(string);
    }

    /**
     * Append a list of suggestions to the first tab item.
     *
     * @param strings A list of strings.
     */
    public void appendBase(List<String> strings) {
        if (this.data.isEmpty()) {
            this.data.add(strings);
        }

        this.data.get(0).addAll(strings);
    }

    /**
     * Used to get the suggestions.
     *
     * @return The suggestions as a 3D list.
     */
    public List<List<String>> get() {
        return this.data;
    }

    /**
     * Used to add the list of online players to the list.
     *
     * @return This instance.
     */
    public CommandSuggestions appendPlayers() {
        this.data.add(PlayerUtility.getPlayers());
        return this;
    }

    /**
     * Used to combine a sub command types suggestions.
     *
     * @param suggestions Suggestions to combine
     */
    public void combineSubType(CommandSuggestions suggestions) {
        if (suggestions == null) return;

        int index = 1;
        for (List<String> list : suggestions.get()) {
            if (this.data.size() >= index + 1) {
                this.data.get(index).addAll(list);
            } else {
                this.data.add(list);
            }

            index++;
        }
    }

    /**
     * Used to add a subcommands names and suggestions to this
     * commands suggestions.
     *
     * @param subCommandTypes The subcommand types of the command.
     * @param section         The configuration section of the command.
     * @param arguments       The arguments suggested.
     * @param user            The instance of the user executing the command.
     */
    public void appendSubCommandTypes(List<CommandType> subCommandTypes, ConfigurationSection section, String[] arguments, User user) {
        for (CommandType commandType : subCommandTypes) {

            ConfigurationSection commandSection = section.getSection(commandType.getName());

            // Get the commands name.
            String name = commandSection.getString("name", commandType.getName());

            // Add all the aliases.
            List<String> commandNames = new ArrayList<>(commandSection.getListString("aliases", new ArrayList<>()));

            // Add the commands main name.
            commandNames.add(name);

            // Add the command names to the base.
            this.appendBase(commandNames);

            if (arguments.length == 0) continue;

            // If the first argument references this command, add the sub commands suggestions.
            for (String commandName : commandNames) {
                if (!commandName.toLowerCase(Locale.ROOT).equals(arguments[0].toLowerCase(Locale.ROOT))) continue;
                this.combineSubType(commandType.getSuggestions(section, user));
            }
        }
    }

    /**
     * Used to set the suggestions to continuous.
     * This makes the last suggestion repeat forever.
     *
     * @return This instance.
     */
    public CommandSuggestions setContinuous() {
        this.isContinuous = true;
        return this;
    }

    /**
     * Used to check if the suggestions are continuous.
     *
     * @return True if the suggestions are continuous.
     */
    public boolean isContinuous() {
        return this.isContinuous;
    }
}

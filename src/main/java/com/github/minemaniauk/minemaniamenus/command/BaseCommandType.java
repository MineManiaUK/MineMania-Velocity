/*
 * MineManiaMenus
 * Used for interacting with the database and message broker.
 *
 * Copyright (C) 2023  MineManiaUK Staff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.minemaniauk.minemaniamenus.command;

import com.github.minemaniauk.minemaniamenus.MessageManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Represents a base command.</h1>
 * A base command is the first word typed after a slash.
 * This type of command can have subcommand types.
 */
public abstract class BaseCommandType implements CommandType {

    /**
     * Represents the list of the sub command types
     * the command can execute.
     */
    private List<CommandType> subCommandTypes = new ArrayList<>();

    /**
     * Used to add a sub command type.
     *
     * @param subCommandType The sub command type to add.
     */
    public void addSubCommandType(CommandType subCommandType) {
        this.subCommandTypes.add(subCommandType);
    }

    /**
     * Used to get the sub command types.
     *
     * @return The request list of sub command types.
     */
    public List<CommandType> getSubCommandTypes() {
        return this.subCommandTypes;
    }

    /**
     * Used to load the commands subcommands.
     */
    public void loadSubCommands() {
    }

    /**
     * Used to remove disabled sub commands.
     *
     * @param section The base command types configuration section.
     */
    public void initialiseSubCommands(ConfigurationSection section) {
        List<CommandType> toRemove = new ArrayList<>();

        for (CommandType commandType : this.subCommandTypes) {

            // Check if the configuration does not exist.
            if (!section.getKeys().contains(commandType.getName())) {
                MessageManager.log("&7[Commands] &7↳ &eDisabling &7sub command (No configuration) : " + commandType.getName());
                toRemove.add(commandType);
                continue;
            }

            // Check if the command is disabled.
            if (!section.getSection(commandType.getName()).getBoolean("enabled", true)) {
                MessageManager.log("&7[Commands] &7↳ &eDisabling &7sub command (Configuration disabled) : " + commandType.getName());
                toRemove.add(commandType);
                continue;
            }

            MessageManager.log("&7[Commands] &7↳ &aEnabling &7sub command : " + commandType.getName());
        }

        for (CommandType commandType : toRemove) {
            this.subCommandTypes.remove(commandType);
        }
    }

    /**
     * Used to remove sub commands.
     */
    public void removeSubCommands() {
        this.subCommandTypes = new ArrayList<>();
    }
}

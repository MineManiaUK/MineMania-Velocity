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

import com.github.minemaniauk.minemaniamenus.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

/**
 * Represents a command type.
 * Commands types can be referenced in the command directory files
 * and used to make commands.
 */
public interface CommandType {

    /**
     * Used to get the command type name.
     *
     * @return The command type name.
     */
    String getName();

    /**
     * Used to get the command's syntax.
     *
     * @return The command's syntax.
     */
    String getSyntax();

    /**
     * Used to get the tab suggestions.
     *
     * @param section The configuration section.
     * @param user    The user completing the command.
     * @return The command's argument suggestions.
     */
    CommandSuggestions getSuggestions(ConfigurationSection section, User user);

    /**
     * Executed when the command is run in the console.
     *
     * @param section   The command's configuration section.
     * @param arguments The arguments given in the command.
     * @return The command's status.
     */
    CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments);

    /**
     * Executed when a player runs the command.
     *
     * @param section   The command's configuration section.
     * @param arguments The arguments given in the command.
     * @param user      The instance of the user running the command.
     * @return The command's status.
     */
    CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user);
}

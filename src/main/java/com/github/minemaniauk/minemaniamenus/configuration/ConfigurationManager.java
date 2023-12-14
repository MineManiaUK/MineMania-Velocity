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

package com.github.minemaniauk.minemaniamenus.configuration;

import java.io.File;

/**
 * Manages all the configuration files.
 */
public class ConfigurationManager {

    private static File folder;

    private static CommandConfigurationHandler commandsConfigurationHandler;

    /**
     * Used to initialise the configuration manager.
     *
     * @param folder The plugin's folder.
     */
    public static void initialise(File folder) {
        ConfigurationManager.folder = folder;

        ConfigurationManager.commandsConfigurationHandler = new CommandConfigurationHandler(folder);

        ConfigMessages.initialise(folder);
    }

    /**
     * Used to reload all the configuration files.
     */
    public static void reload() {
        ConfigurationManager.getCommands().reload();

        ConfigMessages.initialise(folder);
    }

    public static CommandConfigurationHandler getCommands() {
        return ConfigurationManager.commandsConfigurationHandler;
    }
}

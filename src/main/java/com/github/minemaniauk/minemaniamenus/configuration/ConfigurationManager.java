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

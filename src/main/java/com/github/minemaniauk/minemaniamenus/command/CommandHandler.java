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
import com.github.minemaniauk.minemaniamenus.MineManiaMenus;
import com.github.minemaniauk.minemaniamenus.User;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <h1>Represents the command handler.</h1>
 * Handles registering and unregistering commands.
 */
public class CommandHandler {

    private List<Command> commands = new ArrayList<>();

    private final List<BaseCommandType> commandTypes = new ArrayList<>();

    private List<String> registeredCommands = new ArrayList<>();

    /**
     * Used to append a command type to the command handler.
     *
     * @param command The command to append.
     */
    public void append(Command command) {
        this.commands.add(command);
    }

    /**
     * Used to add a type of command to the list.
     *
     * @param commandType Instance of the command type.
     */
    public void addType(BaseCommandType commandType) {
        this.commandTypes.add(commandType);
    }

    /**
     * Used to get a command type.
     *
     * @param name The name of the command type to get.
     * @return The command type instance.
     */
    public BaseCommandType getType(String name) {
        for (BaseCommandType commandType : this.commandTypes) {
            if (Objects.equals(commandType.getName(), name)) return commandType;
        }

        return null;
    }

    /**
     * Used to get a command given the command name.
     *
     * @param name The name of the command or alias.
     * @return The requested command instance.
     */
    public Command getCommand(String name) {
        for (Command command : this.commands) {
            if (Objects.equals(command.getName(), name)) return command;
            if (command.getAliases().get().contains(name)) return command;
        }
        return null;
    }

    /**
     * Used to register the commands with the proxy server.
     */
    public void register() {
        CommandManager manager = MineManiaMenus.getServer().getCommandManager();

        for (Command command : this.commands) {
            // Check if command is enabled.
            if (!command.isEnabled()) {
                MessageManager.log("&7[Commands] " + command.getIdentifier() + " is disabled in the configuration file.");
                continue;
            }

            // Log enable message.
            MessageManager.log("&7[Commands] &aEnabling &7command : " + command.getName());

            // Load subcommands.
            command.getBaseCommandType().loadSubCommands();
            command.getBaseCommandType().initialiseSubCommands(command.getSection());

            // Check if the command is valid.
            if (command.getName() == null) {
                MessageManager.warn("&7[Commands] &f[command] &e: Command name not specified in the configuration file."
                        .replace("[command]", command.getIdentifier()));
                continue;
            }

            // Register the main command name.
            manager.register(manager.metaBuilder(command.getName()).build(), command);
            this.registeredCommands.add(command.getName());

            // Register aliases if they exist.
            if (command.getAliases().get().isEmpty()) continue;

            for (String alias : command.getAliases().get()) {
                manager.register(manager.metaBuilder(alias).build(), command);
                this.registeredCommands.add(alias);
            }
        }
    }

    /**
     * Used to unregister all the commands by
     * this plugin in the proxy server.
     */
    public void unregister() {
        CommandManager manager = MineManiaMenus.getServer().getCommandManager();

        for (String commandName : this.registeredCommands) {
            // Unregister the command
            manager.unregister(commandName);
        }

        for (Command command : this.commands) {
            // Remove subcommands
            command.getBaseCommandType().removeSubCommands();
        }

        this.registeredCommands = new ArrayList<>();
        this.commands = new ArrayList<>();
    }

    /**
     * Used to check if a command string is runnable in this handler.
     *
     * @param commandString The instance of the command string.
     * @return True if it is runnable.
     */
    public boolean isRunnable(@NotNull String commandString) {
        String base = commandString.split(" ")[0];

        for (Command command : this.commands) {
            if (command.getName().equals(base)) return true;
            if (command.getAliases().contains(base)) return true;
        }

        return false;
    }

    /**
     * Used to execute a command.
     *
     * @param commandString The command to execute.
     */
    public void execute(Player player, String commandString) {
        if (Objects.equals(commandString, "")) return;

        // Get the identifier.
        String name = commandString.split(" ")[0];
        String[] arguments = commandString.substring(name.length()).trim().split(" ");

        // Get the instance of the command.
        Command command = this.getCommand(name);

        // Execute the command as a player.
        command.onPlayerRun(arguments, new User(player));
    }
}

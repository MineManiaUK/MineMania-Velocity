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

package com.github.minemaniauk.minemaniamenus;

import com.github.minemaniauk.minemaniamenus.command.BaseCommandType;
import com.github.minemaniauk.minemaniamenus.command.Command;
import com.github.minemaniauk.minemaniamenus.command.CommandHandler;
import com.github.minemaniauk.minemaniamenus.command.type.MainMenu;
import com.github.minemaniauk.minemaniamenus.configuration.ConfigurationManager;
import com.github.minemaniauk.minemaniamenus.dependencys.MiniPlaceholdersDependency;
import com.github.minemaniauk.minemaniamenus.dependencys.ProtocolizeDependency;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "minemaniamenus",
        name = "MineManiaMenus",
        version = "1.0.0"
)
public class MineManiaMenus {

    private static MineManiaMenus instance;
    private static ComponentLogger componentLogger;
    private static ProxyServer server;
    private static CommandHandler commandHandler;

    @Inject
    public MineManiaMenus(ProxyServer server, @DataDirectory final Path folder, ComponentLogger componentLogger) {
        MineManiaMenus.instance = this;
        MineManiaMenus.server = server;
        MineManiaMenus.componentLogger = componentLogger;

        ConfigurationManager.initialise(folder.toFile());

//        // Set up the configuration file.
//        this.configuration = ConfigurationFactory.YAML
//                .create(folder.toFile(), "config")
//                .setDefaultPath("config.yml");
//        this.configuration.load();

//        // Set up the mine mania api connection.
//        this.api = MineManiaAPI.createAndSet(
//                this.configuration,
//                this
//        );
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        // Log header.
        MessageManager.logHeader();

        // Reload configuration to load custom placeholders correctly.
        ConfigurationManager.reload();

        // Append all command types.
        MineManiaMenus.commandHandler = new CommandHandler();

        MineManiaMenus.commandHandler.addType(new MainMenu());

        MineManiaMenus.reloadCommands();

        // Check for dependencies.
        if (!ProtocolizeDependency.isEnabled()) {
            MessageManager.log("&7[Dependencies] Could not find optional dependency &fProtocolize");
            MessageManager.log("&7[Dependencies] Inventories and sounds will be disabled.");
            MessageManager.log(ProtocolizeDependency.getDependencyMessage());
        }

        if (!MiniPlaceholdersDependency.isEnabled()) {
            MessageManager.log("&7[Dependencies] Could not find optional dependency &fMini Placeholders");
            MessageManager.log("&7[Dependencies] This optional plugin lets you use mini placeholders, not to be confused with leaf placeholders.");
            MessageManager.log(MiniPlaceholdersDependency.getDependencyMessage());
        }
    }

    public static ComponentLogger getComponentLogger() {
        return componentLogger;
    }

    public static ProxyServer getServer() {
        return server;
    }

    public static MineManiaMenus getPlugin() {
        return instance;
    }

    public static int getAmountOnline(String serverName) {
        Optional<RegisteredServer> optionalRegisteredServer = MineManiaMenus.server.getServer(serverName);
        return optionalRegisteredServer.map(registeredServer -> getAmountOnline(optionalRegisteredServer.get())).orElse(0);
    }

    public static int getAmountOnline(@NotNull RegisteredServer registeredServer) {
        int amount = 0;
        for (Player player : registeredServer.getPlayersConnected()) {
            if (new User(player).isVanished()) continue;
            amount++;
        }
        return amount;
    }

    public static void reloadCommands() {
        MineManiaMenus.commandHandler.unregister();

        for (String identifier : ConfigurationManager.getCommands().getAllIdentifiers()) {
            String commandTypeString = ConfigurationManager.getCommands().getCommandType(identifier);
            if (commandTypeString == null) continue;

            BaseCommandType commandType = MineManiaMenus.commandHandler.getType(commandTypeString);

            if (commandType == null) {
                MessageManager.warn("Invalid command type for : " + identifier);
                continue;
            }

            Command command = new Command(identifier, commandType);
            MineManiaMenus.commandHandler.append(command);
        }

        MineManiaMenus.commandHandler.register();
    }
}

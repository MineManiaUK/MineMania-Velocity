package com.github.minemaniauk.minemaniamenus;

import com.github.minemaniauk.minemaniamenus.command.BaseCommandType;
import com.github.minemaniauk.minemaniamenus.command.Command;
import com.github.minemaniauk.minemaniamenus.command.CommandHandler;
import com.github.minemaniauk.minemaniamenus.configuration.ConfigurationManager;
import com.github.minemaniauk.minemaniamenus.dependencys.MiniPlaceholdersDependency;
import com.github.minemaniauk.minemaniamenus.dependencys.ProtocolizeDependency;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

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
    public MineManiaMenus(ProxyServer server, ComponentLogger componentLogger) {
        MineManiaMenus.instance = this;
        MineManiaMenus.server = server;
        MineManiaMenus.componentLogger = componentLogger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        // Log header.
        MessageManager.logHeader();

        // Reload configuration to load custom placeholders correctly.
        ConfigurationManager.reload();

        // Append all command types.
        MineManiaMenus.commandHandler = new CommandHandler();

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

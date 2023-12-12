package com.github.minemaniauk.minemaniamenus;

import com.github.kerbity.kerb.event.Priority;
import com.github.minemaniauk.api.MineManiaAPI;
import com.github.minemaniauk.api.MineManiaAPIContract;
import com.github.minemaniauk.api.kerb.event.player.PlayerChatEvent;
import com.github.minemaniauk.api.kerb.event.useraction.UserActionHasPermissionListEvent;
import com.github.minemaniauk.api.kerb.event.useraction.UserActionIsOnlineEvent;
import com.github.minemaniauk.api.kerb.event.useraction.UserActionIsVanishedEvent;
import com.github.minemaniauk.api.kerb.event.useraction.UserActionMessageEvent;
import com.github.minemaniauk.minemaniamenus.command.BaseCommandType;
import com.github.minemaniauk.minemaniamenus.command.Command;
import com.github.minemaniauk.minemaniamenus.command.CommandHandler;
import com.github.minemaniauk.minemaniamenus.configuration.ConfigurationManager;
import com.github.minemaniauk.minemaniamenus.dependencys.MiniPlaceholdersDependency;
import com.github.minemaniauk.minemaniamenus.dependencys.ProtocolizeDependency;
import com.github.smuddgge.squishyconfiguration.ConfigurationFactory;
import com.github.smuddgge.squishyconfiguration.interfaces.Configuration;
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
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "minemaniamenus",
        name = "MineManiaMenus",
        version = "1.0.0"
)
public class MineManiaMenus implements MineManiaAPIContract {

    private static MineManiaMenus instance;
    private static ComponentLogger componentLogger;
    private static ProxyServer server;
    private static CommandHandler commandHandler;
    private final @NotNull MineManiaAPI api;
    private final @NotNull Configuration configuration;

    @Inject
    public MineManiaMenus(ProxyServer server, @DataDirectory final Path folder, ComponentLogger componentLogger) {
        MineManiaMenus.instance = this;
        MineManiaMenus.server = server;
        MineManiaMenus.componentLogger = componentLogger;

        // Set up the configuration file.
        this.configuration = ConfigurationFactory.YAML
                .create(folder.toFile(), "config")
                .setDefaultPath("config.yml");
        this.configuration.load();

        // Set up the mine mania api connection.
        this.api = MineManiaAPI.createAndSet(
                this.configuration,
                this
        );
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

    public static int getAmountOnline(String serverName) {
        Optional<RegisteredServer> optionalRegisteredServer = MineManiaMenus.server.getServer(serverName);
        return optionalRegisteredServer.map(registeredServer -> getAmountOnline(optionalRegisteredServer.get())).orElse(0);
    }

    public static int getAmountOnline(@NotNull RegisteredServer registeredServer) {
        int amount = 0;
        for (Player player : registeredServer.getPlayersConnected()) {

        }
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

    @Override
    public @Nullable UserActionHasPermissionListEvent onHasPermission(@NotNull UserActionHasPermissionListEvent userActionHasPermissionListEvent) {
        return null;
    }

    @Override
    public @Nullable UserActionIsOnlineEvent onIsOnline(@NotNull UserActionIsOnlineEvent userActionIsOnlineEvent) {
        return null;
    }

    @Override
    public @Nullable UserActionIsVanishedEvent onIsVanished(@NotNull UserActionIsVanishedEvent userActionIsVanishedEvent) {
        return null;
    }

    @Override
    public @Nullable UserActionMessageEvent onMessage(@NotNull UserActionMessageEvent userActionMessageEvent) {
        return null;
    }

    @Override
    public @NotNull PlayerChatEvent onChatEvent(@NotNull PlayerChatEvent playerChatEvent) {
        return playerChatEvent;
    }
}

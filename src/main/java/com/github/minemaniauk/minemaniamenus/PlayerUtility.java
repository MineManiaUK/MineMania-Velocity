package com.github.minemaniauk.minemaniamenus;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player utility class.
 * Used to get lists of certain players.
 */
public class PlayerUtility {

    /**
     * Used to get the list of online players.
     * This will not include vanished players.
     *
     * @return The list of online players.
     */
    public static @NotNull List<String> getPlayers() {
        List<String> players = new ArrayList<>();

        for (Player player : MineManiaMenus.getServer().getAllPlayers()) {
            players.add(player.getGameProfile().getName());
        }

        return players;
    }
}

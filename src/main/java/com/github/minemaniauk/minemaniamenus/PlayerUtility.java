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

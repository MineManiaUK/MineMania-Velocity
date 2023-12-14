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

import com.github.minemaniauk.minemaniamenus.dependencys.ProtocolizeDependency;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.Sound;

import java.util.Locale;
import java.util.UUID;

/**
 * Represents the sounds utility.
 * Used to play minecraft sounds.
 */
public class Sounds {

    /**
     * Used to play a sound for a player.
     *
     * @param sound      The sound to play.
     * @param playerUuid The players uuid.
     */
    public static void play(Sound sound, UUID playerUuid) {
        // Check if protocolize is enabled.
        if (!ProtocolizeDependency.isEnabled()) {
            MessageManager.warn("Tried to use sounds when the dependency is not enabled.");
            MessageManager.log("&7" + ProtocolizeDependency.getDependencyMessage());
            return;
        }

        ProtocolizePlayer player = Protocolize.playerProvider().player(playerUuid);
        player.playSound(sound, SoundCategory.MASTER, 1f, 1f);
    }

    /**
     * Used to play a sound for a player.
     *
     * @param sound      The sound as a string.
     * @param playerUuid The players uuid.
     */
    public static void play(String sound, UUID playerUuid) {
        if (sound == null) return;
        if (sound.equals("none")) return;

        try {
            Sounds.play(Sound.valueOf(sound.toUpperCase(Locale.ROOT)), playerUuid);
        } catch (IllegalArgumentException illegalArgumentException) {
            MessageManager.warn("Invalid sound : " + sound + " : ");
            illegalArgumentException.printStackTrace();
        }
    }
}

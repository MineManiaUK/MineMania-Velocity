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

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

package com.github.minemaniauk.minemaniamenus.inventory;

import com.github.minemaniauk.minemaniamenus.MessageManager;
import com.github.minemaniauk.minemaniamenus.MineManiaMenus;
import com.github.minemaniauk.minemaniamenus.User;
import com.github.smuddgge.velocityinventory.Inventory;
import com.github.smuddgge.velocityinventory.InventoryItem;
import com.github.smuddgge.velocityinventory.action.ActionResult;
import com.github.smuddgge.velocityinventory.action.action.ClickAction;
import com.github.smuddgge.velocityinventory.action.action.OpenAction;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents the main menu inventory.
 * You can create a new instance of this and use the
 * {@link Inventory#open(Player)} method to open the
 * inventory for a player.
 */
public class MainMenuInventory extends Inventory {

    /**
     * Used to create a new instance of the main menu inventory.
     */
    public MainMenuInventory() {
        super(InventoryType.GENERIC_9X6);

        // Custom inventory character.
        this.setTitle(MessageManager.convertToLegacy("&f₴₴₴₴₴₴₴₴☀"));

        // Add open action.
        this.addAction(new OpenAction() {
            @Override
            public @NotNull ActionResult onOpen(@NotNull Player player, @NotNull Inventory inventory) {
                MainMenuInventory.this.onOpen(player, inventory);
                return new ActionResult();
            }
        });
    }

    /**
     * This is called when the inventory is opened.
     *
     * @param player    The instance of the player that opened the inventory.
     * @param inventory This instance.
     */
    private void onOpen(@NotNull Player player, @NotNull Inventory inventory) {
        User user = new User(player);

        // Create the smp item.
        this.setTeleportItem(user,
                "smp",
                "&a&lSMP",
                "&7Click to teleport to the public smp.",
                0, 1, 9, 10
        );

        // Create the world of calm item.
        this.setTeleportItem(user,
                "worldofcalm",
                "&b&lWorld of Calm",
                "&7Click to teleport to the world of calm.",
                2, 3, 11, 12
        );

        // Create the red-stone item.
        this.setTeleportItem(user,
                "redstone",
                "&c&lRedstone",
                "&7Click to teleport to the redstone world.",
                6, 7, 15, 16
        );
    }

    private void setTeleportItem(@NotNull User user, @NotNull String serverName, @NotNull String title, @NotNull String loreLine, int... slots) {
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName(title)
                .addLore(loreLine)
                .addLore("&7")
                .addLore("&aOnline &f" + MineManiaMenus.getAmountOnline(serverName))
                .addSlots(slots)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                        Optional<RegisteredServer> optionalRegisteredServer = MineManiaMenus.getServer().getServer(serverName);

                        // Check if the server doesn't currently exist.
                        if (optionalRegisteredServer.isEmpty()) {
                            user.sendMessage("&7This server is currently offline.");
                            return new ActionResult().setCancelled(true);
                        }

                        // Otherwise, attempt connecting them to the server.
                        user.sendMessage("&7Teleporting to &f" + serverName + "...");
                        user.send(optionalRegisteredServer.get());
                        return new ActionResult();
                    }
                })
        );
    }
}

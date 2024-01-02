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
import com.github.minemaniauk.minemaniamenus.User;
import com.github.smuddgge.velocityinventory.Inventory;
import com.github.smuddgge.velocityinventory.InventoryItem;
import com.github.smuddgge.velocityinventory.action.ActionResult;
import com.github.smuddgge.velocityinventory.action.action.ClickAction;
import com.github.smuddgge.velocityinventory.action.action.OpenAction;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the game inventory.
 * Shows all the games a player can play and the rooms.
 */
public class GameInventory extends Inventory {

    /**
     * Used to create the game inventory.
     */
    public GameInventory() {
        super(InventoryType.GENERIC_9X6);

        // Custom inventory character.
        this.setTitle(MessageManager.convertToLegacy("&f₴₴₴₴₴₴₴₴⏅"));

        // Add open action.
        this.addAction(new OpenAction() {
            @Override
            public @NotNull ActionResult onOpen(@NotNull Player player, @NotNull Inventory inventory) {
                GameInventory.this.onOpen(player);
                return new ActionResult();
            }
        });
    }

    /**
     * This is called when the inventory is opened.
     *
     * @param player The instance of the player that opened the inventory.
     */
    private void onOpen(@NotNull Player player) {

        // Spleef.
        this.setGameItem("&b&lSpleef",
                "spleef",
                0, 1, 9, 10
        );

        // Hide and seek.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lHide And Seek")
                .setLore("&eComing soon...")
                .addSlots(2, 3, 11, 12)
        );

        // Tower defence.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&6&lTower Defence")
                .setLore("&eComing soon...")
                .addSlots(4, 5, 13, 14)
        );

        // Tnt run.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&e&lTnt Run")
                .setLore("&eComing soon...")
                .addSlots(6, 7, 15, 16)
        );

        // More.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lMore Games")
                .setLore("&eComing soon...")
                .addSlots(3, 17)
        );

        // TODO: Game Rooms.

        // Back.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lBack")
                .setLore("&7Click to go back to the main menu.")
                        .addClickAction(new ClickAction() {
                            @Override
                            public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                                MainMenuInventory mainMenuInventory = new MainMenuInventory();
                                mainMenuInventory.open(player);
                                return new ActionResult();
                            }
                        })
                .addSlots(45)
        );

        // More Rooms.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&f&lMore Rooms")
                .setLore("&eComing soon...")
                .addSlots(46, 47, 48, 49, 50)
        );

        // Profile.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&d&lProfile")
                .setLore("&eComing soon...",
                        "&7",
                        "&7Paws &f" + new User(player).getPaws())
                .addSlots(51, 52, 53)
        );
    }

    private void setGameItem(@NotNull String title, @NotNull String name, int... slots) {
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName(title)
                .setLore("&7Click to create a game room for &f" + name)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                        // TODO Create a game room.
                        return new ActionResult();
                    }
                })
                .addSlots(slots)
        );
    }
}

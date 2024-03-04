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

import com.github.minemaniauk.api.database.collection.GameRoomCollection;
import com.github.minemaniauk.api.database.record.GameRoomRecord;
import com.github.minemaniauk.api.game.GameType;
import com.github.minemaniauk.api.user.MineManiaUser;
import com.github.minemaniauk.minemaniamenus.MessageManager;
import com.github.minemaniauk.minemaniamenus.MineManiaMenus;
import com.github.minemaniauk.minemaniamenus.PublicTaskContainer;
import com.github.minemaniauk.minemaniamenus.User;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.velocityinventory.Inventory;
import com.github.smuddgge.velocityinventory.InventoryItem;
import com.github.smuddgge.velocityinventory.action.ActionResult;
import com.github.smuddgge.velocityinventory.action.action.ClickAction;
import com.github.smuddgge.velocityinventory.action.action.CloseAction;
import com.github.smuddgge.velocityinventory.action.action.OpenAction;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.inventory.InventoryClose;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

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
        final UUID uuid = UUID.randomUUID();
        this.addAction(new OpenAction() {
            @Override
            public @NotNull ActionResult onOpen(@NotNull Player player, @NotNull Inventory inventory) {
                GameInventory.this.onOpen(player);
                PublicTaskContainer.getInstance().runLoopTask(
                        () -> GameInventory.this.onOpen(player),
                        Duration.ofSeconds(2),
                        "GameInventory" + uuid
                );
                return new ActionResult();
            }
        });

        this.addAction(new CloseAction() {
            @Override
            public @NotNull ActionResult onClose(@NotNull InventoryClose inventoryClose, @NotNull Inventory inventory) {
                PublicTaskContainer.getInstance().stopTask("GameInventory" + uuid);
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
        this.removeActions();

        // Spleef.
        this.setGameItem("&b&lSpleef",
                "spleef",
                player,
                GameType.SPLEEF,
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
        this.setGameItem("&e&lTnt Run",
                "tnt run",
                player,
                GameType.TNT_RUN,
                6, 7, 15, 16
        );

        // More.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lMore Games")
                .setLore("&eComing soon...")
                .addSlots(3, 17, 17)
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
                        new MainMenuInventory().open(player);
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
                .setLore("&7",
                        "&7Paws &f" + new User(player).getPaws())
                .addSlots(51, 52, 53)
        );

        // Add rooms.
        this.addRooms(player);
    }

    private void setGameItem(@NotNull String title, @NotNull String name, @NotNull Player player, @NotNull GameType gameType, int... slots) {
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName(title)
                .setLore("&7Click to create a game room for &f" + name)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {

                        GameRoomRecord record = new GameRoomRecord(player.getUniqueId(), gameType);
                        record.setPrivate(true);
                        record.save();

                        new GameRoomInventory(record.getUuid()).open(player);
                        return new ActionResult();
                    }
                })
                .addSlots(slots)
        );
    }

    private void addRooms(@NotNull Player player) {
        // Get public rooms not in an arena.
        List<GameRoomRecord> roomRecordList = MineManiaMenus.getInstance()
                .getAPI().getDatabase()
                .getTable(GameRoomCollection.class)
                .getRecordList(new Query().match("is_private", false))
                .stream().filter(gameRoom -> MineManiaMenus.getInstance().getAPI()
                        .getGameManager()
                        .getArena(gameRoom.getUuid())
                        .isEmpty()
                )
                .toList();

        // First room.
        if (roomRecordList.isEmpty()) return;
        final GameRoomRecord firstRecord = roomRecordList.get(0);
        int slot = 27;
        for (MineManiaUser user : firstRecord.getPlayers()) {
            slot++;
            if (slot > 32) continue;

            // Create skull texture.
            CompoundTag tag = new CompoundTag();
            tag.putString("SkullOwner", user.getName());

            // Set the player's item.
            this.setItem(new InventoryItem()
                    .setMaterial(ItemType.PLAYER_HEAD)
                    .setNBT(tag)
                    .setName("&f&l" + user.getName())
                    .addSlots(slot)
            );
        }

        this.setItem(new InventoryItem()
                .setMaterial(ItemType.TNT)
                .setName("&c&lTnt Run")
                .setLore("&7These players are waiting to player tnt run.")
                .addSlots(33)
        );

        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lJoin Game Room")
                .addLore("&7Click to join this room and play tnt run!")
                .addSlots(34, 35)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {

                        firstRecord.addPlayer(player.getUniqueId());
                        firstRecord.save();

                        new GameRoomInventory(firstRecord.getUuid()).open(player);
                        return new ActionResult();
                    }
                })
        );

        // Second room.
        if (roomRecordList.size() < 2) return;
        final GameRoomRecord secondRecord = roomRecordList.get(1);
        slot = 36;
        for (MineManiaUser user : firstRecord.getPlayers()) {
            slot++;
            if (slot > 41) continue;

            // Create skull texture.
            CompoundTag tag = new CompoundTag();
            tag.putString("SkullOwner", user.getName());

            // Set the player's item.
            this.setItem(new InventoryItem()
                    .setMaterial(ItemType.PLAYER_HEAD)
                    .setNBT(tag)
                    .setName("&f&l" + user.getName())
                    .addSlots(slot)
            );
        }

        this.setItem(new InventoryItem()
                .setMaterial(ItemType.TNT)
                .setName("&c&lTnt Run")
                .setLore("&7These players are waiting to player tnt run.")
                .addSlots(42)
        );

        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lJoin Game Room")
                .addLore("&7Click to join this room and play tnt run!")
                .addSlots(43, 44)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {

                        secondRecord.addPlayer(player.getUniqueId());
                        secondRecord.save();

                        new GameRoomInventory(secondRecord.getUuid()).open(player);
                        return new ActionResult();
                    }
                })
        );
    }
}

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
import com.github.minemaniauk.minemaniamenus.*;
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

    private boolean closed;

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
                GameInventory.this.startRunTask(player, uuid);
                return new ActionResult();
            }
        });

        this.addAction(new CloseAction() {
            @Override
            public @NotNull ActionResult onClose(@NotNull InventoryClose inventoryClose, @NotNull Inventory inventory) {
                PublicTaskContainer.getInstance().stopTask("GameInventory" + uuid);
                GameInventory.this.closed = true;
                return new ActionResult();
            }
        });
    }

    public void startRunTask(@NotNull Player player, @NotNull UUID uuid) {
        PublicTaskContainer.getInstance().runTask(
                () -> {
                    GameInventory.this.onOpen(player);
                    if (GameInventory.this.closed) return;
                    startRunTask(player, uuid);
                },
                Duration.ofSeconds(2),
                "gameRoomInventory" + uuid
        );
    }

    /**
     * This is called when the inventory is opened.
     *
     * @param player The instance of the player that opened the inventory.
     */
    private void onOpen(@NotNull Player player) {
        this.removeActions();
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&7")
                .addSlots(0, 53)
        );

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

        // Reload.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&b&lReload Game Room List")
                .setLore("&7Click to reload the game room list.",
                        "&7You can also click in any blank space to reload the list.")
                .addSlots(46)
        );

        // More Rooms.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&f&lMore Rooms")
                .setLore("&eComing soon...")
                .addSlots(47, 48, 49, 50)
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
        GameInventory.setRoomLine(
                this,
                firstRecord,
                27,
                player
        );

        // Second room.
        if (roomRecordList.size() < 2) return;
        final GameRoomRecord secondRecord = roomRecordList.get(1);
        GameInventory.setRoomLine(
                this,
                secondRecord,
                36,
                player
        );
    }

    public static void setRoomLine(@NotNull Inventory inventory, @NotNull GameRoomRecord record, int startSlot, @NotNull Player player) {

        // Add the users.
        int slot = startSlot - 1;
        for (MineManiaUser user : record.getPlayers()) {
            slot++;
            if (slot > startSlot + 5) continue;

            // Create skull texture.
            CompoundTag tag = new CompoundTag();
            tag.putString("SkullOwner", user.getName());

            // Set the player item.
            inventory.setItem(new InventoryItem()
                    .setMaterial(ItemType.PLAYER_HEAD)
                    .setNBT(tag)
                    .setName("&f&l" + user.getName())
                    .addSlots(slot)
            );
        }

        // Add the game type.
        inventory.setItem(new InventoryItem()
                .setMaterial(record.getGameType().getMaterial(new MaterialConverter()))
                .setName("&f&l" + record.getGameType().getTitle())
                .setLore("&7This game room will be paying &f" + record.getGameType().getName() + "&7.")
                .addSlots(startSlot + 6)
        );

        // Add join item.
        inventory.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lJoin Game Room")
                .setLore("&7Click to join this game room.",
                        "&7",
                        "&fGame Type &a" + record.getGameType().getName())
                .addSlots(startSlot + 7, startSlot + 8)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                        record.addPlayer(player.getUniqueId());
                        record.save();
                        new GameRoomInventory(record.getUuid()).open(player);
                        return new ActionResult();
                    }
                })
        );
    }
}

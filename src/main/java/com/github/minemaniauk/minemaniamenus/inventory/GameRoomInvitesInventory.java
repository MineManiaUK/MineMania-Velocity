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
import com.github.minemaniauk.api.database.record.GameRoomInviteRecord;
import com.github.minemaniauk.api.database.record.GameRoomRecord;
import com.github.minemaniauk.minemaniamenus.MineManiaMenus;
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

import java.util.List;
import java.util.UUID;

/**
 * Represents the game room invites inventory.
 */
public class GameRoomInvitesInventory extends Inventory {

    /**
     * Used to create a game room invites inventory.
     */
    public GameRoomInvitesInventory() {
        super(InventoryType.GENERIC_9X3);

        this.setTitle("&8&lLoading...");

        this.addAction(new OpenAction() {
            @Override
            public @NotNull ActionResult onOpen(@NotNull Player player, @NotNull Inventory inventory) {
                GameRoomInvitesInventory.this.onOpen(player);
                return new ActionResult();
            }
        });
    }

    public void onOpen(@NotNull Player player) {

        List<GameRoomInviteRecord> recordList = MineManiaMenus.getInstance().getAPI()
                .getGameManager()
                .getInviteList(player.getUniqueId());

        if (recordList.isEmpty()) {
            this.setTitle("&8&lNo Invites");
            this.setItem(new InventoryItem()
                    .setMaterial(ItemType.BLACK_STAINED_GLASS_PANE)
                    .setName("&a&lYou have no current invites to a game room.")
                    .setLore()
            );
            return;
        }

        final GameRoomInviteRecord record = recordList.get(0);
        final GameRoomRecord gameRoom = MineManiaMenus.getInstance().getAPI().getDatabase()
                .getTable(GameRoomCollection.class)
                .getGameRoom(UUID.fromString(record.gameRoomUuid)).orElse(null);

        // Check if the invite is valid.
        if (gameRoom == null) {
            record.remove();
            new GameRoomInvitesInventory().open(player);
            return;
        }

        this.setTitle("&8&lInvite From " + gameRoom.getOwner().getName());

        this.setItem(new InventoryItem()
                .setMaterial(ItemType.LIME_STAINED_GLASS_PANE)
                .setName("&a&lAccept Invite")
                .setLore("&7Click to accept the invite.",
                        "&7",
                        "&fFrom &e" + gameRoom.getOwner().getName(),
                        "&fGame &a" + gameRoom.getGameType().getTitle()
                )
                .addSlots(0, 1, 2, 3,
                        9, 10, 11, 12,
                        18, 19, 20, 21)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                        record.remove();

                        gameRoom.addPlayer(player.getUniqueId());
                        gameRoom.save();

                        new User(player).sendMessage("&7&l> &7You have been added to the game room.");
                        new GameRoomInventory(gameRoom.getUuid()).open(player);
                        return new ActionResult();
                    }
                })
        );

        this.setItem(new InventoryItem()
                .setMaterial(ItemType.RED_STAINED_GLASS_PANE)
                .setName("&c&lDecline Invite")
                .setLore("&7Click to accept the invite.",
                        "&7",
                        "&fFrom &e" + gameRoom.getOwner().getName(),
                        "&fGame &a" + gameRoom.getGameType().getTitle()
                )
                .addSlots(5, 6, 7, 8,
                        14, 15, 16, 17,
                        23, 24, 25, 26)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                        record.remove();
                        new GameRoomInvitesInventory().open(player);
                        return new ActionResult();
                    }
                })
        );
    }
}

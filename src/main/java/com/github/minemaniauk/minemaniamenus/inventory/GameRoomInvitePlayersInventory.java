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
import com.github.minemaniauk.minemaniamenus.MessageManager;
import com.github.minemaniauk.minemaniamenus.MineManiaMenus;
import com.github.minemaniauk.minemaniamenus.PublicTaskContainer;
import com.github.minemaniauk.minemaniamenus.User;
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
import java.util.UUID;

public class GameRoomInvitePlayersInventory extends Inventory {

    private final @NotNull UUID gameRoomIdentifier;

    public GameRoomInvitePlayersInventory(@NotNull UUID gameRoomIdentifier) {
        super(InventoryType.GENERIC_9X6);

        this.gameRoomIdentifier = gameRoomIdentifier;

        // Custom inventory character.
        this.setTitle(MessageManager.convertToLegacy("&8&lInvite Players"));

        // Add open action.
        final UUID uuid = UUID.randomUUID();
        this.addAction(new OpenAction() {
            @Override
            public @NotNull ActionResult onOpen(@NotNull Player player, @NotNull Inventory inventory) {
                GameRoomInvitePlayersInventory.this.onOpen(player);
                return new ActionResult();
            }
        });

        this.addAction(new CloseAction() {
            @Override
            public @NotNull ActionResult onClose(@NotNull InventoryClose inventoryClose, @NotNull Inventory inventory) {
                PublicTaskContainer.getInstance().stopTask("GameRoomInvitePlayersInventory" + uuid);
                return new ActionResult();
            }
        });
    }

    private void onOpen(@NotNull Player player) {
        this.removeActions();
        this.setItem(new InventoryItem().setMaterial(ItemType.AIR).addSlots(0, 53));

        int slot = -1;
        for (Player invitePlayer : MineManiaMenus.getInstance().getProxyServer().getAllPlayers()) {
            slot++;
            if (slot > 44) continue;

            // Check if the player has already been invited.
            if (MineManiaMenus.getInstance().getAPI().getGameManager()
                    .hasBeenInvited(invitePlayer.getUniqueId(), this.gameRoomIdentifier)) {

                this.setItem(new InventoryItem()
                        .setMaterial(ItemType.BLACK_STAINED_GLASS_PANE)
                        .setName("&f&l" + invitePlayer.getGameProfile().getName() + " &a&lHas Been Invited")
                        .addSlots(slot)
                );
                continue;
            }

            // Create skull texture.
            CompoundTag tag = new CompoundTag();
            tag.putString("SkullOwner", invitePlayer.getGameProfile().getName());

            // Set the player's item.
            this.setItem(new InventoryItem()
                    .setMaterial(ItemType.PLAYER_HEAD)
                    .setNBT(tag)
                    .setName("&6&lInvite &f&l" + invitePlayer.getGameProfile().getName())
                    .setLore("&7Click to send a invite to this player.")
                    .addSlots(slot)
                    .addClickAction(new ClickAction() {
                        @Override
                        public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                            GameRoomInvitePlayersInventory.this.removeActions();

                            // Get the game room.
                            GameRoomRecord record = MineManiaMenus.getInstance().getAPI().getDatabase()
                                    .getTable(GameRoomCollection.class)
                                    .getGameRoom(GameRoomInvitePlayersInventory.this.gameRoomIdentifier)
                                    .orElse(null);

                            if (record == null) {
                                new User(player).sendMessage("&7&l> &7The game room you are in no longer exists.");
                                new MainMenuInventory().open(player);
                                return new ActionResult();
                            }

                            MineManiaMenus.getInstance().getAPI().getGameManager()
                                    .sendInvite(invitePlayer.getUniqueId(), record);

                            GameRoomInvitePlayersInventory.this.onOpen(player);
                            return new ActionResult();
                        }
                    })
            );
        }

        this.setItem(new InventoryItem()
                .setMaterial(ItemType.LIME_STAINED_GLASS_PANE)
                .setName("&a&lBack To Game Room")
                .setLore("&7Click to go back to the game room.")
                .addSlots(45, 53)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                        new GameRoomInventory(GameRoomInvitePlayersInventory.this.gameRoomIdentifier).open(player);
                        return new ActionResult();
                    }
                })
        );
    }
}

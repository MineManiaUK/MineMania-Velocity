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
import com.github.minemaniauk.api.game.Arena;
import com.github.minemaniauk.api.user.MineManiaUser;
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
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.inventory.InventoryClose;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the game room inventory.
 */
public class GameRoomInventory extends Inventory {

    private final @NotNull UUID gameRoomIdentifier;

    /**
     * Used to create a game room inventory.
     *
     * @param gameRoomIdentifier The game room identifier to represent.
     */
    public GameRoomInventory(@NotNull UUID gameRoomIdentifier) {
        super(InventoryType.GENERIC_9X6);

        this.gameRoomIdentifier = gameRoomIdentifier;

        // Custom inventory character.
        this.setTitle(MessageManager.convertToLegacy("&f₴₴₴₴₴₴₴₴㉿"));

        // Add open action.
        final UUID uuid = UUID.randomUUID();
        this.addAction(new OpenAction() {
            @Override
            public @NotNull ActionResult onOpen(@NotNull Player player, @NotNull Inventory inventory) {
                GameRoomInventory.this.onOpen(player);
                PublicTaskContainer.getInstance().runLoopTask(
                        () -> GameRoomInventory.this.onOpen(player),
                        Duration.ofSeconds(2),
                        "gameRoomInventory" + uuid
                );
                return new ActionResult();
            }
        });

        this.addAction(new CloseAction() {
            @Override
            public @NotNull ActionResult onClose(@NotNull InventoryClose inventoryClose, @NotNull Inventory inventory) {
                PublicTaskContainer.getInstance().stopTask("gameRoomInventory" + uuid);
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

        final GameRoomRecord record = MineManiaMenus.getInstance().getAPI().getDatabase()
                .getTable(GameRoomCollection.class)
                .getGameRoom(this.gameRoomIdentifier).orElse(null);

        // Check if the record is null.
        if (record == null) {
            new GameInventory().open(player);
            return;
        }

        // Set the players.
        this.setPlayers(record);

        // Back button.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lBack")
                .setLore("&7Click to go back to the &f/menu&7.")
                .addSlots(45)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                        new MainMenuInventory().open(player);
                        return new ActionResult();
                    }
                })
        );

        // Leave button.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&c&lLeave Game Room")
                .setLore("&7Click to leave this game room.")
                .addSlots(46, 47)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {

                        // Check if the player is the owner.
                        if (record.getOwner().getUniqueId().equals(player.getUniqueId())) {
                            MineManiaMenus.getInstance().getAPI()
                                    .getDatabase()
                                    .getTable(GameRoomCollection.class)
                                    .removeRecord(record);

                            new GameInventory().open(player);
                            return new ActionResult();
                        }

                        // Update record.
                        record.removePlayer(player.getUniqueId());
                        record.save();

                        new GameInventory().open(player);
                        return new ActionResult();
                    }
                })
        );

        // Add start button.
        this.setStartButton(record, player);

        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&b&lInvite Players")
                .setLore("&7Invite a player to your game room.")
                .addSlots(51, 52)
                .addClickAction(new ClickAction() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                        new GameRoomInvitePlayersInventory(GameRoomInventory.this.gameRoomIdentifier).open(player);
                        return new ActionResult();
                    }
                })
        );

        // Add lock button.
        if (record.isPrivate()) {
            this.setItem(new InventoryItem()
                    .setMaterial(ItemType.ENDER_PEARL)
                    .setName("&6&lSet Public")
                    .setLore("&7Should any player be allowed to join",
                            "&7your game room?",
                            "&7",
                            "&fCurrently &ePrivate",
                            "&7Only invited players can join.")
                    .addSlots(53)
                    .addClickAction(new ClickAction() {
                        @Override
                        public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                            new Thread(() -> {
                                record.setPrivate(false);
                                record.save();
                                GameRoomInventory.this.onOpen(player);
                            }).start();
                            return new ActionResult();
                        }
                    })
            );
        } else {
            this.setItem(new InventoryItem()
                    .setMaterial(ItemType.ENDER_EYE)
                    .setName("&6&lSet Private")
                    .setLore("&7Should only invited players be",
                            "&7able to join the game room?",
                            "&7",
                            "&fCurrently &ePublic",
                            "&7Anyone can join.")
                    .addSlots(53)
                    .addClickAction(new ClickAction() {
                        @Override
                        public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                            new Thread(() -> {
                                record.setPrivate(true);
                                record.save();
                                GameRoomInventory.this.onOpen(player);
                            }).start();
                            return new ActionResult();
                        }
                    })
            );
        }
    }

    private void setStartButton(@NotNull GameRoomRecord record, @NotNull Player player) {
        if (record.getOwner().getUniqueId().equals(player.getUniqueId())) {
            this.setItem(new InventoryItem()
                    .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                    .setCustomModelData(1)
                    .setName("&a&lStart Game")
                    .setLore("&7Click to start a game.",
                            "&7",
                            "&e&lAvailable Arenas"
                    )
                    .addLore(MineManiaMenus.getInstance().getAPI()
                            .getGameManager()
                            .getArenaAvailabilityAsLore(record.getGameType())
                            .stream().map(line -> "&7- &f" + line)
                            .toList()
                    )
                    .addSlots(48, 49, 50)
                    .addClickAction(new ClickAction() {
                        @Override
                        public @NotNull ActionResult onClick(@NotNull InventoryClick inventoryClick, @NotNull Inventory inventory) {
                            GameRoomInventory.this.startGame(record, player);
                            return new ActionResult();
                        }
                    })
            );
            return;
        }

        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&7&lStart Game")
                .setLore("&fOnly the owner of the game room can start the game.")
                .addSlots(48, 49, 50)
        );
    }

    private void setPlayers(@NotNull GameRoomRecord record) {

        // Get the owner.
        MineManiaUser owner = record.getOwner();

        // Get the owner skull component.
        CompoundTag ownerTag = new CompoundTag();
        ownerTag.putString("SkullOwner", owner.getName());

        // Add the owner.
        this.setItem(new InventoryItem()
                .setMaterial(ItemType.PLAYER_HEAD)
                .setNBT(ownerTag)
                .setName("&6&l" + owner.getName())
                .addSlots(10)
        );

        // Create the iterator for the slots.
        Iterator<Integer> iterator = List.of(
                11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25
        ).iterator();

        for (MineManiaUser user : record.getPlayers()) {

            // Ensure they are not the owner.
            if (user.getUniqueId().equals(owner.getUniqueId())) continue;

            // Check if there are any more slots.
            if (!iterator.hasNext()) return;

            // Create skull texture.
            CompoundTag tag = new CompoundTag();
            tag.putString("SkullOwner", user.getName());

            // Set the players item.
            this.setItem(new InventoryItem()
                    .setMaterial(ItemType.PLAYER_HEAD)
                    .setNBT(tag)
                    .setName("&f&l" + user.getName())
                    .addSlots(iterator.next())
            );
        }
    }

    private void startGame(@NotNull GameRoomRecord record, @NotNull Player player) {

        // Create a user for the player.
        User user = new User(player);

        // Message the owner.
        user.sendMessage("&7&l> &7Searching for an arena...");

        // Check if there is an available arena.
        Arena arena = MineManiaMenus.getInstance().getAPI()
                .getGameManager()
                .getFirstAvailableArena(record.getGameType(), record.getPlayerUuids().size())
                .orElse(null);

        // Check if it returned an arena.
        if (arena == null) {
            user.sendMessage("&c&l> &cThere are currently no available arenas.");
            return;
        }

        // Set game room identifier before other rooms take the arena.
        arena.setGameRoomIdentifier(record.getUuid());
        arena.save();

        // Start the game.
        arena.activate();

        // Attempt to get the instance of the server.
        Optional<RegisteredServer> optionalRegisteredServer = MineManiaMenus.getInstance().getServer(arena.getServerName());
        if (optionalRegisteredServer.isEmpty()) {
            throw new RuntimeException("Could nto find server with name: " + arena.getServerName());
        }

        // Warp players.
        for (MineManiaUser mineManiaUser : record.getPlayers()) {
            Optional<Player> optionalPlayer = MineManiaMenus.getInstance().getPlayer(mineManiaUser);
            if (optionalPlayer.isEmpty()) continue;
            new User(optionalPlayer.get()).send(optionalRegisteredServer.get());
        }
    }
}

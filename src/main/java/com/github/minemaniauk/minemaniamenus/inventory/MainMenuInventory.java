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
import com.github.smuddgge.velocityinventory.Inventory;
import com.github.smuddgge.velocityinventory.InventoryItem;
import com.github.smuddgge.velocityinventory.action.ActionResult;
import com.github.smuddgge.velocityinventory.action.action.OpenAction;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.data.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class MainMenuInventory extends Inventory {

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

    private void onOpen(@NotNull Player player, @NotNull Inventory inventory) {

        inventory.setItem(new InventoryItem()
                .setName("&a&lSMP")
                .addLore("&7Click to join the public smp!")
                .addLore("&7")
                .addLore("&aOnline &f" + MineManiaMenus.getAmountOnline("smp"))
        );
    }
}

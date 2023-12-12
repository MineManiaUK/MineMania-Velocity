package com.github.minemaniauk.minemaniamenus.inventory;

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
        super(InventoryType.GENERIC_9X5);

        // Custom inventory character.
        this.setTitle("₴₴₴₴₴₴₴₴☀");

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

package com.github.minemaniauk.minemaniamenus.inventory;

import com.github.smuddgge.velocityinventory.Inventory;
import com.github.smuddgge.velocityinventory.action.ActionResult;
import com.github.smuddgge.velocityinventory.action.action.OpenAction;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.data.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class MainMenuInventory extends Inventory {

    public MainMenuInventory() {
        super(InventoryType.GENERIC_9X5);

        this.setTitle("")

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

    }
}

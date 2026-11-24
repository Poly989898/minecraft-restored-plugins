package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConsumableItemInteraction {
   boolean handleConsumableEffect(
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, @Nullable Type var5
   );
}

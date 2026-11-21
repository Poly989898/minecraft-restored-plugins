package io.lumine.mythic.bukkit;

import io.lumine.mythic.api.adapters.AbstractTeleportData;
import io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDeathProtectionComponent;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.adapters.BukkitTeleportData;
import io.lumine.mythic.bukkit.adapters.item.ItemComponentBukkitItemStack;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.inventory.ItemStack;

public interface BukkitPlatformBridge {
   default BukkitItemStack wrapItem(ItemStack itemStack) {
      return new ItemComponentBukkitItemStack(itemStack);
   }

   AbstractItemConsumableComponent createItemConsumableComponent(MythicItem mythicItem, MythicConfig mythicConfig);

   AbstractItemDeathProtectionComponent createItemDeathProtectionComponent(MythicItem mythicItem, MythicConfig mythicConfig);

   default AbstractTeleportData createTeleportData(MythicLineConfig config) {
      return new BukkitTeleportData(config);
   }
}

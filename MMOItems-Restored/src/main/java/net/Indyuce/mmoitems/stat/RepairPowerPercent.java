package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RepairPowerPercent extends DoubleStat implements ConsumableItemInteraction {
   public RepairPowerPercent() {
      super(
         "REPAIR_PERCENT",
         Material.DAMAGED_ANVIL,
         "Repair Percentage",
         new String[]{"The percentage of total durability getting", "repaired when dragged onto another item."},
         new String[]{"consumable"}
      );
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent event, @NotNull PlayerData playerData, @NotNull Consumable consumable, @NotNull NBTItem nbtItem, @Nullable Type type
   ) {
      double stat = consumable.getNBTItem().getStat(ItemStats.REPAIR_PERCENT.getId());
      return stat <= 0.0 ? false : RepairPower.handleRepair(playerData, consumable, nbtItem, var3x -> {
         double var4x = var3x != null ? var3x.getMaxDurability() : nbtItem.getItem().getType().getMaxDurability();
         return (int)(stat * var4x / 100.0);
      });
   }
}

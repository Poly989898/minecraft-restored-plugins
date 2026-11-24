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
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, @Nullable Type var5
   ) {
      double var6 = var3.getNBTItem().getStat(ItemStats.REPAIR_PERCENT.getId());
      return var6 <= 0.0 ? false : RepairPower.handleRepair(var2, var3, var4, var3x -> {
         double var4x = var3x != null ? var3x.getMaxDurability() : var4.getItem().getType().getMaxDurability();
         return (int)(var6 * var4x / 100.0);
      });
   }
}

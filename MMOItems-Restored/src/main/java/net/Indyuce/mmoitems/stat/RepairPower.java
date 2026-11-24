package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.function.Function;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.CustomSound;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.interaction.util.DurabilityItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.listener.CustomSoundListener;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import net.Indyuce.mmoitems.util.RepairUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class RepairPower extends DoubleStat implements ConsumableItemInteraction {
   public static final String REPAIR_TYPE_TAG_KEY = "MMOITEMS_REPAIR_TYPE";

   public RepairPower() {
      super(
         "REPAIR",
         Material.ANVIL,
         "Repair Power",
         new String[]{"The flat amount of durability your item", "can repair when set an item."},
         new String[]{"consumable"}
      );
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, Type var5
   ) {
      int var6 = (int)var3.getNBTItem().getStat(ItemStats.REPAIR.getId());
      return var6 <= 0 ? false : handleRepair(var2, var3, var4, var1x -> var6);
   }

   public static boolean handleRepair(
      @NotNull PlayerData var0, @NotNull Consumable var1, @NotNull NBTItem var2, @NotNull Function<DurabilityItem, Integer> var3
   ) {
      Player var4 = var0.getPlayer();
      String var5 = var1.getNBTItem().getString("MMOITEMS_REPAIR_TYPE");
      String var6 = var2.getString("MMOITEMS_REPAIR_TYPE");
      if (!MMOUtils.checkReference(var5, var6)) {
         Message.UNABLE_TO_REPAIR.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(var2.getItem())).send(var4);
         var4.getPlayer().playSound(var4.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
         return false;
      }

      boolean var7 = var2.hasTag("MMOITEMS_DURABILITY");
      if (var7) {
         DurabilityItem var8 = new DurabilityItem(var4, var2);
         if (var8.getDurability() >= var8.getMaxDurability()) {
            return false;
         }

         int var9 = (Integer)var3.apply(var8);
         var2.getItem().setItemMeta(var8.addDurability(var9).toItem().getItemMeta());
         Message.REPAIRED_ITEM.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var2.getItem()), "#amount#", String.valueOf(var9)).send(var4);
         CustomSoundListener.playSound(var1.getItem(), CustomSound.ON_CONSUME, var4);
         return true;
      } else {
         return RepairUtils.repairVanillaItem(var0, var2, var1, (Integer)var3.apply(null));
      }
   }
}

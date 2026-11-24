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
      @NotNull InventoryClickEvent event, @NotNull PlayerData playerData, @NotNull Consumable consumable, @NotNull NBTItem nbtItem, Type type
   ) {
      int stat = (int)consumable.getNBTItem().getStat(ItemStats.REPAIR.getId());
      return stat <= 0 ? false : handleRepair(playerData, consumable, nbtItem, repairedItem -> stat);
   }

   public static boolean handleRepair(
      @NotNull PlayerData playerData, @NotNull Consumable consumable, @NotNull NBTItem nbtItem, @NotNull Function<DurabilityItem, Integer> function
   ) {
      Player player = playerData.getPlayer();
      String mMOITEMSREPAIRTYPE = consumable.getNBTItem().getString("MMOITEMS_REPAIR_TYPE");
      String repairType = nbtItem.getString("MMOITEMS_REPAIR_TYPE");
      if (!MMOUtils.checkReference(mMOITEMSREPAIRTYPE, repairType)) {
         Message.UNABLE_TO_REPAIR.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(nbtItem.getItem())).send(player);
         player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
         return false;
      }

      boolean hasTag = nbtItem.hasTag("MMOITEMS_DURABILITY");
      if (hasTag) {
         DurabilityItem durabilityItem = new DurabilityItem(player, nbtItem);
         if (durabilityItem.getDurability() >= durabilityItem.getMaxDurability()) {
            return false;
         }

         int count = (Integer)function.apply(durabilityItem);
         nbtItem.getItem().setItemMeta(durabilityItem.addDurability(count).toItem().getItemMeta());
         Message.REPAIRED_ITEM.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(nbtItem.getItem()), "#amount#", String.valueOf(count)).send(player);
         CustomSoundListener.playSound(consumable.getItem(), CustomSound.ON_CONSUME, player);
         return true;
      } else {
         return RepairUtils.repairVanillaItem(playerData, nbtItem, consumable, (Integer)function.apply(null));
      }
   }
}

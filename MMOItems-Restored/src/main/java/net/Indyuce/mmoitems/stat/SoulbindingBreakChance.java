package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.Random;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.BreakSoulboundEvent;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.annotation.HasCategory;
import net.Indyuce.mmoitems.stat.data.SoulboundData;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@HasCategory(cat = "soulbound")
public class SoulbindingBreakChance extends DoubleStat implements ConsumableItemInteraction {
   private static final Random RANDOM = new Random();

   public SoulbindingBreakChance() {
      super(
         "SOULBOUND_BREAK_CHANCE",
         Material.ENDER_EYE,
         "Soulbound Break Chance",
         new String[]{
            "The chance of breaking an item's", "soulbound when drag & drop'd on it.", "This chance is lowered depending", "on the soulbound's level."
         },
         new String[]{"consumable"}
      );
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent event, @NotNull PlayerData playerData, @NotNull Consumable consumable, @NotNull NBTItem nbtItem, Type type
   ) {
      Player player = playerData.getPlayer();
      double stat = consumable.getNBTItem().getStat("SOULBOUND_BREAK_CHANCE");
      if (stat <= 0.0) {
         return false;
      }

      VolatileMMOItem volatileMMOItem = new VolatileMMOItem(nbtItem);
      if (!volatileMMOItem.hasData(ItemStats.SOULBOUND)) {
         Message.NO_SOULBOUND.format(ChatColor.RED).send(player);
         player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         return false;
      }

      SoulboundData data = (SoulboundData)volatileMMOItem.getData(ItemStats.SOULBOUND);
      if (Math.max(1.0, consumable.getNBTItem().getStat(ItemStats.SOULBOUND_LEVEL.getId())) < data.getLevel()) {
         Message.LOW_SOULBOUND_LEVEL.format(ChatColor.RED, "#level#", MMOUtils.intToRoman(data.getLevel())).send(player);
         return false;
      }

      if (RANDOM.nextDouble() < stat / 100.0) {
         BreakSoulboundEvent event2 = new BreakSoulboundEvent(playerData, consumable.getMMOItem(), nbtItem);
         Bukkit.getPluginManager().callEvent(event2);
         if (event2.isCancelled()) {
            return false;
         }

         LiveMMOItem liveMMOItem;
         (liveMMOItem = new LiveMMOItem(nbtItem)).removeData(ItemStats.SOULBOUND);
         nbtItem.getItem().setItemMeta(liveMMOItem.newBuilder().build().getItemMeta());
         Message.SUCCESSFULLY_BREAK_BIND.format(ChatColor.YELLOW, "#level#", MMOUtils.intToRoman(data.getLevel())).send(player);
         player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 2.0F);
      } else {
         Message.UNSUCCESSFUL_SOULBOUND_BREAK.format(ChatColor.RED).send(player);
         player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 0.0F);
      }

      return true;
   }
}

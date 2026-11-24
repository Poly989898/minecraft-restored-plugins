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
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, Type var5
   ) {
      Player var6 = var2.getPlayer();
      double var7 = var3.getNBTItem().getStat("SOULBOUND_BREAK_CHANCE");
      if (var7 <= 0.0) {
         return false;
      }

      VolatileMMOItem var9 = new VolatileMMOItem(var4);
      if (!var9.hasData(ItemStats.SOULBOUND)) {
         Message.NO_SOULBOUND.format(ChatColor.RED).send(var6);
         var6.playSound(var6.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         return false;
      }

      SoulboundData var10 = (SoulboundData)var9.getData(ItemStats.SOULBOUND);
      if (Math.max(1.0, var3.getNBTItem().getStat(ItemStats.SOULBOUND_LEVEL.getId())) < var10.getLevel()) {
         Message.LOW_SOULBOUND_LEVEL.format(ChatColor.RED, "#level#", MMOUtils.intToRoman(var10.getLevel())).send(var6);
         return false;
      }

      if (RANDOM.nextDouble() < var7 / 100.0) {
         BreakSoulboundEvent var11 = new BreakSoulboundEvent(var2, var3.getMMOItem(), var4);
         Bukkit.getPluginManager().callEvent(var11);
         if (var11.isCancelled()) {
            return false;
         }

         LiveMMOItem var12;
         (var12 = new LiveMMOItem(var4)).removeData(ItemStats.SOULBOUND);
         var4.getItem().setItemMeta(var12.newBuilder().build().getItemMeta());
         Message.SUCCESSFULLY_BREAK_BIND.format(ChatColor.YELLOW, "#level#", MMOUtils.intToRoman(var10.getLevel())).send(var6);
         var6.playSound(var6.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 2.0F);
      } else {
         Message.UNSUCCESSFUL_SOULBOUND_BREAK.format(ChatColor.RED).send(var6);
         var6.playSound(var6.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 0.0F);
      }

      return true;
   }
}

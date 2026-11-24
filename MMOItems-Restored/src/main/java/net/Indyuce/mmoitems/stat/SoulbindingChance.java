package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.Random;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.ApplySoulboundEvent;
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
import org.jetbrains.annotations.Nullable;

@HasCategory(cat = "soulbound")
public class SoulbindingChance extends DoubleStat implements ConsumableItemInteraction {
   private static final Random random = new Random();

   public SoulbindingChance() {
      super(
         "SOULBINDING_CHANCE",
         Material.ENDER_EYE,
         "Soulbinding Chance",
         new String[]{"Defines the chance your item has to", "link another item to your soul,", "preventing other players from using it."},
         new String[]{"consumable"}
      );
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, @Nullable Type var5
   ) {
      Player var6 = var2.getPlayer();
      if (var5 == null) {
         return false;
      }

      double var7 = var3.getNBTItem().getStat("SOULBINDING_CHANCE");
      if (var7 <= 0.0) {
         return false;
      }

      if (var4.getItem().getAmount() > 1) {
         Message.CANT_BIND_STACKED.format(ChatColor.RED).send(var6);
         return false;
      }

      VolatileMMOItem var9 = new VolatileMMOItem(var4);
      if (var9.hasData(ItemStats.SOULBOUND)) {
         SoulboundData var13 = (SoulboundData)var9.getData(ItemStats.SOULBOUND);
         Message.CANT_BIND_ITEM.format(ChatColor.RED, "#player#", var13.getName(), "#level#", MMOUtils.intToRoman(var13.getLevel())).send(var6);
         return false;
      }

      if (random.nextDouble() < var7 / 100.0) {
         ApplySoulboundEvent var10 = new ApplySoulboundEvent(var2, var3.getMMOItem(), var4);
         Bukkit.getPluginManager().callEvent(var10);
         if (var10.isCancelled()) {
            return false;
         }

         int var11 = (int)Math.max(1.0, var3.getNBTItem().getStat("SOULBOUND_LEVEL"));
         LiveMMOItem var12;
         (var12 = new LiveMMOItem(var4)).setData(ItemStats.SOULBOUND, new SoulboundData(var6.getUniqueId(), var6.getName(), var11));
         var4.getItem().setItemMeta(var12.newBuilder().build().getItemMeta());
         Message.SUCCESSFULLY_BIND_ITEM
            .format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var4.getItem()), "#level#", MMOUtils.intToRoman(var11))
            .send(var6);
         var6.playSound(var6.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
         return true;
      } else {
         Message.UNSUCCESSFUL_SOULBOUND.format(ChatColor.RED).send(var6);
         var6.playSound(var6.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         return true;
      }
   }
}

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
      @NotNull InventoryClickEvent event, @NotNull PlayerData playerData, @NotNull Consumable consumable, @NotNull NBTItem nbtItem, @Nullable Type type
   ) {
      Player player = playerData.getPlayer();
      if (type == null) {
         return false;
      }

      double stat = consumable.getNBTItem().getStat("SOULBINDING_CHANCE");
      if (stat <= 0.0) {
         return false;
      }

      if (nbtItem.getItem().getAmount() > 1) {
         Message.CANT_BIND_STACKED.format(ChatColor.RED).send(player);
         return false;
      }

      VolatileMMOItem volatileMMOItem = new VolatileMMOItem(nbtItem);
      if (volatileMMOItem.hasData(ItemStats.SOULBOUND)) {
         SoulboundData data = (SoulboundData)volatileMMOItem.getData(ItemStats.SOULBOUND);
         Message.CANT_BIND_ITEM.format(ChatColor.RED, "#player#", data.getName(), "#level#", MMOUtils.intToRoman(data.getLevel())).send(player);
         return false;
      }

      if (random.nextDouble() < stat / 100.0) {
         ApplySoulboundEvent event2 = new ApplySoulboundEvent(playerData, consumable.getMMOItem(), nbtItem);
         Bukkit.getPluginManager().callEvent(event2);
         if (event2.isCancelled()) {
            return false;
         }

         int count = (int)Math.max(1.0, consumable.getNBTItem().getStat("SOULBOUND_LEVEL"));
         LiveMMOItem liveMMOItem;
         (liveMMOItem = new LiveMMOItem(nbtItem)).setData(ItemStats.SOULBOUND, new SoulboundData(player.getUniqueId(), player.getName(), count));
         nbtItem.getItem().setItemMeta(liveMMOItem.newBuilder().build().getItemMeta());
         Message.SUCCESSFULLY_BIND_ITEM
            .format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(nbtItem.getItem()), "#level#", MMOUtils.intToRoman(count))
            .send(player);
         player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
         return true;
      } else {
         Message.UNSUCCESSFUL_SOULBOUND.format(ChatColor.RED).send(player);
         player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         return true;
      }
   }
}

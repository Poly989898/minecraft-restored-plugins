package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.List;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.DeconstructItemEvent;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CanDeconstruct extends BooleanStat implements ConsumableItemInteraction {
   public CanDeconstruct() {
      super(
         "CAN_DECONSTRUCT",
         Material.PAPER,
         "Can Deconstruct?",
         new String[]{"Players can deconstruct their item", "using this consumable, creating", "another random item."},
         new String[]{"consumable"}
      );
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, Type var5
   ) {
      String var6 = var4.getString("MMOITEMS_TIER");
      if (!var6.equals("") && var3.getNBTItem().getBoolean("MMOITEMS_CAN_DECONSTRUCT")) {
         ItemTier var7 = MMOItems.plugin.getTiers().get(var6);
         List<ItemStack> var8 = var7.getDeconstructedLoot(var2);
         if (var8.isEmpty()) {
            return false;
         }

         DeconstructItemEvent var9 = new DeconstructItemEvent(var2, var3.getMMOItem(), var4, var8);
         Bukkit.getPluginManager().callEvent(var9);
         if (var9.isCancelled()) {
            return false;
         }

         Player var10 = var2.getPlayer();
         Message.SUCCESSFULLY_DECONSTRUCTED.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var1.getCurrentItem())).send(var10);
         var1.getCurrentItem().setAmount(var1.getCurrentItem().getAmount() - 1);

         for (ItemStack var12 : var10.getInventory().addItem(var8.toArray(new ItemStack[0])).values()) {
            var10.getWorld().dropItem(var10.getLocation(), var12);
         }

         var10.playSound(var10.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
         return true;
      } else {
         return false;
      }
   }
}

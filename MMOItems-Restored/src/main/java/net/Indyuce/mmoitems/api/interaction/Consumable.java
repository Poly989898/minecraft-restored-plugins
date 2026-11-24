package net.Indyuce.mmoitems.api.interaction;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.SmartGive;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.ConsumableConsumedEvent;
import net.Indyuce.mmoitems.api.item.util.LoreUpdate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.stat.type.PlayerConsumable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Consumable extends UseItem {
   @Deprecated
   public Consumable(Player var1, NBTItem var2) {
      super(var1, var2);
   }

   public Consumable(PlayerData var1, NBTItem var2) {
      super(var1, var2);
   }

   @Override
   public boolean checkItemRequirements() {
      return MythicLib.plugin.getFlags().isFlagAllowed(this.player, CustomFlag.MI_CONSUMABLES) && this.playerData.getRPG().canUse(this.getNBTItem(), true);
   }

   public boolean useOnItem(@NotNull InventoryClickEvent var1, @NotNull NBTItem var2) {
      if (var1.getClickedInventory() != var1.getWhoClicked().getInventory()) {
         return false;
      }

      Type var3 = MMOItems.getType(var2);

      for (ConsumableItemInteraction var5 : MMOItems.plugin.getStats().getConsumableActions()) {
         if (var5.handleConsumableEffect(var1, this.playerData, this, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   public Consumable.ConsumableConsumeResult useOnPlayer(EquipmentSlot var1, boolean var2) {
      NBTItem var3 = this.getNBTItem();
      if (var3.getBoolean(ItemStats.INEDIBLE.getNBTPath())) {
         return Consumable.ConsumableConsumeResult.CANCEL;
      }

      ConsumableConsumedEvent var4 = new ConsumableConsumedEvent(this.playerData, this.mmoitem, this);
      Bukkit.getPluginManager().callEvent(var4);
      if (var4.isCancelled()) {
         return Consumable.ConsumableConsumeResult.CANCEL;
      }

      for (PlayerConsumable var6 : MMOItems.plugin.getStats().getPlayerConsumables()) {
         var6.onConsume(this.mmoitem, this.player, var2);
      }

      int var11 = var3.getInteger(ItemStats.MAX_CONSUME.getNBTPath());
      if (var11 > 1) {
         var3.addTag(new ItemTag[]{new ItemTag(ItemStats.MAX_CONSUME.getNBTPath(), --var11)});
         String var13 = MythicLib.inst().parseColors(ItemStats.MAX_CONSUME.getGeneralStatFormat());
         String var7 = var13.replace("{value}", String.valueOf(var11 + 1));
         String var8 = var13.replace("{value}", String.valueOf(var11));
         ItemStack var9 = new LoreUpdate(var3.toItem(), var7, var8).updateLore();
         ItemStack var10 = var3.getItem();
         if (var10.getAmount() > 1) {
            var9.setAmount(1);
            this.player.getInventory().setItem(var1, var9);
            var10.setAmount(var10.getAmount() - 1);
            new SmartGive(this.player).give(new ItemStack[]{var10});
         } else {
            this.player.getInventory().setItem(var1, var9);
         }

         return Consumable.ConsumableConsumeResult.NOT_CONSUME;
      } else {
         return var4.isConsumed() && !var3.getBoolean(ItemStats.DISABLE_RIGHT_CLICK_CONSUME.getNBTPath())
            ? Consumable.ConsumableConsumeResult.CONSUME
            : Consumable.ConsumableConsumeResult.NOT_CONSUME;
      }
   }

   public boolean hasVanillaEating() {
      return (this.getItem().getType().isEdible() || this.getItem().getType() == Material.POTION || this.getItem().getType() == Material.MILK_BUCKET)
         && this.getNBTItem().hasTag("MMOITEMS_VANILLA_EATING");
   }

   public enum ConsumableConsumeResult {
      CANCEL,
      CONSUME,
      NOT_CONSUME;
   }
}

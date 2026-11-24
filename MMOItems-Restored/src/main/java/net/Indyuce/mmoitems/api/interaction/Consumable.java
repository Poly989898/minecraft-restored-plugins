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
   public Consumable(Player player, NBTItem nbtItem) {
      super(player, nbtItem);
   }

   public Consumable(PlayerData playerData, NBTItem nbtItem) {
      super(playerData, nbtItem);
   }

   @Override
   public boolean checkItemRequirements() {
      return MythicLib.plugin.getFlags().isFlagAllowed(this.player, CustomFlag.MI_CONSUMABLES) && this.playerData.getRPG().canUse(this.getNBTItem(), true);
   }

   public boolean useOnItem(@NotNull InventoryClickEvent event, @NotNull NBTItem nbtItem) {
      if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
         return false;
      }

      Type type = MMOItems.getType(nbtItem);

      for (ConsumableItemInteraction consumableAction : MMOItems.plugin.getStats().getConsumableActions()) {
         if (consumableAction.handleConsumableEffect(event, this.playerData, this, nbtItem, type)) {
            return true;
         }
      }

      return false;
   }

   public Consumable.ConsumableConsumeResult useOnPlayer(EquipmentSlot equipmentSlot, boolean flag) {
      NBTItem nBTItem = this.getNBTItem();
      if (nBTItem.getBoolean(ItemStats.INEDIBLE.getNBTPath())) {
         return Consumable.ConsumableConsumeResult.CANCEL;
      }

      ConsumableConsumedEvent event = new ConsumableConsumedEvent(this.playerData, this.mmoitem, this);
      Bukkit.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         return Consumable.ConsumableConsumeResult.CANCEL;
      }

      for (PlayerConsumable playerConsumable : MMOItems.plugin.getStats().getPlayerConsumables()) {
         playerConsumable.onConsume(this.mmoitem, this.player, flag);
      }

      int count = nBTItem.getInteger(ItemStats.MAX_CONSUME.getNBTPath());
      if (count > 1) {
         nBTItem.addTag(new ItemTag[]{new ItemTag(ItemStats.MAX_CONSUME.getNBTPath(), --count)});
         String colors = MythicLib.inst().parseColors(ItemStats.MAX_CONSUME.getGeneralStatFormat());
         String text = colors.replace("{value}", String.valueOf(count + 1));
         String text2 = colors.replace("{value}", String.valueOf(count));
         ItemStack item = new LoreUpdate(nBTItem.toItem(), text, text2).updateLore();
         ItemStack item2 = nBTItem.getItem();
         if (item2.getAmount() > 1) {
            item.setAmount(1);
            this.player.getInventory().setItem(equipmentSlot, item);
            item2.setAmount(item2.getAmount() - 1);
            new SmartGive(this.player).give(new ItemStack[]{item2});
         } else {
            this.player.getInventory().setItem(equipmentSlot, item);
         }

         return Consumable.ConsumableConsumeResult.NOT_CONSUME;
      } else {
         return event.isConsumed() && !nBTItem.getBoolean(ItemStats.DISABLE_RIGHT_CLICK_CONSUME.getNBTPath())
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

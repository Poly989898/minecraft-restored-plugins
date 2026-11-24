package net.Indyuce.mmoitems.api.interaction.util;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import io.lumine.mythic.lib.version.VEnchantment;
import java.util.Objects;
import java.util.Random;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.item.CustomDurabilityDamage;
import net.Indyuce.mmoitems.api.event.item.ItemCustomRepairEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.util.LoreUpdate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.UpgradeData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DurabilityItem {
   private final NBTItem nbtItem;
   private final int maxDurability;
   private final int unbreakingLevel;
   private final int initialDurability;
   private final boolean barHidden;
   private final Player player;
   private int durability;
   private static final Random RANDOM = new Random();

   public DurabilityItem(@NotNull Player player, @NotNull ItemStack item) {
      this(player, NBTItem.get(item));
   }

   public DurabilityItem(@NotNull Player player, @NotNull NBTItem nbtItem) {
      this.player = Objects.requireNonNull(player, "Player cannot be null");
      this.nbtItem = Objects.requireNonNull(nbtItem, "Item cannot be null");
      this.maxDurability = nbtItem.getInteger("MMOITEMS_MAX_DURABILITY");
      this.initialDurability = this.durability = nbtItem.hasTag("MMOITEMS_DURABILITY") ? nbtItem.getInteger("MMOITEMS_DURABILITY") : this.maxDurability;
      this.barHidden = nbtItem.getBoolean("MMOITEMS_DURABILITY_BAR");
      this.unbreakingLevel = MMOUtils.getLevel(nbtItem.getItem(), VEnchantment.UNBREAKING.get());
   }

   public Player getPlayer() {
      return this.player;
   }

   public int getMaxDurability() {
      return this.maxDurability;
   }

   public int getDurability() {
      return this.durability;
   }

   public boolean isBarHidden() {
      return this.barHidden;
   }

   public int getUnbreakingLevel() {
      return this.unbreakingLevel;
   }

   public NBTItem getNBTItem() {
      return this.nbtItem;
   }

   public boolean isBroken() {
      return this.maxDurability > 0 && this.durability <= 0;
   }

   public boolean isLostWhenBroken() {
      return this.nbtItem.getBoolean("MMOITEMS_WILL_BREAK");
   }

   public boolean isDowngradedWhenBroken() {
      return this.nbtItem.getBoolean("MMOITEMS_BREAK_DOWNGRADE");
   }

   public boolean isValid() {
      return this.maxDurability > 0 && this.player.getGameMode() != GameMode.CREATIVE;
   }

   @NotNull
   public DurabilityItem addDurability(int count) {
      Validate.isTrue(count > 0, "Durability gain must be greater than 0");
      ItemCustomRepairEvent event = new ItemCustomRepairEvent(this, count);
      Bukkit.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         return this;
      }

      this.durability = Math.min(this.durability + count, this.maxDurability);
      return this;
   }

   public DurabilityItem decreaseDurability(int count) {
      if (count == 0) {
         return this;
      }

      if (this.nbtItem.getItem().getType().getMaxDurability() == 0) {
         int unbreakingLevel = this.getUnbreakingLevel();
         if (unbreakingLevel > 0 && RANDOM.nextInt(unbreakingLevel + 1) != 0) {
            return this;
         }
      }

      CustomDurabilityDamage customDurabilityDamage = new CustomDurabilityDamage(this, count);
      Bukkit.getPluginManager().callEvent(customDurabilityDamage);
      if (customDurabilityDamage.isCancelled()) {
         return this;
      }

      this.durability = Math.max(0, Math.min(this.durability - count, this.maxDurability));
      if (this.isBroken()) {
         this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
         PlayerData.get(this.player).getInventory().scheduleUpdate();
      }

      return this;
   }

   @Nullable
   public ItemStack toItem() {
      if (this.isBroken()) {
         if (this.isLostWhenBroken()) {
            return null;
         }

         if (this.isDowngradedWhenBroken()) {
            ItemTag tagAtPath = ItemTag.getTagAtPath(ItemStats.UPGRADE.getNBTPath(), this.getNBTItem(), SupportedNBTTagValues.STRING);
            if (tagAtPath != null) {
               try {
                  UpgradeData upgradeData = new UpgradeData(new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonObject());
                  if (upgradeData.getLevel() <= upgradeData.getMin()) {
                     return null;
                  }

                  LiveMMOItem liveMMOItem = new LiveMMOItem(this.getNBTItem());
                  liveMMOItem.setData(ItemStats.CUSTOM_DURABILITY, new DoubleData(this.maxDurability));
                  liveMMOItem.getUpgradeTemplate().upgradeTo(liveMMOItem, upgradeData.getLevel() - 1);
                  return liveMMOItem.newBuilder().buildNBT().toItem();
               } catch (JsonSyntaxException | IllegalStateException exception) {
               }
            }
         }
      }

      if (this.durability == this.initialDurability) {
         return this.nbtItem.getItem();
      }

      this.nbtItem.addTag(new ItemTag[]{new ItemTag("MMOITEMS_DURABILITY", this.durability)});
      ItemStack item = this.nbtItem.toItem();
      if (!this.barHidden && item.getType().getMaxDurability() > 0) {
         int count = this.durability == this.maxDurability
            ? 0
            : Math.max(1, (int)((1.0 - (double)this.durability / this.maxDurability) * this.nbtItem.getItem().getType().getMaxDurability()));
         ItemMeta itemMeta = item.getItemMeta();
         ((Damageable)itemMeta).setDamage(count);
         item.setItemMeta(itemMeta);
      }

      String colors = MythicLib.inst().parseColors(ItemStats.ITEM_DAMAGE.getGeneralStatFormat().replace("{max}", String.valueOf(this.maxDurability)));
      String text = colors.replace("{current}", String.valueOf(this.initialDurability));
      String coloredBar = colors.replace("{current}", String.valueOf(this.durability));
      return new LoreUpdate(item, text, coloredBar).updateLore();
   }
}

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

   public DurabilityItem(@NotNull Player var1, @NotNull ItemStack var2) {
      this(var1, NBTItem.get(var2));
   }

   public DurabilityItem(@NotNull Player var1, @NotNull NBTItem var2) {
      this.player = Objects.requireNonNull(var1, "Player cannot be null");
      this.nbtItem = Objects.requireNonNull(var2, "Item cannot be null");
      this.maxDurability = var2.getInteger("MMOITEMS_MAX_DURABILITY");
      this.initialDurability = this.durability = var2.hasTag("MMOITEMS_DURABILITY") ? var2.getInteger("MMOITEMS_DURABILITY") : this.maxDurability;
      this.barHidden = var2.getBoolean("MMOITEMS_DURABILITY_BAR");
      this.unbreakingLevel = MMOUtils.getLevel(var2.getItem(), VEnchantment.UNBREAKING.get());
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
   public DurabilityItem addDurability(int var1) {
      Validate.isTrue(var1 > 0, "Durability gain must be greater than 0");
      ItemCustomRepairEvent var2 = new ItemCustomRepairEvent(this, var1);
      Bukkit.getPluginManager().callEvent(var2);
      if (var2.isCancelled()) {
         return this;
      }

      this.durability = Math.min(this.durability + var1, this.maxDurability);
      return this;
   }

   public DurabilityItem decreaseDurability(int var1) {
      if (var1 == 0) {
         return this;
      }

      if (this.nbtItem.getItem().getType().getMaxDurability() == 0) {
         int var2 = this.getUnbreakingLevel();
         if (var2 > 0 && RANDOM.nextInt(var2 + 1) != 0) {
            return this;
         }
      }

      CustomDurabilityDamage var3 = new CustomDurabilityDamage(this, var1);
      Bukkit.getPluginManager().callEvent(var3);
      if (var3.isCancelled()) {
         return this;
      }

      this.durability = Math.max(0, Math.min(this.durability - var1, this.maxDurability));
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
            ItemTag var1 = ItemTag.getTagAtPath(ItemStats.UPGRADE.getNBTPath(), this.getNBTItem(), SupportedNBTTagValues.STRING);
            if (var1 != null) {
               try {
                  UpgradeData var8 = new UpgradeData(new JsonParser().parse((String)var1.getValue()).getAsJsonObject());
                  if (var8.getLevel() <= var8.getMin()) {
                     return null;
                  }

                  LiveMMOItem var10 = new LiveMMOItem(this.getNBTItem());
                  var10.setData(ItemStats.CUSTOM_DURABILITY, new DoubleData(this.maxDurability));
                  var10.getUpgradeTemplate().upgradeTo(var10, var8.getLevel() - 1);
                  return var10.newBuilder().buildNBT().toItem();
               } catch (JsonSyntaxException | IllegalStateException var5) {
               }
            }
         }
      }

      if (this.durability == this.initialDurability) {
         return this.nbtItem.getItem();
      }

      this.nbtItem.addTag(new ItemTag[]{new ItemTag("MMOITEMS_DURABILITY", this.durability)});
      ItemStack var6 = this.nbtItem.toItem();
      if (!this.barHidden && var6.getType().getMaxDurability() > 0) {
         int var2 = this.durability == this.maxDurability
            ? 0
            : Math.max(1, (int)((1.0 - (double)this.durability / this.maxDurability) * this.nbtItem.getItem().getType().getMaxDurability()));
         ItemMeta var3 = var6.getItemMeta();
         ((Damageable)var3).setDamage(var2);
         var6.setItemMeta(var3);
      }

      String var7 = MythicLib.inst().parseColors(ItemStats.ITEM_DAMAGE.getGeneralStatFormat().replace("{max}", String.valueOf(this.maxDurability)));
      String var9 = var7.replace("{current}", String.valueOf(this.initialDurability));
      String var4 = var7.replace("{current}", String.valueOf(this.durability));
      return new LoreUpdate(var6, var9, var4).updateLore();
   }
}

package net.Indyuce.mmoitems.api.util;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.Objects;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.ReforgeOptions;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeFinishEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemReforger {
   @NotNull
   private final NBTItem nbtItem;
   @Nullable
   private ItemStack result;
   @Nullable
   private RPGPlayer player;
   @Nullable
   LiveMMOItem oldMMOItem;
   @Nullable
   private final MMOItemTemplate template;
   @Nullable
   private MMOItem freshMMOItem;
   @NotNull
   final ArrayList<ItemStack> reforgingOutput = new ArrayList<>();
   int generationItemLevel;
   public static boolean keepTiersWhenReroll = true;
   public static boolean gemstonesRevIDWhenUnsocket = false;

   public MMOItemReforger(@NotNull ItemStack var1) {
      this(NBTItem.get(var1));
   }

   public MMOItemReforger(@NotNull NBTItem var1) {
      this.nbtItem = var1;
      Validate.isTrue(var1.getItem().getItemMeta() != null, "ItemStack has no ItemMeta, cannot be reforged.");
      this.template = var1.hasType() ? MMOItems.plugin.getTemplates().getTemplate(var1) : null;
   }

   @NotNull
   public NBTItem getNBTItem() {
      return this.nbtItem;
   }

   @NotNull
   public ItemStack getStack() {
      return this.nbtItem.getItem();
   }

   @Nullable
   public ItemStack getResult() {
      return this.result;
   }

   public void setResult(@Nullable ItemStack var1) {
      this.result = var1;
   }

   @Deprecated
   @NotNull
   public ItemMeta getMeta() {
      return this.getStack().getItemMeta();
   }

   @Nullable
   public RPGPlayer getPlayer() {
      return this.player;
   }

   @Deprecated
   public void setPlayer(@Nullable Player var1) {
      this.setPlayer(var1 == null ? null : PlayerData.get(var1).getRPG());
   }

   @Deprecated
   public void setPlayer(@Nullable RPGPlayer var1) {
      this.player = var1;
   }

   @NotNull
   public LiveMMOItem getOldMMOItem() {
      return this.oldMMOItem;
   }

   @NotNull
   public MMOItemTemplate getTemplate() {
      return Objects.requireNonNull(this.template, "No item template was found");
   }

   @NotNull
   public MMOItem getFreshMMOItem() {
      return this.freshMMOItem;
   }

   public void setFreshMMOItem(@NotNull MMOItem var1) {
      this.freshMMOItem = var1;
   }

   @Deprecated
   public boolean canReforge() {
      return this.hasTemplate();
   }

   public boolean hasTemplate() {
      return this.template != null;
   }

   public void addReforgingOutput(@Nullable ItemStack var1) {
      if (!SilentNumbers.isAir(var1) && var1.getType().isItem()) {
         this.reforgingOutput.add(var1);
      }
   }

   public void clearReforgingOutput() {
      this.reforgingOutput.clear();
   }

   @NotNull
   public ArrayList<ItemStack> getReforgingOutput() {
      return this.reforgingOutput;
   }

   public int getGenerationItemLevel() {
      return this.generationItemLevel;
   }

   public boolean reforge(@NotNull ReforgeOptions var1) {
      return this.reforge(var1, (RPGPlayer)null);
   }

   public boolean reforge(@NotNull ReforgeOptions var1, @Nullable Player var2) {
      return this.reforge(var1, var2 == null ? null : PlayerData.get(var2).getRPG());
   }

   public boolean reforge(@NotNull ReforgeOptions var1, @Nullable RPGPlayer var2) {
      if (!this.hasTemplate()) {
         return false;
      }

      this.oldMMOItem = new LiveMMOItem(this.getNBTItem());
      if (var1.isBlacklisted(this.getOldMMOItem().getId())) {
         return false;
      }

      this.player = var2;
      this.generationItemLevel = this.getOldMMOItem().hasData(ItemStats.ITEM_LEVEL)
         ? (int)((DoubleData)this.getOldMMOItem().getData(ItemStats.ITEM_LEVEL)).getValue()
         : 0;
      ItemTier var3 = var1.shouldKeepTier() && this.getOldMMOItem().hasData(ItemStats.TIER)
         ? MMOItems.plugin.getTiers().get(this.getOldMMOItem().getData(ItemStats.TIER).toString())
         : null;
      this.setFreshMMOItem(this.getTemplate().newBuilder(this.generationItemLevel, var3).build());
      MMOItemReforgeEvent var4 = new MMOItemReforgeEvent(this, var1);
      Bukkit.getPluginManager().callEvent(var4);
      if (var4.isCancelled()) {
         return false;
      }

      for (StatHistory var6 : this.getFreshMMOItem().getStatHistories()) {
         this.getFreshMMOItem().setData(var6.getItemStat(), var6.recalculate(this.getFreshMMOItem().getUpgradeLevel()));
      }

      if (this.getFreshMMOItem().hasUpgradeTemplate()) {
         for (ItemStat var10 : this.getFreshMMOItem().getUpgradeTemplate().getKeys()) {
            StatHistory var7 = this.getFreshMMOItem().computeStatHistory(var10);
            this.getFreshMMOItem().setData(var7.getItemStat(), var7.recalculate(this.getFreshMMOItem().getUpgradeLevel()));
         }
      }

      this.result = this.getFreshMMOItem().newBuilder().build();
      MMOItemReforgeFinishEvent var9 = new MMOItemReforgeFinishEvent(this.result, this, var1);
      Bukkit.getPluginManager().callEvent(var9);
      this.setResult(var9.getFinishedItem());
      return !var9.isCancelled();
   }

   public static void reload() {
      keepTiersWhenReroll = MMOItems.plugin.getConfig().getBoolean("item-revision.keep-tiers");
      gemstonesRevIDWhenUnsocket = MMOItems.plugin.getConfig().getBoolean("item-revision.regenerate-gems-when-unsocketed", false);
   }

   @Deprecated
   public MMOItemReforger(@Nullable ItemStack var1, @NotNull NBTItem var2) {
      this(var2);
   }

   @Deprecated
   public void update(@Nullable Player var1, @NotNull ReforgeOptions var2) {
      this.reforge(var2, var1);
   }

   @Deprecated
   public void update(@Nullable RPGPlayer var1, @NotNull ReforgeOptions var2) {
      this.reforge(var2, var1);
   }

   @Deprecated
   void regenerate(@Nullable RPGPlayer var1) {
      this.reforge(new ReforgeOptions(), var1);
   }

   @Deprecated
   int regenerate(@Nullable RPGPlayer var1, @NotNull MMOItemTemplate var2) {
      this.reforge(new ReforgeOptions(), var1);
      return 0;
   }

   @Deprecated
   public void reforge(@Nullable Player var1, @NotNull ReforgeOptions var2) {
      this.reforge(var2, var1);
   }

   @Deprecated
   public void reforge(@Nullable RPGPlayer var1, @NotNull ReforgeOptions var2) {
      this.reforge(var2, var1);
   }

   @Deprecated
   public ItemStack toStack() {
      return this.getResult();
   }

   @Deprecated
   public boolean hasChanges() {
      return this.getResult() != null;
   }

   @Deprecated
   @NotNull
   public ArrayList<MMOItem> getDestroyedGems() {
      return new ArrayList<>();
   }
}

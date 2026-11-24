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

   public MMOItemReforger(@NotNull ItemStack item) {
      this(NBTItem.get(item));
   }

   public MMOItemReforger(@NotNull NBTItem nbtItem) {
      this.nbtItem = nbtItem;
      Validate.isTrue(nbtItem.getItem().getItemMeta() != null, "ItemStack has no ItemMeta, cannot be reforged.");
      this.template = nbtItem.hasType() ? MMOItems.plugin.getTemplates().getTemplate(nbtItem) : null;
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

   public void setResult(@Nullable ItemStack item) {
      this.result = item;
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
   public void setPlayer(@Nullable Player player) {
      this.setPlayer(player == null ? null : PlayerData.get(player).getRPG());
   }

   @Deprecated
   public void setPlayer(@Nullable RPGPlayer rpgPlayer) {
      this.player = rpgPlayer;
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

   public void setFreshMMOItem(@NotNull MMOItem mmoItem) {
      this.freshMMOItem = mmoItem;
   }

   @Deprecated
   public boolean canReforge() {
      return this.hasTemplate();
   }

   public boolean hasTemplate() {
      return this.template != null;
   }

   public void addReforgingOutput(@Nullable ItemStack item) {
      if (!SilentNumbers.isAir(item) && item.getType().isItem()) {
         this.reforgingOutput.add(item);
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

   public boolean reforge(@NotNull ReforgeOptions reforgeOptions) {
      return this.reforge(reforgeOptions, (RPGPlayer)null);
   }

   public boolean reforge(@NotNull ReforgeOptions reforgeOptions, @Nullable Player player) {
      return this.reforge(reforgeOptions, player == null ? null : PlayerData.get(player).getRPG());
   }

   public boolean reforge(@NotNull ReforgeOptions reforgeOptions, @Nullable RPGPlayer rpgPlayer) {
      if (!this.hasTemplate()) {
         return false;
      }

      this.oldMMOItem = new LiveMMOItem(this.getNBTItem());
      if (reforgeOptions.isBlacklisted(this.getOldMMOItem().getId())) {
         return false;
      }

      this.player = rpgPlayer;
      this.generationItemLevel = this.getOldMMOItem().hasData(ItemStats.ITEM_LEVEL)
         ? (int)((DoubleData)this.getOldMMOItem().getData(ItemStats.ITEM_LEVEL)).getValue()
         : 0;
      ItemTier itemTier = reforgeOptions.shouldKeepTier() && this.getOldMMOItem().hasData(ItemStats.TIER)
         ? MMOItems.plugin.getTiers().get(this.getOldMMOItem().getData(ItemStats.TIER).toString())
         : null;
      this.setFreshMMOItem(this.getTemplate().newBuilder(this.generationItemLevel, itemTier).build());
      MMOItemReforgeEvent event = new MMOItemReforgeEvent(this, reforgeOptions);
      Bukkit.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         return false;
      }

      for (StatHistory statHistory : this.getFreshMMOItem().getStatHistories()) {
         this.getFreshMMOItem().setData(statHistory.getItemStat(), statHistory.recalculate(this.getFreshMMOItem().getUpgradeLevel()));
      }

      if (this.getFreshMMOItem().hasUpgradeTemplate()) {
         for (ItemStat key : this.getFreshMMOItem().getUpgradeTemplate().getKeys()) {
            StatHistory statHistory = this.getFreshMMOItem().computeStatHistory(key);
            this.getFreshMMOItem().setData(statHistory.getItemStat(), statHistory.recalculate(this.getFreshMMOItem().getUpgradeLevel()));
         }
      }

      this.result = this.getFreshMMOItem().newBuilder().build();
      MMOItemReforgeFinishEvent finishEvent = new MMOItemReforgeFinishEvent(this.result, this, reforgeOptions);
      Bukkit.getPluginManager().callEvent(finishEvent);
      this.setResult(finishEvent.getFinishedItem());
      return !finishEvent.isCancelled();
   }

   public static void reload() {
      keepTiersWhenReroll = MMOItems.plugin.getConfig().getBoolean("item-revision.keep-tiers");
      gemstonesRevIDWhenUnsocket = MMOItems.plugin.getConfig().getBoolean("item-revision.regenerate-gems-when-unsocketed", false);
   }

   @Deprecated
   public MMOItemReforger(@Nullable ItemStack item, @NotNull NBTItem nbtItem) {
      this(nbtItem);
   }

   @Deprecated
   public void update(@Nullable Player player, @NotNull ReforgeOptions reforgeOptions) {
      this.reforge(reforgeOptions, player);
   }

   @Deprecated
   public void update(@Nullable RPGPlayer rpgPlayer, @NotNull ReforgeOptions reforgeOptions) {
      this.reforge(reforgeOptions, rpgPlayer);
   }

   @Deprecated
   void regenerate(@Nullable RPGPlayer rpgPlayer) {
      this.reforge(new ReforgeOptions(), rpgPlayer);
   }

   @Deprecated
   int regenerate(@Nullable RPGPlayer rpgPlayer, @NotNull MMOItemTemplate mmoItemTemplate) {
      this.reforge(new ReforgeOptions(), rpgPlayer);
      return 0;
   }

   @Deprecated
   public void reforge(@Nullable Player player, @NotNull ReforgeOptions reforgeOptions) {
      this.reforge(reforgeOptions, player);
   }

   @Deprecated
   public void reforge(@Nullable RPGPlayer rpgPlayer, @NotNull ReforgeOptions reforgeOptions) {
      this.reforge(reforgeOptions, rpgPlayer);
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

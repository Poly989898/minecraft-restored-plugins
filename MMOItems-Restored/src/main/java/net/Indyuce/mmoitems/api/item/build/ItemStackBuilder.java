package net.Indyuce.mmoitems.api.item.build;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.util.AdventureUtils;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.GenerateLoreEvent;
import net.Indyuce.mmoitems.api.event.ItemBuildEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.stat.data.MaterialData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.Previewable;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStackBuilder {
   @NotNull
   private final MMOItem mmoitem;
   private final ItemStack item;
   private final ItemMeta meta;
   private final LoreBuilder lore;
   private final List<ItemTag> tags = new ArrayList<>();
   @Deprecated
   private List<Consumer<NBTItem>> futureActions;
   private static final AttributeModifier FAKE_MODIFIER = VersionUtils.attrMod(new NamespacedKey(MMOItems.plugin, "decoy"), 0.0, Operation.ADD_NUMBER);
   public static final String history_keyword = "HSTRY_";

   public ItemStackBuilder(@NotNull MMOItem mmoItem) {
      this.mmoitem = mmoItem;
      this.item = new ItemStack(mmoItem.hasData(ItemStats.MATERIAL) ? ((MaterialData)mmoItem.getData(ItemStats.MATERIAL)).getMaterial() : Material.DIAMOND_SWORD);
      this.lore = new LoreBuilder(mmoItem);
      this.meta = this.item.getItemMeta();
      this.meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
      this.tags.add(new ItemTag("MMOITEMS_ITEM_TYPE", mmoItem.getType().getId()));
      this.tags.add(new ItemTag("MMOITEMS_ITEM_ID", mmoItem.getId()));
   }

   public LoreBuilder getLore() {
      return this.lore;
   }

   @NotNull
   public MMOItem getMMOItem() {
      return this.mmoitem;
   }

   @NotNull
   public ItemStack getItemStack() {
      return this.item;
   }

   @NotNull
   public ItemMeta getMeta() {
      return this.meta;
   }

   @Deprecated
   public void addFutureAction(Consumer<NBTItem> consumer) {
      if (this.futureActions == null) {
         this.futureActions = new ArrayList<>();
      }

      this.futureActions.add(consumer);
   }

   public void addItemTag(List<ItemTag> itemTags) {
      this.tags.addAll(itemTags);
   }

   public void addItemTag(ItemTag... itemTags) {
      this.tags.addAll(Arrays.asList(itemTags));
   }

   public NBTItem buildNBT() {
      return this.buildNBT(false);
   }

   public NBTItem buildNBT(boolean flag) {
      MMOItem mmoItem = this.mmoitem.clone();
      mmoItem.computeData(ItemStats.ENCHANTS);
      mmoItem.computeStatHistory(ItemStats.ENCHANTS);
      mmoItem.computeData(ItemStats.NAME);
      mmoItem.computeStatHistory(ItemStats.NAME);

      for (ItemStat stat : mmoItem.getStats()) {
         try {
            StatHistory statHistory = mmoItem.getStatHistory(stat);
            if (statHistory != null) {
               int upgradeLevel = this.mmoitem.getUpgradeLevel();
               mmoItem.setData(stat, statHistory.recalculate(upgradeLevel));
               if (!statHistory.isEmpty()) {
                  this.addItemTag(new ItemTag("HSTRY_" + stat.getId(), statHistory.toNBTString()));
               }
            }

            if (flag && stat instanceof Previewable) {
               MMOItemTemplate template = MMOItems.plugin.getTemplates().getTemplate(mmoItem.getType(), mmoItem.getId());
               if (template == null) {
                  throw new IllegalArgumentException("MMOItem $r" + mmoItem.getType().getId() + " " + mmoItem.getId() + "$b doesn't exist.");
               }

               ((Previewable)stat).whenPreviewed(this, mmoItem.getData(stat), template.getBaseItemData().get(stat));
            } else {
               stat.whenApplied(this, mmoItem.getData(stat));
            }
         } catch (IllegalArgumentException | NullPointerException exception) {
            MMOItems.print(
               Level.SEVERE,
               "An error occurred while trying to generate item '$f{0}$b' with stat '$f{1}$b': {2}",
               "ItemStackBuilder",
               mmoItem.getId(),
               stat.getId(),
               exception.getMessage()
            );
         }
      }

      if (mmoItem.getType() == Type.GEM_STONE) {
         this.lore.insert("gem-stone-lore", MMOItems.plugin.getLanguage().gemStoneLoreTag);
      }

      this.lore
         .insert(
            "item-type",
            MMOItems.plugin
               .getLanguage()
               .itemTypeLoreTag
               .replace(
                  "{type}", mmoItem.getStats().contains(ItemStats.DISPLAYED_TYPE) ? mmoItem.getData(ItemStats.DISPLAYED_TYPE).toString() : mmoItem.getType().getName()
               )
         );
      List lore = this.lore.getLore();
      List list = this.lore.build();
      GenerateLoreEvent event = new GenerateLoreEvent(mmoItem, this.lore, list, lore);
      Bukkit.getPluginManager().callEvent(event);
      AdventureUtils.setLore(this.meta, event.getParsedLore());
      if (this.meta.hasDisplayName()) {
         String displayName = this.meta.getDisplayName();
         if (this.lore.hasTooltip()) {
            displayName = this.lore.getTooltip().getTop() + displayName;
         }

         displayName = MythicLib.plugin.getPlaceholderParser().parse(null, displayName);
         displayName = this.lore.applySpecialPlaceholders(displayName);
         if (this.lore.hasTooltip() && this.lore.getTooltip().getCenteringOptions() != null && this.lore.getTooltip().getCenteringOptions().displayName()) {
            displayName = this.lore.getTooltip().getCenteringOptions().centerName(displayName);
         }

         AdventureUtils.setDisplayName(this.meta, ChatColor.WHITE + displayName);
      }

      JsonArray jsonArray = new JsonArray();
      event.getParsedLore().forEach(jsonArray::add);
      if (!jsonArray.isEmpty()) {
         this.tags.add(new ItemTag("MMOITEMS_DYNAMIC_LORE", jsonArray.toString()));
      }

      this.meta.addAttributeModifier(Attribute.ATTACK_SPEED, FAKE_MODIFIER);
      this.item.setItemMeta(this.meta);
      NBTItem nbtItem = NBTItem.get(this.item).addTag(this.tags);
      if (this.futureActions != null) {
         this.futureActions.forEach(action -> action.accept(nbtItem));
      }

      return nbtItem;
   }

   @Nullable
   public ItemStack build() {
      ItemBuildEvent event = new ItemBuildEvent(this.buildNBT().toItem());
      Bukkit.getServer().getPluginManager().callEvent(event);
      return event.getItemStack();
   }

   public ItemStack buildSilently() {
      return this.buildNBT().toItem();
   }

   public ItemStack build(boolean flag) {
      return this.buildNBT(flag).toItem();
   }
}

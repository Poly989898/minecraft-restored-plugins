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

   public ItemStackBuilder(@NotNull MMOItem var1) {
      this.mmoitem = var1;
      this.item = new ItemStack(var1.hasData(ItemStats.MATERIAL) ? ((MaterialData)var1.getData(ItemStats.MATERIAL)).getMaterial() : Material.DIAMOND_SWORD);
      this.lore = new LoreBuilder(var1);
      this.meta = this.item.getItemMeta();
      this.meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
      this.tags.add(new ItemTag("MMOITEMS_ITEM_TYPE", var1.getType().getId()));
      this.tags.add(new ItemTag("MMOITEMS_ITEM_ID", var1.getId()));
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
   public void addFutureAction(Consumer<NBTItem> var1) {
      if (this.futureActions == null) {
         this.futureActions = new ArrayList<>();
      }

      this.futureActions.add(var1);
   }

   public void addItemTag(List<ItemTag> var1) {
      this.tags.addAll(var1);
   }

   public void addItemTag(ItemTag... var1) {
      this.tags.addAll(Arrays.asList(var1));
   }

   public NBTItem buildNBT() {
      return this.buildNBT(false);
   }

   public NBTItem buildNBT(boolean var1) {
      MMOItem var2 = this.mmoitem.clone();
      var2.computeData(ItemStats.ENCHANTS);
      var2.computeStatHistory(ItemStats.ENCHANTS);
      var2.computeData(ItemStats.NAME);
      var2.computeStatHistory(ItemStats.NAME);

      for (ItemStat var4 : var2.getStats()) {
         try {
            StatHistory var5 = var2.getStatHistory(var4);
            if (var5 != null) {
               int var6 = this.mmoitem.getUpgradeLevel();
               var2.setData(var4, var5.recalculate(var6));
               if (!var5.isEmpty()) {
                  this.addItemTag(new ItemTag("HSTRY_" + var4.getId(), var5.toNBTString()));
               }
            }

            if (var1 && var4 instanceof Previewable) {
               MMOItemTemplate var12 = MMOItems.plugin.getTemplates().getTemplate(var2.getType(), var2.getId());
               if (var12 == null) {
                  throw new IllegalArgumentException("MMOItem $r" + var2.getType().getId() + " " + var2.getId() + "$b doesn't exist.");
               }

               ((Previewable)var4).whenPreviewed(this, var2.getData(var4), var12.getBaseItemData().get(var4));
            } else {
               var4.whenApplied(this, var2.getData(var4));
            }
         } catch (IllegalArgumentException | NullPointerException var8) {
            MMOItems.print(
               Level.SEVERE,
               "An error occurred while trying to generate item '$f{0}$b' with stat '$f{1}$b': {2}",
               "ItemStackBuilder",
               var2.getId(),
               var4.getId(),
               var8.getMessage()
            );
         }
      }

      if (var2.getType() == Type.GEM_STONE) {
         this.lore.insert("gem-stone-lore", MMOItems.plugin.getLanguage().gemStoneLoreTag);
      }

      this.lore
         .insert(
            "item-type",
            MMOItems.plugin
               .getLanguage()
               .itemTypeLoreTag
               .replace(
                  "{type}", var2.getStats().contains(ItemStats.DISPLAYED_TYPE) ? var2.getData(ItemStats.DISPLAYED_TYPE).toString() : var2.getType().getName()
               )
         );
      List var9 = this.lore.getLore();
      List var10 = this.lore.build();
      GenerateLoreEvent var11 = new GenerateLoreEvent(var2, this.lore, var10, var9);
      Bukkit.getPluginManager().callEvent(var11);
      AdventureUtils.setLore(this.meta, var11.getParsedLore());
      if (this.meta.hasDisplayName()) {
         String var13 = this.meta.getDisplayName();
         if (this.lore.hasTooltip()) {
            var13 = this.lore.getTooltip().getTop() + var13;
         }

         var13 = MythicLib.plugin.getPlaceholderParser().parse(null, var13);
         var13 = this.lore.applySpecialPlaceholders(var13);
         if (this.lore.hasTooltip() && this.lore.getTooltip().getCenteringOptions() != null && this.lore.getTooltip().getCenteringOptions().displayName()) {
            var13 = this.lore.getTooltip().getCenteringOptions().centerName(var13);
         }

         AdventureUtils.setDisplayName(this.meta, ChatColor.WHITE + var13);
      }

      JsonArray var16 = new JsonArray();
      var11.getParsedLore().forEach(var16::add);
      if (!var16.isEmpty()) {
         this.tags.add(new ItemTag("MMOITEMS_DYNAMIC_LORE", var16.toString()));
      }

      this.meta.addAttributeModifier(Attribute.ATTACK_SPEED, FAKE_MODIFIER);
      this.item.setItemMeta(this.meta);
      NBTItem var7 = NBTItem.get(this.item).addTag(this.tags);
      if (this.futureActions != null) {
         this.futureActions.forEach(var1x -> var1x.accept(var7));
      }

      return var7;
   }

   @Nullable
   public ItemStack build() {
      ItemBuildEvent var1 = new ItemBuildEvent(this.buildNBT().toItem());
      Bukkit.getServer().getPluginManager().callEvent(var1);
      return var1.getItemStack();
   }

   public ItemStack buildSilently() {
      return this.buildNBT().toItem();
   }

   public ItemStack build(boolean var1) {
      return this.buildNBT(var1).toItem();
   }
}

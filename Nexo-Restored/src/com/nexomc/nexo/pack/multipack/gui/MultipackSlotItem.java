package com.nexomc.nexo.pack.multipack.gui;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemModelBuilder;
import com.nexomc.nexo.items.ItemModelParser;
import com.nexomc.nexo.items.ItemParser;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.metadata.pack.PackFormat;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u0000 \t2\u00020\u0001:\u0004\u0006\u0007\b\tB\t\b\u0004¢\u0006\u0004\b\u0002\u0010\u0003J\n\u0010\u0004\u001a\u0004\u0018\u00010\u0005H&\u0082\u0001\u0003\n\u000b\f¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "", "<init>", "()V", "buildItemStack", "Lorg/bukkit/inventory/ItemStack;", "NexoItem", "ItemBuilder", "ItemModelRef", "Companion", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem$ItemBuilder;", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem$ItemModelRef;", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem$NexoItem;", "com.nexomc:core"}
)
public abstract sealed class MultipackSlotItem {
   @NotNull
   public static final Companion Companion = new Companion();

   private MultipackSlotItem() {
   }

   @Nullable
   public abstract ItemStack buildItemStack();

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\n\u0010\b\u001a\u0004\u0018\u00010\tH\u0016J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fHÖ\u0083\u0004J\n\u0010\u0010\u001a\u00020\u0011HÖ\u0081\u0004J\n\u0010\u0012\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0013"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem$NexoItem;", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "id", "", "<init>", "(Ljava/lang/String;)V", "getId", "()Ljava/lang/String;", "buildItemStack", "Lorg/bukkit/inventory/ItemStack;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "com.nexomc:core"}
   )
   public static final class NexoItem extends MultipackSlotItem {
      @NotNull
      private final String id;

      public NexoItem(@NotNull String id) {
         super();
         this.id = id;
      }

      @NotNull
      public final String getId() {
         return this.id;
      }

      @Nullable
      public ItemStack buildItemStack() {
         com.nexomc.nexo.items.ItemBuilder itemBuilder = NexoItems.itemFromId(this.id);
         return itemBuilder != null ? itemBuilder.build() : null;
      }

      @NotNull
      public final String component1() {
         return this.id;
      }

      @NotNull
      public final NexoItem copy(@NotNull String id) {
         return new NexoItem(id);
      }

      @NotNull
      public String toString() {
         return "NexoItem(id=" + this.id + ")";
      }

      public int hashCode() {
         return this.id.hashCode();
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof NexoItem)) {
            return false;
         } else {
            NexoItem nexoItem = (NexoItem)other;
            return Intrinsics.areEqual(this.id, nexoItem.id);
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\b\u001a\u00020\tH\u0016J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fHÖ\u0083\u0004J\n\u0010\u0010\u001a\u00020\u0011HÖ\u0081\u0004J\n\u0010\u0012\u001a\u00020\u0013HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0014"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem$ItemBuilder;", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "parser", "Lcom/nexomc/nexo/items/ItemParser;", "<init>", "(Lcom/nexomc/nexo/items/ItemParser;)V", "getParser", "()Lcom/nexomc/nexo/items/ItemParser;", "buildItemStack", "Lorg/bukkit/inventory/ItemStack;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class ItemBuilder extends MultipackSlotItem {
      @NotNull
      private final ItemParser parser;

      public ItemBuilder(@NotNull ItemParser parser) {
         super();
         this.parser = parser;
      }

      @NotNull
      public final ItemParser getParser() {
         return this.parser;
      }

      @NotNull
      public ItemStack buildItemStack() {
         return this.parser.buildItem().removeCustomTag(NexoItems.INSTANCE.getITEM_ID()).build();
      }

      @NotNull
      public final ItemParser component1() {
         return this.parser;
      }

      @NotNull
      public final ItemBuilder copy(@NotNull ItemParser parser) {
         return new ItemBuilder(parser);
      }

      @NotNull
      public String toString() {
         return "ItemBuilder(parser=" + this.parser + ")";
      }

      public int hashCode() {
         return this.parser.hashCode();
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof ItemBuilder)) {
            return false;
         } else {
            ItemBuilder itemBuilder = (ItemBuilder)other;
            return Intrinsics.areEqual(this.parser, itemBuilder.parser);
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\f\u001a\u00020\rH\u0016J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014HÖ\u0083\u0004J\n\u0010\u0015\u001a\u00020\u0016HÖ\u0081\u0004J\n\u0010\u0017\u001a\u00020\u0018HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0019"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem$ItemModelRef;", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "baseMaterial", "Lorg/bukkit/Material;", "model", "Lnet/kyori/adventure/key/Key;", "<init>", "(Lorg/bukkit/Material;Lnet/kyori/adventure/key/Key;)V", "getBaseMaterial", "()Lorg/bukkit/Material;", "getModel", "()Lnet/kyori/adventure/key/Key;", "buildItemStack", "Lorg/bukkit/inventory/ItemStack;", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class ItemModelRef extends MultipackSlotItem {
      @NotNull
      private final Material baseMaterial;
      @NotNull
      private final Key model;

      public ItemModelRef(@NotNull Material baseMaterial, @NotNull Key model) {
         super();
         this.baseMaterial = baseMaterial;
         this.model = model;
      }

      @NotNull
      public final Material getBaseMaterial() {
         return this.baseMaterial;
      }

      @NotNull
      public final Key getModel() {
         return this.model;
      }

      @NotNull
      public ItemStack buildItemStack() {
         ItemStack itemStack = ItemStack.of(this.baseMaterial);
         int value = 0;
         itemStack.setData(DataComponentTypes.ITEM_MODEL, this.model);
         return itemStack;
      }

      @NotNull
      public final Material component1() {
         return this.baseMaterial;
      }

      @NotNull
      public final Key component2() {
         return this.model;
      }

      @NotNull
      public final ItemModelRef copy(@NotNull Material baseMaterial, @NotNull Key model) {
         return new ItemModelRef(baseMaterial, model);
      }

      @NotNull
      public String toString() {
         return "ItemModelRef(baseMaterial=" + this.baseMaterial + ", model=" + this.model + ")";
      }

      public int hashCode() {
         int result = this.baseMaterial.hashCode();
         result = result * 31 + this.model.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof ItemModelRef)) {
            return false;
         } else {
            ItemModelRef itemModelRef = (ItemModelRef)other;
            if (this.baseMaterial != itemModelRef.baseMaterial) {
               return false;
            } else {
               return Intrinsics.areEqual(this.model, itemModelRef.model);
            }
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\tJ\u0010\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u000b¨\u0006\r"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem$Companion;", "", "<init>", "()V", "fromSection", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "keyContext", "", "emptyPlaceholder", "Lorg/bukkit/inventory/ItemStack;", "baseItem", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @Nullable
      public final MultipackSlotItem fromSection(@Nullable ConfigurationSection section, @NotNull String keyContext) {
         if (section == null) {
            return null;
         } else {
            String str = NexoYamlKt.stringOrNull(section, "nexo_item");
            if (str != null) {
               String it = str;
               int value = 0;
               return new NexoItem(it);
            } else {
               ConfigurationSection configurationSection = NexoYamlKt.section(section, "item_model");
               if (configurationSection != null) {
                  ConfigurationSection modelSection = configurationSection;
                  int value2 = 0;
                  Key tempKey = KeyUtils.parseKey("nexo:multipack_menu/" + keyContext, true);
                  PackFormat packFormat = PackFormat.format(NMSHandlers.handler().resourcepackFormat());
                  PackFormat format = packFormat;
                  ItemModelBuilder item = new ItemModelBuilder(tempKey, modelSection, format);
                  ((Collection)ItemModelParser.INSTANCE.getExtraItems$com_nexomc_core()).add(item);
                  return new ItemModelRef(Material.PAPER, item.getKey());
               } else {
                  return new ItemBuilder(new ItemParser(NexoYamlKt.plus(section, "inject_id", false)));
               }
            }
         }
      }

      @NotNull
      public final ItemStack emptyPlaceholder(@Nullable ItemStack baseItem) {
         ItemStack itemStack = ItemStack.of(Material.PAPER);
         int value = 0;
         if (baseItem != null) {
            int value2 = 0;
            itemStack.copyDataFrom(baseItem, (a0_ -> Companion.emptyPlaceholder$lambda$0$0$0(a0_)));
         }

         itemStack.setData(DataComponentTypes.ITEM_MODEL, Key.key("nexo", "empty"));
         return itemStack;
      }

      private static final boolean emptyPlaceholder$lambda$0$0$0(DataComponentType it) {
         return true;
      }
   }
}

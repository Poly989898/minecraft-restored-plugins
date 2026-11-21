package com.nexomc.nexo.nms.v1_21_R5.packets.transformers;

import com.nexomc.nexo.api.ComponentProjections;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.nms.GlyphHandlers;
import com.nexomc.nexo.nms.Transformer;
import com.nexomc.nexo.nms.commons.packets.HashedComponentRewriter;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import io.papermc.paper.adventure.PaperAdventure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.optionals.OptionalsKt;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u00022\u0006\u0010\f\u001a\u00020\u0002H\u0016J\u0018\u0010\u000b\u001a\u00020\u00022\u0006\u0010\f\u001a\u00020\u00022\b\u0010\r\u001a\u0004\u0018\u00010\u000eJ\u000e\u0010\u000b\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fJ\f\u0010\u000b\u001a\u00020\u0011*\u00020\u0011H\u0002J\u000e\u0010\u0012\u001a\u0004\u0018\u00010\u000e*\u00020\u0013H\u0002J\f\u0010\u0014\u001a\u00020\u0015*\u00020\u0002H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R5/packets/transformers/ItemTransformer;", "Lcom/nexomc/nexo/nms/Transformer;", "Lnet/minecraft/world/item/ItemStack;", "player", "Lorg/bukkit/entity/Player;", "componentTransformer", "Lcom/nexomc/nexo/nms/v1_21_R5/packets/transformers/ComponentTransformer;", "hashRewriter", "Lcom/nexomc/nexo/nms/commons/packets/HashedComponentRewriter;", "<init>", "(Lorg/bukkit/entity/Player;Lcom/nexomc/nexo/nms/v1_21_R5/packets/transformers/ComponentTransformer;Lcom/nexomc/nexo/nms/commons/packets/HashedComponentRewriter;)V", "transform", "input", "originalName", "", "Lnet/minecraft/core/component/DataComponentExactPredicate;", "predicate", "Lnet/minecraft/world/item/component/ItemLore;", "fetchOriginalName", "Lnet/minecraft/world/item/component/CustomData;", "shouldTransform", "", "com.nexomc:v1_21_R5"}
)
public final class ItemTransformer implements Transformer<ItemStack> {
   @NotNull
   private final Player player;
   @NotNull
   private final ComponentTransformer componentTransformer;
   @NotNull
   private final HashedComponentRewriter hashRewriter;

   public ItemTransformer(@NotNull Player player, @NotNull ComponentTransformer componentTransformer, @NotNull HashedComponentRewriter hashRewriter) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(componentTransformer, "componentTransformer");
      Intrinsics.checkNotNullParameter(hashRewriter, "hashRewriter");
      super();
      this.player = player;
      this.componentTransformer = componentTransformer;
      this.hashRewriter = hashRewriter;
   }

   @NotNull
   public ItemStack transform(@NotNull ItemStack input) {
      Intrinsics.checkNotNullParameter(input, "input");
      boolean condition$iv = this.shouldTransform(input);
      if (condition$iv) {
         int var6 = 0;
         Component itemName = (Component)input.get(DataComponents.ITEM_NAME);
         boolean itemNamePatched = input.hasNonDefault(DataComponents.ITEM_NAME);
         ItemLore lore = (ItemLore)input.get(DataComponents.LORE);
         boolean lorePatched = input.hasNonDefault(DataComponents.LORE);
         Component rawCustomName = (Component)input.get(DataComponents.CUSTOM_NAME);
         boolean customNamePatched = input.hasNonDefault(DataComponents.CUSTOM_NAME);
         ItemStack var10000 = input;
         DataComponentType var10001 = DataComponents.ITEM_NAME;
         Component var10002;
         if (itemName != null) {
            DataComponentType var14 = var10001;
            int var16 = 0;
            Component var17 = this.componentTransformer.transform(itemName, true, false);
            var10000 = input;
            var10001 = var14;
            var10002 = var17;
         } else {
            var10002 = null;
         }

         block: {
            var10000.set(var10001, var10002);
            input.set(DataComponents.LORE, lore != null ? this.transform(lore) : null);
            CustomData var25 = (CustomData)input.get(DataComponents.CUSTOM_DATA);
            if (var25 != null) {
               String var26 = this.fetchOriginalName(var25);
               if (var26 != null) {
                  net.kyori.adventure.text.Component var27 = AdventureUtilsKt.deserialize(var26);
                  if (var27 != null) {
                     net.kyori.adventure.text.Component it = var27;
                     int var19 = 0;
                     net.kyori.adventure.text.Component escaped = GlyphHandlers.transformGlyphs(GlyphHandlers.INSTANCE.escapeGlyphs(it, this.player), (Locale)null);
                     var28 = PaperAdventure.asVanilla(ComponentProjections.INSTANCE.apply(this.player, GlyphHandlers.INSTANCE.unescapeGlyphs(escaped)));
                     if (var28 != null) {
                        break block;
                     }
                  }
               }
            }

            if (rawCustomName != null) {
               ComponentTransformer var23 = this.componentTransformer;
               int var24 = 0;
               var28 = var23.transform(rawCustomName);
            } else {
               var28 = null;
            }
         }

         Component customName = var28;
         input.set(DataComponents.CUSTOM_NAME, customName);
         HashedComponentRewriter var29 = this.hashRewriter;
         var10001 = DataComponents.ITEM_NAME;
         Intrinsics.checkNotNullExpressionValue(var10001, "ITEM_NAME");
         var29.record(var10001, itemName, input.get(DataComponents.ITEM_NAME), itemNamePatched);
         var29 = this.hashRewriter;
         var10001 = DataComponents.LORE;
         Intrinsics.checkNotNullExpressionValue(var10001, "LORE");
         var29.record(var10001, lore, input.get(DataComponents.LORE), lorePatched);
         var29 = this.hashRewriter;
         var10001 = DataComponents.CUSTOM_NAME;
         Intrinsics.checkNotNullExpressionValue(var10001, "CUSTOM_NAME");
         var29.record(var10001, rawCustomName, customName, customNamePatched);
      }

      return input;
   }

   @NotNull
   public final ItemStack transform(@NotNull ItemStack input, @Nullable String originalName) {
      Intrinsics.checkNotNullParameter(input, "input");
      boolean condition$iv = this.shouldTransform(input);
      if (condition$iv) {
         int var7 = 0;
         Component itemName = (Component)input.get(DataComponents.ITEM_NAME);
         boolean itemNamePatched = input.hasNonDefault(DataComponents.ITEM_NAME);
         ItemLore lore = (ItemLore)input.get(DataComponents.LORE);
         boolean lorePatched = input.hasNonDefault(DataComponents.LORE);
         Component rawCustomName = (Component)input.get(DataComponents.CUSTOM_NAME);
         boolean customNamePatched = input.hasNonDefault(DataComponents.CUSTOM_NAME);
         ItemStack var10000 = input;
         DataComponentType var10001 = DataComponents.ITEM_NAME;
         Component var10002;
         if (itemName != null) {
            DataComponentType var15 = var10001;
            int var17 = 0;
            Component var18 = this.componentTransformer.transform(itemName, true, false);
            var10000 = input;
            var10001 = var15;
            var10002 = var18;
         } else {
            var10002 = null;
         }

         var10000.set(var10001, var10002);
         input.set(DataComponents.LORE, lore != null ? this.transform(lore) : null);
         String var26 = originalName;
         if (originalName == null) {
            CustomData var27 = (CustomData)input.get(DataComponents.CUSTOM_DATA);
            var26 = var27 != null ? this.fetchOriginalName(var27) : null;
         }

         block: {
            if (var26 != null) {
               net.kyori.adventure.text.Component var28 = AdventureUtilsKt.deserialize(var26);
               if (var28 != null) {
                  net.kyori.adventure.text.Component it = var28;
                  int var20 = 0;
                  net.kyori.adventure.text.Component escaped = GlyphHandlers.transformGlyphs(GlyphHandlers.INSTANCE.escapeGlyphs(it, this.player), (Locale)null);
                  var29 = PaperAdventure.asVanilla(ComponentProjections.INSTANCE.apply(this.player, GlyphHandlers.INSTANCE.unescapeGlyphs(escaped)));
                  if (var29 != null) {
                     break block;
                  }
               }
            }

            if (rawCustomName != null) {
               ComponentTransformer var24 = this.componentTransformer;
               int var25 = 0;
               var29 = var24.transform(rawCustomName);
            } else {
               var29 = null;
            }
         }

         Component customName = var29;
         input.set(DataComponents.CUSTOM_NAME, customName);
         HashedComponentRewriter var30 = this.hashRewriter;
         var10001 = DataComponents.ITEM_NAME;
         Intrinsics.checkNotNullExpressionValue(var10001, "ITEM_NAME");
         var30.record(var10001, itemName, input.get(DataComponents.ITEM_NAME), itemNamePatched);
         var30 = this.hashRewriter;
         var10001 = DataComponents.LORE;
         Intrinsics.checkNotNullExpressionValue(var10001, "LORE");
         var30.record(var10001, lore, input.get(DataComponents.LORE), lorePatched);
         var30 = this.hashRewriter;
         var10001 = DataComponents.CUSTOM_NAME;
         Intrinsics.checkNotNullExpressionValue(var10001, "CUSTOM_NAME");
         var30.record(var10001, rawCustomName, customName, customNamePatched);
      }

      return input;
   }

   @NotNull
   public final DataComponentExactPredicate transform(@NotNull DataComponentExactPredicate predicate) {
      Intrinsics.checkNotNullParameter(predicate, "predicate");
      PatchedDataComponentMap map = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, predicate.asPatch());
      if (map.hasNonDefault(DataComponents.ITEM_NAME)) {
         PatchedDataComponentMap var10000 = map;
         DataComponentType var10001 = DataComponents.ITEM_NAME;
         Component var10002 = (Component)map.get(DataComponents.ITEM_NAME);
         if (var10002 != null) {
            Component it = var10002;
            DataComponentType var11 = var10001;
            int var4 = 0;
            Component var12 = this.componentTransformer.transform(it, true, false);
            var10000 = map;
            var10001 = var11;
            var10002 = var12;
         } else {
            var10002 = null;
         }

         var10000.set(var10001, var10002);
      }

      if (map.hasNonDefault(DataComponents.LORE)) {
         DataComponentType var25 = DataComponents.LORE;
         ItemLore var28 = (ItemLore)map.get(DataComponents.LORE);
         map.set(var25, var28 != null ? this.transform(var28) : null);
      }

      block: {
         CustomData var19 = (CustomData)map.get(DataComponents.CUSTOM_DATA);
         if (var19 != null) {
            String var20 = this.fetchOriginalName(var19);
            if (var20 != null) {
               String it = var20;
               int var6 = 0;
               net.kyori.adventure.text.Component escaped = GlyphHandlers.transformGlyphs(GlyphHandlers.INSTANCE.escapeGlyphs(AdventureUtilsKt.deserialize(it), this.player), this.player.locale());
               if ((Component)map.set(DataComponents.CUSTOM_NAME, PaperAdventure.asVanilla(ComponentProjections.INSTANCE.apply(this.player, GlyphHandlers.INSTANCE.unescapeGlyphs(escaped)))) != null) {
                  break block;
               }
            }
         }

         int var15 = 0;
         PatchedDataComponentMap var13 = map.hasNonDefault(DataComponents.CUSTOM_NAME) ? map : null;
         if (var13 != null) {
            PatchedDataComponentMap var21 = var13;
            DataComponentType var26 = DataComponents.CUSTOM_NAME;
            Component var29 = (Component)map.get(DataComponents.CUSTOM_NAME);
            if (var29 != null) {
               Component it = var29;
               ComponentTransformer var16 = this.componentTransformer;
               DataComponentType var17 = var26;
               int var9 = 0;
               Component var18 = var16.transform(it);
               var21 = var13;
               var26 = var17;
               var29 = var18;
            } else {
               var29 = null;
            }

            Component var22 = (Component)var21.set(var26, var29);
         } else {
            Object var23 = null;
         }
      }

      DataComponentExactPredicate var24 = DataComponentExactPredicate.allOf((DataComponentMap)map);
      Intrinsics.checkNotNullExpressionValue(var24, "allOf(...)");
      return var24;
   }

   private final ItemLore transform(ItemLore $this$transform) {
      List var10000 = $this$transform.lines();
      Intrinsics.checkNotNullExpressionValue(var10000, "lines(...)");
      Iterable $this$map$iv = (Iterable)var10000;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

      for(Object item$iv$iv : $this$map$iv) {
         Component it = (Component)item$iv$iv;
         int var10 = 0;
         ComponentTransformer var23 = this.componentTransformer;
         Intrinsics.checkNotNull(it);
         destination$iv$iv.add(var23.transform(it, true, false));
      }

      var10000 = (List)destination$iv$iv;
      List var10001 = $this$transform.styledLines();
      Intrinsics.checkNotNullExpressionValue(var10001, "styledLines(...)");
      $this$map$iv = (Iterable)var10001;
      List var11 = var10000;
      destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

      for(Object item$iv$iv : $this$map$iv) {
         Component it = (Component)item$iv$iv;
         int var22 = 0;
         ComponentTransformer var25 = this.componentTransformer;
         Intrinsics.checkNotNull(it);
         destination$iv$iv.add(var25.transform(it, true, false));
      }

      List var13 = (List)destination$iv$iv;
      return new ItemLore(var11, var13);
   }

   private final String fetchOriginalName(CustomData $this$fetchOriginalName) {
      Optional var10000 = $this$fetchOriginalName.getUnsafe().getString(Glyph.Companion.getORIGINAL_ITEM_RENAME_STRING());
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var4 = (String)OptionalsKt.getOrNull(var10000);
      if (var4 != null) {
         CharSequence var2 = (CharSequence)var4;
         CharSequence var5;
         if (var2.length() == 0) {
            int var3 = 0;
            var5 = null;
         } else {
            var5 = var2;
         }

         var4 = (String)var5;
      } else {
         var4 = null;
      }

      return var4;
   }

   private final boolean shouldTransform(ItemStack $this$shouldTransform) {
      return !$this$shouldTransform.isEmpty() && ($this$shouldTransform.hasNonDefault(DataComponents.ITEM_NAME) || $this$shouldTransform.hasNonDefault(DataComponents.LORE) || $this$shouldTransform.hasNonDefault(DataComponents.CUSTOM_NAME));
   }
}

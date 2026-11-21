package com.nexomc.nexo.nms.v1_21_R4.packets.transformers;

import com.nexomc.nexo.api.ComponentProjections;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.nms.GlyphHandlers;
import com.nexomc.nexo.nms.Transformer;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import io.papermc.paper.adventure.PaperAdventure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPredicate;
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
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u0002H\u0016J\u0018\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u00022\b\u0010\u000b\u001a\u0004\u0018\u00010\fJ\u000e\u0010\t\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rJ\f\u0010\t\u001a\u00020\u000f*\u00020\u000fH\u0002J\u000e\u0010\u0010\u001a\u0004\u0018\u00010\f*\u00020\u0011H\u0002J\f\u0010\u0012\u001a\u00020\u0013*\u00020\u0002H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R4/packets/transformers/ItemTransformer;", "Lcom/nexomc/nexo/nms/Transformer;", "Lnet/minecraft/world/item/ItemStack;", "player", "Lorg/bukkit/entity/Player;", "componentTransformer", "Lcom/nexomc/nexo/nms/v1_21_R4/packets/transformers/ComponentTransformer;", "<init>", "(Lorg/bukkit/entity/Player;Lcom/nexomc/nexo/nms/v1_21_R4/packets/transformers/ComponentTransformer;)V", "transform", "input", "originalName", "", "Lnet/minecraft/core/component/DataComponentPredicate;", "predicate", "Lnet/minecraft/world/item/component/ItemLore;", "fetchOriginalName", "Lnet/minecraft/world/item/component/CustomData;", "shouldTransform", "", "com.nexomc:v1_21_R4"}
)
public final class ItemTransformer implements Transformer<ItemStack> {
   @NotNull
   private final Player player;
   @NotNull
   private final ComponentTransformer componentTransformer;

   public ItemTransformer(@NotNull Player player, @NotNull ComponentTransformer componentTransformer) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(componentTransformer, "componentTransformer");
      super();
      this.player = player;
      this.componentTransformer = componentTransformer;
   }

   @NotNull
   public ItemStack transform(@NotNull ItemStack input) {
      Intrinsics.checkNotNullParameter(input, "input");
      boolean condition$iv = this.shouldTransform(input);
      if (condition$iv) {
         int var6 = 0;
         ItemStack var10000 = input;
         DataComponentType var10001 = DataComponents.ITEM_NAME;
         Component var10002 = (Component)input.get(DataComponents.ITEM_NAME);
         if (var10002 != null) {
            Component it = var10002;
            DataComponentType var8 = var10001;
            int var10 = 0;
            Component var11 = this.componentTransformer.transform(it, true, false);
            var10000 = input;
            var10001 = var8;
            var10002 = var11;
         } else {
            var10002 = null;
         }

         block: {
            var10000.set(var10001, var10002);
            var10001 = DataComponents.LORE;
            ItemLore var26 = (ItemLore)input.get(DataComponents.LORE);
            input.set(var10001, var26 != null ? this.transform(var26) : null);
            CustomData var19 = (CustomData)input.get(DataComponents.CUSTOM_DATA);
            if (var19 != null) {
               String var20 = this.fetchOriginalName(var19);
               if (var20 != null) {
                  net.kyori.adventure.text.Component var21 = AdventureUtilsKt.deserialize(var20);
                  if (var21 != null) {
                     net.kyori.adventure.text.Component it = var21;
                     int var13 = 0;
                     net.kyori.adventure.text.Component escaped = GlyphHandlers.transformGlyphs(GlyphHandlers.INSTANCE.escapeGlyphs(it, this.player), (Locale)null);
                     var22 = PaperAdventure.asVanilla(ComponentProjections.INSTANCE.apply(this.player, GlyphHandlers.INSTANCE.unescapeGlyphs(escaped)));
                     if (var22 != null) {
                        break block;
                     }
                  }
               }
            }

            Component var23 = (Component)input.get(DataComponents.CUSTOM_NAME);
            if (var23 != null) {
               Component p0 = var23;
               ComponentTransformer var18 = this.componentTransformer;
               int var15 = 0;
               var22 = var18.transform(p0);
            } else {
               var22 = null;
            }
         }

         Component customName = var22;
         input.set(DataComponents.CUSTOM_NAME, customName);
      }

      return input;
   }

   @NotNull
   public final ItemStack transform(@NotNull ItemStack input, @Nullable String originalName) {
      Intrinsics.checkNotNullParameter(input, "input");
      boolean condition$iv = this.shouldTransform(input);
      if (condition$iv) {
         int var7 = 0;
         ItemStack var10000 = input;
         DataComponentType var10001 = DataComponents.ITEM_NAME;
         Component var10002 = (Component)input.get(DataComponents.ITEM_NAME);
         if (var10002 != null) {
            Component it = var10002;
            DataComponentType var9 = var10001;
            int var11 = 0;
            Component var12 = this.componentTransformer.transform(it, true, false);
            var10000 = input;
            var10001 = var9;
            var10002 = var12;
         } else {
            var10002 = null;
         }

         var10000.set(var10001, var10002);
         var10001 = DataComponents.LORE;
         ItemLore var27 = (ItemLore)input.get(DataComponents.LORE);
         input.set(var10001, var27 != null ? this.transform(var27) : null);
         String var20 = originalName;
         if (originalName == null) {
            CustomData var21 = (CustomData)input.get(DataComponents.CUSTOM_DATA);
            var20 = var21 != null ? this.fetchOriginalName(var21) : null;
         }

         block: {
            if (var20 != null) {
               net.kyori.adventure.text.Component var22 = AdventureUtilsKt.deserialize(var20);
               if (var22 != null) {
                  net.kyori.adventure.text.Component it = var22;
                  int var14 = 0;
                  net.kyori.adventure.text.Component escaped = GlyphHandlers.transformGlyphs(GlyphHandlers.INSTANCE.escapeGlyphs(it, this.player), (Locale)null);
                  var23 = PaperAdventure.asVanilla(ComponentProjections.INSTANCE.apply(this.player, GlyphHandlers.INSTANCE.unescapeGlyphs(escaped)));
                  if (var23 != null) {
                     break block;
                  }
               }
            }

            Component var24 = (Component)input.get(DataComponents.CUSTOM_NAME);
            if (var24 != null) {
               Component p0 = var24;
               ComponentTransformer var19 = this.componentTransformer;
               int var16 = 0;
               var23 = var19.transform(p0);
            } else {
               var23 = null;
            }
         }

         Component customName = var23;
         input.set(DataComponents.CUSTOM_NAME, customName);
      }

      return input;
   }

   @NotNull
   public final DataComponentPredicate transform(@NotNull DataComponentPredicate predicate) {
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

      DataComponentPredicate var24 = DataComponentPredicate.allOf((DataComponentMap)map);
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
      CharSequence var2 = (CharSequence)$this$fetchOriginalName.getUnsafe().getString(Glyph.Companion.getORIGINAL_ITEM_RENAME_STRING());
      CharSequence var10000;
      if (var2.length() == 0) {
         int var3 = 0;
         var10000 = null;
      } else {
         var10000 = var2;
      }

      return (String)var10000;
   }

   private final boolean shouldTransform(ItemStack $this$shouldTransform) {
      return !$this$shouldTransform.isEmpty() && ($this$shouldTransform.hasNonDefault(DataComponents.ITEM_NAME) || $this$shouldTransform.hasNonDefault(DataComponents.LORE) || $this$shouldTransform.hasNonDefault(DataComponents.CUSTOM_NAME));
   }
}

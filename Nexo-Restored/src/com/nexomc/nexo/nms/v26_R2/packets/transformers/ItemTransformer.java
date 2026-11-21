package com.nexomc.nexo.nms.v26_R2.packets.transformers;

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
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.optionals.OptionalsKt;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B/\u0012\u000e\u0010\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rJ\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013J\u0010\u0010\u0012\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u0002H\u0016J\u0018\u0010\u0012\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u00022\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016J\u000e\u0010\u0012\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0017J\f\u0010\u0012\u001a\u00020\u0019*\u00020\u0019H\u0002J\u000e\u0010\u001a\u001a\u0004\u0018\u00010\u0016*\u00020\u001bH\u0002J\f\u0010\u001c\u001a\u00020\u001d*\u00020\u0002H\u0002R\u0016\u0010\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u00020\u0007X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"},
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ItemTransformer;", "Lcom/nexomc/nexo/nms/Transformer;", "Lnet/minecraft/world/item/ItemStack;", "player", "Lkotlin/Function0;", "Lorg/bukkit/entity/Player;", "locale", "Ljava/util/Locale;", "componentTransformer", "Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ComponentTransformer;", "hashRewriter", "Lcom/nexomc/nexo/nms/commons/packets/HashedComponentRewriter;", "<init>", "(Lkotlin/jvm/functions/Function0;Ljava/util/Locale;Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ComponentTransformer;Lcom/nexomc/nexo/nms/commons/packets/HashedComponentRewriter;)V", "getLocale$com_nexomc_v26_R2", "()Ljava/util/Locale;", "setLocale$com_nexomc_v26_R2", "(Ljava/util/Locale;)V", "transform", "Lnet/minecraft/world/item/ItemStackTemplate;", "input", "originalName", "", "Lnet/minecraft/core/component/DataComponentExactPredicate;", "predicate", "Lnet/minecraft/world/item/component/ItemLore;", "fetchOriginalName", "Lnet/minecraft/world/item/component/CustomData;", "shouldTransform", "", "com.nexomc:v26_R2"}
)
public final class ItemTransformer implements Transformer<ItemStack> {
   @NotNull
   private final Function0<Player> player;
   @NotNull
   private Locale locale;
   @NotNull
   private final ComponentTransformer componentTransformer;
   @NotNull
   private final HashedComponentRewriter hashRewriter;

   public ItemTransformer(@NotNull Function0<Player> player, @NotNull Locale locale, @NotNull ComponentTransformer componentTransformer, @NotNull HashedComponentRewriter hashRewriter) {
      super();
      this.player = player;
      this.locale = locale;
      this.componentTransformer = componentTransformer;
      this.hashRewriter = hashRewriter;
   }

   @NotNull
   public final Locale getLocale$com_nexomc_v26_R2() {
      return this.locale;
   }

   public final void setLocale$com_nexomc_v26_R2(@NotNull Locale locale2) {
      this.locale = locale2;
   }

   @NotNull
   public final ItemStackTemplate transform(@NotNull ItemStackTemplate input) {
      net.minecraft.network.chat.Component component;
      ItemStack itemStack = input.create();
      boolean condition = this.shouldTransform(itemStack);
      ItemStackTemplate itemStackTemplate;
      if (condition) {
         DataComponentPatch.Builder builder;
         block: {
            int value = 0;
            builder = DataComponentPatch.builder();
            int value2 = 0;
            Component component2 = (Component)input.get(DataComponents.ITEM_NAME);
            if (component2 != null) {
               Component it = component2;
               int value3 = 0;
               component2 = this.componentTransformer.transform(it, true, false);
               if (component2 != null) {
                  Component it_1 = component2;
                  int value4 = 0;
                  if (builder.set(DataComponents.ITEM_NAME, it_1) != null) {
                     break block;
                  }
               }
            }

            Intrinsics.checkNotNullExpressionValue(builder.remove(DataComponents.ITEM_NAME), "remove(...)");
         }

         block: {
            ItemLore itemLore = (ItemLore)input.get(DataComponents.LORE);
            if (itemLore != null) {
               itemLore = this.transform(itemLore);
               if (itemLore != null) {
                  ItemLore it = itemLore;
                  int value5 = 0;
                  if (builder.set(DataComponents.LORE, it) != null) {
                     break block;
                  }
               }
            }

            Intrinsics.checkNotNullExpressionValue(builder.remove(DataComponents.LORE), "remove(...)");
         }

         block: {
            CustomData customData = (CustomData)input.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
               String str = this.fetchOriginalName(customData);
               if (str != null) {
                  net.kyori.adventure.text.Component component3 = AdventureUtilsKt.deserialize(str);
                  if (component3 != null) {
                     net.kyori.adventure.text.Component it = component3;
                     int value6 = 0;
                     net.kyori.adventure.text.Component escaped = GlyphHandlers.transformGlyphs(GlyphHandlers.INSTANCE.escapeGlyphs(it, (Player)this.player.invoke()), this.locale);
                     component = PaperAdventure.asVanilla(ComponentProjections.INSTANCE.apply((Player)this.player.invoke(), GlyphHandlers.INSTANCE.unescapeGlyphs(escaped)));
                     if (component != null) {
                        break block;
                     }
                  }
               }
            }

            Component component4 = (Component)input.get(DataComponents.CUSTOM_NAME);
            if (component4 != null) {
               Component p0 = component4;
               ComponentTransformer componentTransformer2 = this.componentTransformer;
               int value7 = 0;
               component = componentTransformer2.transform(p0);
            } else {
               component = null;
            }
         }

         Component customName = component;
         if (customName == null) {
            builder.remove(DataComponents.CUSTOM_NAME);
         } else {
            builder.set(DataComponents.CUSTOM_NAME, customName);
         }

         itemStackTemplate = ItemStackTemplate.fromNonEmptyStack(input.apply(builder.build()));
      } else {
         itemStackTemplate = input;
      }

      return itemStackTemplate;
   }

   @NotNull
   public ItemStack transform(@NotNull ItemStack input) {
      net.minecraft.network.chat.Component component;
      boolean condition = this.shouldTransform(input);
      if (condition) {
         int value = 0;
         Component itemName = (Component)input.get(DataComponents.ITEM_NAME);
         boolean itemNamePatched = input.hasNonDefault(DataComponents.ITEM_NAME);
         ItemLore lore = (ItemLore)input.get(DataComponents.LORE);
         boolean lorePatched = input.hasNonDefault(DataComponents.LORE);
         Component rawCustomName = (Component)input.get(DataComponents.CUSTOM_NAME);
         boolean customNamePatched = input.hasNonDefault(DataComponents.CUSTOM_NAME);
         ItemStack itemStack = input;
         DataComponentType dataComponentType = DataComponents.ITEM_NAME;
         Component component2;
         if (itemName != null) {
            DataComponentType dataComponentType2 = dataComponentType;
            int value2 = 0;
            Component component3 = this.componentTransformer.transform(itemName, true, false);
            itemStack = input;
            dataComponentType = dataComponentType2;
            component2 = component3;
         } else {
            component2 = null;
         }

         block: {
            itemStack.set(dataComponentType, component2);
            input.set(DataComponents.LORE, lore != null ? this.transform(lore) : null);
            CustomData customData = (CustomData)input.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
               String str = this.fetchOriginalName(customData);
               if (str != null) {
                  net.kyori.adventure.text.Component component4 = AdventureUtilsKt.deserialize(str);
                  if (component4 != null) {
                     net.kyori.adventure.text.Component it = component4;
                     int value3 = 0;
                     net.kyori.adventure.text.Component escaped = GlyphHandlers.transformGlyphs(GlyphHandlers.INSTANCE.escapeGlyphs(it, (Player)this.player.invoke()), this.locale);
                     component = PaperAdventure.asVanilla(ComponentProjections.INSTANCE.apply((Player)this.player.invoke(), GlyphHandlers.INSTANCE.unescapeGlyphs(escaped)));
                     if (component != null) {
                        break block;
                     }
                  }
               }
            }

            if (rawCustomName != null) {
               ComponentTransformer componentTransformer2 = this.componentTransformer;
               int value4 = 0;
               component = componentTransformer2.transform(rawCustomName);
            } else {
               component = null;
            }
         }

         Component customName = component;
         input.set(DataComponents.CUSTOM_NAME, customName);
         HashedComponentRewriter hashedComponentRewriter = this.hashRewriter;
         dataComponentType = DataComponents.ITEM_NAME;
         hashedComponentRewriter.record(dataComponentType, itemName, input.get(DataComponents.ITEM_NAME), itemNamePatched);
         hashedComponentRewriter = this.hashRewriter;
         dataComponentType = DataComponents.LORE;
         hashedComponentRewriter.record(dataComponentType, lore, input.get(DataComponents.LORE), lorePatched);
         hashedComponentRewriter = this.hashRewriter;
         dataComponentType = DataComponents.CUSTOM_NAME;
         hashedComponentRewriter.record(dataComponentType, rawCustomName, customName, customNamePatched);
      }

      return input;
   }

   @NotNull
   public final ItemStack transform(@NotNull ItemStack input, @Nullable String originalName) {
      net.minecraft.network.chat.Component component;
      boolean condition = this.shouldTransform(input);
      if (condition) {
         int value = 0;
         Component itemName = (Component)input.get(DataComponents.ITEM_NAME);
         boolean itemNamePatched = input.hasNonDefault(DataComponents.ITEM_NAME);
         ItemLore lore = (ItemLore)input.get(DataComponents.LORE);
         boolean lorePatched = input.hasNonDefault(DataComponents.LORE);
         Component rawCustomName = (Component)input.get(DataComponents.CUSTOM_NAME);
         boolean customNamePatched = input.hasNonDefault(DataComponents.CUSTOM_NAME);
         ItemStack itemStack = input;
         DataComponentType dataComponentType = DataComponents.ITEM_NAME;
         Component component2;
         if (itemName != null) {
            DataComponentType dataComponentType2 = dataComponentType;
            int value2 = 0;
            Component component3 = this.componentTransformer.transform(itemName, true, false);
            itemStack = input;
            dataComponentType = dataComponentType2;
            component2 = component3;
         } else {
            component2 = null;
         }

         itemStack.set(dataComponentType, component2);
         input.set(DataComponents.LORE, lore != null ? this.transform(lore) : null);
         String str = originalName;
         if (originalName == null) {
            CustomData customData = (CustomData)input.get(DataComponents.CUSTOM_DATA);
            str = customData != null ? this.fetchOriginalName(customData) : null;
         }

         block: {
            if (str != null) {
               net.kyori.adventure.text.Component component4 = AdventureUtilsKt.deserialize(str);
               if (component4 != null) {
                  net.kyori.adventure.text.Component it = component4;
                  int value3 = 0;
                  net.kyori.adventure.text.Component escaped = GlyphHandlers.transformGlyphs(GlyphHandlers.INSTANCE.escapeGlyphs(it, (Player)this.player.invoke()), this.locale);
                  component = PaperAdventure.asVanilla(ComponentProjections.INSTANCE.apply((Player)this.player.invoke(), GlyphHandlers.INSTANCE.unescapeGlyphs(escaped)));
                  if (component != null) {
                     break block;
                  }
               }
            }

            if (rawCustomName != null) {
               ComponentTransformer componentTransformer2 = this.componentTransformer;
               int value4 = 0;
               component = componentTransformer2.transform(rawCustomName);
            } else {
               component = null;
            }
         }

         Component customName = component;
         input.set(DataComponents.CUSTOM_NAME, customName);
         HashedComponentRewriter hashedComponentRewriter = this.hashRewriter;
         dataComponentType = DataComponents.ITEM_NAME;
         hashedComponentRewriter.record(dataComponentType, itemName, input.get(DataComponents.ITEM_NAME), itemNamePatched);
         hashedComponentRewriter = this.hashRewriter;
         dataComponentType = DataComponents.LORE;
         hashedComponentRewriter.record(dataComponentType, lore, input.get(DataComponents.LORE), lorePatched);
         hashedComponentRewriter = this.hashRewriter;
         dataComponentType = DataComponents.CUSTOM_NAME;
         hashedComponentRewriter.record(dataComponentType, rawCustomName, customName, customNamePatched);
      }

      return input;
   }

   @NotNull
   public final DataComponentExactPredicate transform(@NotNull DataComponentExactPredicate predicate) {
      PatchedDataComponentMap patchedDataComponentMap = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, predicate.asPatch());
      PatchedDataComponentMap map = patchedDataComponentMap;
      if (map.hasNonDefault(DataComponents.ITEM_NAME)) {
         patchedDataComponentMap = map;
         DataComponentType dataComponentType = DataComponents.ITEM_NAME;
         Component component = (Component)map.get(DataComponents.ITEM_NAME);
         if (component != null) {
            Component it = component;
            DataComponentType dataComponentType2 = dataComponentType;
            int value = 0;
            Component component2 = this.componentTransformer.transform(it, true, false);
            patchedDataComponentMap = map;
            dataComponentType = dataComponentType2;
            component = component2;
         } else {
            component = null;
         }

         patchedDataComponentMap.set(dataComponentType, component);
      }

      if (map.hasNonDefault(DataComponents.LORE)) {
         DataComponentType dataComponentType3 = DataComponents.LORE;
         ItemLore itemLore = (ItemLore)map.get(DataComponents.LORE);
         map.set(dataComponentType3, itemLore != null ? this.transform(itemLore) : null);
      }

      block: {
         CustomData customData = (CustomData)map.get(DataComponents.CUSTOM_DATA);
         if (customData != null) {
            String str = this.fetchOriginalName(customData);
            if (str != null) {
               String it = str;
               int value2 = 0;
               net.kyori.adventure.text.Component escaped = GlyphHandlers.transformGlyphs(GlyphHandlers.INSTANCE.escapeGlyphs(AdventureUtilsKt.deserialize(it), (Player)this.player.invoke()), this.locale);
               if ((Component)map.set(DataComponents.CUSTOM_NAME, PaperAdventure.asVanilla(ComponentProjections.INSTANCE.apply((Player)this.player.invoke(), GlyphHandlers.INSTANCE.unescapeGlyphs(escaped)))) != null) {
                  break block;
               }
            }
         }

         int value3 = 0;
         PatchedDataComponentMap patchedDataComponentMap2 = map.hasNonDefault(DataComponents.CUSTOM_NAME) ? map : null;
         if (patchedDataComponentMap2 != null) {
            PatchedDataComponentMap patchedDataComponentMap3 = patchedDataComponentMap2;
            DataComponentType dataComponentType4 = DataComponents.CUSTOM_NAME;
            Component component3 = (Component)map.get(DataComponents.CUSTOM_NAME);
            if (component3 != null) {
               Component it = component3;
               ComponentTransformer componentTransformer2 = this.componentTransformer;
               DataComponentType dataComponentType5 = dataComponentType4;
               int value4 = 0;
               Component component4 = componentTransformer2.transform(it);
               patchedDataComponentMap3 = patchedDataComponentMap2;
               dataComponentType4 = dataComponentType5;
               component3 = component4;
            } else {
               component3 = null;
            }

            Component component5 = (Component)patchedDataComponentMap3.set(dataComponentType4, component3);
         } else {
            Object obj = null;
         }
      }

      DataComponentExactPredicate dataComponentExactPredicate = DataComponentExactPredicate.allOf((DataComponentMap)map);
      return dataComponentExactPredicate;
   }

   private final ItemLore transform(ItemLore itemLore) {
      List lines = itemLore.lines();
      Iterable iterable = (Iterable)lines;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         Component it = (Component)item;
         int value = 0;
         ComponentTransformer componentTransformer2 = this.componentTransformer;
         destination.add(componentTransformer2.transform(it, true, false));
      }

      lines = (List)destination;
      List styledLines = itemLore.styledLines();
      iterable = (Iterable)styledLines;
      List list = lines;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj : iterable) {
         Component it = (Component)obj;
         int value2 = 0;
         ComponentTransformer componentTransformer3 = this.componentTransformer;
         destination.add(componentTransformer3.transform(it, true, false));
      }

      List list2 = (List)destination;
      return new ItemLore(list, list2);
   }

   private final String fetchOriginalName(CustomData customData) {
      Optional string = customData.getUnsafe().getString(Glyph.Companion.getORIGINAL_ITEM_RENAME_STRING());
      String orNull = (String)OptionalsKt.getOrNull(string);
      if (orNull != null) {
         CharSequence charSequence = (CharSequence)orNull;
         CharSequence charSequence2;
         if (charSequence.length() == 0) {
            int value = 0;
            charSequence2 = null;
         } else {
            charSequence2 = charSequence;
         }

         orNull = (String)charSequence2;
      } else {
         orNull = null;
      }

      return orNull;
   }

   private final boolean shouldTransform(ItemStack itemStack) {
      return !itemStack.isEmpty() && (itemStack.hasNonDefault(DataComponents.ITEM_NAME) || itemStack.hasNonDefault(DataComponents.LORE) || itemStack.hasNonDefault(DataComponents.CUSTOM_NAME));
   }
}

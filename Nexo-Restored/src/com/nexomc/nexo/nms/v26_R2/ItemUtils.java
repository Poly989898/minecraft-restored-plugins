package com.nexomc.nexo.nms.v26_R2;

import com.nexomc.nexo.decomp.ResultShim;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.nms.IItemUtils;
import com.nexomc.nexo.utils.Colorable;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlockItemDataProperties;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.PaperBlockItemDataProperties;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.Repairable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.datacomponent.item.consumable.PaperApplyStatusEffects;
import io.papermc.paper.datacomponent.item.consumable.PaperClearAllStatusEffects;
import io.papermc.paper.datacomponent.item.consumable.PaperPlaySound;
import io.papermc.paper.datacomponent.item.consumable.PaperRemoveStatusEffects;
import io.papermc.paper.datacomponent.item.consumable.PaperTeleportRandomly;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.optionals.OptionalsKt;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.advancements.predicates.BlockPredicate.Builder;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.SnbtPrinterTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.PlaySoundConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000Ð\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000f\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0004\n\u0002\b\u0002\u0018\u0000 T2\u00020\u0001:\u0001TB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0012\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u001a\u0010\b\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016J\u0018\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u0007H\u0016J\u0018\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0014\u0010\u000e\u001a\u0004\u0018\u00010\u00132\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u001a\u0010\u000e\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0016J\u001e\u0010\u0016\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00120\u0018H\u0016J\u001a\u0010\u0016\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0019\u001a\u0004\u0018\u00010\u0015H\u0016J\u001e\u0010\u001a\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00120\u0018H\u0016J\u001a\u0010\u001a\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0019\u001a\u0004\u0018\u00010\u0015H\u0016J\u0018\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00120\u0018H\u0002J\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!H\u0002J\u001c\u0010\"\u001a\u0004\u0018\u00010#2\b\u0010$\u001a\u0004\u0018\u00010\u00122\u0006\u0010%\u001a\u00020&H\u0002J.\u0010'\u001a\u00020(\"\u000e\b\u0000\u0010)*\b\u0012\u0004\u0012\u0002H)0*2\u0006\u0010 \u001a\u00020!2\f\u0010+\u001a\b\u0012\u0004\u0012\u0002H)0,H\u0002J\u001e\u0010-\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010.\u001a\b\u0012\u0004\u0012\u00020/0\u0018H\u0016J\u0014\u0010-\u001a\u0004\u0018\u0001002\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u001a\u0010-\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u00101\u001a\u0004\u0018\u00010\u0015H\u0016J \u00102\u001a\u0010\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u00020(\u0018\u0001032\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J&\u00102\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\u0014\u00104\u001a\u0010\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u00020(\u0018\u000103H\u0016J&\u00105\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\u0014\u00106\u001a\u0010\u0012\u0004\u0012\u00020(\u0012\u0006\u0012\u0004\u0018\u00010\u001503H\u0016J\u0014\u00107\u001a\u0004\u0018\u00010\u00152\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u0018\u00107\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u00108\u001a\u00020\u0012H\u0016J\u001a\u00107\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u00109\u001a\u0004\u0018\u00010\u0015H\u0016J\u001a\u0010:\u001a\n\u0012\u0004\u0012\u00020;\u0018\u00010\u00182\b\u0010<\u001a\u0004\u0018\u00010\u0012H\u0016J \u0010=\u001a\u00020\n2\u0006\u0010>\u001a\u00020?2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010@\u001a\u00020AH\u0016J6\u0010B\u001a\u00020C\"\b\b\u0000\u0010)*\u00020\u00152\f\u0010D\u001a\b\u0012\u0004\u0012\u0002H)0E2\f\u0010F\u001a\b\u0012\u0004\u0012\u00020C0G2\u0006\u0010H\u001a\u00020\u0015H\u0002J@\u0010I\u001a\u00020\n2\u0012\u0010J\u001a\u000e\u0012\u0004\u0012\u00020L\u0012\u0004\u0012\u00020\n0K2\u0012\u0010M\u001a\u000e\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u00020\n0K2\u0006\u0010N\u001a\u00020(2\u0006\u0010O\u001a\u00020CH\u0002J\u0018\u0010P\u001a\u00020Q*\u000e\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u00020\u001503H\u0002J\f\u0010R\u001a\u00020C*\u00020\u0015H\u0002J\f\u0010R\u001a\u00020C*\u00020SH\u0002¨\u0006U"},
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/ItemUtils;", "Lcom/nexomc/nexo/nms/IItemUtils;", "<init>", "()V", "asColorable", "Lcom/nexomc/nexo/utils/Colorable;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "paintingVariant", "Lorg/bukkit/Art;", "", "copyItemNBTTags", "oldItem", "newItem", "consumableComponent", "itemBuilder", "Lcom/nexomc/nexo/items/ItemBuilder;", "consumableSection", "Lorg/bukkit/configuration/ConfigurationSection;", "Lio/papermc/paper/datacomponent/item/Consumable;", "consumable", "", "canPlaceOnComponent", "predicateSections", "", "predicate", "canBreakComponent", "adventureModePredicate", "Lnet/minecraft/world/item/AdventureModePredicate;", "sections", "blockPredicate", "Lnet/minecraft/advancements/predicates/BlockPredicate;", "state", "Lnet/minecraft/world/level/block/state/BlockState;", "stateProperties", "Lnet/minecraft/advancements/predicates/StatePropertiesPredicate$Builder;", "stateSection", "block", "Lnet/minecraft/world/level/block/Block;", "propertyValue", "", "T", "", "property", "Lnet/minecraft/world/level/block/state/properties/Property;", "repairableComponent", "repairableWith", "Lnet/kyori/adventure/key/Key;", "Lio/papermc/paper/datacomponent/item/Repairable;", "repairable", "blockstateComponent", "", "blockstates", "customDataComponent", "customData", "deathProtectionComponent", "deathProtectionSection", "deathProtection", "consumeEffectsFromSection", "Lio/papermc/paper/datacomponent/item/consumable/ConsumeEffect;", "effectsSection", "dumpItem", "sender", "Lorg/bukkit/command/CommandSender;", "includeAllComponents", "", "encodeComponentValue", "Lnet/minecraft/nbt/Tag;", "type", "Lnet/minecraft/core/component/DataComponentType;", "ops", "Lcom/mojang/serialization/DynamicOps;", "value", "writeComponentValue", "visualOutput", "Lkotlin/Function1;", "Lnet/kyori/adventure/text/Component;", "commandOutput", "path", "serialized", "toCompound", "Lnet/minecraft/nbt/CompoundTag;", "toTag", "", "Companion", "com.nexomc:v26_R2"}
)
public final class ItemUtils implements IItemUtils {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private static final Method getCraftStackMethod;

   public ItemUtils() {
      Consumable.consumable();
      DyedItemColor.dyedItemColor();
   }

   @Nullable
   public Colorable asColorable(@NotNull final ItemStack itemStack) {
      final DyedItemColor dyedColor = (DyedItemColor)itemStack.getData(DataComponentTypes.DYED_COLOR);
      final DyeColor baseColor = (DyeColor)itemStack.getData(DataComponentTypes.BASE_COLOR);
      final Function1 potionColorableSetLogic = (a0_ -> ItemUtils.asColorable$lambda$0(itemStack, (Color)a0_));
      return (baseColor != null ? (Colorable)(new Colorable() {
         public Color getColor() {
            return baseColor.getColor();
         }

         public void setColor(Color value) {
            com.nexomc.nexo.utils.ItemUtils itemUtils = com.nexomc.nexo.utils.ItemUtils.INSTANCE;
            ItemStack itemStack2 = itemStack;
            DataComponentType.Valued valued = DataComponentTypes.BASE_COLOR;
            DataComponentType.Valued type = valued;
            DyeColor dyeColor;
            if (value != null) {
               int value2 = 0;
               dyeColor = DyeColor.getByColor(value);
            } else {
               dyeColor = null;
            }

            Object obj = dyeColor;
            if (obj != null) {
               itemStack2.setData(type, obj);
            } else {
               itemStack2.resetData((DataComponentType)type);
            }

         }

         public Color getPotionContentColor() {
            PotionContents data = (PotionContents)itemStack.getData(DataComponentTypes.POTION_CONTENTS);
            return data != null ? data.customColor() : null;
         }

         public void setPotionContentColor(Color value) {
            potionColorableSetLogic.invoke(value);
         }
      }) : (Colorable)(new Colorable() {
         public Color getColor() {
            DyedItemColor dyedItemColor = dyedColor;
            return dyedItemColor != null ? dyedItemColor.color() : null;
         }

         public void setColor(Color value) {
            com.nexomc.nexo.utils.ItemUtils itemUtils2 = com.nexomc.nexo.utils.ItemUtils.INSTANCE;
            ItemStack itemStack3 = itemStack;
            DataComponentType.Valued valued2 = DataComponentTypes.DYED_COLOR;
            DataComponentType.Valued valued3 = valued2;
            DyedItemColor dyedItemColor2;
            if (value != null) {
               int value3 = 0;
               dyedItemColor2 = DyedItemColor.dyedItemColor(value);
            } else {
               dyedItemColor2 = null;
            }

            Object obj2 = dyedItemColor2;
            if (obj2 != null) {
               itemStack3.setData(valued3, obj2);
            } else {
               itemStack3.resetData((DataComponentType)valued3);
            }

         }

         public Color getPotionContentColor() {
            PotionContents potionContents = (PotionContents)itemStack.getData(DataComponentTypes.POTION_CONTENTS);
            return potionContents != null ? potionContents.customColor() : null;
         }

         public void setPotionContentColor(Color value) {
            potionColorableSetLogic.invoke(value);
         }
      }));
   }

   @Nullable
   public Art paintingVariant(@NotNull ItemStack itemStack) {
      return (Art)itemStack.getData(DataComponentTypes.PAINTING_VARIANT);
   }

   public void paintingVariant(@NotNull ItemStack itemStack, @Nullable Art paintingVariant) {
      com.nexomc.nexo.utils.ItemUtils itemUtils = com.nexomc.nexo.utils.ItemUtils.INSTANCE;
      DataComponentType.Valued valued = DataComponentTypes.PAINTING_VARIANT;
      DataComponentType.Valued type = valued;
      if (paintingVariant != null) {
         itemStack.setData(type, paintingVariant);
      } else {
         itemStack.resetData((DataComponentType)type);
      }

   }

   @NotNull
   public ItemStack copyItemNBTTags(@NotNull ItemStack oldItem, @NotNull ItemStack newItem) {
      Pair pair = TuplesKt.to(CraftItemStack.asNMSCopy(newItem), CraftItemStack.asNMSCopy(oldItem));
      net.minecraft.world.item.ItemStack newNmsItem = (net.minecraft.world.item.ItemStack)pair.component1();
      net.minecraft.world.item.ItemStack oldItemStack = (net.minecraft.world.item.ItemStack)pair.component2();
      net.minecraft.core.component.DataComponentType dataComponentType = DataComponents.CUSTOM_DATA;
      net.minecraft.core.component.DataComponentType type = dataComponentType;
      CustomData customData = (CustomData)oldItemStack.getComponents().get(type);
      if (customData == null) {
         return newItem;
      } else {
         CustomData customData2 = (CustomData)newNmsItem.getComponents().get(type);
         if (customData2 == null) {
            return newItem;
         } else {
            Pair pair2 = TuplesKt.to(customData, customData2);
            CustomData oldData = (CustomData)pair2.component1();
            CustomData newData = (CustomData)pair2.component2();
            Pair pair3 = TuplesKt.to(oldData.copyTag(), newData.copyTag());
            Object component1 = pair3.component1();
            CompoundTag oldTag = (CompoundTag)component1;
            component1 = pair3.component2();
            CompoundTag newTag = (CompoundTag)component1;
            Set keySet = oldTag.keySet();
            Iterable iterable = (Iterable)keySet;
            Collection destination = (Collection)(new ArrayList());

            for(Object element : iterable) {
               String it = (String)element;
               int value = 0;
               if (!IItemUtils.vanillaKeys.contains(it)) {
                  destination.add(element);
               }
            }

            iterable = (Iterable)((List)destination);

            for(Object obj : iterable) {
               String key = (String)obj;
               int value2 = 0;
               Tag tag = oldTag.get(key);
               if (tag != null) {
                  Tag it = tag;
                  int value3 = 0;
                  newTag.put(key, it);
                  if (it != null) {
                     continue;
                  }
               }

               newTag.remove(key);
            }

            newNmsItem.set(type, CustomData.of(newTag));
            ItemStack itemStack = CraftItemStack.asBukkitMirror(newNmsItem);
            return itemStack;
         }
      }
   }

   public void consumableComponent(@NotNull ItemBuilder itemBuilder, @NotNull ConfigurationSection consumableSection) {
      Consumable.Builder builder = Consumable.consumable();
      Consumable.Builder consumable = builder;
      Consumable data = (Consumable)ItemStack.of(itemBuilder.getType()).getData(DataComponentTypes.CONSUMABLE);
      if (data == null) {
         Object obj = Consumable.consumable().build();
         data = (Consumable)obj;
      }

      Consumable template = data;
      Duration duration = NexoYamlKt.durationOrNull(consumableSection, "consume_duration", (DurationUnit)null);
      consumable.consumeSeconds(duration != null ? RangesKt.coerceAtLeast(DurationUtilsKt.toFloat_VtjQ1oo(duration.unbox_impl(), DurationUnit.SECONDS), 0.0F) : template.consumeSeconds());
      consumable.animation((ItemUseAnimation)EnumUtils.getEnum(ItemUseAnimation.class, consumableSection.getString("animation"), template.animation()));
      consumable.hasConsumeParticles(consumableSection.getBoolean("consume_particles", template.hasConsumeParticles()));
      Key sound = template.sound();
      consumable.sound(NexoYamlKt.key(consumableSection, "sound", sound));
      List list = this.consumeEffectsFromSection(NexoYamlKt.section(consumableSection, "effects"));
      if (list == null) {
         list = template.consumeEffects();
      }

      consumable.addEffects(list);
      itemBuilder.setConsumableComponent(consumable.build());
   }

   @Nullable
   public Consumable consumableComponent(@Nullable ItemStack itemStack) {
      return itemStack != null ? (Consumable)itemStack.getData(DataComponentTypes.CONSUMABLE) : null;
   }

   public void consumableComponent(@NotNull ItemStack itemStack, @Nullable Object consumable) {
      DataComponentType.Valued valued = DataComponentTypes.CONSUMABLE;
      Consumable consumable2 = consumable instanceof Consumable ? (Consumable)consumable : null;
      if ((consumable instanceof Consumable ? (Consumable)consumable : null) != null) {
         itemStack.setData(valued, consumable2);
      }
   }

   public void canPlaceOnComponent(@NotNull ItemBuilder itemBuilder, @NotNull List<? extends ConfigurationSection> predicateSections) {
      itemBuilder.setCanPlaceOn(predicateSections);
   }

   public void canPlaceOnComponent(@NotNull ItemStack itemStack, @Nullable Object predicate) {
      Object obj = predicate;
      if (!(predicate instanceof List)) {
         obj = null;
      }

      List list = (List)obj;
      if (list != null) {
         List sections = list;
         net.minecraft.world.item.ItemStack handle = Companion.toCraftStack(itemStack).handle;
         net.minecraft.core.component.DataComponentType dataComponentType = DataComponents.CAN_PLACE_ON;
         AdventureModePredicate adventureModePredicate = this.adventureModePredicate(sections);
         if (adventureModePredicate != null) {
            handle.set(dataComponentType, adventureModePredicate);
         }
      }
   }

   public void canBreakComponent(@NotNull ItemBuilder itemBuilder, @NotNull List<? extends ConfigurationSection> predicateSections) {
      itemBuilder.setCanBreak(predicateSections);
   }

   public void canBreakComponent(@NotNull ItemStack itemStack, @Nullable Object predicate) {
      Object obj = predicate;
      if (!(predicate instanceof List)) {
         obj = null;
      }

      List list = (List)obj;
      if (list != null) {
         List sections = list;
         net.minecraft.world.item.ItemStack handle = Companion.toCraftStack(itemStack).handle;
         net.minecraft.core.component.DataComponentType dataComponentType = DataComponents.CAN_BREAK;
         AdventureModePredicate adventureModePredicate = this.adventureModePredicate(sections);
         if (adventureModePredicate != null) {
            handle.set(dataComponentType, adventureModePredicate);
         }
      }
   }

   private final AdventureModePredicate adventureModePredicate(List<? extends ConfigurationSection> sections) {
      DefaultedRegistry defaultedRegistry = BuiltInRegistries.BLOCK;
      DefaultedRegistry blockRegistry = defaultedRegistry;
      List predicates = (List)(new ArrayList());

      for(ConfigurationSection section : sections) {
         Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle(section, "nexo_block", (String)null);
         int value7 = 0;

         for(Object element : iterable) {
            String id = (String)element;
            int value = 0;
            BlockData blockData2 = NexoBlocks.blockData(id);
            if (blockData2 == null) {
               Logs.logWarn("Could not resolve nexo_block <" + id + "> for can_place_on/can_break", false);
            } else {
               BlockData blockData = blockData2;
               Collection collection = (Collection)predicates;
               BlockState state = ((CraftBlockData)blockData).getState();
               collection.add(this.blockPredicate(state));
            }
         }

         List blocks = (List)(new ArrayList());
         Iterable iterable2 = (Iterable)NexoYamlKt.stringListOrSingle(section, "block", (String)null);

         for(Object obj : iterable2) {
            String key = (String)obj;
            int value2 = 0;
            Identifier identifier2 = Identifier.parse(StringsKt.removePrefix(key, (CharSequence)"#"));
            Identifier identifier = identifier2;
            TagKey tagKey2 = TagKey.create(Registries.BLOCK, identifier);
            TagKey tagKey = tagKey2;
            if (blockRegistry.get(tagKey).isPresent()) {
               ((Collection)predicates).add(Builder.block().of((HolderGetter)blockRegistry, tagKey).build());
            } else {
               Optional optional = blockRegistry.getOptional(identifier);
               Block orNull = (Block)OptionalsKt.getOrNull(optional);
               if (orNull != null) {
                  Block p0 = orNull;
                  int value3 = 0;
                  blocks.add(p0);
               } else {
                  Logs.logWarn("Could not resolve block <" + key + "> for can_place_on/can_break", false);
               }
            }
         }

         iterable2 = (Iterable)blocks;
         if (CollectionsKt.count(iterable2) > 0) {
            List it = (List)iterable2;
            int value4 = 0;
            BlockPredicate.Builder builder2 = Builder.block().of((HolderGetter)blockRegistry, (Collection)blocks);
            BlockPredicate.Builder builder = builder2;
            StatePropertiesPredicate.Builder builder3 = this.stateProperties(NexoYamlKt.section(section, "state"), (Block)CollectionsKt.first(blocks));
            if (builder3 != null) {
               StatePropertiesPredicate.Builder p0 = builder3;
               int value5 = 0;
               builder.setProperties(p0);
            }

            ((Collection)predicates).add(builder.build());
         }
      }

      Iterable iterable3 = (Iterable)predicates;
      AdventureModePredicate adventureModePredicate;
      if (CollectionsKt.count(iterable3) > 0) {
         List p0 = (List)iterable3;
         int value6 = 0;
         adventureModePredicate = new AdventureModePredicate(p0);
      } else {
         adventureModePredicate = null;
      }

      return adventureModePredicate;
   }

   private final BlockPredicate blockPredicate(BlockState state) {
      BlockPredicate.Builder block = Builder.block();
      HolderGetter holderGetter = (HolderGetter)BuiltInRegistries.BLOCK;
      Block[] blocks = new Block[]{state.getBlock()};
      block = block.of(holderGetter, blocks);
      BlockPredicate.Builder builder = block;
      Collection collection = state.getProperties();
      if (!collection.isEmpty()) {
         StatePropertiesPredicate.Builder builder2 = net.minecraft.advancements.predicates.StatePropertiesPredicate.Builder.properties();
         StatePropertiesPredicate.Builder properties = builder2;
         Collection collection2 = state.getProperties();
         Iterable iterable = (Iterable)collection2;
         int value2 = 0;

         for(Object element : iterable) {
            Property property = (Property)element;
            int value = 0;
            properties.hasProperty(property, this.propertyValue(state, property));
         }

         builder.setProperties(properties);
      }

      BlockPredicate blockPredicate = builder.build();
      return blockPredicate;
   }

   private final StatePropertiesPredicate.Builder stateProperties(ConfigurationSection stateSection, Block block) {
      if (stateSection == null) {
         return null;
      } else {
         StateDefinition stateDefinition2 = block.getStateDefinition();
         StateDefinition stateDefinition = stateDefinition2;
         StatePropertiesPredicate.Builder builder = net.minecraft.advancements.predicates.StatePropertiesPredicate.Builder.properties();
         StatePropertiesPredicate.Builder properties = builder;
         boolean any = false;
         Set keys = stateSection.getKeys(false);
         Iterable iterable = (Iterable)keys;
         int value3 = 0;

         for(Object element : iterable) {
            String propertyName = (String)element;
            int value = 0;
            Property property2 = stateDefinition.getProperty(propertyName);
            if (property2 == null) {
               ItemUtils itemUtils = this;
               int value2 = 0;
               Logs.logWarn("Unknown blockstate-property <" + propertyName + "> for block <" + BuiltInRegistries.BLOCK.getKey(block) + ">", false);
            } else {
               Property property = property2;
               String string = stateSection.getString(propertyName);
               if (string != null) {
                  properties.hasProperty(property, string);
                  any = true;
               }
            }
         }

         return any ? properties : null;
      }
   }

   private final <T extends Comparable<T>> String propertyValue(BlockState state, Property<T> property) {
      String name = property.getName(state.getValue(property));
      return name;
   }

   public void repairableComponent(@NotNull ItemBuilder itemBuilder, @NotNull List<? extends Key> repairableWith) {
      Registry registry2 = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
      Registry registry = registry2;
      Iterable iterable = (Iterable)repairableWith;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         Key key = (Key)element;
         int value = 0;
         ItemUtils itemUtils = this;

         Object obj2;
         try {
            ItemUtils itemUtils2 = itemUtils;
            int value2 = 0;
            Iterable iterable2 = (Iterable)registry.getTag(io.papermc.paper.registry.tag.TagKey.create(RegistryKey.ITEM, key)).values();
            Collection collection = (Collection)(new ArrayList());
            int value6 = 0;

            for(Object obj3 : iterable2) {
               int value3 = 0;
               TypedKey p0 = (TypedKey)obj3;
               int value4 = 0;
               ItemType itemType = (ItemType)registry.get(p0);
               if (itemType != null) {
                  Object obj4 = itemType;
                  int value5 = 0;
                  collection.add(obj4);
               }
            }

            obj2 = ResultShim.ok((List)collection);
         } catch (Throwable e) {
            obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         Object obj = obj2;
         List emptyList = CollectionsKt.emptyList();
         Iterable list = (Iterable)CollectionsKt.plus((Collection)(ResultShim.isFailure(obj) ? emptyList : obj), registry.get(key));
         CollectionsKt.addAll(destination, list);
      }

      List it = CollectionsKt.filterNotNull((Iterable)((List)destination));
      int value7 = 0;
      RegistryKeySet registryKeySet = RegistrySet.keySetFromValues(RegistryKey.ITEM, (Iterable)it);
      RegistryKeySet keySet = registryKeySet;
      itemBuilder.setRepairableComponent(Repairable.repairable(keySet));
   }

   @Nullable
   public Repairable repairableComponent(@Nullable ItemStack itemStack) {
      return itemStack != null ? (Repairable)itemStack.getData(DataComponentTypes.REPAIRABLE) : null;
   }

   public void repairableComponent(@NotNull ItemStack itemStack, @Nullable Object repairable) {
      DataComponentType.Valued valued = DataComponentTypes.REPAIRABLE;
      Repairable repairable2 = repairable instanceof Repairable ? (Repairable)repairable : null;
      if ((repairable instanceof Repairable ? (Repairable)repairable : null) != null) {
         itemStack.setData(valued, repairable2);
      }
   }

   @Nullable
   public Map<String, String> blockstateComponent(@Nullable ItemStack itemStack) {
      BlockItemDataProperties blockItemDataProperties = itemStack != null ? (BlockItemDataProperties)itemStack.getData(DataComponentTypes.BLOCK_DATA) : null;
      PaperBlockItemDataProperties paperBlockItemDataProperties = blockItemDataProperties instanceof PaperBlockItemDataProperties ? (PaperBlockItemDataProperties)blockItemDataProperties : null;
      Map map;
      if ((blockItemDataProperties instanceof PaperBlockItemDataProperties ? (PaperBlockItemDataProperties)blockItemDataProperties : null) != null) {
         BlockItemStateProperties impl = paperBlockItemDataProperties.impl();
         if (impl != null) {
            map = impl.properties();
            return map;
         }
      }

      map = null;
      return map;
   }

   public void blockstateComponent(@NotNull ItemStack itemStack, @Nullable Map<String, String> blockstates) {
      DataComponentType.Valued valued = DataComponentTypes.BLOCK_DATA;
      PaperBlockItemDataProperties paperBlockItemDataProperties;
      BlockItemStateProperties blockItemStateProperties;
      if (blockstates != null) {
         blockItemStateProperties = new BlockItemStateProperties(blockstates);
         paperBlockItemDataProperties = new PaperBlockItemDataProperties(blockItemStateProperties);
         itemStack.setData(valued, paperBlockItemDataProperties);
      }
   }

   public void customDataComponent(@NotNull ItemStack itemStack, @NotNull Map<String, ? extends Object> customData) {
      if (!customData.isEmpty()) {
         net.minecraft.world.item.ItemStack itemStack_12;
         CompoundTag compoundTag;
         block: {
            itemStack_12 = Companion.toCraftStack(itemStack).handle;
            CustomData customData2 = (CustomData)itemStack_12.get(DataComponents.CUSTOM_DATA);
            if (customData2 != null) {
               compoundTag = customData2.copyTag();
               if (compoundTag != null) {
                  break block;
               }
            }

            compoundTag = new CompoundTag();
         }

         CompoundTag existingData = compoundTag;
         LinkedHashMap result = new LinkedHashMap();

         for(Map.Entry entry : customData.entrySet()) {
            Object it = entry.getValue();
            int value = 0;
            if (it == null) {
               result.put(entry.getKey(), entry.getValue());
            }
         }

         Set removeKeys = ((Map)result).keySet();
         LinkedHashMap linkedHashMap = new LinkedHashMap();

         for(Map.Entry entry2 : customData.entrySet()) {
            Object it = entry2.getValue();
            int value2 = 0;
            if (it != null) {
               linkedHashMap.put(entry2.getKey(), entry2.getValue());
            }
         }

         Map map = (Map)linkedHashMap;
         CompoundTag newNbt = this.toCompound(map);
         existingData.merge(newNbt);
         Iterable iterable = (Iterable)removeKeys;
         int value4 = 0;

         for(Object element : iterable) {
            String p0 = (String)element;
            int value3 = 0;
            existingData.remove(p0);
         }

         itemStack_12.set(DataComponents.CUSTOM_DATA, CustomData.of(existingData));
      }
   }

   @Nullable
   public Object deathProtectionComponent(@Nullable ItemStack itemStack) {
      return itemStack != null ? (DeathProtection)itemStack.getData(DataComponentTypes.DEATH_PROTECTION) : null;
   }

   public void deathProtectionComponent(@NotNull ItemBuilder itemBuilder, @NotNull ConfigurationSection deathProtectionSection) {
      DeathProtection.Builder builder = DeathProtection.deathProtection();
      DeathProtection.Builder deathProtection = builder;
      DeathProtection data = (DeathProtection)ItemStack.of(itemBuilder.getType()).getData(DataComponentTypes.DEATH_PROTECTION);
      if (data == null) {
         Object obj = DeathProtection.deathProtection().build();
         data = (DeathProtection)obj;
      }

      DeathProtection template = data;
      ConfigurationSection configurationSection = NexoYamlKt.section(deathProtectionSection, "death_effects");
      if (configurationSection == null) {
         configurationSection = deathProtectionSection;
      }

      ConfigurationSection effectsSection = configurationSection;
      List list = this.consumeEffectsFromSection(effectsSection);
      if (list == null) {
         list = template.deathEffects();
      }

      List effects = list;
      deathProtection.addEffects(effects);
      itemBuilder.setDeathProtectionComponent(deathProtection.build());
   }

   public void deathProtectionComponent(@NotNull ItemStack itemStack, @Nullable Object deathProtection) {
      DataComponentType.Valued valued = DataComponentTypes.DEATH_PROTECTION;
      DeathProtection deathProtection2 = deathProtection instanceof DeathProtection ? (DeathProtection)deathProtection : null;
      if ((deathProtection instanceof DeathProtection ? (DeathProtection)deathProtection : null) != null) {
         itemStack.setData(valued, deathProtection2);
      }
   }

   @Nullable
   public List<ConsumeEffect> consumeEffectsFromSection(@Nullable ConfigurationSection effectsSection) {
      if (effectsSection == null) {
         return null;
      } else {
         List consumeEffects = (List)(new ArrayList());
         NexoYamlKt.forEach(effectsSection, ((a0_, a1_) -> ItemUtils.consumeEffectsFromSection$lambda$0(consumeEffects, this, a0_, a1_)));
         return consumeEffects;
      }
   }

   public void dumpItem(@NotNull CommandSender sender, @NotNull ItemStack itemStack, boolean includeAllComponents) {
      net.minecraft.world.item.ItemStack itemStack_9 = CraftItemStack.asNMSCopy(itemStack);
      TextComponent.Builder text = Component.text();
      TextComponent.Builder visualOutput = text;
      StringBuilder itemCommandBuilder = new StringBuilder();
      String str = ((ResourceKey)itemStack_9.typeHolder().unwrapKey().orElseThrow()).identifier().toString();
      String itemName = str;
      itemCommandBuilder.append(itemName);
      visualOutput.append((Component)Component.text(itemName, (TextColor)NamedTextColor.YELLOW));
      Set referencedComponentTypes = Collections.newSetFromMap((Map)(new IdentityHashMap()));
      DataComponentPatch componentsPatch = itemStack_9.getComponentsPatch();
      DataComponentPatch patch = componentsPatch;
      Collection collection = (Collection)referencedComponentTypes;
      DataComponentPatch.SplitResult split = patch.split();
      collection.addAll(split.added().keySet());
      collection.addAll(split.removed());
      DataComponentMap components = itemStack_9.getItem().components();
      DataComponentMap prototype = components;
      if (includeAllComponents) {
         Set keySet = prototype.keySet();
         referencedComponentTypes.addAll((Collection)keySet);
      }

      Server server = Bukkit.getServer();
      net.minecraft.core.RegistryAccess.Frozen registryAccess = ((CraftServer)server).getServer().registryAccess();
      net.minecraft.core.RegistryAccess.Frozen access = registryAccess;
      RegistryOps registryOps = access.createSerializationContext((DynamicOps)NbtOps.INSTANCE);
      RegistryOps ops = registryOps;
      net.minecraft.core.Registry registry2 = access.lookupOrThrow(Registries.DATA_COMPONENT_TYPE);
      net.minecraft.core.Registry registry = registry2;
      List componentComponents = (List)(new ArrayList());
      List commandComponents = (List)(new ArrayList());

      for(net.minecraft.core.component.DataComponentType type : (java.lang.Iterable<net.minecraft.core.component.DataComponentType>)(java.lang.Iterable)referencedComponentTypes) {
         String str2 = ((ResourceKey)registry.getResourceKey(type).orElseThrow()).identifier().getPath();
         String path = str2;
         Optional patchedValue = split.removed().contains(type) ? Optional.empty() : (split.added().has(type) ? Optional.of(split.added().get(type)) : null);
         TypedDataComponent prototypeValue = prototype.getTyped(type);
         if (patchedValue != null) {
            if (patchedValue.isEmpty()) {
               ComponentBuilder componentBuilder = ((TextComponent.Builder)Component.text().append((Component)Component.text('!', (TextColor)NamedTextColor.RED))).append((Component)Component.text(path, (TextColor)NamedTextColor.AQUA));
               TextComponent.Builder component = (TextComponent.Builder)componentBuilder;
               ((Collection)componentComponents).add(component);
               ((Collection)commandComponents).add("!" + path);
            } else {
               Object obj = patchedValue.get();
               Object value = obj;
               Tag serialized = this.encodeComponentValue(type, (DynamicOps)ops, value);
               this.writeComponentValue((Function1<ComponentLike, Unit>)((ComponentLike p0) -> {
((List)componentComponents).add(p0);
return Unit.INSTANCE;
}), (Function1<String, Unit>)((String p0) -> {
((List)commandComponents).add(p0);
return Unit.INSTANCE;
}), path, serialized);
            }
         } else if (includeAllComponents && prototypeValue != null) {
            Tag serialized = (Tag)prototypeValue.encodeValue((DynamicOps)ops).getOrThrow();
            Function1 function1 = (Function1<ComponentLike, Unit>)((ComponentLike p0) -> {
((List)componentComponents).add(p0);
return Unit.INSTANCE;
});
            Function1 function12 = (Function1<String, Unit>)((String p0) -> {
((List)commandComponents).add(p0);
return Unit.INSTANCE;
});
            this.writeComponentValue(function1, function12, path, serialized);
         }
      }

      if (!componentComponents.isEmpty()) {
         TextComponent textComponent = Component.text("[", TextColor.color(8982734));
         TextComponent prefix = textComponent;
         textComponent = Component.text("]", TextColor.color(8982734));
         TextComponent suffix = textComponent;
         JoinConfiguration joinConfiguration = JoinConfiguration.separator((ComponentLike)Component.text(",", (TextColor)NamedTextColor.GRAY));
         JoinConfiguration join = joinConfiguration;
         Component component2 = Component.join(join, (Iterable)componentComponents);
         Component content = component2;
         Component[] components2 = new Component[]{prefix, content, suffix};
         visualOutput.append(components2);
         itemCommandBuilder.append("[" + CollectionsKt.joinToString((Iterable)commandComponents, (CharSequence)",", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null) + "]");
      }

      sender.sendMessage(((TextComponent)visualOutput.build()).compact());
   }

   private final <T> Tag encodeComponentValue(net.minecraft.core.component.DataComponentType<T> type, DynamicOps<Tag> ops, Object value) {
      Codec codecOrThrow = type.codecOrThrow();
      Codec codec = codecOrThrow;
      Object orThrow = codec.encodeStart(ops, value).getOrThrow();
      return (Tag)orThrow;
   }

   private final void writeComponentValue(Function1<? super Component, Unit> visualOutput, Function1<? super String, Unit> commandOutput, String path, Tag serialized) {
      ComponentLike[] componentLikes = new ComponentLike[]{Component.text(path, TextColor.color(16744407)), Component.text("=", (TextColor)NamedTextColor.WHITE), PaperAdventure.asAdventure(NbtUtils.toPrettyComponent(serialized))};
      TextComponent textComponent = Component.textOfChildren(componentLikes);
      visualOutput.invoke(textComponent);
      commandOutput.invoke(path + "=" + (new SnbtPrinterTagVisitor("", 0, (List)(new ArrayList()))).visit(serialized));
   }

   private final CompoundTag toCompound(Map<String, ? extends Object> map) {
      CompoundTag compound = new CompoundTag();
      int value3 = 0;

      for(Map.Entry element : map.entrySet()) {
         int value2 = 0;
         String key = (String)element.getKey();
         Object value = element.getValue();
         compound.put(key, this.toTag(value));
      }

      return compound;
   }

   private final Tag toTag(Object obj) {
      Tag tag;
      if (obj instanceof String) {
         StringTag stringTag = StringTag.valueOf((String)obj);
         tag = (Tag)stringTag;
      } else if (obj instanceof Boolean) {
         ByteTag byteTag = ByteTag.valueOf((Boolean)obj);
         tag = (Tag)byteTag;
      } else if (obj instanceof Number) {
         tag = this.toTag((Number)obj);
      } else if (obj instanceof ConfigurationSection) {
         CompoundTag compoundTag = new CompoundTag();
         CompoundTag compoundTag3 = compoundTag;
         int value = 0;
         Set keys = ((ConfigurationSection)obj).getKeys(false);
         Iterable iterable = (Iterable)keys;
         int value11 = 0;

         for(Object element : iterable) {
            String key = (String)element;
            int value2 = 0;
            Object v = ((ConfigurationSection)obj).get(key);
            if (v != null) {
               compoundTag3.put(key, this.toTag(v));
            }
         }

         tag = (Tag)compoundTag;
      } else if (obj instanceof Map) {
         CompoundTag compoundTag2 = new CompoundTag();
         CompoundTag compoundTag4 = compoundTag2;
         int value3 = 0;
         Map map = (Map)obj;
         int value12 = 0;

         for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            int value4 = 0;
            Object k = entry.getKey();
            Object v = entry.getValue();
            if (k instanceof String && v != null) {
               compoundTag4.put((String)k, this.toTag(v));
            }
         }

         tag = (Tag)compoundTag2;
      } else if (obj instanceof Collection) {
         Iterable iterable2 = (Iterable)obj;
         boolean flag;
         if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
            flag = true;
         } else {
            Iterator iterator2 = iterable2.iterator();

            while(true) {
               if (!iterator2.hasNext()) {
                  flag = true;
                  break;
               }

               Object obj2 = iterator2.next();
               int value5 = 0;
               if (!(obj2 instanceof Integer)) {
                  flag = false;
                  break;
               }
            }
         }

         CollectionTag collectionTag;
         if (flag) {
            iterable2 = (Iterable)obj;
            Collection destination = (Collection)(new ArrayList());

            for(Object obj3 : iterable2) {
               if (obj3 instanceof Integer) {
                  destination.add(obj3);
               }
            }

            int[] ints = CollectionsKt.toIntArray((Collection)((List)destination));
            collectionTag = (CollectionTag)(new IntArrayTag(ints));
         } else {
            iterable2 = (Iterable)obj;
            boolean flag2;
            if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
               flag2 = true;
            } else {
               Iterator iterator3 = iterable2.iterator();

               while(true) {
                  if (!iterator3.hasNext()) {
                     flag2 = true;
                     break;
                  }

                  Object obj4 = iterator3.next();
                  int value6 = 0;
                  if (!(obj4 instanceof Byte)) {
                     flag2 = false;
                     break;
                  }
               }
            }

            if (flag2) {
               iterable2 = (Iterable)obj;
               Collection collection = (Collection)(new ArrayList());

               for(Object obj5 : iterable2) {
                  if (obj5 instanceof Byte) {
                     collection.add(obj5);
                  }
               }

               byte[] bs = CollectionsKt.toByteArray((Collection)((List)collection));
               collectionTag = (CollectionTag)(new ByteArrayTag(bs));
            } else {
               iterable2 = (Iterable)obj;
               if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
                  flag2 = true;
               } else {
                  Iterator iterator4 = iterable2.iterator();

                  while(true) {
                     if (!iterator4.hasNext()) {
                        flag2 = true;
                        break;
                     }

                     Object obj6 = iterator4.next();
                     int value13 = 0;
                     if (!(obj6 instanceof Long)) {
                        flag2 = false;
                        break;
                     }
                  }
               }

               if (flag2) {
                  iterable2 = (Iterable)obj;
                  Collection collection2 = (Collection)(new ArrayList());

                  for(Object obj7 : iterable2) {
                     if (obj7 instanceof Long) {
                        collection2.add(obj7);
                     }
                  }

                  long[] longs = CollectionsKt.toLongArray((Collection)((List)collection2));
                  collectionTag = (CollectionTag)(new LongArrayTag(longs));
               } else {
                  iterable2 = (Iterable)obj;
                  if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
                     flag2 = true;
                  } else {
                     Iterator iterator = iterable2.iterator();

                     while(true) {
                        if (!iterator.hasNext()) {
                           flag2 = true;
                           break;
                        }

                        Object obj8 = iterator.next();
                        int value14 = 0;
                        if (!(obj8 instanceof String)) {
                           flag2 = false;
                           break;
                        }
                     }
                  }

                  if (flag2) {
                     ListTag listTag = new ListTag();
                     ListTag listTag2 = listTag;
                     int value7 = 0;
                     Iterable iterable3 = (Iterable)obj;
                     int value15 = 0;
                     Collection collection3 = (Collection)(new ArrayList());

                     for(Object obj9 : iterable3) {
                        if (obj9 instanceof String) {
                           collection3.add(obj9);
                        }
                     }

                     iterable3 = (Iterable)((List)collection3);
                     value15 = 0;

                     for(Object obj10 : iterable3) {
                        String it = (String)obj10;
                        int value8 = 0;
                        listTag2.add(StringTag.valueOf(it));
                     }

                     collectionTag = (CollectionTag)listTag;
                  } else {
                     ListTag listTag3 = new ListTag();
                     int value9 = 0;
                     Iterable iterable4 = (Iterable)CollectionsKt.filterNotNull((Iterable)obj);
                     Collection collection4 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable4, 10)));

                     for(Object item : iterable4) {
                        int value10 = 0;
                        collection4.add(this.toTag(item));
                     }

                     List list = (List)collection4;
                     collectionTag = (CollectionTag)listTag3;
                  }
               }
            }
         }

         tag = (Tag)collectionTag;
      } else {
         StringTag stringTag2 = StringTag.valueOf(obj.toString());
         tag = (Tag)stringTag2;
      }

      return tag;
   }

   private final Tag toTag(Number number) {
      Tag tag;
      if (number instanceof Integer) {
         IntTag intTag = IntTag.valueOf(number.intValue());
         tag = (Tag)intTag;
      } else if (number instanceof Long) {
         LongTag longTag = LongTag.valueOf(number.longValue());
         tag = (Tag)longTag;
      } else if (number instanceof Float) {
         FloatTag floatTag = FloatTag.valueOf(number.floatValue());
         tag = (Tag)floatTag;
      } else if (number instanceof Double) {
         DoubleTag doubleTag = DoubleTag.valueOf(number.doubleValue());
         tag = (Tag)doubleTag;
      } else if (number instanceof Short) {
         ShortTag shortTag = ShortTag.valueOf(number.shortValue());
         tag = (Tag)shortTag;
      } else if (number instanceof Byte) {
         ByteTag byteTag = ByteTag.valueOf(number.byteValue());
         tag = (Tag)byteTag;
      } else {
         StringTag stringTag = StringTag.valueOf(number.toString());
         tag = (Tag)stringTag;
      }

      return tag;
   }

   private static final Unit asColorable$lambda$0(ItemStack itemStack, Color value) {
      PotionContents data = (PotionContents)itemStack.getData(DataComponentTypes.POTION_CONTENTS);
      if (data == null) {
         return Unit.INSTANCE;
      } else {
         PotionContents contents = data;
         PotionContents.Builder builder = PotionContents.potionContents().customColor(value).potion(contents.potion()).addCustomEffects(contents.customEffects()).customName(contents.customName());
         PotionContents.Builder colored = builder;
         itemStack.setData(DataComponentTypes.POTION_CONTENTS, (DataComponentBuilder)colored);
         return Unit.INSTANCE;
      }
   }

   private static final Unit consumeEffectsFromSection$lambda$0(List consumeEffects, ItemUtils itemUtils, String key, Object value) {
      if (Intrinsics.areEqual(key, "APPLY_EFFECTS") && value instanceof ConfigurationSection) {
         Map map = NexoYamlKt.childSections((ConfigurationSection)value);
         int value10 = 0;

         for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            String effectKey = (String)element.getKey();
            ConfigurationSection effectProperties = (ConfigurationSection)element.getValue();

            Object ambient;
            try {
               int value2 = 0;
               ambient = ResultShim.ok(Identifier.parse(effectKey));
            } catch (Throwable e) {
               ambient = ResultShim.ok(ResultKt.createFailure(e));
            }

            Object obj = ambient;
            Throwable throwable = ResultShim.exceptionOrNull(ambient);
            if (throwable != null) {
               Throwable it = throwable;
               int p0 = 0;
               Logs.logError("Invalid potion effect: " + effectKey + ", in consumable-property!", false);
               if (Settings.DEBUG.toBool()) {
                  Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
               }
            }

            Identifier p0 = (Identifier)(ResultShim.isFailure(obj) ? null : obj);
            if (p0 != null) {
               net.minecraft.core.Registry registry = BuiltInRegistries.MOB_EFFECT;
               net.minecraft.core.Registry it = registry;
               int probability = 0;
               Optional optional = it.get(p0);
               if (optional != null) {
                  Holder.Reference it_6 = (Holder.Reference)OptionalsKt.getOrNull(optional);
                  if (it_6 != null) {
                     Holder.Reference reference = it_6;
                     int p0_5 = DurationUtilsKt.getTicks_LRDsOJo(NexoYamlKt.duration(effectProperties, "duration", DurationUnit.SECONDS));
                     int value3 = effectProperties.getInt("amplifier", 0);
                     boolean it_4 = effectProperties.getBoolean("ambient", true);
                     boolean particles = effectProperties.getBoolean("show_particles", true);
                     boolean icon = effectProperties.getBoolean("show_icon", true);
                     float value4 = (float)effectProperties.getDouble("probability", (double)1.0F);
                     MobEffectInstance it_3 = new MobEffectInstance((Holder)reference, p0_5, value3, it_4, particles, icon);
                     ApplyStatusEffectsConsumeEffect it_2 = new ApplyStatusEffectsConsumeEffect(it_3, value4);
                     ((Collection)consumeEffects).add(new PaperApplyStatusEffects(it_2));
                  }
               }
            }
         }
      } else if (Intrinsics.areEqual(key, "REMOVE_EFFECTS") && value instanceof List) {
         net.minecraft.core.Holder.Reference reference2;
         Iterable iterable = (Iterable)value;
         int value11 = 0;
         Collection destination = (Collection)(new ArrayList());

         for(Object obj2 : iterable) {
            if (obj2 instanceof String) {
               destination.add(obj2);
            }
         }

         iterable = (Iterable)((List)destination);
         value11 = 0;
         destination = (Collection)(new ArrayList());
         int value12 = 0;

         for(Object obj3 : iterable) {
            int value5 = 0;
            String effect = (String)obj3;
            int value6 = 0;

            Object instance;
            try {
               int applyEffect = 0;
               instance = ResultShim.ok(Identifier.parse(effect));
            } catch (Throwable ex) {
               instance = ResultShim.ok(ResultKt.createFailure(ex));
            }

            Object mobEffect = instance;
            Throwable throwable2 = ResultShim.exceptionOrNull(instance);
            if (throwable2 != null) {
               Throwable it = throwable2;
               int value7 = 0;
               Logs.logError("Invalid potion effect: " + effect + ", in consumable-property!", false);
               if (Settings.DEBUG.toBool()) {
                  Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
               }
            }

            block: {
               Identifier p0 = (Identifier)(ResultShim.isFailure(mobEffect) ? null : mobEffect);
               if (p0 != null) {
                  net.minecraft.core.Registry registry2 = BuiltInRegistries.MOB_EFFECT;
                  net.minecraft.core.Registry registry3 = registry2;
                  int value8 = 0;
                  Optional optional2 = registry3.get(p0);
                  if (optional2 != null) {
                     reference2 = (Holder.Reference)OptionalsKt.getOrNull(optional2);
                     break block;
                  }
               }

               reference2 = null;
            }

            if (reference2 != null) {
               Object obj4 = reference2;
               int value9 = 0;
               destination.add(obj4);
            }
         }

         List soundEffect = (List)destination;
         int value13 = 0;
         List soundKey = !soundEffect.isEmpty() ? soundEffect : null;
         if (soundKey != null) {
            value13 = 0;
            RemoveStatusEffectsConsumeEffect removeEffect = new RemoveStatusEffectsConsumeEffect((HolderSet)HolderSet.direct(soundKey));
            ((Collection)consumeEffects).add(new PaperRemoveStatusEffects(removeEffect));
         }
      } else if (Intrinsics.areEqual(key, "CLEAR_ALL_EFFECTS")) {
         ((Collection)consumeEffects).add(new PaperClearAllStatusEffects(new ClearAllStatusEffectsConsumeEffect()));
      } else if (Intrinsics.areEqual(key, "TELEPORT_RANDOMLY") && value instanceof ConfigurationSection) {
         TeleportRandomlyConsumeEffect teleportEffect = new TeleportRandomlyConsumeEffect((float)((ConfigurationSection)value).getDouble("diameter", (double)16.0F), true);
         ((Collection)consumeEffects).add(new PaperTeleportRandomly(teleportEffect));
      } else if (Intrinsics.areEqual(key, "PLAY_SOUND") && value instanceof ConfigurationSection) {
         Key key2 = NexoYamlKt.key((ConfigurationSection)value, "sound");
         if (key2 == null) {
            return Unit.INSTANCE;
         }

         Key soundKey = key2;
         Sound sound = (Sound)RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).get(soundKey);
         if (sound == null) {
            return Unit.INSTANCE;
         }

         int effectKey = 0;
         Holder holder = CraftSound.bukkitToMinecraftHolder(sound);
         if (holder == null) {
            return Unit.INSTANCE;
         }

         int effectProperties = 0;
         PlaySoundConsumeEffect playSoundConsumeEffect = new PlaySoundConsumeEffect(holder);
         ((Collection)consumeEffects).add(new PaperPlaySound(playSoundConsumeEffect));
      }

      return Unit.INSTANCE;
   }

   static {
      Companion companion = Companion;

      Object obj2;
      try {
         int value = 0;
         Class[] clazzs = new Class[]{ItemStack.class};
         Method declaredMethod = CraftItemStack.class.getDeclaredMethod("getCraftStack", clazzs);
         int value2 = 0;
         declaredMethod.setAccessible(true);
         obj2 = ResultShim.ok(declaredMethod);
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = UtilsKt.printOnFailure(obj2, false);
      getCraftStackMethod = (Method)(ResultShim.isFailure(obj) ? null : obj);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\n\u0010\u0006\u001a\u00020\u0007*\u00020\bR\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/nms/v26_R2/ItemUtils$Companion;", "", "<init>", "()V", "getCraftStackMethod", "Ljava/lang/reflect/Method;", "toCraftStack", "Lorg/bukkit/craftbukkit/inventory/CraftItemStack;", "Lorg/bukkit/inventory/ItemStack;", "com.nexomc:v26_R2"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final CraftItemStack toCraftStack(@NotNull ItemStack itemStack) {
         Method method = ItemUtils.getCraftStackMethod;
         Object[] objs = new Object[]{itemStack};
         Object obj;
         try {
            obj = method.invoke((Object)null, objs);
         } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
         }

         return (CraftItemStack)obj;
      }
   }
}

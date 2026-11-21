package com.nexomc.nexo.nms.v1_21_R5;

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
import io.papermc.paper.datacomponent.item.MapItemColor;
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
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.advancements.critereon.BlockPredicate.Builder;
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
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
   d1 = {"\u0000Ð\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000f\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0004\n\u0002\b\u0002\u0018\u0000 T2\u00020\u0001:\u0001TB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0012\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u001a\u0010\b\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016J\u0018\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u0007H\u0016J\u0018\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0014\u0010\u000e\u001a\u0004\u0018\u00010\u00132\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u001a\u0010\u000e\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0016J\u001e\u0010\u0016\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00120\u0018H\u0016J\u001a\u0010\u0016\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0019\u001a\u0004\u0018\u00010\u0015H\u0016J\u001e\u0010\u001a\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00120\u0018H\u0016J\u001a\u0010\u001a\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0019\u001a\u0004\u0018\u00010\u0015H\u0016J\u0018\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00120\u0018H\u0002J\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!H\u0002J\u001c\u0010\"\u001a\u0004\u0018\u00010#2\b\u0010$\u001a\u0004\u0018\u00010\u00122\u0006\u0010%\u001a\u00020&H\u0002J.\u0010'\u001a\u00020(\"\u000e\b\u0000\u0010)*\b\u0012\u0004\u0012\u0002H)0*2\u0006\u0010 \u001a\u00020!2\f\u0010+\u001a\b\u0012\u0004\u0012\u0002H)0,H\u0002J\u001e\u0010-\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010.\u001a\b\u0012\u0004\u0012\u00020/0\u0018H\u0016J\u0014\u0010-\u001a\u0004\u0018\u0001002\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u001a\u0010-\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u00101\u001a\u0004\u0018\u00010\u0015H\u0016J \u00102\u001a\u0010\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u00020(\u0018\u0001032\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J&\u00102\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\u0014\u00104\u001a\u0010\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u00020(\u0018\u000103H\u0016J&\u00105\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\u0014\u00106\u001a\u0010\u0012\u0004\u0012\u00020(\u0012\u0006\u0012\u0004\u0018\u00010\u001503H\u0016J\u0014\u00107\u001a\u0004\u0018\u00010\u00152\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u0018\u00107\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u00108\u001a\u00020\u0012H\u0016J\u001a\u00107\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\u00109\u001a\u0004\u0018\u00010\u0015H\u0016J\u001a\u0010:\u001a\n\u0012\u0004\u0012\u00020;\u0018\u00010\u00182\b\u0010<\u001a\u0004\u0018\u00010\u0012H\u0016J \u0010=\u001a\u00020\n2\u0006\u0010>\u001a\u00020?2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010@\u001a\u00020AH\u0016J2\u0010B\u001a\u00020C\"\u0004\b\u0000\u0010)2\f\u0010D\u001a\b\u0012\u0004\u0012\u0002H)0E2\f\u0010F\u001a\b\u0012\u0004\u0012\u00020C0G2\u0006\u0010H\u001a\u00020\u0015H\u0002J@\u0010I\u001a\u00020\n2\u0012\u0010J\u001a\u000e\u0012\u0004\u0012\u00020L\u0012\u0004\u0012\u00020\n0K2\u0012\u0010M\u001a\u000e\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u00020\n0K2\u0006\u0010N\u001a\u00020(2\u0006\u0010O\u001a\u00020CH\u0002J\u0018\u0010P\u001a\u00020Q*\u000e\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u00020\u001503H\u0002J\f\u0010R\u001a\u00020C*\u00020\u0015H\u0002J\f\u0010R\u001a\u00020C*\u00020SH\u0002¨\u0006U"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R5/ItemUtils;", "Lcom/nexomc/nexo/nms/IItemUtils;", "<init>", "()V", "asColorable", "Lcom/nexomc/nexo/utils/Colorable;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "paintingVariant", "Lorg/bukkit/Art;", "", "copyItemNBTTags", "oldItem", "newItem", "consumableComponent", "itemBuilder", "Lcom/nexomc/nexo/items/ItemBuilder;", "consumableSection", "Lorg/bukkit/configuration/ConfigurationSection;", "Lio/papermc/paper/datacomponent/item/Consumable;", "consumable", "", "canPlaceOnComponent", "predicateSections", "", "predicate", "canBreakComponent", "adventureModePredicate", "Lnet/minecraft/world/item/AdventureModePredicate;", "sections", "blockPredicate", "Lnet/minecraft/advancements/critereon/BlockPredicate;", "state", "Lnet/minecraft/world/level/block/state/BlockState;", "stateProperties", "Lnet/minecraft/advancements/critereon/StatePropertiesPredicate$Builder;", "stateSection", "block", "Lnet/minecraft/world/level/block/Block;", "propertyValue", "", "T", "", "property", "Lnet/minecraft/world/level/block/state/properties/Property;", "repairableComponent", "repairableWith", "Lnet/kyori/adventure/key/Key;", "Lio/papermc/paper/datacomponent/item/Repairable;", "repairable", "blockstateComponent", "", "blockstates", "customDataComponent", "customData", "deathProtectionComponent", "deathProtectionSection", "deathProtection", "consumeEffectsFromSection", "Lio/papermc/paper/datacomponent/item/consumable/ConsumeEffect;", "effectsSection", "dumpItem", "sender", "Lorg/bukkit/command/CommandSender;", "includeAllComponents", "", "encodeComponentValue", "Lnet/minecraft/nbt/Tag;", "type", "Lnet/minecraft/core/component/DataComponentType;", "ops", "Lcom/mojang/serialization/DynamicOps;", "value", "writeComponentValue", "visualOutput", "Lkotlin/Function1;", "Lnet/kyori/adventure/text/Component;", "commandOutput", "path", "serialized", "toCompound", "Lnet/minecraft/nbt/CompoundTag;", "toTag", "", "Companion", "com.nexomc:v1_21_R5"}
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
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      final DyedItemColor dyedColor = (DyedItemColor)itemStack.getData(DataComponentTypes.DYED_COLOR);
      final MapItemColor mapColor = (MapItemColor)itemStack.getData(DataComponentTypes.MAP_COLOR);
      final DyeColor baseColor = (DyeColor)itemStack.getData(DataComponentTypes.BASE_COLOR);
      final Function1 potionColorableSetLogic = ItemUtils::asColorable$lambda$0;
      return mapColor != null ? (Colorable)(new Colorable() {
         public Color getColor() {
            return mapColor.color();
         }

         public void setColor(Color value) {
            ItemStack $this$setOrReset$iv;
            DataComponentType.Valued type$iv;
            MapItemColor var12;
            block: {
               com.nexomc.nexo.utils.ItemUtils var2 = com.nexomc.nexo.utils.ItemUtils.INSTANCE;
               $this$setOrReset$iv = itemStack;
               DataComponentType.Valued var10000 = DataComponentTypes.MAP_COLOR;
               Intrinsics.checkNotNullExpressionValue(var10000, "MAP_COLOR");
               type$iv = var10000;
               if (value != null) {
                  MapItemColor.Builder var10 = MapItemColor.mapItemColor();
                  Intrinsics.checkNotNullExpressionValue(var10, "mapItemColor(...)");
                  MapItemColor.Builder var7 = var10;
                  int var9 = 0;
                  var10 = var7.color(value);
                  if (var10 != null) {
                     var12 = (MapItemColor)var10.build();
                     break block;
                  }
               }

               var12 = null;
            }

            Object value$iv = var12;
            if (value$iv != null) {
               $this$setOrReset$iv.setData(type$iv, value$iv);
            } else {
               $this$setOrReset$iv.resetData((DataComponentType)type$iv);
            }

         }

         public Color getPotionContentColor() {
            PotionContents var10000 = (PotionContents)itemStack.getData(DataComponentTypes.POTION_CONTENTS);
            return var10000 != null ? var10000.customColor() : null;
         }

         public void setPotionContentColor(Color value) {
            potionColorableSetLogic.invoke(value);
         }
      }) : (baseColor != null ? (Colorable)(new Colorable() {
         public Color getColor() {
            return baseColor.getColor();
         }

         public void setColor(Color value) {
            com.nexomc.nexo.utils.ItemUtils var2 = com.nexomc.nexo.utils.ItemUtils.INSTANCE;
            ItemStack $this$setOrReset$iv = itemStack;
            DataComponentType.Valued var10000 = DataComponentTypes.BASE_COLOR;
            Intrinsics.checkNotNullExpressionValue(var10000, "BASE_COLOR");
            DataComponentType.Valued type$iv = var10000;
            DyeColor var8;
            if (value != null) {
               int var7 = 0;
               var8 = DyeColor.getByColor(value);
            } else {
               var8 = null;
            }

            Object value$iv = var8;
            if (value$iv != null) {
               $this$setOrReset$iv.setData(type$iv, value$iv);
            } else {
               $this$setOrReset$iv.resetData((DataComponentType)type$iv);
            }

         }

         public Color getPotionContentColor() {
            PotionContents var10000 = (PotionContents)itemStack.getData(DataComponentTypes.POTION_CONTENTS);
            return var10000 != null ? var10000.customColor() : null;
         }

         public void setPotionContentColor(Color value) {
            potionColorableSetLogic.invoke(value);
         }
      }) : (Colorable)(new Colorable() {
         public Color getColor() {
            DyedItemColor var10000 = dyedColor;
            return var10000 != null ? var10000.color() : null;
         }

         public void setColor(Color value) {
            com.nexomc.nexo.utils.ItemUtils var2 = com.nexomc.nexo.utils.ItemUtils.INSTANCE;
            ItemStack $this$setOrReset$iv = itemStack;
            DataComponentType.Valued var10000 = DataComponentTypes.DYED_COLOR;
            Intrinsics.checkNotNullExpressionValue(var10000, "DYED_COLOR");
            DataComponentType.Valued type$iv = var10000;
            DyedItemColor var8;
            if (value != null) {
               int var7 = 0;
               var8 = DyedItemColor.dyedItemColor(value);
            } else {
               var8 = null;
            }

            Object value$iv = var8;
            if (value$iv != null) {
               $this$setOrReset$iv.setData(type$iv, value$iv);
            } else {
               $this$setOrReset$iv.resetData((DataComponentType)type$iv);
            }

         }

         public Color getPotionContentColor() {
            PotionContents var10000 = (PotionContents)itemStack.getData(DataComponentTypes.POTION_CONTENTS);
            return var10000 != null ? var10000.customColor() : null;
         }

         public void setPotionContentColor(Color value) {
            potionColorableSetLogic.invoke(value);
         }
      }));
   }

   @Nullable
   public Art paintingVariant(@NotNull ItemStack itemStack) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      return (Art)itemStack.getData(DataComponentTypes.PAINTING_VARIANT);
   }

   public void paintingVariant(@NotNull ItemStack itemStack, @Nullable Art paintingVariant) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      com.nexomc.nexo.utils.ItemUtils var3 = com.nexomc.nexo.utils.ItemUtils.INSTANCE;
      DataComponentType.Valued var10000 = DataComponentTypes.PAINTING_VARIANT;
      Intrinsics.checkNotNullExpressionValue(var10000, "PAINTING_VARIANT");
      DataComponentType.Valued type$iv = var10000;
      if (paintingVariant != null) {
         itemStack.setData(type$iv, paintingVariant);
      } else {
         itemStack.resetData((DataComponentType)type$iv);
      }

   }

   @NotNull
   public ItemStack copyItemNBTTags(@NotNull ItemStack oldItem, @NotNull ItemStack newItem) {
      Intrinsics.checkNotNullParameter(oldItem, "oldItem");
      Intrinsics.checkNotNullParameter(newItem, "newItem");
      Pair var3 = TuplesKt.to(CraftItemStack.asNMSCopy(newItem), CraftItemStack.asNMSCopy(oldItem));
      net.minecraft.world.item.ItemStack newNmsItem = (net.minecraft.world.item.ItemStack)var3.component1();
      net.minecraft.world.item.ItemStack oldItemStack = (net.minecraft.world.item.ItemStack)var3.component2();
      net.minecraft.core.component.DataComponentType type = DataComponents.CUSTOM_DATA;
      CustomData var10000 = (CustomData)oldItemStack.getComponents().get(type);
      if (var10000 == null) {
         return newItem;
      } else {
         CustomData var10001 = (CustomData)newNmsItem.getComponents().get(type);
         if (var10001 == null) {
            return newItem;
         } else {
            Pair var7 = TuplesKt.to(var10000, var10001);
            CustomData oldData = (CustomData)var7.component1();
            CustomData newData = (CustomData)var7.component2();
            Pair var10 = TuplesKt.to(oldData.copyTag(), newData.copyTag());
            CompoundTag oldTag = (CompoundTag)var10.component1();
            CompoundTag newTag = (CompoundTag)var10.component2();
            Set var29 = oldTag.keySet();
            Intrinsics.checkNotNullExpressionValue(var29, "keySet(...)");
            Iterable $this$filter$iv = (Iterable)var29;
            Collection destination$iv$iv = (Collection)(new ArrayList());

            for(Object element$iv$iv : $this$filter$iv) {
               String it = (String)element$iv$iv;
               int var21 = 0;
               if (!IItemUtils.vanillaKeys.contains(it)) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            $this$filter$iv = (Iterable)((List)destination$iv$iv);

            for(Object element$iv : $this$filter$iv) {
               String key = (String)element$iv;
               int var26 = 0;
               Tag var30 = oldTag.get(key);
               if (var30 != null) {
                  Tag it = var30;
                  int var28 = 0;
                  newTag.put(key, it);
                  if (it != null) {
                     continue;
                  }
               }

               newTag.remove(key);
               Unit var31 = Unit.INSTANCE;
            }

            newNmsItem.set(type, CustomData.of(newTag));
            CraftItemStack var32 = CraftItemStack.asCraftMirror(newNmsItem);
            Intrinsics.checkNotNullExpressionValue(var32, "asCraftMirror(...)");
            return (ItemStack)var32;
         }
      }
   }

   public void consumableComponent(@NotNull ItemBuilder itemBuilder, @NotNull ConfigurationSection consumableSection) {
      Intrinsics.checkNotNullParameter(itemBuilder, "itemBuilder");
      Intrinsics.checkNotNullParameter(consumableSection, "consumableSection");
      Consumable.Builder var10000 = Consumable.consumable();
      Intrinsics.checkNotNullExpressionValue(var10000, "consumable(...)");
      Consumable.Builder consumable = var10000;
      Consumable var5 = (Consumable)ItemStack.of(itemBuilder.getType()).getData(DataComponentTypes.CONSUMABLE);
      if (var5 == null) {
         Object var6 = Consumable.consumable().build();
         Intrinsics.checkNotNullExpressionValue(var6, "build(...)");
         var5 = (Consumable)var6;
      }

      Consumable template = var5;
      Duration var10001 = NexoYamlKt.durationOrNull(consumableSection, "consume_duration", (DurationUnit)null);
      consumable.consumeSeconds(var10001 != null ? RangesKt.coerceAtLeast(DurationUtilsKt.toFloat_VtjQ1oo(var10001.unbox_impl(), DurationUnit.SECONDS), 0.0F) : template.consumeSeconds());
      consumable.animation((ItemUseAnimation)EnumUtils.getEnum(ItemUseAnimation.class, consumableSection.getString("animation"), (Enum)template.animation()));
      consumable.hasConsumeParticles(consumableSection.getBoolean("consume_particles", template.hasConsumeParticles()));
      Key var10003 = template.sound();
      Intrinsics.checkNotNullExpressionValue(var10003, "sound(...)");
      consumable.sound(NexoYamlKt.key(consumableSection, "sound", var10003));
      List var7 = this.consumeEffectsFromSection(NexoYamlKt.section(consumableSection, "effects"));
      if (var7 == null) {
         var7 = template.consumeEffects();
      }

      consumable.addEffects(var7);
      itemBuilder.setConsumableComponent(consumable.build());
   }

   @Nullable
   public Consumable consumableComponent(@Nullable ItemStack itemStack) {
      return itemStack != null ? (Consumable)itemStack.getData(DataComponentTypes.CONSUMABLE) : null;
   }

   public void consumableComponent(@NotNull ItemStack itemStack, @Nullable Object consumable) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      DataComponentType.Valued var10001 = DataComponentTypes.CONSUMABLE;
      Consumable var10002 = consumable instanceof Consumable ? (Consumable)consumable : null;
      if ((consumable instanceof Consumable ? (Consumable)consumable : null) != null) {
         itemStack.setData(var10001, var10002);
      }
   }

   public void canPlaceOnComponent(@NotNull ItemBuilder itemBuilder, @NotNull List<? extends ConfigurationSection> predicateSections) {
      Intrinsics.checkNotNullParameter(itemBuilder, "itemBuilder");
      Intrinsics.checkNotNullParameter(predicateSections, "predicateSections");
      itemBuilder.setCanPlaceOn(predicateSections);
   }

   public void canPlaceOnComponent(@NotNull ItemStack itemStack, @Nullable Object predicate) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      Object var10000 = predicate;
      if (!(predicate instanceof List)) {
         var10000 = null;
      }

      List var6 = (List)var10000;
      if (var6 != null) {
         List sections = var6;
         net.minecraft.world.item.ItemStack var7 = Companion.toCraftStack(itemStack).handle;
         net.minecraft.core.component.DataComponentType var10001 = DataComponents.CAN_PLACE_ON;
         AdventureModePredicate var10002 = this.adventureModePredicate(sections);
         if (var10002 != null) {
            var7.set(var10001, var10002);
         }
      }
   }

   public void canBreakComponent(@NotNull ItemBuilder itemBuilder, @NotNull List<? extends ConfigurationSection> predicateSections) {
      Intrinsics.checkNotNullParameter(itemBuilder, "itemBuilder");
      Intrinsics.checkNotNullParameter(predicateSections, "predicateSections");
      itemBuilder.setCanBreak(predicateSections);
   }

   public void canBreakComponent(@NotNull ItemStack itemStack, @Nullable Object predicate) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      Object var10000 = predicate;
      if (!(predicate instanceof List)) {
         var10000 = null;
      }

      List var6 = (List)var10000;
      if (var6 != null) {
         List sections = var6;
         net.minecraft.world.item.ItemStack var7 = Companion.toCraftStack(itemStack).handle;
         net.minecraft.core.component.DataComponentType var10001 = DataComponents.CAN_BREAK;
         AdventureModePredicate var10002 = this.adventureModePredicate(sections);
         if (var10002 != null) {
            var7.set(var10001, var10002);
         }
      }
   }

   private final AdventureModePredicate adventureModePredicate(List<? extends ConfigurationSection> sections) {
      DefaultedRegistry blockRegistry = BuiltInRegistries.BLOCK;
      List predicates = (List)(new ArrayList());

      for(ConfigurationSection section : sections) {
         Iterable $this$forEach$iv = (Iterable)NexoYamlKt.stringListOrSingle(section, "nexo_block", (String)null);
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            String id = (String)element$iv;
            int var11 = 0;
            BlockData var10000 = NexoBlocks.blockData(id);
            if (var10000 == null) {
               Logs.logWarn("Could not resolve nexo_block <" + id + "> for can_place_on/can_break", false);
            } else {
               BlockData blockData = var10000;
               Collection var13 = (Collection)predicates;
               Intrinsics.checkNotNull(blockData, "null cannot be cast to non-null type org.bukkit.craftbukkit.block.data.CraftBlockData");
               BlockState var10001 = ((CraftBlockData)blockData).getState();
               Intrinsics.checkNotNullExpressionValue(var10001, "getState(...)");
               var13.add(this.blockPredicate(var10001));
            }
         }

         List blocks = (List)(new ArrayList());
         Iterable $this$forEach$iv = (Iterable)NexoYamlKt.stringListOrSingle(section, "block", (String)null);
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            String key = (String)element$iv;
            int var32 = 0;
            ResourceLocation identifier = ResourceLocation.parse(StringsKt.removePrefix(key, (CharSequence)"#"));
            TagKey tagKey = TagKey.create(Registries.BLOCK, identifier);
            if (blockRegistry.get(tagKey).isPresent()) {
               ((Collection)predicates).add(Builder.block().of((HolderGetter)blockRegistry, tagKey).build());
            } else {
               Optional var36 = blockRegistry.getOptional(identifier);
               Intrinsics.checkNotNullExpressionValue(var36, "getOptional(...)");
               Block var37 = (Block)OptionalsKt.getOrNull(var36);
               if (var37 != null) {
                  Block p0 = var37;
                  int var16 = 0;
                  blocks.add(p0);
               } else {
                  Logs.logWarn("Could not resolve block <" + key + "> for can_place_on/can_break", false);
               }
            }
         }

         $this$forEach$iv = (Iterable)blocks;
         $i$f$forEach = 0;
         if (CollectionsKt.count($this$forEach$iv) > 0) {
            List it = (List)$this$forEach$iv;
            int var29 = 0;
            BlockPredicate.Builder builder = Builder.block().of((HolderGetter)blockRegistry, (Collection)blocks);
            StatePropertiesPredicate.Builder var38 = this.stateProperties(NexoYamlKt.section(section, "state"), (Block)CollectionsKt.first(blocks));
            if (var38 != null) {
               StatePropertiesPredicate.Builder p0 = var38;
               Intrinsics.checkNotNull(builder);
               int var35 = 0;
               builder.setProperties(p0);
            }

            ((Collection)predicates).add(builder.build());
         }
      }

      Iterable $this$ifNotEmptyOrNull$iv = (Iterable)predicates;
      AdventureModePredicate var39;
      if (CollectionsKt.count($this$ifNotEmptyOrNull$iv) > 0) {
         List p0 = (List)$this$ifNotEmptyOrNull$iv;
         int var23 = 0;
         var39 = new AdventureModePredicate(p0);
      } else {
         var39 = null;
      }

      return var39;
   }

   private final BlockPredicate blockPredicate(BlockState state) {
      BlockPredicate.Builder var10000 = Builder.block();
      HolderGetter var10001 = (HolderGetter)BuiltInRegistries.BLOCK;
      Block[] var3 = new Block[]{state.getBlock()};
      BlockPredicate.Builder builder = var10000.of(var10001, var3);
      Collection var11 = state.getProperties();
      Intrinsics.checkNotNullExpressionValue(var11, "getProperties(...)");
      if (!var11.isEmpty()) {
         StatePropertiesPredicate.Builder properties = net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties();
         var11 = state.getProperties();
         Intrinsics.checkNotNullExpressionValue(var11, "getProperties(...)");
         Iterable $this$forEach$iv = (Iterable)var11;
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            Property property = (Property)element$iv;
            int var9 = 0;
            Intrinsics.checkNotNull(property);
            properties.hasProperty(property, this.propertyValue(state, property));
         }

         builder.setProperties(properties);
      }

      BlockPredicate var13 = builder.build();
      Intrinsics.checkNotNullExpressionValue(var13, "build(...)");
      return var13;
   }

   private final StatePropertiesPredicate.Builder stateProperties(ConfigurationSection stateSection, Block block) {
      if (stateSection == null) {
         return null;
      } else {
         StateDefinition stateDefinition = block.getStateDefinition();
         StatePropertiesPredicate.Builder properties = net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties();
         boolean any = false;
         Set var10000 = stateSection.getKeys(false);
         Intrinsics.checkNotNullExpressionValue(var10000, "getKeys(...)");
         Iterable $this$forEach$iv = (Iterable)var10000;
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            String propertyName = (String)element$iv;
            int var11 = 0;
            Property var15 = stateDefinition.getProperty(propertyName);
            if (var15 == null) {
               ItemUtils $this$stateProperties_u24lambda_u240_u240 = this;
               int var13 = 0;
               Logs.logWarn("Unknown blockstate-property <" + propertyName + "> for block <" + BuiltInRegistries.BLOCK.getKey(block) + ">", false);
            } else {
               Property property = var15;
               String var10002 = stateSection.getString(propertyName);
               if (var10002 != null) {
                  properties.hasProperty(property, var10002);
                  any = true;
               }
            }
         }

         return any ? properties : null;
      }
   }

   private final <T extends Comparable<? super T>> String propertyValue(BlockState state, Property<T> property) {
      String var10000 = property.getName(state.getValue(property));
      Intrinsics.checkNotNullExpressionValue(var10000, "getName(...)");
      return var10000;
   }

   public void repairableComponent(@NotNull ItemBuilder itemBuilder, @NotNull List<? extends Key> repairableWith) {
      Intrinsics.checkNotNullParameter(itemBuilder, "itemBuilder");
      Intrinsics.checkNotNullParameter(repairableWith, "repairableWith");
      Registry var10000 = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
      Intrinsics.checkNotNullExpressionValue(var10000, "getRegistry(...)");
      Registry registry = var10000;
      Iterable $this$flatMap$iv = (Iterable)repairableWith;
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv : $this$flatMap$iv) {
         Key key = (Key)element$iv$iv;
         int var13 = 0;
         ItemUtils var14 = this;

         Object $this$repairableComponent_u24lambda_u240_u240;
         try {
            ItemUtils $this$repairableComponent_u24lambda_u240_u240 = var14;
            int var16 = 0;
            Iterable $this$mapNotNull$iv = (Iterable)registry.getTag(io.papermc.paper.registry.tag.TagKey.create(RegistryKey.ITEM, key)).values();
            Collection destination$iv$iv = (Collection)(new ArrayList());
            int $i$f$forEach = 0;

            for(Object element$iv$iv$iv : $this$mapNotNull$iv) {
               int var27 = 0;
               TypedKey p0 = (TypedKey)element$iv$iv$iv;
               int var29 = 0;
               ItemType var40 = (ItemType)registry.get(p0);
               if (var40 != null) {
                  Object it$iv$iv = var40;
                  int var31 = 0;
                  destination$iv$iv.add(it$iv$iv);
               }
            }

            $this$repairableComponent_u24lambda_u240_u240 = ResultShim.ok((List)destination$iv$iv);
         } catch (Throwable var32) {
            $this$repairableComponent_u24lambda_u240_u240 = ResultShim.ok(ResultKt.createFailure(var32));
         }

         Object var35 = $this$repairableComponent_u24lambda_u240_u240;
         List var37 = CollectionsKt.emptyList();
         Iterable list$iv$iv = (Iterable)CollectionsKt.plus((Collection)(ResultShim.isFailure(var35) ? var37 : var35), registry.get(key));
         CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
      }

      List it = CollectionsKt.filterNotNull((Iterable)((List)destination$iv$iv));
      int $this$flatMapTo$iv$iv = 0;
      RegistryKeySet var41 = RegistrySet.keySetFromValues(RegistryKey.ITEM, (Iterable)it);
      Intrinsics.checkNotNullExpressionValue(var41, "let(...)");
      RegistryKeySet keySet = var41;
      itemBuilder.setRepairableComponent(Repairable.repairable(keySet));
   }

   @Nullable
   public Repairable repairableComponent(@Nullable ItemStack itemStack) {
      return itemStack != null ? (Repairable)itemStack.getData(DataComponentTypes.REPAIRABLE) : null;
   }

   public void repairableComponent(@NotNull ItemStack itemStack, @Nullable Object repairable) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      DataComponentType.Valued var10001 = DataComponentTypes.REPAIRABLE;
      Repairable var10002 = repairable instanceof Repairable ? (Repairable)repairable : null;
      if ((repairable instanceof Repairable ? (Repairable)repairable : null) != null) {
         itemStack.setData(var10001, var10002);
      }
   }

   @Nullable
   public Map<String, String> blockstateComponent(@Nullable ItemStack itemStack) {
      BlockItemDataProperties var2 = itemStack != null ? (BlockItemDataProperties)itemStack.getData(DataComponentTypes.BLOCK_DATA) : null;
      PaperBlockItemDataProperties var10000 = var2 instanceof PaperBlockItemDataProperties ? (PaperBlockItemDataProperties)var2 : null;
      Map var4;
      if ((var2 instanceof PaperBlockItemDataProperties ? (PaperBlockItemDataProperties)var2 : null) != null) {
         BlockItemStateProperties var3 = var10000.impl();
         if (var3 != null) {
            var4 = var3.properties();
            return var4;
         }
      }

      var4 = null;
      return var4;
   }

   public void blockstateComponent(@NotNull ItemStack itemStack, @Nullable Map<String, String> blockstates) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      DataComponentType.Valued var10001 = DataComponentTypes.BLOCK_DATA;
      PaperBlockItemDataProperties var10002;
      BlockItemStateProperties var10004;
      if (blockstates != null) {
         var10004 = new BlockItemStateProperties(blockstates);
         var10002 = new PaperBlockItemDataProperties(var10004);
         itemStack.setData(var10001, var10002);
      }
   }

   public void customDataComponent(@NotNull ItemStack itemStack, @NotNull Map<String, ? extends Object> customData) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      Intrinsics.checkNotNullParameter(customData, "customData");
      if (!customData.isEmpty()) {
         net.minecraft.world.item.ItemStack itemStack;
         CompoundTag var28;
         block: {
            itemStack = Companion.toCraftStack(itemStack).handle;
            CustomData var10000 = (CustomData)itemStack.get(DataComponents.CUSTOM_DATA);
            if (var10000 != null) {
               var28 = var10000.copyTag();
               if (var28 != null) {
                  break block;
               }
            }

            var28 = new CompoundTag();
         }

         CompoundTag existingData = var28;
         LinkedHashMap result$iv = new LinkedHashMap();

         for(Map.Entry entry$iv : customData.entrySet()) {
            Object it = entry$iv.getValue();
            int var12 = 0;
            if (it == null) {
               result$iv.put(entry$iv.getKey(), entry$iv.getValue());
            }
         }

         Set removeKeys = ((Map)result$iv).keySet();
         LinkedHashMap result$iv = new LinkedHashMap();

         for(Map.Entry entry$iv : customData.entrySet()) {
            Object it = entry$iv.getValue();
            int var13 = 0;
            if (it != null) {
               result$iv.put(entry$iv.getKey(), entry$iv.getValue());
            }
         }

         Map $this$filterValues$iv = (Map)result$iv;
         CompoundTag newNbt = this.toCompound($this$filterValues$iv);
         existingData.merge(newNbt);
         Iterable $this$forEach$iv = (Iterable)removeKeys;
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            String p0 = (String)element$iv;
            int var27 = 0;
            existingData.remove(p0);
         }

         itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(existingData));
      }
   }

   @Nullable
   public Object deathProtectionComponent(@Nullable ItemStack itemStack) {
      return itemStack != null ? (DeathProtection)itemStack.getData(DataComponentTypes.DEATH_PROTECTION) : null;
   }

   public void deathProtectionComponent(@NotNull ItemBuilder itemBuilder, @NotNull ConfigurationSection deathProtectionSection) {
      Intrinsics.checkNotNullParameter(itemBuilder, "itemBuilder");
      Intrinsics.checkNotNullParameter(deathProtectionSection, "deathProtectionSection");
      DeathProtection.Builder var10000 = DeathProtection.deathProtection();
      Intrinsics.checkNotNullExpressionValue(var10000, "deathProtection(...)");
      DeathProtection.Builder deathProtection = var10000;
      DeathProtection var7 = (DeathProtection)ItemStack.of(itemBuilder.getType()).getData(DataComponentTypes.DEATH_PROTECTION);
      if (var7 == null) {
         Object var8 = DeathProtection.deathProtection().build();
         Intrinsics.checkNotNullExpressionValue(var8, "build(...)");
         var7 = (DeathProtection)var8;
      }

      DeathProtection template = var7;
      ConfigurationSection var9 = NexoYamlKt.section(deathProtectionSection, "death_effects");
      if (var9 == null) {
         var9 = deathProtectionSection;
      }

      ConfigurationSection effectsSection = var9;
      List var10 = this.consumeEffectsFromSection(effectsSection);
      if (var10 == null) {
         var10 = template.deathEffects();
      }

      List effects = var10;
      deathProtection.addEffects(effects);
      itemBuilder.setDeathProtectionComponent(deathProtection.build());
   }

   public void deathProtectionComponent(@NotNull ItemStack itemStack, @Nullable Object deathProtection) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      DataComponentType.Valued var10001 = DataComponentTypes.DEATH_PROTECTION;
      DeathProtection var10002 = deathProtection instanceof DeathProtection ? (DeathProtection)deathProtection : null;
      if ((deathProtection instanceof DeathProtection ? (DeathProtection)deathProtection : null) != null) {
         itemStack.setData(var10001, var10002);
      }
   }

   @Nullable
   public List<ConsumeEffect> consumeEffectsFromSection(@Nullable ConfigurationSection effectsSection) {
      if (effectsSection == null) {
         return null;
      } else {
         List consumeEffects = (List)(new ArrayList());
         NexoYamlKt.forEach(effectsSection, ItemUtils::consumeEffectsFromSection$lambda$0);
         return consumeEffects;
      }
   }

   public void dumpItem(@NotNull CommandSender sender, @NotNull ItemStack itemStack, boolean includeAllComponents) {
      Intrinsics.checkNotNullParameter(sender, "sender");
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(itemStack);
      TextComponent.Builder var10000 = Component.text();
      Intrinsics.checkNotNullExpressionValue(var10000, "text(...)");
      TextComponent.Builder visualOutput = var10000;
      StringBuilder itemCommandBuilder = new StringBuilder();
      String var41 = ((ResourceKey)itemStack.getItemHolder().unwrapKey().orElseThrow()).location().toString();
      Intrinsics.checkNotNullExpressionValue(var41, "toString(...)");
      String itemName = var41;
      itemCommandBuilder.append(itemName);
      visualOutput.append((Component)Component.text(itemName, (TextColor)NamedTextColor.YELLOW));
      Set referencedComponentTypes = Collections.newSetFromMap((Map)(new IdentityHashMap()));
      DataComponentPatch patch = itemStack.getComponentsPatch();
      Intrinsics.checkNotNull(referencedComponentTypes);
      Collection var10 = (Collection)referencedComponentTypes;
      Set var42 = patch.entrySet();
      Intrinsics.checkNotNullExpressionValue(var42, "entrySet(...)");
      Iterable $this$map$iv = (Iterable)var42;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

      for(Object item$iv$iv : $this$map$iv) {
         Map.Entry it = (Map.Entry)item$iv$iv;
         int var19 = 0;
         destination$iv$iv.add((net.minecraft.core.component.DataComponentType)it.getKey());
      }

      $this$map$iv = (Iterable)((List)destination$iv$iv);
      CollectionsKt.addAll(var10, $this$map$iv);
      DataComponentMap prototype = itemStack.getItem().components();
      if (includeAllComponents) {
         Set var10001 = prototype.keySet();
         Intrinsics.checkNotNullExpressionValue(var10001, "keySet(...)");
         referencedComponentTypes.addAll((Collection)var10001);
      }

      Server var43 = Bukkit.getServer();
      Intrinsics.checkNotNull(var43, "null cannot be cast to non-null type org.bukkit.craftbukkit.CraftServer");
      net.minecraft.core.RegistryAccess.Frozen access = ((CraftServer)var43).getServer().registryAccess();
      RegistryOps ops = access.createSerializationContext((DynamicOps)NbtOps.INSTANCE);
      net.minecraft.core.Registry registry = access.lookupOrThrow(Registries.DATA_COMPONENT_TYPE);
      List componentComponents = (List)(new ArrayList());
      List commandComponents = (List)(new ArrayList());

      for(net.minecraft.core.component.DataComponentType type : referencedComponentTypes) {
         String path = ((ResourceKey)registry.getResourceKey(type).orElseThrow()).location().getPath();
         Optional patchedValue = patch.get(type);
         TypedDataComponent prototypeValue = prototype.getTyped(type);
         if (patchedValue != null) {
            if (patchedValue.isEmpty()) {
               ComponentBuilder var44 = ((TextComponent.Builder)Component.text().append((Component)Component.text('!', (TextColor)NamedTextColor.RED))).append((Component)Component.text(path, (TextColor)NamedTextColor.AQUA));
               Intrinsics.checkNotNullExpressionValue(var44, "append(...)");
               TextComponent.Builder component = (TextComponent.Builder)var44;
               ((Collection)componentComponents).add(component);
               ((Collection)commandComponents).add("!" + path);
            } else {
               Object var45 = patchedValue.get();
               Intrinsics.checkNotNullExpressionValue(var45, "get(...)");
               Object value = var45;
               Intrinsics.checkNotNull(type, "null cannot be cast to non-null type net.minecraft.core.component.DataComponentType<kotlin.Any?>");
               Intrinsics.checkNotNull(ops);
               Tag serialized = this.encodeComponentValue(type, (DynamicOps)ops, value);
               Function1 var50 = (Function1<ComponentLike, Unit>)((ComponentLike p0) -> {
((List)componentComponents).add(p0);
return Unit.INSTANCE;
});
               Function1 var10002 = (Function1<String, Unit>)((String p0) -> {
((List)commandComponents).add(p0);
return Unit.INSTANCE;
});
               Intrinsics.checkNotNull(path);
               this.writeComponentValue(var50, var10002, path, serialized);
            }
         } else if (includeAllComponents && prototypeValue != null) {
            Tag serialized = (Tag)prototypeValue.encodeValue((DynamicOps)ops).getOrThrow();
            Function1 var51 = (Function1<ComponentLike, Unit>)((ComponentLike p0) -> {
((List)componentComponents).add(p0);
return Unit.INSTANCE;
});
            Function1 var52 = (Function1<String, Unit>)((String p0) -> {
((List)commandComponents).add(p0);
return Unit.INSTANCE;
});
            Intrinsics.checkNotNull(path);
            Intrinsics.checkNotNull(serialized);
            this.writeComponentValue(var51, var52, path, serialized);
         }
      }

      if (!componentComponents.isEmpty()) {
         TextComponent var46 = Component.text("[", TextColor.color(8982734));
         Intrinsics.checkNotNullExpressionValue(var46, "text(...)");
         TextComponent prefix = var46;
         var46 = Component.text("]", TextColor.color(8982734));
         Intrinsics.checkNotNullExpressionValue(var46, "text(...)");
         TextComponent suffix = var46;
         JoinConfiguration var48 = JoinConfiguration.separator((ComponentLike)Component.text(",", (TextColor)NamedTextColor.GRAY));
         Intrinsics.checkNotNullExpressionValue(var48, "separator(...)");
         JoinConfiguration join = var48;
         Component var49 = Component.join(join, (Iterable)componentComponents);
         Intrinsics.checkNotNullExpressionValue(var49, "join(...)");
         Component content = var49;
         Component[] var38 = new Component[]{prefix, content, suffix};
         visualOutput.append(var38);
         itemCommandBuilder.append("[" + CollectionsKt.joinToString((Iterable)commandComponents, (CharSequence)",", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null) + "]");
      }

      sender.sendMessage(((TextComponent)visualOutput.build()).compact());
   }

   private final <T> Tag encodeComponentValue(net.minecraft.core.component.DataComponentType<T> type, DynamicOps<Tag> ops, Object value) {
      Codec var10000 = type.codecOrThrow();
      Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type com.mojang.serialization.Codec<T of com.nexomc.nexo.nms.v1_21_R5.ItemUtils.encodeComponentValue>");
      Codec codec = var10000;
      Object var5 = codec.encodeStart(ops, value).getOrThrow();
      Intrinsics.checkNotNullExpressionValue(var5, "getOrThrow(...)");
      return (Tag)var5;
   }

   private final void writeComponentValue(Function1<? super Component, Unit> visualOutput, Function1<? super String, Unit> commandOutput, String path, Tag serialized) {
      ComponentLike[] var5 = new ComponentLike[]{Component.text(path, TextColor.color(16744407)), Component.text("=", (TextColor)NamedTextColor.WHITE), PaperAdventure.asAdventure(NbtUtils.toPrettyComponent(serialized))};
      TextComponent var10001 = Component.textOfChildren(var5);
      Intrinsics.checkNotNullExpressionValue(var10001, "textOfChildren(...)");
      visualOutput.invoke(var10001);
      commandOutput.invoke(path + "=" + (new SnbtPrinterTagVisitor("", 0, (List)(new ArrayList()))).visit(serialized));
   }

   private final CompoundTag toCompound(Map<String, ? extends Object> $this$toCompound) {
      CompoundTag compound = new CompoundTag();
      int $i$f$forEach = 0;

      for(Map.Entry element$iv : $this$toCompound.entrySet()) {
         int var8 = 0;
         String key = (String)element$iv.getKey();
         Object value = element$iv.getValue();
         compound.put(key, this.toTag(value));
      }

      return compound;
   }

   private final Tag toTag(Object $this$toTag) {
      Tag var89;
      if ($this$toTag instanceof String) {
         StringTag var10000 = StringTag.valueOf((String)$this$toTag);
         Intrinsics.checkNotNullExpressionValue(var10000, "valueOf(...)");
         var89 = (Tag)var10000;
      } else if ($this$toTag instanceof Boolean) {
         ByteTag var90 = ByteTag.valueOf((Boolean)$this$toTag);
         Intrinsics.checkNotNullExpressionValue(var90, "valueOf(...)");
         var89 = (Tag)var90;
      } else if ($this$toTag instanceof Number) {
         var89 = this.toTag((Number)$this$toTag);
      } else if ($this$toTag instanceof ConfigurationSection) {
         CompoundTag var3 = new CompoundTag();
         CompoundTag $this$toTag_u24lambda_u240 = var3;
         int var5 = 0;
         Set var91 = ((ConfigurationSection)$this$toTag).getKeys(false);
         Intrinsics.checkNotNullExpressionValue(var91, "getKeys(...)");
         Iterable $this$forEach$iv = (Iterable)var91;
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            String key = (String)element$iv;
            int var11 = 0;
            Object v = ((ConfigurationSection)$this$toTag).get(key);
            if (v != null) {
               $this$toTag_u24lambda_u240.put(key, this.toTag(v));
            }
         }

         var89 = (Tag)var3;
      } else if ($this$toTag instanceof Map) {
         CompoundTag var19 = new CompoundTag();
         CompoundTag $this$toTag_u24lambda_u241 = var19;
         int var38 = 0;
         Map $this$forEach$iv = (Map)$this$toTag;
         int $i$f$forEach = 0;

         for(Map.Entry element$iv : $this$forEach$iv.entrySet()) {
            int var82 = 0;
            Object k = element$iv.getKey();
            Object v = element$iv.getValue();
            if (k instanceof String && v != null) {
               $this$toTag_u24lambda_u241.put((String)k, this.toTag(v));
            }
         }

         var89 = (Tag)var19;
      } else if ($this$toTag instanceof Collection) {
         Iterable $this$all$iv = (Iterable)$this$toTag;
         boolean var92;
         if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
            var92 = true;
         } else {
            Iterator $this$filterIsInstanceTo$iv$iv = $this$all$iv.iterator();

            while(true) {
               if (!$this$filterIsInstanceTo$iv$iv.hasNext()) {
                  var92 = true;
                  break;
               }

               Object element$iv = $this$filterIsInstanceTo$iv$iv.next();
               int var64 = 0;
               if (!(element$iv instanceof Integer)) {
                  var92 = false;
                  break;
               }
            }
         }

         CollectionTag var97;
         if (var92) {
            $this$all$iv = (Iterable)$this$toTag;
            Collection destination$iv$iv = (Collection)(new ArrayList());

            for(Object element$iv$iv : $this$all$iv) {
               if (element$iv$iv instanceof Integer) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            int[] var16 = CollectionsKt.toIntArray((Collection)((List)destination$iv$iv));
            var97 = (CollectionTag)(new IntArrayTag(var16));
         } else {
            $this$all$iv = (Iterable)$this$toTag;
            boolean var93;
            if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
               var93 = true;
            } else {
               Iterator $this$filterIsInstanceTo$iv$iv = $this$all$iv.iterator();

               while(true) {
                  if (!$this$filterIsInstanceTo$iv$iv.hasNext()) {
                     var93 = true;
                     break;
                  }

                  Object element$iv = $this$filterIsInstanceTo$iv$iv.next();
                  int var65 = 0;
                  if (!(element$iv instanceof Byte)) {
                     var93 = false;
                     break;
                  }
               }
            }

            if (var93) {
               $this$all$iv = (Iterable)$this$toTag;
               Collection destination$iv$iv = (Collection)(new ArrayList());

               for(Object element$iv$iv : $this$all$iv) {
                  if (element$iv$iv instanceof Byte) {
                     destination$iv$iv.add(element$iv$iv);
                  }
               }

               byte[] var17 = CollectionsKt.toByteArray((Collection)((List)destination$iv$iv));
               var97 = (CollectionTag)(new ByteArrayTag(var17));
            } else {
               $this$all$iv = (Iterable)$this$toTag;
               if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
                  var93 = true;
               } else {
                  Iterator $this$filterIsInstanceTo$iv$iv = $this$all$iv.iterator();

                  while(true) {
                     if (!$this$filterIsInstanceTo$iv$iv.hasNext()) {
                        var93 = true;
                        break;
                     }

                     Object element$iv = $this$filterIsInstanceTo$iv$iv.next();
                     int $this$filterIsInstanceTo$iv$iv = 0;
                     if (!(element$iv instanceof Long)) {
                        var93 = false;
                        break;
                     }
                  }
               }

               if (var93) {
                  $this$all$iv = (Iterable)$this$toTag;
                  Collection destination$iv$iv = (Collection)(new ArrayList());

                  for(Object element$iv$iv : $this$all$iv) {
                     if (element$iv$iv instanceof Long) {
                        destination$iv$iv.add(element$iv$iv);
                     }
                  }

                  long[] var18 = CollectionsKt.toLongArray((Collection)((List)destination$iv$iv));
                  var97 = (CollectionTag)(new LongArrayTag(var18));
               } else {
                  $this$all$iv = (Iterable)$this$toTag;
                  if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
                     var93 = true;
                  } else {
                     Iterator var42 = $this$all$iv.iterator();

                     while(true) {
                        if (!var42.hasNext()) {
                           var93 = true;
                           break;
                        }

                        Object element$iv = var42.next();
                        int $this$mapTo$iv$iv = 0;
                        if (!(element$iv instanceof String)) {
                           var93 = false;
                           break;
                        }
                     }
                  }

                  if (var93) {
                     ListTag var25 = new ListTag();
                     ListTag $this$toTag_u24lambda_u246 = var25;
                     int var44 = 0;
                     Iterable $this$filterIsInstance$iv = (Iterable)$this$toTag;
                     int $i$f$filterIsInstance = 0;
                     Collection destination$iv$iv = (Collection)(new ArrayList());

                     for(Object element$iv$iv : $this$filterIsInstance$iv) {
                        if (element$iv$iv instanceof String) {
                           destination$iv$iv.add(element$iv$iv);
                        }
                     }

                     $this$filterIsInstance$iv = (Iterable)((List)destination$iv$iv);
                     $i$f$filterIsInstance = 0;

                     for(Object element$iv : $this$filterIsInstance$iv) {
                        String it = (String)element$iv;
                        int var85 = 0;
                        $this$toTag_u24lambda_u246.add(StringTag.valueOf(it));
                     }

                     var97 = (CollectionTag)var25;
                  } else {
                     ListTag $this$all$iv = new ListTag();
                     int var43 = 0;
                     Iterable $this$map$iv = (Iterable)CollectionsKt.filterNotNull((Iterable)$this$toTag);
                     Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

                     for(Object item$iv$iv : $this$map$iv) {
                        int var15 = 0;
                        destination$iv$iv.add(this.toTag(item$iv$iv));
                     }

                     List var96 = (List)destination$iv$iv;
                     var97 = (CollectionTag)$this$all$iv;
                  }
               }
            }
         }

         var89 = (Tag)var97;
      } else {
         StringTag var98 = StringTag.valueOf($this$toTag.toString());
         Intrinsics.checkNotNullExpressionValue(var98, "valueOf(...)");
         var89 = (Tag)var98;
      }

      return var89;
   }

   private final Tag toTag(Number $this$toTag) {
      Tag var3;
      if ($this$toTag instanceof Integer) {
         IntTag var10000 = IntTag.valueOf($this$toTag.intValue());
         Intrinsics.checkNotNullExpressionValue(var10000, "valueOf(...)");
         var3 = (Tag)var10000;
      } else if ($this$toTag instanceof Long) {
         LongTag var4 = LongTag.valueOf($this$toTag.longValue());
         Intrinsics.checkNotNullExpressionValue(var4, "valueOf(...)");
         var3 = (Tag)var4;
      } else if ($this$toTag instanceof Float) {
         FloatTag var5 = FloatTag.valueOf($this$toTag.floatValue());
         Intrinsics.checkNotNullExpressionValue(var5, "valueOf(...)");
         var3 = (Tag)var5;
      } else if ($this$toTag instanceof Double) {
         DoubleTag var6 = DoubleTag.valueOf($this$toTag.doubleValue());
         Intrinsics.checkNotNullExpressionValue(var6, "valueOf(...)");
         var3 = (Tag)var6;
      } else if ($this$toTag instanceof Short) {
         ShortTag var7 = ShortTag.valueOf($this$toTag.shortValue());
         Intrinsics.checkNotNullExpressionValue(var7, "valueOf(...)");
         var3 = (Tag)var7;
      } else if ($this$toTag instanceof Byte) {
         ByteTag var8 = ByteTag.valueOf($this$toTag.byteValue());
         Intrinsics.checkNotNullExpressionValue(var8, "valueOf(...)");
         var3 = (Tag)var8;
      } else {
         StringTag var9 = StringTag.valueOf($this$toTag.toString());
         Intrinsics.checkNotNullExpressionValue(var9, "valueOf(...)");
         var3 = (Tag)var9;
      }

      return var3;
   }

   private static final Unit asColorable$lambda$0(ItemStack $itemStack, Color value) {
      PotionContents var10000 = (PotionContents)$itemStack.getData(DataComponentTypes.POTION_CONTENTS);
      if (var10000 == null) {
         return Unit.INSTANCE;
      } else {
         PotionContents contents = var10000;
         PotionContents.Builder var4 = PotionContents.potionContents().customColor(value).potion(contents.potion()).addCustomEffects(contents.customEffects()).customName(contents.customName());
         Intrinsics.checkNotNullExpressionValue(var4, "customName(...)");
         PotionContents.Builder colored = var4;
         $itemStack.setData(DataComponentTypes.POTION_CONTENTS, (DataComponentBuilder)colored);
         return Unit.INSTANCE;
      }
   }

   private static final Unit consumeEffectsFromSection$lambda$0(List $consumeEffects, ItemUtils this$0, String key, Object value) {
      Intrinsics.checkNotNullParameter(key, "key");
      if (Intrinsics.areEqual(key, "APPLY_EFFECTS") && value instanceof ConfigurationSection) {
         Map $this$forEach$iv = NexoYamlKt.childSections((ConfigurationSection)value);
         int $i$f$forEach = 0;

         for(Map.Entry element$iv : $this$forEach$iv.entrySet()) {
            String effectKey = (String)element$iv.getKey();
            ConfigurationSection effectProperties = (ConfigurationSection)element$iv.getValue();

            Object ambient;
            try {
               int var15 = 0;
               ambient = ResultShim.ok(ResourceLocation.parse(effectKey));
            } catch (Throwable var28) {
               ambient = ResultShim.ok(ResultKt.createFailure(var28));
            }

            Object var51 = ambient;
            Throwable var79 = ResultShim.exceptionOrNull(ambient);
            if (var79 != null) {
               Throwable it = var79;
               int p0 = 0;
               Logs.logError("Invalid potion effect: " + effectKey + ", in consumable-property!", false);
               if (Settings.DEBUG.toBool()) {
                  Logs.logError(ExceptionsKt.stackTraceToString(it), false);
               }
            }

            ResourceLocation p0 = (ResourceLocation)(ResultShim.isFailure(var51) ? null : var51);
            if (p0 != null) {
               net.minecraft.core.Registry var80 = BuiltInRegistries.MOB_EFFECT;
               Intrinsics.checkNotNullExpressionValue(var80, "MOB_EFFECT");
               net.minecraft.core.Registry it = var80;
               int probability = 0;
               Optional var52 = it.get(p0);
               if (var52 != null) {
                  Holder.Reference it = (Holder.Reference)OptionalsKt.getOrNull(var52);
                  if (it != null) {
                     Holder.Reference var67 = it;
                     int p0 = DurationUtilsKt.getTicks_LRDsOJo(NexoYamlKt.duration(effectProperties, "duration", DurationUnit.SECONDS));
                     int var53 = effectProperties.getInt("amplifier", 0);
                     boolean it = effectProperties.getBoolean("ambient", true);
                     boolean particles = effectProperties.getBoolean("show_particles", true);
                     boolean icon = effectProperties.getBoolean("show_icon", true);
                     float var65 = (float)effectProperties.getDouble("probability", (double)1.0F);
                     MobEffectInstance it = new MobEffectInstance((Holder)var67, p0, var53, it, particles, icon);
                     ApplyStatusEffectsConsumeEffect it = new ApplyStatusEffectsConsumeEffect(it, var65);
                     ((Collection)$consumeEffects).add(new PaperApplyStatusEffects(it));
                  }
               }
            }
         }
      } else if (Intrinsics.areEqual(key, "REMOVE_EFFECTS") && value instanceof List) {
         Iterable $this$filterIsInstance$iv = (Iterable)value;
         int $i$f$filterIsInstance = 0;
         Collection destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv : $this$filterIsInstance$iv) {
            if (element$iv$iv instanceof String) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         $this$filterIsInstance$iv = (Iterable)((List)destination$iv$iv);
         $i$f$filterIsInstance = 0;
         destination$iv$iv = (Collection)(new ArrayList());
         int $i$f$forEach = 0;

         for(Object element$iv$iv$iv : $this$filterIsInstance$iv) {
            int var16 = 0;
            String effect = (String)element$iv$iv$iv;
            int var18 = 0;

            Object instance;
            try {
               int applyEffect = 0;
               instance = ResultShim.ok(ResourceLocation.parse(effect));
            } catch (Throwable var27) {
               instance = ResultShim.ok(ResultKt.createFailure(var27));
            }

            Object mobEffect = instance;
            Throwable var74 = ResultShim.exceptionOrNull(instance);
            if (var74 != null) {
               Throwable it = var74;
               int var22 = 0;
               Logs.logError("Invalid potion effect: " + effect + ", in consumable-property!", false);
               if (Settings.DEBUG.toBool()) {
                  Logs.logError(ExceptionsKt.stackTraceToString(it), false);
               }
            }

            block: {
               ResourceLocation p0 = (ResourceLocation)(ResultShim.isFailure(mobEffect) ? null : mobEffect);
               if (p0 != null) {
                  net.minecraft.core.Registry var75 = BuiltInRegistries.MOB_EFFECT;
                  Intrinsics.checkNotNullExpressionValue(var75, "MOB_EFFECT");
                  net.minecraft.core.Registry var70 = var75;
                  int var24 = 0;
                  Optional var66 = var70.get(p0);
                  if (var66 != null) {
                     var76 = (Holder.Reference)OptionalsKt.getOrNull(var66);
                     break block;
                  }
               }

               var76 = null;
            }

            if (var76 != null) {
               Object it$iv$iv = var76;
               int var26 = 0;
               destination$iv$iv.add(it$iv$iv);
            }
         }

         List soundEffect = (List)destination$iv$iv;
         int $this$filterIsInstanceTo$iv$iv = 0;
         List soundKey = !soundEffect.isEmpty() ? soundEffect : null;
         if (soundKey != null) {
            $this$filterIsInstanceTo$iv$iv = 0;
            RemoveStatusEffectsConsumeEffect removeEffect = new RemoveStatusEffectsConsumeEffect((HolderSet)HolderSet.direct(soundKey));
            ((Collection)$consumeEffects).add(new PaperRemoveStatusEffects(removeEffect));
         }
      } else if (Intrinsics.areEqual(key, "CLEAR_ALL_EFFECTS")) {
         ((Collection)$consumeEffects).add(new PaperClearAllStatusEffects(new ClearAllStatusEffectsConsumeEffect()));
      } else if (Intrinsics.areEqual(key, "TELEPORT_RANDOMLY") && value instanceof ConfigurationSection) {
         TeleportRandomlyConsumeEffect teleportEffect = new TeleportRandomlyConsumeEffect((float)((ConfigurationSection)value).getDouble("diameter", (double)16.0F));
         ((Collection)$consumeEffects).add(new PaperTeleportRandomly(teleportEffect));
      } else if (Intrinsics.areEqual(key, "PLAY_SOUND") && value instanceof ConfigurationSection) {
         Key var10000 = NexoYamlKt.key((ConfigurationSection)value, "sound");
         if (var10000 == null) {
            return Unit.INSTANCE;
         }

         Key soundKey = var10000;
         Sound $i$f$filterIsInstance = (Sound)RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).get(soundKey);
         if ($i$f$filterIsInstance == null) {
            return Unit.INSTANCE;
         }

         int effectKey = 0;
         Holder element$iv = CraftSound.bukkitToMinecraftHolder($i$f$filterIsInstance);
         if (element$iv == null) {
            return Unit.INSTANCE;
         }

         int effectProperties = 0;
         PlaySoundConsumeEffect $i$f$forEach = new PlaySoundConsumeEffect(element$iv);
         ((Collection)$consumeEffects).add(new PaperPlaySound($i$f$forEach));
      }

      return Unit.INSTANCE;
   }

   static {
      Companion var0 = Companion;

      Object $this$getCraftStackMethod_u24lambda_u240;
      try {
         int var2 = 0;
         Class[] $this$getCraftStackMethod_u24lambda_u240_u240 = new Class[]{ItemStack.class};
         Method $this$getCraftStackMethod_u24lambda_u240_u240 = CraftItemStack.class.getDeclaredMethod("getCraftStack", $this$getCraftStackMethod_u24lambda_u240_u240);
         int var5 = 0;
         $this$getCraftStackMethod_u24lambda_u240_u240.setAccessible(true);
         $this$getCraftStackMethod_u24lambda_u240 = ResultShim.ok($this$getCraftStackMethod_u24lambda_u240_u240);
      } catch (Throwable var6) {
         $this$getCraftStackMethod_u24lambda_u240 = ResultShim.ok(ResultKt.createFailure(var6));
      }

      Object var7 = UtilsKt.printOnFailure($this$getCraftStackMethod_u24lambda_u240, false);
      getCraftStackMethod = (Method)(ResultShim.isFailure(var7) ? null : var7);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\n\u0010\u0006\u001a\u00020\u0007*\u00020\bR\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/nms/v1_21_R5/ItemUtils$Companion;", "", "<init>", "()V", "getCraftStackMethod", "Ljava/lang/reflect/Method;", "toCraftStack", "Lorg/bukkit/craftbukkit/inventory/CraftItemStack;", "Lorg/bukkit/inventory/ItemStack;", "com.nexomc:v1_21_R5"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final CraftItemStack toCraftStack(@NotNull ItemStack $this$toCraftStack) {
         Intrinsics.checkNotNullParameter($this$toCraftStack, "<this>");
         Method var10000 = ItemUtils.getCraftStackMethod;
         Intrinsics.checkNotNull(var10000);
         Object[] var2 = new Object[]{$this$toCraftStack};
         Object var3 = var10000.invoke((Object)null, var2);
         Intrinsics.checkNotNull(var3, "null cannot be cast to non-null type org.bukkit.craftbukkit.inventory.CraftItemStack");
         return (CraftItemStack)var3;
      }
   }
}

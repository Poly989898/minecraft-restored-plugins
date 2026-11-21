package com.nexomc.nexo.nms;

import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.utils.Colorable;
import com.nexomc.nexo.utils.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.functions.Function1;
import net.kyori.adventure.key.Key;
import org.bukkit.Art;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u001e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u0000 12\u00020\u0001:\u000212J\u0012\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u001a\u0010\u0006\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u0014\u0010\t\u001a\u0004\u0018\u00010\u00012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0016J\u0018\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u001a\u0010\t\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u0016J\u001e\u0010\u000f\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\u0011H\u0016J\u001a\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u0016J\u001e\u0010\u0013\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\u0011H\u0016J\u001a\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u0016J\u0014\u0010\u0014\u001a\u0004\u0018\u00010\u00012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0016J\u001e\u0010\u0014\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\u0011H\u0016J\u001a\u0010\u0014\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u0016J \u0010\u0018\u001a\u0010\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u001a\u0018\u00010\u00192\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0016J&\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\u0014\u0010\u001b\u001a\u0010\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u001a\u0018\u00010\u0019H\u0016J&\u0010\u001c\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\u0014\u0010\u001d\u001a\u0010\u0012\u0004\u0012\u00020\u001a\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0019H\u0016J\u0014\u0010\u001e\u001a\u0004\u0018\u00010\u00012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0016J\u0018\u0010\u001e\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u001f\u001a\u00020\rH\u0016J\u001a\u0010\u001e\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u0016J\u001a\u0010!\u001a\n\u0012\u0004\u0012\u00020\"\u0018\u00010\u00112\b\u0010#\u001a\u0004\u0018\u00010\rH\u0016J\u001e\u0010$\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00160&H\u0016J\u0018\u0010'\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010(\u001a\u00020)H\u0016J\u0010\u0010(\u001a\u00020)2\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u0018\u0010*\u001a\u00020\u00052\u0006\u0010+\u001a\u00020\u00052\u0006\u0010,\u001a\u00020\u0005H&J \u0010-\u001a\u00020\b2\u0006\u0010.\u001a\u00020/2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u00100\u001a\u00020)H\u0016¨\u00063À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/nms/IItemUtils;", "", "asColorable", "Lcom/nexomc/nexo/utils/Colorable;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "paintingVariant", "Lorg/bukkit/Art;", "", "consumableComponent", "itemBuilder", "Lcom/nexomc/nexo/items/ItemBuilder;", "consumableSection", "Lorg/bukkit/configuration/ConfigurationSection;", "consumable", "canPlaceOnComponent", "predicateSections", "", "predicate", "canBreakComponent", "repairableComponent", "repairableWith", "Lnet/kyori/adventure/key/Key;", "repairable", "blockstateComponent", "", "", "blockstates", "customDataComponent", "customData", "deathProtectionComponent", "deathProtectionSection", "deathProtection", "consumeEffectsFromSection", "Lio/papermc/paper/datacomponent/item/consumable/ConsumeEffect;", "effectsSection", "handleTooltipDisplay", "hiddenComponents", "", "setUnbreakable", "unbreakable", "", "copyItemNBTTags", "oldItem", "newItem", "dumpItem", "sender", "Lorg/bukkit/command/CommandSender;", "includeAllComponents", "Companion", "EmptyItemUtils", "com.nexomc:core"}
)
public interface IItemUtils {
   @NotNull
   Companion Companion = IItemUtils.Companion.$$INSTANCE;
   @JvmField
   @NotNull
   Set<String> vanillaKeys = SetsKt.setOf(new String[]{"PublicBukkitValues", "display", "CustomModelData", "Damage", "AttributeModifiers", "Unbreakable", "CanDestroy", "slot", "count", "HideFlags", "CanPlaceOn", "Enchantments", "StoredEnchantments", "RepairCost", "CustomPotionEffects", "Potion", "CustomPotionColor", "Trim", "EntityTag", "pages", "filtered_pages", "filtered_title", "resolved", "generation", "author", "title", "BucketVariantTag", "Items", "LodestoneTracked", "LodestoneDimension", "LodestonePos", "ChargedProjectiles", "Charged", "DebugProperty", "Fireworks", "Explosion", "Flight", "map", "map_scale_direction", "map_to_lock", "Decorations", "SkullOwner", "Effects", "BlockEntityTag", "BlockStateTag"});

   @Nullable
   Colorable asColorable(@NotNull ItemStack itemStack);

   @Nullable
   default Art paintingVariant(@NotNull ItemStack itemStack) {
      return null;
   }

   default void paintingVariant(@NotNull ItemStack itemStack, @Nullable Art paintingVariant) {
   }

   @Nullable
   default Object consumableComponent(@Nullable ItemStack itemStack) {
      return null;
   }

   default void consumableComponent(@NotNull ItemBuilder itemBuilder, @NotNull ConfigurationSection consumableSection) {
   }

   default void consumableComponent(@NotNull ItemStack itemStack, @Nullable Object consumable) {
   }

   default void canPlaceOnComponent(@NotNull ItemBuilder itemBuilder, @NotNull List<? extends ConfigurationSection> predicateSections) {
   }

   default void canPlaceOnComponent(@NotNull ItemStack itemStack, @Nullable Object predicate) {
   }

   default void canBreakComponent(@NotNull ItemBuilder itemBuilder, @NotNull List<? extends ConfigurationSection> predicateSections) {
   }

   default void canBreakComponent(@NotNull ItemStack itemStack, @Nullable Object predicate) {
   }

   @Nullable
   default Object repairableComponent(@Nullable ItemStack itemStack) {
      return null;
   }

   default void repairableComponent(@NotNull ItemBuilder itemBuilder, @NotNull List<? extends Key> repairableWith) {
   }

   default void repairableComponent(@NotNull ItemStack itemStack, @Nullable Object repairable) {
   }

   @Nullable
   default Map<String, String> blockstateComponent(@Nullable ItemStack itemStack) {
      return null;
   }

   default void blockstateComponent(@NotNull ItemStack itemStack, @Nullable Map<String, String> blockstates) {
   }

   default void customDataComponent(@NotNull ItemStack itemStack, @NotNull Map<String, ? extends Object> customData) {
   }

   @Nullable
   default Object deathProtectionComponent(@Nullable ItemStack itemStack) {
      return null;
   }

   default void deathProtectionComponent(@NotNull ItemBuilder itemBuilder, @NotNull ConfigurationSection deathProtectionSection) {
   }

   default void deathProtectionComponent(@NotNull ItemStack itemStack, @Nullable Object deathProtection) {
   }

   @Nullable
   default List<ConsumeEffect> consumeEffectsFromSection(@Nullable ConfigurationSection effectsSection) {
      return null;
   }

   default void handleTooltipDisplay(@NotNull ItemStack itemStack, @NotNull Collection<? extends Key> hiddenComponents) {
   }

   default void setUnbreakable(@NotNull ItemStack itemStack, boolean unbreakable) {
      if (unbreakable) {
         itemStack.setData(DataComponentTypes.UNBREAKABLE);
      } else {
         itemStack.unsetData((DataComponentType)DataComponentTypes.UNBREAKABLE);
      }

   }

   default boolean unbreakable(@NotNull ItemStack itemStack) {
      return itemStack.hasData((DataComponentType)DataComponentTypes.UNBREAKABLE);
   }

   @NotNull
   ItemStack copyItemNBTTags(@NotNull ItemStack itemStack, @NotNull ItemStack itemStack2);

   default void dumpItem(@NotNull CommandSender sender, @NotNull ItemStack itemStack, boolean includeAllComponents) {
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      @Nullable
      public static Art paintingVariant(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack) {
         return iItemUtils.paintingVariant(itemStack);
      }

      /** @deprecated */
      @Deprecated
      public static void paintingVariant(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack, @Nullable Art paintingVariant) {
         iItemUtils.paintingVariant(itemStack, paintingVariant);
      }

      /** @deprecated */
      @Deprecated
      @Nullable
      public static Object consumableComponent(@NotNull IItemUtils iItemUtils, @Nullable ItemStack itemStack) {
         return iItemUtils.consumableComponent(itemStack);
      }

      /** @deprecated */
      @Deprecated
      public static void consumableComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemBuilder itemBuilder, @NotNull ConfigurationSection consumableSection) {
         iItemUtils.consumableComponent(itemBuilder, consumableSection);
      }

      /** @deprecated */
      @Deprecated
      public static void consumableComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack, @Nullable Object consumable) {
         iItemUtils.consumableComponent(itemStack, consumable);
      }

      /** @deprecated */
      @Deprecated
      public static void canPlaceOnComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemBuilder itemBuilder, @NotNull List<? extends ConfigurationSection> predicateSections) {
         iItemUtils.canPlaceOnComponent(itemBuilder, predicateSections);
      }

      /** @deprecated */
      @Deprecated
      public static void canPlaceOnComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack, @Nullable Object predicate) {
         iItemUtils.canPlaceOnComponent(itemStack, predicate);
      }

      /** @deprecated */
      @Deprecated
      public static void canBreakComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemBuilder itemBuilder, @NotNull List<? extends ConfigurationSection> predicateSections) {
         iItemUtils.canBreakComponent(itemBuilder, predicateSections);
      }

      /** @deprecated */
      @Deprecated
      public static void canBreakComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack, @Nullable Object predicate) {
         iItemUtils.canBreakComponent(itemStack, predicate);
      }

      /** @deprecated */
      @Deprecated
      @Nullable
      public static Object repairableComponent(@NotNull IItemUtils iItemUtils, @Nullable ItemStack itemStack) {
         return iItemUtils.repairableComponent(itemStack);
      }

      /** @deprecated */
      @Deprecated
      public static void repairableComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemBuilder itemBuilder, @NotNull List<? extends Key> repairableWith) {
         iItemUtils.repairableComponent(itemBuilder, repairableWith);
      }

      /** @deprecated */
      @Deprecated
      public static void repairableComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack, @Nullable Object repairable) {
         iItemUtils.repairableComponent(itemStack, repairable);
      }

      /** @deprecated */
      @Deprecated
      @Nullable
      public static Map<String, String> blockstateComponent(@NotNull IItemUtils iItemUtils, @Nullable ItemStack itemStack) {
         return iItemUtils.blockstateComponent(itemStack);
      }

      /** @deprecated */
      @Deprecated
      public static void blockstateComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack, @Nullable Map<String, String> blockstates) {
         iItemUtils.blockstateComponent(itemStack, blockstates);
      }

      /** @deprecated */
      @Deprecated
      public static void customDataComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack, @NotNull Map<String, ? extends Object> customData) {
         iItemUtils.customDataComponent(itemStack, customData);
      }

      /** @deprecated */
      @Deprecated
      @Nullable
      public static Object deathProtectionComponent(@NotNull IItemUtils iItemUtils, @Nullable ItemStack itemStack) {
         return iItemUtils.deathProtectionComponent(itemStack);
      }

      /** @deprecated */
      @Deprecated
      public static void deathProtectionComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemBuilder itemBuilder, @NotNull ConfigurationSection deathProtectionSection) {
         iItemUtils.deathProtectionComponent(itemBuilder, deathProtectionSection);
      }

      /** @deprecated */
      @Deprecated
      public static void deathProtectionComponent(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack, @Nullable Object deathProtection) {
         iItemUtils.deathProtectionComponent(itemStack, deathProtection);
      }

      /** @deprecated */
      @Deprecated
      @Nullable
      public static List<ConsumeEffect> consumeEffectsFromSection(@NotNull IItemUtils iItemUtils, @Nullable ConfigurationSection effectsSection) {
         return iItemUtils.consumeEffectsFromSection(effectsSection);
      }

      /** @deprecated */
      @Deprecated
      public static void handleTooltipDisplay(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack, @NotNull Collection<? extends Key> hiddenComponents) {
         iItemUtils.handleTooltipDisplay(itemStack, hiddenComponents);
      }

      /** @deprecated */
      @Deprecated
      public static void setUnbreakable(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack, boolean unbreakable) {
         iItemUtils.setUnbreakable(itemStack, unbreakable);
      }

      /** @deprecated */
      @Deprecated
      public static boolean unbreakable(@NotNull IItemUtils iItemUtils, @NotNull ItemStack itemStack) {
         return iItemUtils.unbreakable(itemStack);
      }

      /** @deprecated */
      @Deprecated
      public static void dumpItem(@NotNull IItemUtils iItemUtils, @NotNull CommandSender sender, @NotNull ItemStack itemStack, boolean includeAllComponents) {
         iItemUtils.dumpItem(sender, itemStack, includeAllComponents);
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001e\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0007¢\u0006\u0002\n\u0000¨\u0006\u0001¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/nms/IItemUtils$Companion;", "", "<init>", "()V", "vanillaKeys", "", "", "Lkotlin/jvm/JvmField;", "com.nexomc:core"}
   )
   public static final class Companion {
      public static final Companion $$INSTANCE = new Companion();
      private Companion() {
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007H\u0016¨\u0006\u000b"},
      d2 = {"Lcom/nexomc/nexo/nms/IItemUtils$EmptyItemUtils;", "Lcom/nexomc/nexo/nms/IItemUtils;", "<init>", "()V", "asColorable", "Lcom/nexomc/nexo/utils/Colorable;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "copyItemNBTTags", "oldItem", "newItem", "com.nexomc:core"}
   )
   public static final class EmptyItemUtils implements IItemUtils {
      @Nullable
      public Colorable asColorable(@NotNull final ItemStack itemStack) {
         final DyedItemColor dyedColor = (DyedItemColor)itemStack.getData(DataComponentTypes.DYED_COLOR);
         final DyeColor baseColor = (DyeColor)itemStack.getData(DataComponentTypes.BASE_COLOR);
         final Function1 potionColorableSetLogic = (a0_ -> EmptyItemUtils.asColorable$lambda$0(itemStack, (Color)a0_));
         return (baseColor != null ? (Colorable)(new Colorable() {
            public Color getColor() {
               return baseColor.getColor();
            }

            public void setColor(Color value) {
               ItemUtils itemUtils = ItemUtils.INSTANCE;
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
               ItemStack itemStack3;
               DataComponentType.Valued valued3;
               DyedItemColor dyedItemColor2;
               block: {
                  ItemUtils itemUtils2 = ItemUtils.INSTANCE;
                  itemStack3 = itemStack;
                  DataComponentType.Valued valued2 = DataComponentTypes.DYED_COLOR;
                  valued3 = valued2;
                  if (value != null) {
                     DyedItemColor.Builder builder = DyedItemColor.dyedItemColor();
                     DyedItemColor.Builder builder2 = builder;
                     int value3 = 0;
                     builder = builder2.color(value);
                     if (builder != null) {
                        dyedItemColor2 = (DyedItemColor)builder.build();
                        break block;
                     }
                  }

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

      @NotNull
      public ItemStack copyItemNBTTags(@NotNull ItemStack oldItem, @NotNull ItemStack newItem) {
         return newItem;
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
   }
}

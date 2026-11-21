package com.nexomc.nexo.mechanics.custom_block;

import com.moulberry.axiom.paperapi.AxiomCustomBlocksAPI;
import com.moulberry.axiom.paperapi.block.AxiomCustomBlockBuilder;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.drops.Drop;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.ranges.IntRange;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u00020\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\n\u0010\u0006\u001a\u0004\u0018\u00010\u0007H&J\u0017\u0010\b\u001a\u0004\u0018\u00018\u00002\u0006\u0010\t\u001a\u00020\nH&¢\u0006\u0002\u0010\u000bJ\u0017\u0010\b\u001a\u0004\u0018\u00018\u00002\u0006\u0010\f\u001a\u00020\rH&¢\u0006\u0002\u0010\u000eJ\u000e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0010H&J=\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00028\u00002\u0006\u0010\u001a\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020\u001cH&¢\u0006\u0002\u0010\u001dJ\u001a\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0005H&J$\u0010!\u001a\u00020\"2\u0006\u0010\t\u001a\u00020\n2\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\u0010#\u001a\u0004\u0018\u00010$H&J\u001d\u0010%\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u0019\u001a\u00028\u0000H&¢\u0006\u0002\u0010&J\u001d\u0010'\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u0019\u001a\u00028\u0000H&¢\u0006\u0002\u0010&J\u0015\u0010(\u001a\u00020\u00122\u0006\u0010\u0019\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010)J\u001e\u0010*\u001a\u00020+2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020+0-2\u0006\u0010.\u001a\u00020+H\u0016R\u0018\u0010/\u001a\b\u0012\u0004\u0012\u00028\u000000X¦\u0004¢\u0006\u0006\u001a\u0004\b1\u00102¨\u00063À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockType;", "T", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "", "name", "", "factory", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "getMechanic", "block", "Lorg/bukkit/block/Block;", "(Lorg/bukkit/block/Block;)Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "blockData", "Lorg/bukkit/block/data/BlockData;", "(Lorg/bukkit/block/data/BlockData;)Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "toolTypes", "", "placeCustomBlock", "", "player", "Lorg/bukkit/entity/Player;", "hand", "Lorg/bukkit/inventory/EquipmentSlot;", "item", "Lorg/bukkit/inventory/ItemStack;", "mechanic", "placedAgainst", "blockFace", "Lorg/bukkit/block/BlockFace;", "(Lorg/bukkit/entity/Player;Lorg/bukkit/inventory/EquipmentSlot;Lorg/bukkit/inventory/ItemStack;Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/block/BlockFace;)V", "location", "Lorg/bukkit/Location;", "itemID", "removeCustomBlock", "", "overrideDrop", "Lcom/nexomc/nexo/utils/drops/Drop;", "placeWorldEdit", "(Lorg/bukkit/Location;Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;)V", "removeWorldEdit", "registerAxiom", "(Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;)V", "generateVariation", "", "usedVariations", "", "currentMax", "clazz", "Ljava/lang/Class;", "getClazz", "()Ljava/lang/Class;", "com.nexomc:core"}
)
public interface CustomBlockType<T extends CustomBlockMechanic> {
   @NotNull
   String name();

   @Nullable
   MechanicFactory factory();

   @Nullable
   T getMechanic(@NotNull Block block);

   @Nullable
   T getMechanic(@NotNull BlockData blockData);

   @NotNull
   List<String> toolTypes();

   void placeCustomBlock(@NotNull Player player, @NotNull EquipmentSlot equipmentSlot, @NotNull ItemStack itemStack, @NotNull T t, @NotNull Block block, @NotNull BlockFace blockFace);

   void placeCustomBlock(@NotNull Location location, @Nullable String str);

   boolean removeCustomBlock(@NotNull Block block, @Nullable Player player, @Nullable Drop drop);

   void placeWorldEdit(@NotNull Location location, @NotNull T t);

   void removeWorldEdit(@NotNull Location location, @NotNull T t);

   default void registerAxiom(@NotNull T mechanic) {
      java.lang.String str;
      ItemBuilder nexoItem;
      Key key;
      Component component;
      block: {
         nexoItem = NexoItems.itemFromId(((Mechanic)mechanic).getItemID());
         Key key2 = Key.key("nexo", ((Mechanic)mechanic).getItemID());
         key = key2;
         if (nexoItem != null) {
            component = nexoItem.getItemName();
            if (component != null) {
               break block;
            }
         }

         component = nexoItem != null ? nexoItem.getCustomName() : null;
      }

      block: {
         if (component != null) {
            Component p0 = component;
            MiniMessage minI_MESSAGE = AdventureUtils.INSTANCE.getMINI_MESSAGE();
            int value = 0;
            str = minI_MESSAGE.serialize(p0);
            if (str != null) {
               break block;
            }
         }

         str = ((Mechanic)mechanic).getItemID();
      }

      String translationKey = str;
      AxiomCustomBlockBuilder builder = AxiomCustomBlocksAPI.getAPI().createSingle(key, translationKey, mechanic.getBlockData());
      if (nexoItem != null) {
         Map customDataMap = nexoItem.getCustomDataMap();
         if (customDataMap != null) {
            customDataMap.put("Axiom", MapsKt.mapOf(TuplesKt.to("CustomBlockPlacer", key.asString())));
         }
      }

      builder.preventShapeUpdates(true);
      builder.pickBlockItemStack(nexoItem != null ? nexoItem.build() : null);
      AxiomCustomBlocksAPI.getAPI().register((Plugin)NexoPlugin.Companion.instance(), builder);
   }

   default int generateVariation(@NotNull Set<Integer> usedVariations, int currentMax) {
      Iterable iterable = (Iterable)(new IntRange(1, currentMax));
      Iterator iterator = iterable.iterator();

      Object obj;
      while(true) {
         if (iterator.hasNext()) {
            Object element = iterator.next();
            int it = ((Number)element).intValue();
            int value = 0;
            if (usedVariations.contains(it)) {
               continue;
            }

            obj = element;
            break;
         }

         obj = null;
         break;
      }

      return (Integer)obj != null ? (Integer)obj : currentMax + 1;
   }

   @NotNull
   Class<T> getClazz();

   @Metadata(
      mv = {2, 4, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      public static <T extends CustomBlockMechanic> void registerAxiom(@NotNull CustomBlockType<T> customBlockType, @NotNull T mechanic) {
         customBlockType.registerAxiom(mechanic);
      }

      /** @deprecated */
      @Deprecated
      public static <T extends CustomBlockMechanic> int generateVariation(@NotNull CustomBlockType<T> customBlockType, @NotNull Set<Integer> usedVariations, int currentMax) {
         return customBlockType.generateVariation(usedVariations, currentMax);
      }
   }
}

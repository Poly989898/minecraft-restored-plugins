package com.nexomc.nexo.mechanics.light;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.RangeUtilsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 *2\u00020\u0001:\u0001*B7\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n¢\u0006\u0004\b\u000b\u0010\fB\u0011\b\u0016\u0012\u0006\u0010\r\u001a\u00020\u000e¢\u0006\u0004\b\u000b\u0010\u000fJ\u001c\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020 0\u001f2\u0006\u0010!\u001a\u00020 2\u0006\u0010\"\u001a\u00020#J\u0016\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\u0014\u001a\u0004\u0018\u00010\u00158FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0018\u0010\u0019\u001a\u0004\b\u0016\u0010\u0017R\u001d\u0010\u001a\u001a\u0004\u0018\u00010\u00158FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001c\u0010\u0019\u001a\u0004\b\u001b\u0010\u0017R\u0011\u0010\u001d\u001a\u00020\u00068F¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u0013¨\u0006+"},
   d2 = {"Lcom/nexomc/nexo/mechanics/light/LightMechanic;", "", "lightBlocks", "", "Lcom/nexomc/nexo/mechanics/light/LightBlock;", "toggleable", "", "_toggledModel", "", "_toggledItemModel", "Lnet/kyori/adventure/key/Key;", "<init>", "(Ljava/util/List;ZLjava/lang/String;Lnet/kyori/adventure/key/Key;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getLightBlocks", "()Ljava/util/List;", "getToggleable", "()Z", "toggledModel", "Lcom/nexomc/nexo/items/ItemBuilder;", "getToggledModel", "()Lcom/nexomc/nexo/items/ItemBuilder;", "toggledModel$delegate", "Lkotlin/Lazy;", "toggledItemModel", "getToggledItemModel", "toggledItemModel$delegate", "isEmpty", "lightBlockLocations", "", "Lorg/bukkit/Location;", "center", "rotation", "", "refreshLights", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "Companion", "com.nexomc:core"}
)
public final class LightMechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final List<LightBlock> lightBlocks;
   private final boolean toggleable;
   @Nullable
   private final String _toggledModel;
   @Nullable
   private final Key _toggledItemModel;
   @NotNull
   private final Lazy toggledModel$delegate;
   @NotNull
   private final Lazy toggledItemModel$delegate;
   @NotNull
   private static final LightMechanic EMPTY = new LightMechanic((List)(new ArrayList()), false, null, null);

   public LightMechanic(@NotNull List<LightBlock> lightBlocks, boolean toggleable, @Nullable String _toggledModel, @Nullable Key _toggledItemModel) {
      super();
      this.lightBlocks = lightBlocks;
      this.toggleable = toggleable;
      this._toggledModel = _toggledModel;
      this._toggledItemModel = _toggledItemModel;
      this.toggledModel$delegate = LazyKt.lazy((() -> LightMechanic.toggledModel_delegate$lambda$0(this)));
      this.toggledItemModel$delegate = LazyKt.lazy((() -> LightMechanic.toggledItemModel_delegate$lambda$0(this)));
   }

   @NotNull
   public final List<LightBlock> getLightBlocks() {
      return this.lightBlocks;
   }

   public final boolean getToggleable() {
      return this.toggleable;
   }

   @Nullable
   public final ItemBuilder getToggledModel() {
      Lazy lazy = this.toggledModel$delegate;
      return (ItemBuilder)lazy.getValue();
   }

   @Nullable
   public final ItemBuilder getToggledItemModel() {
      Lazy lazy = this.toggledItemModel$delegate;
      return (ItemBuilder)lazy.getValue();
   }

   public LightMechanic(@NotNull ConfigurationSection section) {
      Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle(section, "lights.lights", (String)null);
      Companion companion = Companion;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         String p0 = (String)element;
         int value = 0;
         Iterable list = (Iterable)companion.parseLights(p0);
         CollectionsKt.addAll(destination, list);
      }

      this(CollectionsKt.toMutableList((Collection)((List)destination)), section.getBoolean("lights.toggleable"), section.getString("lights.toggled_model"), NexoYamlKt.key(section, "lights.toggled_item_model"));
   }

   public final boolean isEmpty() {
      Iterable iterable = (Iterable)this.lightBlocks;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = true;
      } else {
         for(Object element : iterable) {
            LightBlock it = (LightBlock)element;
            int value = 0;
            if (it.getLightLevel() != 0) {
               flag = false;
               return flag;
            }
         }

         flag = true;
      }

      return flag;
   }

   @NotNull
   public final List<Location> lightBlockLocations(@NotNull Location center, float rotation) {
      Iterable iterable = (Iterable)this.lightBlocks;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         LightBlock it = (LightBlock)item;
         int value = 0;
         destination.add(it.groundRotate(rotation).add(center));
      }

      return (List)destination;
   }

   public final void refreshLights(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager = iFurniturePacketManager;
            packetManager.removeLightMechanicPacket(baseEntity, mechanic);
            packetManager.sendLightMechanicPacket(baseEntity, mechanic);
            return;
         }
      }

   }

   private static final ItemBuilder toggledModel_delegate$lambda$0(LightMechanic lightMechanic) {
      return NexoItems.itemFromId(lightMechanic._toggledModel);
   }

   private static final ItemBuilder toggledItemModel_delegate$lambda$0(LightMechanic lightMechanic) {
      Key key = lightMechanic._toggledItemModel;
      ItemBuilder itemBuilder;
      if (key != null) {
         Key it = key;
         int value = 0;
         itemBuilder = (new ItemBuilder(Material.LEATHER_HORSE_ARMOR)).setItemModel(it);
      } else {
         itemBuilder = null;
      }

      return itemBuilder;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/light/LightMechanic$Companion;", "", "<init>", "()V", "EMPTY", "Lcom/nexomc/nexo/mechanics/light/LightMechanic;", "parseLights", "", "Lcom/nexomc/nexo/mechanics/light/LightBlock;", "hitboxString", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final List<LightBlock> parseLights(@NotNull String hitboxString) {
         List list;
         if (Intrinsics.areEqual(hitboxString, "origin")) {
            list = CollectionsKt.listOf(new LightBlock("0,0,0"));
         } else if (StringsKt.contains((CharSequence)hitboxString, (CharSequence)"..", false)) {
            CharSequence charSequence = (CharSequence)hitboxString;
            String[] strs = new String[]{","};
            Iterable iterable = (Iterable)StringsKt.split(charSequence, strs, false, 0);
            Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

            for(Object item : iterable) {
               String r = (String)item;
               int value = 0;
               IntRange intRange = RangeUtilsKt.toIntRangeOrNull(StringsKt.substringBefore(r, " ", r));
               if (intRange == null) {
                  Integer number = StringsKt.toIntOrNull(StringsKt.substringBefore(r, "", r));
                  int it = number != null ? number : 0;
                  int value2 = 0;
                  intRange = new IntRange(it, it);
               }

               destination.add(intRange);
            }

            List coordinates = (List)destination;
            IntRange xRange = (IntRange)coordinates.get(0);
            IntRange yRange = (IntRange)coordinates.get(1);
            IntRange zRange = (IntRange)coordinates.get(2);
            Integer number2 = StringsKt.toIntOrNull(StringsKt.substringAfter(hitboxString, " ", hitboxString));
            int lightLevel = number2 != null ? RangesKt.coerceIn((int)(number2), 1, 15) : 15;
            List list2 = (List)(new ArrayList());
            List list3 = list2;
            int value3 = 0;
            int x = xRange.getFirst();
            int last = xRange.getLast();
            if (x <= last) {
               while(true) {
                  int y = yRange.getFirst();
                  int value4 = yRange.getLast();
                  if (y <= value4) {
                     while(true) {
                        int z = zRange.getFirst();
                        int value5 = zRange.getLast();
                        if (z <= value5) {
                           while(true) {
                              ((Collection)list3).add(new LightBlock(x, y, z, lightLevel));
                              if (z == value5) {
                                 break;
                              }

                              ++z;
                           }
                        }

                        if (y == value4) {
                           break;
                        }

                        ++y;
                     }
                  }

                  if (x == last) {
                     break;
                  }

                  ++x;
               }
            }

            list = list2;
         } else {
            list = CollectionsKt.listOf(new LightBlock(hitboxString));
         }

         return list;
      }
   }
}

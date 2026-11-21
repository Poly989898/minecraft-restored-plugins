package com.nexomc.nexo.mechanics.furniture.placements;

import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0004\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u000bR\u001d\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\n¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00138F¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0015¨\u0006\u0017"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/placements/FurniturePlacement;", "", "parentSection", "Lorg/bukkit/configuration/ConfigurationSection;", "placementsSection", "factory", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;Lorg/bukkit/configuration/ConfigurationSection;Lcom/nexomc/nexo/mechanics/MechanicFactory;)V", "variants", "", "Lcom/nexomc/nexo/mechanics/furniture/placements/PlacementType;", "Lcom/nexomc/nexo/mechanics/furniture/placements/FurniturePlacementMechanic;", "getVariants", "()Ljava/util/Map;", "mechanicFor", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "type", "definedTypes", "", "getDefinedTypes", "()Ljava/util/Set;", "Companion", "com.nexomc:core"}
)
public final class FurniturePlacement {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Map<PlacementType, FurniturePlacementMechanic> variants;
   @NotNull
   private static final NamespacedKey PLACEMENT_KEY;

   public FurniturePlacement(@NotNull ConfigurationSection parentSection, @NotNull ConfigurationSection placementsSection, @NotNull MechanicFactory factory) {
      super();
      Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(PlacementType.values());
      LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         PlacementType type = (PlacementType)element;
         int value = 0;
         String str = type.name().toLowerCase(Locale.ROOT);
         ConfigurationSection configurationSection = NexoYamlKt.section(placementsSection, str);
         FurniturePlacementMechanic furniturePlacementMechanic;
         if (configurationSection == null) {
            furniturePlacementMechanic = null;
         } else {
            ConfigurationSection childSection = configurationSection;
            ConfigurationSection merged = Companion.mergeOntoParent(parentSection, childSection);
            furniturePlacementMechanic = new FurniturePlacementMechanic(factory, merged);
         }

         FurniturePlacementMechanic furniturePlacementMechanic2 = furniturePlacementMechanic;
         destination.put(element, furniturePlacementMechanic2);
      }

      Map map = CollectionUtilsKt.filterValuesNotNull(destination);
      FurniturePlacement furniturePlacement = this;
      Map map2 = map;
      if (map2.isEmpty()) {
         Logs.logWarn("Furniture " + NexoYamlKt.getRootId(parentSection) + " has a placements section but no valid placement types defined", false);
         map = MapsKt.emptyMap();
         furniturePlacement = this;
      } else {
         map = map2;
      }

      this.variants = map;
   }

   @NotNull
   public final Map<PlacementType, FurniturePlacementMechanic> getVariants() {
      return this.variants;
   }

   @Nullable
   public final FurnitureMechanic mechanicFor(@NotNull PlacementType type) {
      return (FurnitureMechanic)this.variants.get(type);
   }

   @NotNull
   public final Set<PlacementType> getDefinedTypes() {
      return this.variants.keySet();
   }

   static {
      NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:furniture_placement");
      PLACEMENT_KEY = namespacedKey;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000eH\u0002R\u0016\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0011"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/placements/FurniturePlacement$Companion;", "", "<init>", "()V", "PLACEMENT_KEY", "Lorg/bukkit/NamespacedKey;", "Lorg/jetbrains/annotations/Nullable;", "getPLACEMENT_KEY", "()Lorg/bukkit/NamespacedKey;", "placementType", "Lcom/nexomc/nexo/mechanics/furniture/placements/PlacementType;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mergeOntoParent", "Lorg/bukkit/configuration/ConfigurationSection;", "parent", "child", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getPLACEMENT_KEY() {
         return FurniturePlacement.PLACEMENT_KEY;
      }

      @Nullable
      public final PlacementType placementType(@NotNull ItemDisplay baseEntity) {
         String str = baseEntity.getPersistentDataContainer().get(this.getPLACEMENT_KEY(), PersistentDataType.STRING);
         PlacementType placementType;
         if (str != null) {
            String name = str;
            int value = 0;
            Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(PlacementType.values());
            Iterator iterator = iterable.iterator();

            Object obj;
            while(true) {
               if (!iterator.hasNext()) {
                  obj = null;
                  break;
               }

               Object element = iterator.next();
               PlacementType it = (PlacementType)element;
               int value2 = 0;
               if (Intrinsics.areEqual(it.name(), name)) {
                  obj = element;
                  break;
               }
            }

            placementType = (PlacementType)obj;
         } else {
            placementType = null;
         }

         return placementType;
      }

      private final ConfigurationSection mergeOntoParent(ConfigurationSection parent, ConfigurationSection child) {
         ConfigurationSection configurationSection = (new YamlConfiguration()).createSection(NexoYamlKt.getRootId(parent) + ".Mechanics.furniture");
         ConfigurationSection merged = configurationSection;
         NexoYaml.Companion.copyConfigurationSection(parent, merged);
         merged.set("placements", (Object)null);
         NexoYaml.Companion.copyConfigurationSection(child, merged);
         return merged;
      }
   }
}

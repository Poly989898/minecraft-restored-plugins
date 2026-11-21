package com.nexomc.nexo.utils.breaker;

import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\u001f\b\u0016\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bB-\b\u0016\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0007\u0010\nB-\b\u0016\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f¢\u0006\u0004\b\u0007\u0010\u000eJ\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003J\u0006\u0010\u0005\u001a\u00020\u0006J\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003J\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\u0003R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/utils/breaker/ToolTypeSpeedModifier;", "", "toolType", "", "Lorg/bukkit/Material;", "speedModifier", "", "<init>", "(Ljava/util/Set;F)V", "materials", "(Ljava/util/Set;FLjava/util/Set;)V", "customBlocks", "", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "(Ljava/util/Set;FLjava/util/Collection;)V", "toolTypes", "Companion", "com.nexomc:core"}
)
public final class ToolTypeSpeedModifier {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Set<Material> toolType;
   private final float speedModifier;
   @NotNull
   private final Set<Material> materials;
   @NotNull
   private final Set<CustomBlockMechanic> customBlocks;
   @JvmField
   @NotNull
   public static final ToolTypeSpeedModifier EMPTY;
   @JvmField
   @NotNull
   public static final Set<ToolTypeSpeedModifier> VANILLA;

   public ToolTypeSpeedModifier(@NotNull Set<Material> toolType, float speedModifier) {
      super();
      this.toolType = toolType;
      this.speedModifier = speedModifier;
      this.materials = (Set)(new HashSet());
      this.customBlocks = (Set)(new HashSet());
   }

   public ToolTypeSpeedModifier(@NotNull Set<Material> toolType, float speedModifier, @NotNull Set<Material> materials) {
      super();
      this.toolType = toolType;
      this.speedModifier = speedModifier;
      this.materials = materials;
      this.customBlocks = (Set)(new HashSet());
   }

   public ToolTypeSpeedModifier(@NotNull Set<Material> toolType, float speedModifier, @NotNull Collection<? extends CustomBlockMechanic> customBlocks) {
      super();
      this.toolType = toolType;
      this.speedModifier = speedModifier;
      this.materials = (Set)(new HashSet());
      this.customBlocks = (Set)(new HashSet(customBlocks));
   }

   @NotNull
   public final Set<Material> toolTypes() {
      return this.toolType;
   }

   public final float speedModifier() {
      return this.speedModifier;
   }

   @NotNull
   public final Set<Material> materials() {
      return this.materials;
   }

   @NotNull
   public final Set<CustomBlockMechanic> customBlocks() {
      return this.customBlocks;
   }

   static {
      Material[] materials2 = new Material[]{Material.AIR};
      EMPTY = new ToolTypeSpeedModifier(SetsKt.mutableSetOf(materials2), 1.0F);
      VANILLA = (Set)(new LinkedHashSet());
      VANILLA.add(EMPTY);
      Set itemTools = (Set)(new LinkedHashSet());
      Collection collection = (Collection)itemTools;
      Set values = Tag.ITEMS_SHOVELS.getValues();
      CollectionsKt.addAll(collection, (Iterable)values);
      collection = (Collection)itemTools;
      values = Tag.ITEMS_SWORDS.getValues();
      CollectionsKt.addAll(collection, (Iterable)values);
      collection = (Collection)itemTools;
      values = Tag.ITEMS_AXES.getValues();
      CollectionsKt.addAll(collection, (Iterable)values);
      collection = (Collection)itemTools;
      values = Tag.ITEMS_PICKAXES.getValues();
      CollectionsKt.addAll(collection, (Iterable)values);
      collection = (Collection)itemTools;
      values = Tag.ITEMS_HOES.getValues();
      CollectionsKt.addAll(collection, (Iterable)values);
      collection = (Collection)VANILLA;
      Iterable iterable = (Iterable)itemTools;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         Material it = (Material)element;
         int value = 0;
         if (StringsKt.startsWith(it.name(), "WOODEN_", false)) {
            destination.add(element);
         }
      }

      values = CollectionsKt.toSet((Iterable)((List)destination));
      float value2 = 2.0F;
      Set set = values;
      collection.add(new ToolTypeSpeedModifier(set, value2));
      collection = (Collection)VANILLA;
      iterable = (Iterable)itemTools;
      destination = (Collection)(new ArrayList());

      for(Object obj : iterable) {
         Material it = (Material)obj;
         int value3 = 0;
         if (StringsKt.startsWith(it.name(), "STONE_", false)) {
            destination.add(obj);
         }
      }

      values = CollectionsKt.toSet((Iterable)((List)destination));
      float value4 = 4.0F;
      Set set2 = values;
      collection.add(new ToolTypeSpeedModifier(set2, value4));
      collection = (Collection)VANILLA;
      iterable = (Iterable)itemTools;
      destination = (Collection)(new ArrayList());

      for(Object obj2 : iterable) {
         Material it = (Material)obj2;
         int value5 = 0;
         if (StringsKt.startsWith(it.name(), "IRON_", false)) {
            destination.add(obj2);
         }
      }

      values = CollectionsKt.toSet((Iterable)((List)destination));
      float value6 = 6.0F;
      Set set3 = values;
      collection.add(new ToolTypeSpeedModifier(set3, value6));
      collection = (Collection)VANILLA;
      iterable = (Iterable)itemTools;
      destination = (Collection)(new ArrayList());

      for(Object obj3 : iterable) {
         Material it = (Material)obj3;
         int value7 = 0;
         if (StringsKt.startsWith(it.name(), "DIAMOND_", false)) {
            destination.add(obj3);
         }
      }

      values = CollectionsKt.toSet((Iterable)((List)destination));
      float value8 = 8.0F;
      Set set4 = values;
      collection.add(new ToolTypeSpeedModifier(set4, value8));
      collection = (Collection)VANILLA;
      iterable = (Iterable)itemTools;
      destination = (Collection)(new ArrayList());

      for(Object obj4 : iterable) {
         Material it = (Material)obj4;
         int value9 = 0;
         if (StringsKt.startsWith(it.name(), "NETHERITE_", false)) {
            destination.add(obj4);
         }
      }

      values = CollectionsKt.toSet((Iterable)((List)destination));
      float value10 = 9.0F;
      Set set5 = values;
      collection.add(new ToolTypeSpeedModifier(set5, value10));
      collection = (Collection)VANILLA;
      iterable = (Iterable)itemTools;
      destination = (Collection)(new ArrayList());

      for(Object obj5 : iterable) {
         Material it = (Material)obj5;
         int value11 = 0;
         if (StringsKt.startsWith(it.name(), "GOLDEN_", false)) {
            destination.add(obj5);
         }
      }

      values = CollectionsKt.toSet((Iterable)((List)destination));
      float value12 = 12.0F;
      Set set6 = values;
      collection.add(new ToolTypeSpeedModifier(set6, value12));
      collection = (Collection)VANILLA;
      Material[] materials3 = new Material[]{Material.SHEARS};
      Set set7 = SetsKt.mutableSetOf(materials3);
      Set<Material> materials4 = Tag.LEAVES.getValues();
      collection.add(new ToolTypeSpeedModifier(set7, 15.0F, materials4));
      collection = (Collection)VANILLA;
      materials3 = new Material[]{Material.SHEARS};
      set7 = SetsKt.mutableSetOf(materials3);
      materials3 = new Material[]{Material.COBWEB};
      collection.add(new ToolTypeSpeedModifier(set7, 15.0F, SetsKt.mutableSetOf(materials3)));
      collection = (Collection)VANILLA;
      materials3 = new Material[]{Material.SHEARS};
      set7 = SetsKt.mutableSetOf(materials3);
      materials4 = Tag.WOOL.getValues();
      collection.add(new ToolTypeSpeedModifier(set7, 5.0F, materials4));
      collection = (Collection)VANILLA;
      materials3 = new Material[]{Material.SHEARS};
      collection.add(new ToolTypeSpeedModifier(SetsKt.mutableSetOf(materials3), 2.0F));
      collection = (Collection)VANILLA;
      set7 = Tag.ITEMS_SWORDS.getValues();
      materials4 = Tag.SWORD_EFFICIENT.getValues();
      collection.add(new ToolTypeSpeedModifier(set7, 15.0F, materials4));
      collection = (Collection)VANILLA;
      set7 = Tag.ITEMS_SWORDS.getValues();
      collection.add(new ToolTypeSpeedModifier(set7, 1.5F));
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0015\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0006¢\u0006\u0002\n\u0000R\u001b\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\b8\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0006¢\u0006\u0002\n\u0000¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/utils/breaker/ToolTypeSpeedModifier$Companion;", "", "<init>", "()V", "EMPTY", "Lcom/nexomc/nexo/utils/breaker/ToolTypeSpeedModifier;", "Lkotlin/jvm/JvmField;", "VANILLA", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}

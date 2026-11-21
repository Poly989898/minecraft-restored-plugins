package com.nexomc.nexo.mechanics.custom_block.stringblock.stackable;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0018\u0010\t\u001a\u0004\u0018\u00010\u00042\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0019\u0010\u000f\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0001J\u0014\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0013\u001a\u00020\u0014HÖ\u0081\u0004J\n\u0010\u0015\u001a\u00020\u0016HÖ\u0081\u0004R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/stackable/StackableBlock;", "", "stackables", "", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/stackable/StackableChild;", "<init>", "(Ljava/util/List;)V", "getStackables", "()Ljava/util/List;", "nextStackable", "block", "Lorg/bukkit/block/Block;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "Companion", "com.nexomc:core"}
)
public final class StackableBlock {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final List<StackableChild> stackables;

   public StackableBlock(@NotNull List<StackableChild> stackables) {
      super();
      this.stackables = stackables;
   }

   @NotNull
   public final List<StackableChild> getStackables() {
      return this.stackables;
   }

   @Nullable
   public final StackableChild nextStackable(@NotNull Block block, @NotNull StringBlockMechanic mechanic) {
      StackableChild stackableChild = (StackableChild)(NexoBlocks.stringMechanic(block));
      if (stackableChild == null) {
         return null;
      } else {
         StringBlockMechanic blockMechanic = stackableChild;
         if (Intrinsics.areEqual(mechanic.getItemID(), blockMechanic.getItemID())) {
            stackableChild = CollectionsKt.firstOrNull(this.stackables);
         } else if (blockMechanic instanceof StackableChild && Intrinsics.areEqual(((StackableChild)blockMechanic).getParentBlock(), mechanic)) {
            Collection collection = (Collection)this.stackables;
            Integer number = CollectionsKt.indexOf((Iterable)collection, blockMechanic);
            int it = ((Number)number).intValue();
            int value = 0;
            Integer number2 = it >= 0 ? number : null;
            if ((it >= 0 ? number : null) != null) {
               int element = number2 + 1;
               List list = this.stackables;
               it = 0;
               stackableChild = (StackableChild)CollectionsKt.getOrNull(list, element);
            } else {
               stackableChild = null;
            }
         } else {
            stackableChild = null;
         }

         return stackableChild;
      }
   }

   @NotNull
   public final List<StackableChild> component1() {
      return this.stackables;
   }

   @NotNull
   public final StackableBlock copy(@NotNull List<StackableChild> stackables) {
      return new StackableBlock(stackables);
   }

   @NotNull
   public String toString() {
      return "StackableBlock(stackables=" + this.stackables + ")";
   }

   public int hashCode() {
      return this.stackables.hashCode();
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof StackableBlock)) {
         return false;
      } else {
         StackableBlock stackableBlock = (StackableBlock)other;
         return Intrinsics.areEqual(this.stackables, stackableBlock.stackables);
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u0004\u0018\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007J\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00072\u0006\u0010\u000b\u001a\u00020\b¨\u0006\f"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/stackable/StackableBlock$Companion;", "", "<init>", "()V", "fromSections", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/stackable/StackableBlock;", "sections", "", "Lorg/bukkit/configuration/ConfigurationSection;", "extraVariations", "", "blockSection", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @Nullable
      public final StackableBlock fromSections(@NotNull List<? extends ConfigurationSection> sections) {
         Iterable iterable = (Iterable)sections;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));
         int index = 0;

         for(Object item : iterable) {
            int value = index++;
            if (value < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            ConfigurationSection s = (ConfigurationSection)item;
            int value2 = 0;
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            String rootId = NexoYamlKt.getRootId(s);
            ConfigurationSection configurationSection = yamlConfiguration.createSection(rootId + "_" + value);
            ConfigurationSection section = configurationSection;
            NexoYamlKt.plus(NexoYamlKt.copyFrom(section, s), "type", "STRINGBLOCK");
            destination.add(new StackableChild(section, value));
         }

         Collection collection = (Collection)((List)destination);
         Collection collection2;
         if (collection.isEmpty()) {
            collection2 = null;
         } else {
            collection2 = collection;
         }

         List p0 = (List)collection2;
         StackableBlock stackableBlock;
         if (p0 != null) {
            int value3 = 0;
            stackableBlock = new StackableBlock(p0);
         } else {
            stackableBlock = null;
         }

         return stackableBlock;
      }

      @NotNull
      public final List<Integer> extraVariations(@NotNull ConfigurationSection blockSection) {
         Iterable iterable = (Iterable)NexoYamlKt.sectionList(blockSection, "stackable");
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value = 0;
            ConfigurationSection it = (ConfigurationSection)element;
            int value2 = 0;
            Integer number = NexoYamlKt.intOrNull(it, "custom_variation");
            if (number != null) {
               Object obj = number;
               int value3 = 0;
               destination.add(obj);
            }
         }

         return (List)destination;
      }
   }
}

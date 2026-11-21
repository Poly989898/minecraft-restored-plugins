package com.nexomc.nexo.mechanics.custom_block.stringblock;

import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.stringblock.sapling.SaplingMechanic;
import com.nexomc.nexo.mechanics.custom_block.stringblock.stackable.StackableBlock;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0016\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\u001b\u001a\u00020\u0018H\u0016J\u0006\u0010\u001c\u001a\u00020\u000bJ\b\u0010\b\u001a\u0004\u0018\u00010\tJ\u0006\u0010\u001d\u001a\u00020\u000bJ\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011J\u0006\u0010\u001e\u001a\u00020\u000bJ\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0015\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\rR\u0016\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a¨\u0006\u001f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "factory", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "sapling", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/sapling/SaplingMechanic;", "tall", "", "getTall", "()Z", "placeableOnWater", "getPlaceableOnWater", "stackable", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/stackable/StackableBlock;", "randomPlace", "", "", "requireSupporting", "getRequireSupporting", "blockData", "Lorg/bukkit/block/data/type/Tripwire;", "getBlockData", "()Lorg/bukkit/block/data/type/Tripwire;", "createBlockData", "isSapling", "isStackable", "hasRandomPlace", "com.nexomc:core"}
)
public class StringBlockMechanic extends CustomBlockMechanic {
   @Nullable
   private final SaplingMechanic sapling;
   private final boolean tall;
   private final boolean placeableOnWater;
   @Nullable
   private final StackableBlock stackable;
   @NotNull
   private final List<String> randomPlace;
   private final boolean requireSupporting;
   @Nullable
   private final Tripwire blockData;

   public StringBlockMechanic(@NotNull StringBlockMechanicFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      StringBlockMechanic stringBlockMechanic = this;
      ConfigurationSection configurationSection = NexoYamlKt.section(section, "sapling");
      SaplingMechanic saplingMechanic;
      if (configurationSection != null) {
         ConfigurationSection it = configurationSection;
         int value = 0;
         saplingMechanic = new SaplingMechanic(this.getItemID(), it);
         stringBlockMechanic = this;
      } else {
         saplingMechanic = null;
      }

      this.sapling = saplingMechanic;
      this.tall = section.getBoolean("is_tall");
      this.placeableOnWater = section.getBoolean("placeable_on_water");
      StackableBlock.Companion companion = StackableBlock.Companion;
      Iterable iterable = (Iterable)NexoYamlKt.sectionListOrSingle(section, "stackable");
      StackableBlock.Companion companion2 = companion;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         ConfigurationSection it = (ConfigurationSection)item;
         int value2 = 0;
         destination.add(NexoYamlKt.copyWithParent(it, NexoYamlKt.getRootId(section)));
      }

      List list = (List)destination;
      this.stackable = companion2.fromSections(list);
      List stringList = section.getStringList("random_place");
      this.randomPlace = stringList;
      this.requireSupporting = section.getBoolean("require_supporting", factory.getDefaultRequireSupporting());
      BlockData blockData2 = super.getBlockData();
      this.blockData = blockData2 instanceof Tripwire ? (Tripwire)blockData2 : null;
   }

   public final boolean getTall() {
      return this.tall;
   }

   public final boolean getPlaceableOnWater() {
      return this.placeableOnWater;
   }

   public final boolean getRequireSupporting() {
      return this.requireSupporting;
   }

   @Nullable
   public Tripwire getBlockData() {
      return this.blockData;
   }

   @NotNull
   public Tripwire createBlockData() {
      return StringMechanicHelpers.INSTANCE.modernBlockData(this.getCustomVariation());
   }

   public final boolean isSapling() {
      return this.sapling != null;
   }

   @Nullable
   public final SaplingMechanic sapling() {
      return this.sapling;
   }

   public final boolean isStackable() {
      return this.stackable != null;
   }

   @Nullable
   public final StackableBlock stackable() {
      return this.stackable;
   }

   public final boolean hasRandomPlace() {
      return !((Collection)this.randomPlace).isEmpty();
   }

   @NotNull
   public final List<String> randomPlace() {
      return this.randomPlace;
   }
}

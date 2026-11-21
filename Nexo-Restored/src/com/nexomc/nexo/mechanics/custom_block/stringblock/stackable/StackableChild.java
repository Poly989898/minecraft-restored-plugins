package com.nexomc.nexo.mechanics.custom_block.stringblock.stackable;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanicFactory;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.drops.Loot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.blockstate.MultiVariant;
import team.unnamed.creative.blockstate.Variant;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001b\u0010\f\u001a\u00020\u00018FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\r\u0010\u000e¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/stackable/StackableChild;", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "childSection", "Lorg/bukkit/configuration/ConfigurationSection;", "index", "", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;I)V", "parentId", "", "getParentId", "()Ljava/lang/String;", "parentBlock", "getParentBlock", "()Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "parentBlock$delegate", "Lkotlin/Lazy;", "com.nexomc:core"}
)
public final class StackableChild extends StringBlockMechanic {
   private final int index;
   @NotNull
   private final String parentId;
   @NotNull
   private final Lazy parentBlock$delegate;

   public StackableChild(@NotNull ConfigurationSection childSection, int index) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory2;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value4 = 0;
            mechanicFactory2 = it;
            if (!(it instanceof StringBlockMechanicFactory)) {
               mechanicFactory2 = null;
            }

            it = (StringBlockMechanicFactory)mechanicFactory2;
            if (it == null) {
               continue;
            }

            mechanicFactory2 = it;
            break;
         }

         mechanicFactory2 = null;
         break;
      }

      super((StringBlockMechanicFactory)mechanicFactory2, childSection);
      this.index = index;
      this.parentId = StringsKt.substringBeforeLast(NexoYamlKt.getRootId(childSection), "_", NexoYamlKt.getRootId(childSection));
      this.parentBlock$delegate = LazyKt.lazy((() -> StackableChild.parentBlock_delegate$lambda$0(this)));
      NexoYamlKt.plus(childSection, "type", "STRINGBLOCK");
      Key key = NexoYamlKt.key(childSection, "model");
      if (key == null) {
         key = Key.key(NexoYamlKt.getRootId(childSection));
      }

      Key model = key;
      Variant[] variants = new Variant[]{Variant.builder().model(model).build()};
      MultiVariant variant = MultiVariant.of(variants);
      Iterable loots = (Iterable)this.getBreakable().getDrop().getLoots();
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(loots, 10)));

      for(Object item : loots) {
         Loot loot = (Loot)item;
         int value = 0;
         Loot loot2;
         if (!Intrinsics.areEqual(loot.getSourceID(), this.getItemID())) {
            loot2 = loot;
         } else {
            IntRange amount = new IntRange(loot.getAmount().getFirst() * (this.index + 2), loot.getAmount().getLast() * (this.index + 2));
            loot2 = loot.copy(this.parentId, (ItemStack)null, (double)0.0F, amount, false, (ConfigurationSection)null);
         }

         destination.add(loot2);
      }

      List stackableLoot = (List)destination;
      this.getBreakable().getDrop().getLoots().clear();
      CollectionsKt.addAll((Collection)this.getBreakable().getDrop().getLoots(), (Iterable)stackableLoot);
      MechanicsManager mechanicsManager2 = MechanicsManager.INSTANCE;
      Iterator iterator2 = ((Iterable)mechanicsManager2.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      while(true) {
         if (iterator2.hasNext()) {
            MechanicFactory mechanicFactory4 = (MechanicFactory)iterator2.next();
            int value2 = 0;
            MechanicFactory mechanicFactory3 = mechanicFactory4;
            if (!(mechanicFactory4 instanceof StringBlockMechanicFactory)) {
               mechanicFactory3 = null;
            }

            mechanicFactory4 = (StringBlockMechanicFactory)mechanicFactory3;
            if (mechanicFactory4 == null) {
               continue;
            }

            mechanicFactory = mechanicFactory4;
            break;
         }

         mechanicFactory = null;
         break;
      }

      StringBlockMechanicFactory stringBlockMechanicFactory = (StringBlockMechanicFactory)mechanicFactory;
      if (stringBlockMechanicFactory != null) {
         StringBlockMechanicFactory stringBlockMechanicFactory2 = stringBlockMechanicFactory;
         int value3 = 0;
         stringBlockMechanicFactory2.parse$com_nexomc_core(this);
         stringBlockMechanicFactory2.registerVariant(this, variant);
      }

   }

   @NotNull
   public final String getParentId() {
      return this.parentId;
   }

   @NotNull
   public final StringBlockMechanic getParentBlock() {
      Lazy lazy = this.parentBlock$delegate;
      return (StringBlockMechanic)lazy.getValue();
   }

   private static final StringBlockMechanic parentBlock_delegate$lambda$0(StackableChild stackableChild) {
      StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(stackableChild.parentId);
      return stringBlockMechanic;
   }
}

package com.nexomc.nexo.mechanics.custom_block.chorusblock;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockFactory;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Deprecated;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.blockstate.BlockState;
import team.unnamed.creative.blockstate.MultiVariant;
import team.unnamed.creative.blockstate.Variant;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 !2\u00020\u0001:\u0001!B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0015\u001a\u00020\u0016J\u0014\u0010\u0017\u001a\u0004\u0018\u00010\u000f2\b\u0010\u0018\u001a\u0004\u0018\u00010\bH\u0016J\u0014\u0010\u0017\u001a\u0004\u0018\u00010\u000f2\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0016J\u0012\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\u0006\u0010\u0002\u001a\u00020\u0003H\u0016J\u0010\u0010\u001d\u001a\u00020\b2\u0006\u0010\u001e\u001a\u00020\u000fH\u0002J\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u001f\u001a\u00020 R!\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\t\u0010\nR\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00140\u0013X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\""},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "toolTypes", "", "", "getToolTypes", "()Ljava/util/List;", "toolTypes$delegate", "Lkotlin/Lazy;", "BLOCK_PER_VARIATION", "Lit/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap;", "Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockMechanic;", "getBLOCK_PER_VARIATION", "()Lit/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap;", "variants", "Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;", "Lteam/unnamed/creative/blockstate/MultiVariant;", "generateBlockState", "Lteam/unnamed/creative/blockstate/BlockState;", "getMechanic", "itemID", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "parse", "Lcom/nexomc/nexo/mechanics/Mechanic;", "blockstateVariant", "mechanic", "blockData", "Lorg/bukkit/block/data/BlockData;", "Companion", "com.nexomc:core"}
)
public final class ChorusBlockFactory extends MechanicFactory {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Lazy toolTypes$delegate;
   @NotNull
   private final Int2ObjectLinkedOpenHashMap<ChorusBlockMechanic> BLOCK_PER_VARIATION;
   @NotNull
   private final Object2ObjectLinkedOpenHashMap<String, MultiVariant> variants;
   @NotNull
   private static final IntRange MAX_BLOCK_VARIATION = new IntRange(1, 63);
   @Nullable
   private static ChorusBlockFactory instance;

   public ChorusBlockFactory(@NotNull ConfigurationSection section) {
      super(section);
      this.toolTypes$delegate = LazyKt.lazy((() -> ChorusBlockFactory.toolTypes_delegate$lambda$0(section)));
      this.BLOCK_PER_VARIATION = new Int2ObjectLinkedOpenHashMap();
      this.variants = new Object2ObjectLinkedOpenHashMap();
      instance = this;
      Listener[] listeners = new Listener[]{new ChorusBlockListener()};
      this.registerListeners(listeners);
      if (!NMSHandlers.handler().chorusplantUpdateDisabled()) {
         Logs.logError("Papers block-updates.disable-chorus-plant-updates is not enabled.", false);
         Logs.logError("It is HIGHLY recommended to enable this setting for improved performance and prevent bugs with chorus-plants", false);
         Logs.logError("Otherwise Nexo needs to listen to very taxing events, which also introduces some bugs", false);
         Logs.logError("You can enable this setting in ServerFolder/config/paper-global.yml", true);
      }

   }

   @NotNull
   public final List<String> getToolTypes() {
      Lazy lazy = this.toolTypes$delegate;
      return (List)lazy.getValue();
   }

   @NotNull
   public final Int2ObjectLinkedOpenHashMap<ChorusBlockMechanic> getBLOCK_PER_VARIATION() {
      return this.BLOCK_PER_VARIATION;
   }

   @NotNull
   public final BlockState generateBlockState() {
      Key key = Key.key("minecraft:chorus_plant");
      Key chorusKey = key;
      Map map = (Map)this.variants;
      String str = "east=false,west=false,south=false,north=false,up=false,down=false";
      Variant[] variants2 = new Variant[]{Variant.builder().model(Key.key("minecraft:block/chorus_plant")).build()};
      MultiVariant multiVariant = MultiVariant.of(variants2);
      map.put(str, multiVariant);
      BlockState chorusState = NexoPlugin.Companion.instance().packGenerator().resourcePack().blockState(chorusKey);
      if (chorusState != null) {
         Map map2 = (Map)this.variants;
         Map map3 = chorusState.variants();
         map2.putAll(map3);
      }

      BlockState blockState = BlockState.of(chorusKey, (Map)this.variants);
      return blockState;
   }

   @Nullable
   public ChorusBlockMechanic getMechanic(@Nullable String itemID) {
      Mechanic mechanic = super.getMechanic(itemID);
      return mechanic instanceof ChorusBlockMechanic ? (ChorusBlockMechanic)mechanic : null;
   }

   @Nullable
   public ChorusBlockMechanic getMechanic(@Nullable ItemStack itemStack) {
      Mechanic mechanic = super.getMechanic(itemStack);
      return mechanic instanceof ChorusBlockMechanic ? (ChorusBlockMechanic)mechanic : null;
   }

   @Nullable
   public Mechanic parse(@NotNull ConfigurationSection section) {
      ChorusBlockMechanic mechanic = new ChorusBlockMechanic(this, section);
      IntRange it = MAX_BLOCK_VARIATION;
      int it_5 = it.getFirst();
      int last = it.getLast();
      int customVariation = mechanic.getCustomVariation();
      if (!(it_5 <= customVariation ? customVariation <= last : false)) {
         Logs.logError("The custom variation of the block " + mechanic.getItemID() + " is not between 1 and " + MAX_BLOCK_VARIATION.getLast() + "!", false);
         Logs.logWarn("The item has failed to build for now to prevent bugs and issues.", false);
         return null;
      } else {
         ChorusBlockMechanic it_4 = this.BLOCK_PER_VARIATION.get(mechanic.getCustomVariation());
         if (it_4 != null) {
            int value = 0;
            ChorusBlockMechanic it_3 = !Intrinsics.areEqual(it_4.getItemID(), mechanic.getItemID()) ? it_4 : null;
            if (it_3 != null) {
               value = 0;
               String itemID = mechanic.getItemID();
               Logs.logError(itemID + " is set to use custom_variation " + mechanic.getCustomVariation() + " but it is already used by " + it_3.getItemID(), false);
               Logs.logWarn("The item has failed to build for now to prevent bugs and issues.", false);
               return null;
            }
         }

         Map it_2 = (Map)this.variants;
         String it_1 = this.blockstateVariant(mechanic);
         Variant[] variants2 = new Variant[]{Variant.builder().model(mechanic.getModel()).build()};
         MultiVariant multiVariant = MultiVariant.of(variants2);
         it_2.put(it_1, multiVariant);
         ((Map)this.BLOCK_PER_VARIATION).put(mechanic.getCustomVariation(), mechanic);
         this.addToImplemented(mechanic);
         return mechanic;
      }
   }

   private final String blockstateVariant(ChorusBlockMechanic mechanic) {
      MultipleFacing data = mechanic.getBlockData();
      int value = 0;
      Set allowedFaces = data.getAllowedFaces();
      Iterable iterable = (Iterable)allowedFaces;
      return CollectionsKt.joinToString((Iterable)CollectionsKt.sortedWith(iterable, new ChorusBlockFactory$blockstateVariant$lambda$0$$inlined$sortedBy$1()), (CharSequence)",", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (a0_ -> ChorusBlockFactory.blockstateVariant$lambda$0$1(data, (BlockFace)a0_)));
   }

   @Nullable
   public final ChorusBlockMechanic getMechanic(@NotNull BlockData blockData) {
      java.lang.Object obj;
      if (blockData.getMaterial() != Material.CHORUS_PLANT) {
         return null;
      } else {
         Collection collection = this.BLOCK_PER_VARIATION.values();
         Iterable iterable = (Iterable)collection;
         Iterator iterator = iterable.iterator();

         while(true) {
            if (iterator.hasNext()) {
               Object element = iterator.next();
               ChorusBlockMechanic it = (ChorusBlockMechanic)element;
               int value = 0;
               if (!Intrinsics.areEqual(it.getBlockData(), blockData)) {
                  continue;
               }

               obj = element;
               break;
            }

            obj = null;
            break;
         }

         return (ChorusBlockMechanic)obj;
      }
   }

   private static final List toolTypes_delegate$lambda$0(ConfigurationSection section) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      List list = NexoYamlKt.stringListOrNull(section, "tool_types");
      if (list == null) {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;

         block: {
            for(MechanicFactory it : (java.lang.Iterable<MechanicFactory>)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
               int value = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof CustomBlockFactory)) {
                  mechanicFactory2 = null;
               }

               it = (CustomBlockFactory)mechanicFactory2;
               if (it != null) {
                  mechanicFactory = it;
                  break block;
               }
            }

            mechanicFactory = null;
         }

         list = (CustomBlockFactory)mechanicFactory != null ? ((CustomBlockFactory)mechanicFactory).getToolTypes() : null;
         if (list == null) {
            list = CollectionsKt.emptyList();
         }
      }

      return list;
   }

   private static final CharSequence blockstateVariant$lambda$0$1(MultipleFacing data, BlockFace it) {
      String str = it.name().toLowerCase(Locale.ROOT);
      return (CharSequence)(str + "=" + data.hasFace(it));
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JJ\u0010\b\u001a\u0004\u0018\u00010\tH\u0007b0\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u000f\u0012\"\b\u0010\u0012\u001e\b\u000bB\u001a\b\u0011\u0012\b\b\u0012\u0012\u0004\b\b(\u0013\u0012\f\b\u0014\u0012\b\b\fJ\u0004\b\b(\u0015b\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018J\u0018\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u000b8F¢\u0006\u0006\u001a\u0004\b\n\u0010\f¨\u0006\u001f"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockFactory$Companion;", "", "<init>", "()V", "MAX_BLOCK_VARIATION", "Lkotlin/ranges/IntRange;", "getMAX_BLOCK_VARIATION", "()Lkotlin/ranges/IntRange;", "instance", "Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockFactory;", "isEnabled", "", "()Z", "Lkotlin/Deprecated;", "message", "Use MechanicsManager.factory<ChorusBlockFactory>() instead.", "replaceWith", "Lkotlin/ReplaceWith;", "expression", "MechanicsManager.factory<ChorusBlockFactory>()", "imports", "com.nexomc.nexo.mechanics.MechanicsManager", "Lorg/jetbrains/annotations/ApiStatus$ScheduledForRemoval;", "inVersion", "1.24.0", "setBlockModel", "", "block", "Lorg/bukkit/block/Block;", "itemId", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final IntRange getMAX_BLOCK_VARIATION() {
         return ChorusBlockFactory.MAX_BLOCK_VARIATION;
      }

      public final boolean isEnabled() {
         return ChorusBlockFactory.instance != null;
      }

      /** @deprecated */
      @Deprecated(
         message = "Use MechanicsManager.factory<ChorusBlockFactory>() instead.",
         replaceWith = @ReplaceWith(
   expression = "MechanicsManager.factory<ChorusBlockFactory>()",
   imports = {"com.nexomc.nexo.mechanics.MechanicsManager"}
)
      )
      @ScheduledForRemoval(
         inVersion = "1.24.0"
      )
      @Nullable
      public final ChorusBlockFactory instance() {
         return ChorusBlockFactory.instance;
      }

      public final void setBlockModel(@NotNull Block block, @Nullable String itemId) {
         ChorusBlockMechanic chorusBlockMechanic = NexoBlocks.chorusBlockMechanic(itemId);
         if (chorusBlockMechanic != null) {
            MultipleFacing blockData = chorusBlockMechanic.getBlockData();
            if (blockData != null) {
               block.setBlockData((BlockData)blockData);
               return;
            }
         }

      }
   }
}

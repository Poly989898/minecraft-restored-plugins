package com.nexomc.nexo.mechanics.custom_block.stringblock;

import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.compatibilities.worldedit.WrappedWorldEdit;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockFactory;
import com.nexomc.nexo.mechanics.custom_block.stringblock.sapling.SaplingListener;
import com.nexomc.nexo.mechanics.custom_block.stringblock.sapling.SaplingTask;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.TuplesKt;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Deprecated;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Triple;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Tripwire;
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
   d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 /2\u00020\u0001:\u0001/B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u001e\u001a\u00020\u001fJ\u0014\u0010 \u001a\u0004\u0018\u00010\u00182\b\u0010!\u001a\u0004\u0018\u00010\bH\u0016J\u0014\u0010 \u001a\u0004\u0018\u00010\u00182\b\u0010\"\u001a\u0004\u0018\u00010#H\u0016J\u0012\u0010$\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0002\u001a\u00020\u0003H\u0016J\u0017\u0010$\u001a\u0004\u0018\u00010\u00182\u0006\u0010%\u001a\u00020\u0018H\u0000¢\u0006\u0002\b&J\u0018\u0010)\u001a\u00020*2\u0006\u0010%\u001a\u00020\u00182\b\u0010+\u001a\u0004\u0018\u00010\u001dJ\u0006\u0010,\u001a\u00020*J\u0010\u0010 \u001a\u0004\u0018\u00010\u00182\u0006\u0010-\u001a\u00020.R!\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\t\u0010\nR\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0011\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0014\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0013R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u001a\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u001d0\u001cX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010'\u001a\b\u0012\u0004\u0012\u00020(0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00060"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanicFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "toolTypes", "", "", "getToolTypes", "()Ljava/util/List;", "toolTypes$delegate", "Lkotlin/Lazy;", "sapling", "", "saplingGrowthCheckDelay", "", "disableVanillaString", "getDisableVanillaString", "()Z", "defaultRequireSupporting", "getDefaultRequireSupporting", "BLOCK_PER_VARIATION", "Lit/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap;", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "getBLOCK_PER_VARIATION", "()Lit/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap;", "variants", "Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;", "Lteam/unnamed/creative/blockstate/MultiVariant;", "generateBlockState", "Lteam/unnamed/creative/blockstate/BlockState;", "getMechanic", "itemID", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "parse", "mechanic", "parse$com_nexomc_core", "variantFaces", "Lorg/bukkit/block/BlockFace;", "registerVariant", "", "variant", "registerSaplingMechanic", "blockData", "Lorg/bukkit/block/data/BlockData;", "Companion", "com.nexomc:core"}
)
public final class StringBlockMechanicFactory extends MechanicFactory {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Lazy toolTypes$delegate;
   private boolean sapling;
   private final int saplingGrowthCheckDelay;
   private final boolean disableVanillaString;
   private final boolean defaultRequireSupporting;
   @NotNull
   private final Int2ObjectLinkedOpenHashMap<StringBlockMechanic> BLOCK_PER_VARIATION;
   @NotNull
   private final Object2ObjectLinkedOpenHashMap<String, MultiVariant> variants;
   @NotNull
   private final List<BlockFace> variantFaces;
   @NotNull
   private static final IntRange MAX_BLOCK_VARIATION = new IntRange(1, 127);
   @Nullable
   private static StringBlockMechanicFactory instance;
   @Nullable
   private static WrappedTask saplingTask;

   public StringBlockMechanicFactory(@NotNull ConfigurationSection section) {
      super(section);
      this.toolTypes$delegate = LazyKt.lazy((() -> StringBlockMechanicFactory.toolTypes_delegate$lambda$0(section)));
      this.saplingGrowthCheckDelay = section.getInt("sapling_growth_check_delay");
      this.disableVanillaString = section.getBoolean("disable_vanilla_strings", true);
      this.defaultRequireSupporting = section.getBoolean("default_require_supporting", true);
      this.BLOCK_PER_VARIATION = new Int2ObjectLinkedOpenHashMap();
      this.variants = new Object2ObjectLinkedOpenHashMap();
      instance = this;
      Listener[] listeners = new Listener[]{new StringBlockMechanicListener(), new SaplingListener()};
      this.registerListeners(listeners);
      if (this.saplingGrowthCheckDelay > 0) {
         this.registerSaplingMechanic();
      }

      listeners = new Listener[]{new StringBlockMechanicPaperListener()};
      this.registerListeners(listeners);
      if (!NMSHandlers.handler().tripwireUpdatesDisabled()) {
         listeners = new Listener[]{new StringBlockMechanicPhysicsListener()};
         this.registerListeners(listeners);
         Logs.logError("Papers block-updates.disable-tripwire-updates is not enabled.", false);
         Logs.logError("It is HIGHLY recommended to enable this setting for improved performance and prevent bugs with tripwires", false);
         Logs.logError("Otherwise Nexo needs to listen to very taxing events, which also introduces some bugs", false);
         Logs.logError("You can enable this setting in ServerFolder/config/paper-global.yml", true);
      }

      BlockFace[] blockFaces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};
      this.variantFaces = CollectionsKt.listOf(blockFaces);
   }

   @NotNull
   public final List<String> getToolTypes() {
      Lazy lazy = this.toolTypes$delegate;
      return (List)lazy.getValue();
   }

   public final boolean getDisableVanillaString() {
      return this.disableVanillaString;
   }

   public final boolean getDefaultRequireSupporting() {
      return this.defaultRequireSupporting;
   }

   @NotNull
   public final Int2ObjectLinkedOpenHashMap<StringBlockMechanic> getBLOCK_PER_VARIATION() {
      return this.BLOCK_PER_VARIATION;
   }

   @NotNull
   public final BlockState generateBlockState() {
      Key key = Key.key("minecraft:tripwire");
      Key stringKey = key;
      Map map = (Map)this.variants;
      String str = "east=false,west=false,south=false,north=false,attached=false,disarmed=false,powered=false";
      Variant[] variants2 = new Variant[]{Variant.builder().model(Key.key("block/barrier")).build()};
      MultiVariant multiVariant = MultiVariant.of(variants2);
      map.put(str, multiVariant);
      BlockState stringState = NexoPlugin.Companion.instance().packGenerator().resourcePack().blockState(stringKey);
      if (stringState != null) {
         Map map2 = (Map)this.variants;
         Map map3 = stringState.variants();
         map2.putAll(map3);
      }

      BlockState blockState = BlockState.of(stringKey, (Map)this.variants);
      return blockState;
   }

   @Nullable
   public StringBlockMechanic getMechanic(@Nullable String itemID) {
      Mechanic mechanic = super.getMechanic(itemID);
      return mechanic instanceof StringBlockMechanic ? (StringBlockMechanic)mechanic : null;
   }

   @Nullable
   public StringBlockMechanic getMechanic(@Nullable ItemStack itemStack) {
      Mechanic mechanic = super.getMechanic(itemStack);
      return mechanic instanceof StringBlockMechanic ? (StringBlockMechanic)mechanic : null;
   }

   @Nullable
   public StringBlockMechanic parse(@NotNull ConfigurationSection section) {
      return this.parse$com_nexomc_core(new StringBlockMechanic(this, section));
   }

   @Nullable
   public final StringBlockMechanic parse$com_nexomc_core(@NotNull StringBlockMechanic mechanic) {
      IntRange it = MAX_BLOCK_VARIATION;
      int it_4 = it.getFirst();
      int last = it.getLast();
      int customVariation = mechanic.getCustomVariation();
      if (!(it_4 <= customVariation ? customVariation <= last : false)) {
         Logs.logError("The custom variation of the block " + mechanic.getItemID() + " is not between 1 and " + MAX_BLOCK_VARIATION.getLast() + "!", false);
         Logs.logWarn("The item has failed to build for now to prevent bugs and issues.", false);
         return null;
      } else {
         StringBlockMechanic it_3 = this.BLOCK_PER_VARIATION.get(mechanic.getCustomVariation());
         if (it_3 != null) {
            int value = 0;
            StringBlockMechanic it_2 = !Intrinsics.areEqual(it_3.getItemID(), mechanic.getItemID()) ? it_3 : null;
            if (it_2 != null) {
               value = 0;
               String itemID = mechanic.getItemID();
               Logs.logError(itemID + " is set to use custom_variation " + mechanic.getCustomVariation() + " but it is already used by " + it_2.getItemID(), false);
               Logs.logWarn("The item has failed to build for now to prevent bugs and issues.", false);
               return null;
            }
         }

         Variant[] it_1 = new Variant[]{Variant.builder().model(mechanic.getModel()).build()};
         this.registerVariant(mechanic, MultiVariant.of(it_1));
         ((Map)this.BLOCK_PER_VARIATION).put(mechanic.getCustomVariation(), mechanic);
         this.addToImplemented(mechanic);
         return mechanic;
      }
   }

   public final void registerVariant(@NotNull StringBlockMechanic mechanic, @Nullable MultiVariant variant) {
      String faces = CollectionsKt.joinToString((Iterable)this.variantFaces, (CharSequence)",", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (a0_ -> StringBlockMechanicFactory.registerVariant$lambda$0(mechanic, (BlockFace)a0_)));
      Tripwire blockData = mechanic.getBlockData();
      if (blockData != null) {
         Tripwire it = blockData;
         int value = 0;
         Triple triple = TuplesKt.to(kotlin.TuplesKt.to(it.isAttached(), it.isDisarmed()), it.isPowered());
         if (triple != null) {
            Triple triple2 = triple;
            boolean attached = (Boolean)triple2.component1();
            boolean disarmed = (Boolean)triple2.component2();
            boolean powered = (Boolean)triple2.component3();
            ((Map)this.variants).put(faces + ",attached=" + attached + ",disarmed=" + disarmed + ",powered=" + powered, variant);
            return;
         }
      }

   }

   public final void registerSaplingMechanic() {
      if (!this.sapling && WrappedWorldEdit.INSTANCE.getLoaded()) {
         WrappedTask wrappedTask = saplingTask;
         if (wrappedTask != null) {
            wrappedTask.cancel();
         }

         saplingTask = SchedulerUtils.INSTANCE.getFoliaScheduler().runTimer(new SaplingTask(this.saplingGrowthCheckDelay), 0L, (long)this.saplingGrowthCheckDelay * 20L);
         this.sapling = true;
      }
   }

   @Nullable
   public final StringBlockMechanic getMechanic(@NotNull BlockData blockData) {
      java.lang.Object obj;
      if (!(blockData instanceof Tripwire)) {
         return null;
      } else {
         Collection collection = this.BLOCK_PER_VARIATION.values();
         Iterable iterable = (Iterable)collection;
         Iterator iterator = iterable.iterator();

         while(true) {
            if (iterator.hasNext()) {
               Object element = iterator.next();
               StringBlockMechanic it = (StringBlockMechanic)element;
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

         return (StringBlockMechanic)obj;
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

   private static final CharSequence registerVariant$lambda$0(StringBlockMechanic mechanic, BlockFace it) {
      String str = it.name().toLowerCase(Locale.ROOT);
      Tripwire blockData = mechanic.getBlockData();
      return (CharSequence)(str + "=" + (blockData != null ? blockData.hasFace(it) : null));
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JJ\u0010\b\u001a\u0004\u0018\u00010\tH\u0007b0\b\u000f\u0012\b\b\u0010\u0012\u0004\b\b(\u0011\u0012\"\b\u0012\u0012\u001e\b\u000bB\u001a\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015\u0012\f\b\u0016\u0012\b\b\fJ\u0004\b\b(\u0017b\f\b\u0018\u0012\b\b\u0019\u0012\u0004\b\b(\u001aJ\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010 R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\f\u001a\u00020\r8F¢\u0006\u0006\u001a\u0004\b\f\u0010\u000e¨\u0006!"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanicFactory$Companion;", "", "<init>", "()V", "MAX_BLOCK_VARIATION", "Lkotlin/ranges/IntRange;", "getMAX_BLOCK_VARIATION", "()Lkotlin/ranges/IntRange;", "instance", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanicFactory;", "saplingTask", "Lcom/nexomc/libs/folialib/wrapper/task/WrappedTask;", "isEnabled", "", "()Z", "Lkotlin/Deprecated;", "message", "Use MechanicsManager.factory<StringBlockMechanicFactory>() instead.", "replaceWith", "Lkotlin/ReplaceWith;", "expression", "MechanicsManager.factory<StringBlockMechanicFactory>()", "imports", "com.nexomc.nexo.mechanics.MechanicsManager", "Lorg/jetbrains/annotations/ApiStatus$ScheduledForRemoval;", "inVersion", "1.24.0", "setBlockModel", "", "block", "Lorg/bukkit/block/Block;", "itemId", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final IntRange getMAX_BLOCK_VARIATION() {
         return StringBlockMechanicFactory.MAX_BLOCK_VARIATION;
      }

      public final boolean isEnabled() {
         return StringBlockMechanicFactory.instance != null;
      }

      /** @deprecated */
      @Deprecated(
         message = "Use MechanicsManager.factory<StringBlockMechanicFactory>() instead.",
         replaceWith = @ReplaceWith(
   expression = "MechanicsManager.factory<StringBlockMechanicFactory>()",
   imports = {"com.nexomc.nexo.mechanics.MechanicsManager"}
)
      )
      @ScheduledForRemoval(
         inVersion = "1.24.0"
      )
      @Nullable
      public final StringBlockMechanicFactory instance() {
         return StringBlockMechanicFactory.instance;
      }

      public final void setBlockModel(@NotNull Block block, @Nullable String itemId) {
         StringBlockMechanicFactory stringBlockMechanicFactory = StringBlockMechanicFactory.instance;
         if (stringBlockMechanicFactory != null) {
            StringBlockMechanic mechanic = stringBlockMechanicFactory.getMechanic(itemId);
            if (mechanic != null) {
               Tripwire blockData = mechanic.getBlockData();
               if (blockData != null) {
                  block.setBlockData((BlockData)blockData);
                  return;
               }
            }
         }

      }
   }
}

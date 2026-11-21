package com.nexomc.nexo.mechanics.custom_block.noteblock;

import com.nexomc.nexo.NexoBootstrap;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockFactory;
import com.nexomc.nexo.mechanics.custom_block.noteblock.beacon.BeaconListener;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock;
import com.nexomc.nexo.mechanics.custom_block.noteblock.logstrip.LogStripListener;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.NexoYamlKt;
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
import net.kyori.adventure.key.Key;
import org.bukkit.Instrument;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
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
   d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 &2\u00020\u0001:\u0001&B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0019\u001a\u00020\u001aJ\u0014\u0010\u001b\u001a\u0004\u0018\u00010\u00132\b\u0010\u001c\u001a\u0004\u0018\u00010\bH\u0016J\u0014\u0010\u001b\u001a\u0004\u0018\u00010\u00132\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0016J\u0012\u0010\u001f\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0002\u001a\u00020\u0003H\u0016J\u0018\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u00132\b\u0010#\u001a\u0004\u0018\u00010\u0018J\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u00132\u0006\u0010$\u001a\u00020%R!\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\t\u0010\nR\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001a\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00180\u0017X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006'"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanicFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "toolTypes", "", "", "getToolTypes", "()Ljava/util/List;", "toolTypes$delegate", "Lkotlin/Lazy;", "reimplementNoteblockFeatures", "", "getReimplementNoteblockFeatures", "()Z", "BLOCK_PER_VARIATION", "Lit/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap;", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "getBLOCK_PER_VARIATION", "()Lit/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap;", "variants", "Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;", "Lteam/unnamed/creative/blockstate/MultiVariant;", "generateBlockState", "Lteam/unnamed/creative/blockstate/BlockState;", "getMechanic", "itemID", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "parse", "registerVariant", "", "mechanic", "variant", "blockData", "Lorg/bukkit/block/data/BlockData;", "Companion", "com.nexomc:core"}
)
public final class NoteBlockMechanicFactory extends MechanicFactory {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Lazy toolTypes$delegate;
   private final boolean reimplementNoteblockFeatures;
   @NotNull
   private final Int2ObjectLinkedOpenHashMap<NoteBlockMechanic> BLOCK_PER_VARIATION;
   @NotNull
   private final Object2ObjectLinkedOpenHashMap<String, MultiVariant> variants;
   private static final int MAX_PER_INSTRUMENT = 50;
   private static final int MAX_BLOCK_VARIATION;
   @Nullable
   private static NoteBlockMechanicFactory instance;

   public NoteBlockMechanicFactory(@NotNull ConfigurationSection section) {
      super(section);
      this.toolTypes$delegate = LazyKt.lazy((() -> NoteBlockMechanicFactory.toolTypes_delegate$lambda$0(section)));
      this.reimplementNoteblockFeatures = section.getBoolean("reimplement_noteblock_features", false);
      this.BLOCK_PER_VARIATION = new Int2ObjectLinkedOpenHashMap();
      this.variants = new Object2ObjectLinkedOpenHashMap();
      instance = this;
      if (!NexoBootstrap.Companion.getBootsStrung()) {
         (new NoteBlockDatapack()).createDatapack();
      }

      Listener[] listeners = new Listener[]{new NoteBlockMechanicListener(), new LogStripListener(), new BeaconListener()};
      this.registerListeners(listeners);
      if (!NMSHandlers.handler().noteblockUpdatesDisabled()) {
         listeners = new Listener[]{new NoteBlockMechanicPhysicsListener()};
         this.registerListeners(listeners);
      }

      if (!NMSHandlers.handler().noteblockUpdatesDisabled()) {
         Logs.logError("Papers block-updates.disable-noteblock-updates is not enabled.", false);
         if (this.reimplementNoteblockFeatures) {
            Logs.logError("reimplement_noteblock_feature mechanic will not be enabled", false);
         }

         Logs.logError("It is HIGHLY recommended to enable this setting for improved performance and prevent bugs with noteblocks", false);
         Logs.logError("Otherwise Nexo needs to listen to very taxing events, which also introduces some bugs", false);
         Logs.logError("You can enable this setting in ServerFolder/config/paper-global.yml", true);
      } else if (this.reimplementNoteblockFeatures) {
         listeners = new Listener[]{new RegularNoteBlockListener()};
         this.registerListeners(listeners);
      }

   }

   @NotNull
   public final List<String> getToolTypes() {
      Lazy lazy = this.toolTypes$delegate;
      return (List)lazy.getValue();
   }

   public final boolean getReimplementNoteblockFeatures() {
      return this.reimplementNoteblockFeatures;
   }

   @NotNull
   public final Int2ObjectLinkedOpenHashMap<NoteBlockMechanic> getBLOCK_PER_VARIATION() {
      return this.BLOCK_PER_VARIATION;
   }

   @NotNull
   public final BlockState generateBlockState() {
      Key key = Key.key("minecraft:note_block");
      Key noteKey = key;
      Map map = (Map)this.variants;
      String str = "instrument=harp,powered=false,note=0";
      Variant[] variants2 = new Variant[]{Variant.builder().model(Key.key("block/note_block")).build()};
      MultiVariant multiVariant = MultiVariant.of(variants2);
      map.put(str, multiVariant);
      BlockState noteState = NexoPlugin.Companion.instance().packGenerator().resourcePack().blockState(noteKey);
      if (noteState != null) {
         Map map2 = (Map)this.variants;
         Map map3 = noteState.variants();
         map2.putAll(map3);
      }

      BlockState blockState = BlockState.of(noteKey, (Map)this.variants);
      return blockState;
   }

   @Nullable
   public NoteBlockMechanic getMechanic(@Nullable String itemID) {
      Mechanic mechanic = super.getMechanic(itemID);
      return mechanic instanceof NoteBlockMechanic ? (NoteBlockMechanic)mechanic : null;
   }

   @Nullable
   public NoteBlockMechanic getMechanic(@Nullable ItemStack itemStack) {
      Mechanic mechanic = super.getMechanic(itemStack);
      return mechanic instanceof NoteBlockMechanic ? (NoteBlockMechanic)mechanic : null;
   }

   @Nullable
   public NoteBlockMechanic parse(@NotNull ConfigurationSection section) {
      NoteBlockMechanic mechanic = new NoteBlockMechanic(this, section);
      int p0 = MAX_BLOCK_VARIATION;
      int it = mechanic.getCustomVariation();
      if (!(1 <= it ? it <= p0 : false)) {
         Logs.logError("The custom variation of the block " + mechanic.getItemID() + " is not between 1 and " + MAX_BLOCK_VARIATION + "!", false);
         Logs.logWarn("The item has failed to build for now to prevent bugs and issues.", false);
         return null;
      } else {
         NoteBlockMechanic p0_2 = this.BLOCK_PER_VARIATION.get(mechanic.getCustomVariation());
         if (p0_2 != null) {
            int value = 0;
            NoteBlockMechanic it_1 = !mechanic.allowedSameVariation(p0_2) ? p0_2 : null;
            if (it_1 != null) {
               value = 0;
               String itemID = mechanic.getItemID();
               Logs.logError(itemID + " is set to use custom_variation " + mechanic.getCustomVariation() + " but it is already used by " + it_1.getItemID(), false);
               Logs.logWarn("The item has failed to build for now to prevent bugs and issues.", false);
               return null;
            }
         }

         MultiVariant variantModel = Companion.directionalMultiVariant(mechanic);
         DirectionalBlock directional = mechanic.getDirectional();
         if (!(directional != null ? directional.isParentBlock() : false)) {
            this.registerVariant(mechanic, variantModel);
         }

         ((Map)this.BLOCK_PER_VARIATION).put(mechanic.getCustomVariation(), mechanic);
         this.addToImplemented(mechanic);
         return mechanic;
      }
   }

   public final void registerVariant(@NotNull NoteBlockMechanic mechanic, @Nullable MultiVariant variant) {
      NoteBlock blockData = mechanic.getBlockData();
      if (blockData != null) {
         NoteBlock it = blockData;
         int value = 0;
         Companion companion = Companion;
         Instrument instrument2 = it.getInstrument();
         Triple triple = TuplesKt.to(kotlin.TuplesKt.to(companion.instrumentName(instrument2), it.getNote().getId()), it.isPowered());
         if (triple != null) {
            Triple triple2 = triple;
            String instrument = (String)triple2.component1();
            byte note = ((Number)triple2.component2()).byteValue();
            boolean powered = (Boolean)triple2.component3();
            ((Map)this.variants).put("instrument=" + instrument + ",note=" + note + ",powered=" + powered, variant);
            return;
         }
      }

   }

   @Nullable
   public final NoteBlockMechanic getMechanic(@NotNull BlockData blockData) {
      java.lang.Object obj;
      if (!(blockData instanceof NoteBlock)) {
         return null;
      } else {
         Collection collection = this.BLOCK_PER_VARIATION.values();
         Iterable iterable = (Iterable)collection;
         Iterator iterator = iterable.iterator();

         while(true) {
            if (iterator.hasNext()) {
               Object element = iterator.next();
               NoteBlockMechanic it = (NoteBlockMechanic)element;
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

         return (NoteBlockMechanic)obj;
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

   static {
      MAX_BLOCK_VARIATION = NoteBlockMechanicFactory.EntriesMappings.entries$0.size() * 50 - 1;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JJ\u0010\t\u001a\u0004\u0018\u00010\nH\u0007b0\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u0010\u0012\"\b\u0011\u0012\u001e\b\u000bB\u001a\b\u0012\u0012\b\b\u0013\u0012\u0004\b\b(\u0014\u0012\f\b\u0015\u0012\b\b\fJ\u0004\b\b(\u0016b\f\b\u0017\u0012\b\b\u0018\u0012\u0004\b\b(\u0019J\u0018\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fJ\u0010\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\"H\u0002J\u0010\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u000b\u001a\u00020\f8F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\r¨\u0006'"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanicFactory$Companion;", "", "<init>", "()V", "MAX_PER_INSTRUMENT", "", "MAX_BLOCK_VARIATION", "getMAX_BLOCK_VARIATION", "()I", "instance", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanicFactory;", "isEnabled", "", "()Z", "Lkotlin/Deprecated;", "message", "Use MechanicsManager.factory<NoteBlockMechanicFactory>() instead.", "replaceWith", "Lkotlin/ReplaceWith;", "expression", "MechanicsManager.factory<NoteBlockMechanicFactory>()", "imports", "com.nexomc.nexo.mechanics.MechanicsManager", "Lorg/jetbrains/annotations/ApiStatus$ScheduledForRemoval;", "inVersion", "1.24.0", "setBlockModel", "", "block", "Lorg/bukkit/block/Block;", "itemId", "", "instrumentName", "instrument", "Lorg/bukkit/Instrument;", "directionalMultiVariant", "Lteam/unnamed/creative/blockstate/MultiVariant;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final int getMAX_BLOCK_VARIATION() {
         return NoteBlockMechanicFactory.MAX_BLOCK_VARIATION;
      }

      public final boolean isEnabled() {
         return NoteBlockMechanicFactory.instance != null;
      }

      /** @deprecated */
      @Deprecated(
         message = "Use MechanicsManager.factory<NoteBlockMechanicFactory>() instead.",
         replaceWith = @ReplaceWith(
   expression = "MechanicsManager.factory<NoteBlockMechanicFactory>()",
   imports = {"com.nexomc.nexo.mechanics.MechanicsManager"}
)
      )
      @ScheduledForRemoval(
         inVersion = "1.24.0"
      )
      @Nullable
      public final NoteBlockMechanicFactory instance() {
         return NoteBlockMechanicFactory.instance;
      }

      public final void setBlockModel(@NotNull Block block, @Nullable String itemId) {
         NoteBlockMechanic noteBlockMechanic = NexoBlocks.noteBlockMechanic(itemId);
         if (noteBlockMechanic != null) {
            NoteBlock blockData = noteBlockMechanic.getBlockData();
            if (blockData != null) {
               block.setBlockData((BlockData)blockData);
               return;
            }
         }

      }

      private final String instrumentName(Instrument instrument) {
         String str;
         switch (instrument) {
            case BASS_DRUM:
               str = "basedrum";
               break;
            case PIANO:
               str = "harp";
               break;
            case SNARE_DRUM:
               str = "snare";
               break;
            case STICKS:
               str = "hat";
               break;
            case BASS_GUITAR:
               str = "bass";
               break;
            default:
               str = instrument.name().toLowerCase(Locale.ROOT);
         }

         return str;
      }

      private final MultiVariant directionalMultiVariant(NoteBlockMechanic mechanic) {
         Key key;
         block: {
            DirectionalBlock directional = mechanic.getDirectional();
            if (directional != null) {
               NoteBlockMechanic parentMechanic = directional.getParentMechanic();
               if (parentMechanic != null) {
                  key = parentMechanic.getModel();
                  if (key != null) {
                     break block;
                  }
               }
            }

            key = mechanic.getModel();
         }

         Key modelKey;
         Variant.Builder variantBuilder;
         block: {
            modelKey = key;
            variantBuilder = Variant.builder();
            DirectionalBlock directionalBlock = mechanic.getDirectional();
            if (directionalBlock != null) {
               key = directionalBlock.directionalModel(mechanic);
               if (key != null) {
                  break block;
               }
            }

            key = modelKey;
         }

         Key subBlockModel = key;
         variantBuilder.model(subBlockModel);
         if (!Intrinsics.areEqual(subBlockModel, modelKey)) {
            Variant[] variants2 = new Variant[]{variantBuilder.build()};
            MultiVariant multiVariant = MultiVariant.of(variants2);
            return multiVariant;
         } else {
            DirectionalBlock directionalBlock2 = mechanic.getDirectional();
            if (directionalBlock2 != null) {
               NoteBlockMechanic noteBlockMechanic = directionalBlock2.getParentMechanic();
               if (noteBlockMechanic != null) {
                  DirectionalBlock directionalBlock3 = noteBlockMechanic.getDirectional();
                  if (directionalBlock3 != null) {
                     directionalBlock3.parseVariantBuilder(variantBuilder, mechanic);
                  }
               }
            }

            Variant[] variants3 = new Variant[]{variantBuilder.build()};
            MultiVariant multiVariant2 = MultiVariant.of(variants3);
            return multiVariant2;
         }
      }
   }

   public static final class EntriesMappings {
      public static final kotlin.enums.EnumEntries entries$0 = kotlin.enums.EnumEntriesKt.enumEntries(org.bukkit.Instrument.values());
   }
}

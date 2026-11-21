package com.nexomc.nexo.mechanics.custom_block;

import com.nexomc.nexo.compatibilities.blocklocker.BlockLockerMechanic;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.breakable.Breakable;
import com.nexomc.nexo.mechanics.light.LightMechanic;
import com.nexomc.nexo.mechanics.limitedplacing.LimitedPlacing;
import com.nexomc.nexo.mechanics.storage.StorageMechanic;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.actions.ClickAction;
import com.nexomc.nexo.utils.blocksounds.BlockSounds;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import net.kyori.adventure.key.Key;
import nl.rutgerkok.blocklocker.BlockLockerAPIv2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0090\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\b&\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\n\u0010:\u001a\u0004\u0018\u00010\u0015H\u0016J\u001a\u0010;\u001a\u0002022\b\u0010<\u001a\u0004\u0018\u00010=2\u0006\u0010>\u001a\u00020?H\u0016J\u0006\u0010@\u001a\u000202J\n\u00106\u001a\u0004\u0018\u000107H\u0016J\b\u0010A\u001a\u000202H\u0016J\u0006\u0010B\u001a\u000202J\u0006\u0010C\u001a\u000202J\u0006\u0010D\u001a\u000202J\u000e\u0010E\u001a\u00020F2\u0006\u0010<\u001a\u00020=R\u0015\u0010\b\u001a\u0006\u0012\u0002\b\u00030\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0016\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u001c\u001a\u00020\u001d¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0013\u0010 \u001a\u0004\u0018\u00010!¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0017\u0010$\u001a\b\u0012\u0004\u0012\u00020&0%¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u0013\u0010)\u001a\u0004\u0018\u00010*¢\u0006\b\n\u0000\u001a\u0004\b+\u0010,R\u0014\u0010-\u001a\u00020.X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0011\u00101\u001a\u000202¢\u0006\b\n\u0000\u001a\u0004\b1\u00103R\u0011\u00104\u001a\u000202¢\u0006\b\n\u0000\u001a\u0004\b5\u00103R\u0016\u00106\u001a\u0004\u0018\u000107X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b8\u00109¨\u0006G"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/MechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "type", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockType;", "getType", "()Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockType;", "model", "Lnet/kyori/adventure/key/Key;", "getModel", "()Lnet/kyori/adventure/key/Key;", "customVariation", "", "getCustomVariation", "()I", "blockData", "Lorg/bukkit/block/data/BlockData;", "getBlockData", "()Lorg/bukkit/block/data/BlockData;", "blockSounds", "Lcom/nexomc/nexo/utils/blocksounds/BlockSounds;", "getBlockSounds", "()Lcom/nexomc/nexo/utils/blocksounds/BlockSounds;", "light", "Lcom/nexomc/nexo/mechanics/light/LightMechanic;", "getLight", "()Lcom/nexomc/nexo/mechanics/light/LightMechanic;", "limitedPlacing", "Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing;", "getLimitedPlacing", "()Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing;", "clickActions", "", "Lcom/nexomc/nexo/utils/actions/ClickAction;", "getClickActions", "()Ljava/util/List;", "blockLocker", "Lcom/nexomc/nexo/compatibilities/blocklocker/BlockLockerMechanic;", "getBlockLocker", "()Lcom/nexomc/nexo/compatibilities/blocklocker/BlockLockerMechanic;", "breakable", "Lcom/nexomc/nexo/mechanics/breakable/Breakable;", "getBreakable", "()Lcom/nexomc/nexo/mechanics/breakable/Breakable;", "isBlastResistant", "", "()Z", "immovable", "getImmovable", "storage", "Lcom/nexomc/nexo/mechanics/storage/StorageMechanic;", "getStorage$com_nexomc_core", "()Lcom/nexomc/nexo/mechanics/storage/StorageMechanic;", "createBlockData", "isInteractable", "player", "Lorg/bukkit/entity/Player;", "block", "Lorg/bukkit/block/Block;", "isStorage", "hasLight", "hasLimitedPlacing", "hasBlockSounds", "hasClickActions", "runClickActions", "", "com.nexomc:core"}
)
public abstract class CustomBlockMechanic extends Mechanic {
   @NotNull
   private final CustomBlockType<?> type;
   @NotNull
   private final Key model;
   private final int customVariation;
   @Nullable
   private final BlockData blockData;
   @Nullable
   private final BlockSounds blockSounds;
   @NotNull
   private final LightMechanic light;
   @Nullable
   private final LimitedPlacing limitedPlacing;
   @NotNull
   private final List<ClickAction> clickActions;
   @Nullable
   private final BlockLockerMechanic blockLocker;
   @NotNull
   private final Breakable breakable;
   private final boolean isBlastResistant;
   private final boolean immovable;
   @Nullable
   private final StorageMechanic storage;

   public CustomBlockMechanic(@NotNull MechanicFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      CustomBlockType customBlockType = CustomBlockRegistry.INSTANCE.fromMechanicSection(section);
      if (customBlockType == null) {
         throw new IllegalArgumentException("Invalid CustomBlock-type defined in " + NexoYamlKt.getRootId(section));
      } else {
         this.type = customBlockType;
         String string = section.getString("model", NexoYamlKt.getRootSection(section).getString("Pack.model", this.getItemID()));
         Key key = Key.key(string);
         this.model = key;
         this.customVariation = section.getInt("custom_variation", CustomBlockRegistry.INSTANCE.generateVariation(this.model, this.type, true));
         this.blockData = this.createBlockData();
         CustomBlockMechanic customBlockMechanic = this;
         ConfigurationSection configurationSection = NexoYamlKt.section(section, "block_sounds");
         BlockSounds blockSounds2;
         if (configurationSection != null) {
            ConfigurationSection p0 = configurationSection;
            int value = 0;
            blockSounds2 = new BlockSounds(p0);
            customBlockMechanic = this;
         } else {
            blockSounds2 = null;
         }

         this.blockSounds = blockSounds2;
         this.light = new LightMechanic(section);
         customBlockMechanic = this;
         ConfigurationSection configurationSection2 = NexoYamlKt.section(section, "limited_placing");
         LimitedPlacing limitedPlacing2;
         if (configurationSection2 != null) {
            ConfigurationSection p0 = configurationSection2;
            int value2 = 0;
            limitedPlacing2 = new LimitedPlacing(p0);
            customBlockMechanic = this;
         } else {
            limitedPlacing2 = null;
         }

         this.limitedPlacing = limitedPlacing2;
         Iterable iterable = (Iterable)NexoYamlKt.sectionList(section, "clickActions");
         ClickAction.Companion companion = ClickAction.Companion;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value3 = 0;
            ConfigurationSection p0 = (ConfigurationSection)element;
            int value4 = 0;
            ClickAction clickAction = companion.from(p0);
            if (clickAction != null) {
               Object it = clickAction;
               int value5 = 0;
               destination.add(it);
            }
         }

         this.clickActions = (List)destination;
         customBlockMechanic = this;
         ConfigurationSection configurationSection3 = NexoYamlKt.section(section, "blocklocker");
         BlockLockerMechanic blockLockerMechanic;
         if (configurationSection3 != null) {
            ConfigurationSection p0 = configurationSection3;
            int value7 = 0;
            blockLockerMechanic = new BlockLockerMechanic(p0);
            customBlockMechanic = this;
         } else {
            blockLockerMechanic = null;
         }

         this.blockLocker = blockLockerMechanic;
         this.breakable = new Breakable(section);
         this.isBlastResistant = section.getBoolean("blast_resistant");
         this.immovable = section.getBoolean("immovable");
         customBlockMechanic = this;
         ConfigurationSection configurationSection4 = NexoYamlKt.section(section, "storage");
         StorageMechanic storageMechanic;
         if (configurationSection4 != null) {
            ConfigurationSection p0 = configurationSection4;
            int value6 = 0;
            storageMechanic = new StorageMechanic(p0);
            customBlockMechanic = this;
         } else {
            storageMechanic = null;
         }

         this.storage = storageMechanic;
      }
   }

   @NotNull
   public final CustomBlockType<?> getType() {
      return this.type;
   }

   @NotNull
   public final Key getModel() {
      return this.model;
   }

   public final int getCustomVariation() {
      return this.customVariation;
   }

   @Nullable
   public BlockData getBlockData() {
      return this.blockData;
   }

   @Nullable
   public BlockSounds getBlockSounds() {
      return this.blockSounds;
   }

   @NotNull
   public final LightMechanic getLight() {
      return this.light;
   }

   @Nullable
   public final LimitedPlacing getLimitedPlacing() {
      return this.limitedPlacing;
   }

   @NotNull
   public final List<ClickAction> getClickActions() {
      return this.clickActions;
   }

   @Nullable
   public final BlockLockerMechanic getBlockLocker() {
      return this.blockLocker;
   }

   @NotNull
   public Breakable getBreakable() {
      return this.breakable;
   }

   public final boolean isBlastResistant() {
      return this.isBlastResistant;
   }

   public final boolean getImmovable() {
      return this.immovable;
   }

   @Nullable
   public final StorageMechanic getStorage$com_nexomc_core() {
      return this.storage;
   }

   @Nullable
   public BlockData createBlockData() {
      return Material.AIR.createBlockData();
   }

   public boolean isInteractable(@Nullable Player player, @NotNull Block block) {
      boolean flag;
      if (!this.light.getToggleable() && ((Collection)this.clickActions).isEmpty() && !this.isStorage()) {
         if (this.blockLocker != null) {
            int value = 0;
            flag = BlockLockerAPIv2.isAllowed(player, block, true);
         } else {
            flag = false;
         }

         if (!flag) {
            flag = false;
            return flag;
         }
      }

      flag = true;
      return flag;
   }

   public final boolean isStorage() {
      return this.storage() != null;
   }

   @Nullable
   public StorageMechanic storage() {
      return this.storage;
   }

   public boolean hasLight() {
      return this.light.getLightBlocks().isEmpty();
   }

   public final boolean hasLimitedPlacing() {
      return this.limitedPlacing != null;
   }

   public final boolean hasBlockSounds() {
      return this.getBlockSounds() != null;
   }

   public final boolean hasClickActions() {
      return !((Collection)this.clickActions).isEmpty();
   }

   public final void runClickActions(@NotNull Player player) {
      Sequence sequence = SequencesKt.filter(CollectionsKt.asSequence((Iterable)this.clickActions), (a0_ -> CustomBlockMechanic.runClickActions$lambda$0(player, (ClickAction)a0_)));

      for(Object element : kotlin.sequences.SequencesKt.asIterable(sequence)) {
         ClickAction it = (ClickAction)element;
         int value = 0;
         it.performActions(player);
      }

   }

   private static final boolean runClickActions$lambda$0(Player player, ClickAction it) {
      return it.canRun(player);
   }
}

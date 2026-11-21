package com.nexomc.nexo.mechanics.custom_block;

import com.moulberry.axiom.paperapi.AxiomCustomBlocksAPI;
import com.moulberry.axiom.paperapi.block.AxiomCustomBlockBuilder;
import com.nexomc.customblockdata.CustomBlockData;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.events.custom_block.chorusblock.NexoChorusBlockBreakEvent;
import com.nexomc.nexo.api.events.custom_block.chorusblock.NexoChorusBlockDropLootEvent;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockBreakEvent;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockDropLootEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockBreakEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockDropLootEvent;
import com.nexomc.nexo.compatibilities.axiom.AxiomCompatibility;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.chorusblock.ChorusBlockFactory;
import com.nexomc.nexo.mechanics.custom_block.chorusblock.ChorusBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanicFactory;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteMechanicHelpers;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanicFactory;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringMechanicHelpers;
import com.nexomc.nexo.mechanics.custom_block.stringblock.sapling.SaplingMechanic;
import com.nexomc.nexo.mechanics.custom_block.stringblock.stackable.StackableChild;
import com.nexomc.nexo.mechanics.storage.StorageMechanic;
import com.nexomc.nexo.mechanics.storage.StorageType;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.blocksounds.BlockSounds;
import com.nexomc.nexo.utils.drops.Drop;
import com.nexomc.nexo.utils.logs.Logs;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Effect;
import org.bukkit.GameEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.blockstate.BlockState;
import team.unnamed.creative.sound.SoundRegistry;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 -2\u00020\u0001:\u0002,-B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"J\u000e\u0010#\u001a\u00020 2\u0006\u0010!\u001a\u00020\"J\u0018\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\n\u0010$\u001a\u0006\u0012\u0002\b\u00030\u0013J\u0012\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010\u0002\u001a\u00020\u0003H\u0016J\u0014\u0010'\u001a\u0004\u0018\u00010(2\b\u0010)\u001a\u0004\u0018\u00010\bH\u0016J\u0014\u0010'\u001a\u0004\u0018\u00010(2\b\u0010*\u001a\u0004\u0018\u00010+H\u0016R=\u0010\u0006\u001a.\u0012\f\u0012\n \t*\u0004\u0018\u00010\b0\b \t*\u0015\u0012\f\u0012\n \t*\u0004\u0018\u00010\b0\b0\u000b¢\u0006\u0002\b\n0\u0007¢\u0006\u0002\b\n¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u001d\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013¢\u0006\u000e\n\u0000\u0012\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0017\u0010\u0018R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0018R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0018¨\u0006."},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "toolTypes", "", "", "kotlin.jvm.PlatformType", "Lorg/jetbrains/annotations/NotNull;", "", "getToolTypes", "()Ljava/util/List;", "customSounds", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockFactory$CustomBlockSounds;", "getCustomSounds", "()Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockFactory$CustomBlockSounds;", "NOTEBLOCK", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockType;", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "getNOTEBLOCK$annotations", "()V", "getNOTEBLOCK", "()Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockType;", "STRINGBLOCK", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "getSTRINGBLOCK", "CHORUSBLOCK", "Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockMechanic;", "getCHORUSBLOCK", "blockStates", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "soundRegistries", "type", "parse", "Lcom/nexomc/nexo/mechanics/Mechanic;", "getMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "itemID", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "CustomBlockSounds", "Companion", "com.nexomc:core"}
)
public final class CustomBlockFactory extends MechanicFactory {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final List<String> toolTypes;
   @NotNull
   private final CustomBlockSounds customSounds;
   @NotNull
   private final CustomBlockType<NoteBlockMechanic> NOTEBLOCK;
   @NotNull
   private final CustomBlockType<StringBlockMechanic> STRINGBLOCK;
   @NotNull
   private final CustomBlockType<ChorusBlockMechanic> CHORUSBLOCK;
   @Nullable
   private static CustomBlockFactory instance;

   public CustomBlockFactory(@NotNull ConfigurationSection section) {
      super(section);
      List stringList = section.getStringList("tool_types");
      this.toolTypes = stringList;
      CustomBlockFactory customBlockFactory = this;
      ConfigurationSection configurationSection = NexoYamlKt.section(section, "custom_block_sounds");
      CustomBlockSounds customBlockSounds;
      if (configurationSection != null) {
         ConfigurationSection p0 = configurationSection;
         int value = 0;
         customBlockSounds = new CustomBlockSounds(p0);
         customBlockFactory = this;
      } else {
         customBlockSounds = new CustomBlockSounds(true);
      }

      this.customSounds = customBlockSounds;
      this.NOTEBLOCK = new CustomBlockType<NoteBlockMechanic>() {
         public String name() {
            return "NOTEBLOCK";
         }

         public NoteBlockMechanicFactory factory() {
            MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
            Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

            MechanicFactory mechanicFactory;
            while(true) {
               if (iterator.hasNext()) {
                  MechanicFactory mechanicFactory4 = (MechanicFactory)iterator.next();
                  int value2 = 0;
                  mechanicFactory = mechanicFactory4;
                  if (!(mechanicFactory4 instanceof NoteBlockMechanicFactory)) {
                     mechanicFactory = null;
                  }

                  mechanicFactory4 = (NoteBlockMechanicFactory)mechanicFactory;
                  if (mechanicFactory4 == null) {
                     continue;
                  }

                  mechanicFactory = mechanicFactory4;
                  break;
               }

               mechanicFactory = null;
               break;
            }

            return (NoteBlockMechanicFactory)mechanicFactory;
         }

         public NoteBlockMechanic getMechanic(Block block) {
            NoteBlockMechanicFactory factory = this.factory();
            NoteBlockMechanic noteBlockMechanic;
            if (factory != null) {
               BlockData blockData2 = block.getBlockData();
               noteBlockMechanic = factory.getMechanic(blockData2);
            } else {
               noteBlockMechanic = null;
            }

            return noteBlockMechanic;
         }

         public NoteBlockMechanic getMechanic(BlockData blockData) {
            NoteBlockMechanicFactory noteBlockMechanicFactory = this.factory();
            return noteBlockMechanicFactory != null ? noteBlockMechanicFactory.getMechanic(blockData) : null;
         }

         public List<String> toolTypes() {
            NoteBlockMechanicFactory noteBlockMechanicFactory2 = this.factory();
            List list;
            if (noteBlockMechanicFactory2 != null) {
               list = noteBlockMechanicFactory2.getToolTypes();
               if (list != null) {
                  return list;
               }
            }

            list = CustomBlockFactory.this.getToolTypes();
            return list;
         }

         public void placeCustomBlock(Location location, String itemID) {
            Block block2 = location.getBlock();
            Block block = block2;
            NoteBlockMechanicFactory.Companion.setBlockModel(block, itemID);
            NoteBlockMechanic noteBlockMechanic2 = this.getMechanic(block);
            if (noteBlockMechanic2 != null) {
               NoteBlockMechanic mechanic = noteBlockMechanic2;
               StorageMechanic storage = mechanic.storage();
               if ((storage != null ? storage.getStorageType() : null) == StorageType.STORAGE) {
                  BlockHelpers.INSTANCE.getPersistentDataContainer(block).set(StorageMechanic.Companion.getSTORAGE_KEY(), DataType.ITEM_STACK_ARRAY, new ItemStack[0]);
               }

               NoteMechanicHelpers.checkNoteBlockAbove(location);
            }
         }

         public void placeCustomBlock(Player player, EquipmentSlot hand, ItemStack item, NoteBlockMechanic mechanic, Block placedAgainst, BlockFace blockFace) {
            CustomBlockHelpers.INSTANCE.makePlayerPlaceBlock(player, hand, item, placedAgainst, blockFace, mechanic, (BlockData)mechanic.getBlockData());
         }

         public boolean removeCustomBlock(Block block, Player player, Drop overrideDrop) {
            ItemStack itemStack;
            block: {
               if (player != null) {
                  PlayerInventory inventory = player.getInventory();
                  if (inventory != null) {
                     itemStack = inventory.getItemInMainHand();
                     if (itemStack != null) {
                        break block;
                     }
                  }
               }

               itemStack = ItemStack.of(Material.AIR);
            }

            Pair pair = TuplesKt.to(itemStack, block.getLocation());
            ItemStack itemInHand = (ItemStack)pair.component1();
            Object component2 = pair.component2();
            Location loc = (Location)component2;
            NoteBlockMechanic noteBlockMechanic3 = this.getMechanic(block);
            if (noteBlockMechanic3 != null) {
               block: {
                  NoteBlockMechanic it = noteBlockMechanic3;
                  int value3 = 0;
                  DirectionalBlock directionalBlock = it.getDirectional();
                  if (directionalBlock != null) {
                     noteBlockMechanic3 = directionalBlock.getParentMechanic();
                     if (noteBlockMechanic3 != null) {
                        break block;
                     }
                  }

                  noteBlockMechanic3 = it;
               }

               if (noteBlockMechanic3 != null) {
                  NoteBlockMechanic mechanic = noteBlockMechanic3;
                  Drop drop2 = overrideDrop;
                  if (overrideDrop == null) {
                     drop2 = mechanic.getBreakable().getDrop();
                  }

                  Drop drop = drop2;
                  if (player != null) {
                     if (player.getGameMode() == GameMode.CREATIVE) {
                        drop = Drop.Companion.emptyDrop();
                     }

                     NexoNoteBlockBreakEvent noteBlockBreakEvent = new NexoNoteBlockBreakEvent(mechanic, block, player, drop);
                     if (!EventUtils.INSTANCE.call(noteBlockBreakEvent)) {
                        return false;
                     }

                     if (overrideDrop != null || player.getGameMode() != GameMode.CREATIVE) {
                        drop = noteBlockBreakEvent.getDrop();
                     }

                     block.getWorld().sendGameEvent((Entity)player, GameEvent.BLOCK_DESTROY, loc.toVector());
                     Collection nearbyPlayers = loc.getNearbyPlayers((double)64.0F);
                     Iterable iterable = (Iterable)nearbyPlayers;

                     for(Object element : iterable) {
                        Player it = (Player)element;
                        int value4 = 0;
                        if (!Intrinsics.areEqual(it, player)) {
                           it.playEffect(loc, Effect.STEP_SOUND, block.getBlockData());
                        }
                     }
                  }

                  if (!drop.isEmpty()) {
                     List loots = drop.spawns(loc, itemInHand);
                     if (!((Collection)loots).isEmpty() && player != null) {
                        EventUtils.INSTANCE.call(new NexoNoteBlockDropLootEvent(mechanic, block, player, loots));
                        if (itemInHand.hasData((DataComponentType)DataComponentTypes.TOOL)) {
                           player.damageItemStack(itemInHand, 1);
                        }
                     }
                  }

                  StorageMechanic storageMechanic = mechanic.storage();
                  if (storageMechanic != null) {
                     StorageMechanic it = storageMechanic;
                     int value5 = 0;
                     storageMechanic = it.getStorageType() == StorageType.STORAGE ? it : null;
                     if (storageMechanic != null) {
                        storageMechanic.dropStorageContent(block);
                     }
                  }

                  block.setType(Material.AIR);
                  NoteMechanicHelpers.checkNoteBlockAbove(loc);
                  return true;
               }
            }

            return false;
         }

         public void placeWorldEdit(Location location, NoteBlockMechanic mechanic) {
         }

         public void removeWorldEdit(Location location, NoteBlockMechanic mechanic) {
         }

         public void registerAxiom(NoteBlockMechanic mechanic) {
            if (mechanic.getDirectional() == null) {
               CustomBlockType.super.registerAxiom(mechanic);
            } else if (mechanic.getDirectional().isParentBlock()) {
               java.lang.String str;
               AxiomCompatibility.DirectionalBlocks directional;
               ItemBuilder nexoItem;
               Key key;
               Component component;
               block: {
                  directional = new AxiomCompatibility.DirectionalBlocks(mechanic.getDirectional());
                  nexoItem = NexoItems.itemFromId(mechanic.getItemID());
                  Key key2 = Key.key("nexo", mechanic.getItemID());
                  key = key2;
                  if (nexoItem != null) {
                     component = nexoItem.getItemName();
                     if (component != null) {
                        break block;
                     }
                  }

                  component = nexoItem != null ? nexoItem.getCustomName() : null;
               }

               block: {
                  if (component != null) {
                     Component p0 = component;
                     MiniMessage minI_MESSAGE = AdventureUtils.INSTANCE.getMINI_MESSAGE();
                     int value6 = 0;
                     str = (String)minI_MESSAGE.serialize(p0);
                     if (str != null) {
                        break block;
                     }
                  }

                  str = mechanic.getItemID();
               }

               String translationKey = str;
               AxiomCustomBlockBuilder axiomCustomBlockBuilder;
               switch (directional.getType()) {
                  case LOG:
                     Iterable blocks = (Iterable)directional.getBlocks();
                     Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(blocks, 10)));

                     for(Object obj3 : blocks) {
                        NoteBlockMechanic p0xx = (NoteBlockMechanic)obj3;
                        int value16 = 0;
                        destination.add(p0xx.getBlockData());
                     }

                     List list2 = (List)destination;
                     NoteBlock x = (NoteBlock)list2.get(0);
                     NoteBlock y = (NoteBlock)list2.get(1);
                     NoteBlock z = (NoteBlock)list2.get(2);
                     axiomCustomBlockBuilder = AxiomCustomBlocksAPI.getAPI().createAxis(key, translationKey, (BlockData)z, (BlockData)y, (BlockData)x);
                     break;
                  case FURNACE:
                     Iterable iterable2 = (Iterable)directional.getBlocks();
                     Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

                     for(Object obj4 : iterable2) {
                        NoteBlockMechanic p0x = (NoteBlockMechanic)obj4;
                        int value7 = 0;
                        collection2.add(p0x.getBlockData());
                     }

                     List list3 = (List)collection2;
                     NoteBlock n = (NoteBlock)list3.get(0);
                     NoteBlock e = (NoteBlock)list3.get(1);
                     NoteBlock s = (NoteBlock)list3.get(2);
                     NoteBlock w = (NoteBlock)list3.get(3);
                     axiomCustomBlockBuilder = AxiomCustomBlocksAPI.getAPI().createHorizontalFacing(key, translationKey, (BlockData)n, (BlockData)e, (BlockData)s, (BlockData)w);
                     break;
                  case DROPPER:
                  case BARREL:
                     if (directional.getType() == DirectionalBlock.DirectionalType.BARREL) {
                        return;
                     }

                     Iterable iterable3 = (Iterable)CollectionsKt.drop((Iterable)directional.getBlocks(), 2);
                     Collection collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));

                     for(Object obj5 : iterable3) {
                        NoteBlockMechanic p0xxx = (NoteBlockMechanic)obj5;
                        int value8 = 0;
                        collection3.add(p0xxx.getBlockData());
                     }

                     List list4 = (List)collection3;
                     NoteBlock n_13 = (NoteBlock)list4.get(0);
                     NoteBlock e_12 = (NoteBlock)list4.get(1);
                     NoteBlock s_11 = (NoteBlock)list4.get(2);
                     NoteBlock w_10 = (NoteBlock)list4.get(3);
                     Iterable iterable4 = (Iterable)CollectionsKt.take((Iterable)directional.getBlocks(), 2);
                     Collection collection4 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable4, 10)));

                     for(Object obj6 : iterable4) {
                        NoteBlockMechanic p0xxxx = (NoteBlockMechanic)obj6;
                        int value9 = 0;
                        collection4.add(p0xxxx.getBlockData());
                     }

                     List list7 = (List)collection4;
                     NoteBlock u = (NoteBlock)list7.get(0);
                     NoteBlock d = (NoteBlock)list7.get(1);
                     axiomCustomBlockBuilder = AxiomCustomBlocksAPI.getAPI().createFacing(key, translationKey, (BlockData)n_13, (BlockData)e_12, (BlockData)s_11, (BlockData)w_10, (BlockData)u, (BlockData)d);
                     break;
                  default:
                     throw new NoWhenBranchMatchedException();
               }

               AxiomCustomBlockBuilder builder = axiomCustomBlockBuilder;
               if (nexoItem != null) {
                  Map customDataMap = nexoItem.getCustomDataMap();
                  if (customDataMap != null) {
                     customDataMap.put("Axiom", MapsKt.mapOf(TuplesKt.to("CustomBlockPlacer", key.asString())));
                  }
               }

               builder.preventShapeUpdates(true);
               builder.pickBlockItemStack(nexoItem != null ? nexoItem.build() : null);
               AxiomCustomBlocksAPI.getAPI().register((Plugin)NexoPlugin.Companion.instance(), builder);
            }
         }

         public Class<NoteBlockMechanic> getClazz() {
            return NoteBlockMechanic.class;
         }
      };
      this.STRINGBLOCK = new CustomBlockType<StringBlockMechanic>() {
         public String name() {
            return "STRINGBLOCK";
         }

         public StringBlockMechanicFactory factory() {
            MechanicsManager mechanicsManager2 = MechanicsManager.INSTANCE;
            Iterator iterator2 = ((Iterable)mechanicsManager2.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

            MechanicFactory mechanicFactory2;
            while(true) {
               if (iterator2.hasNext()) {
                  MechanicFactory mechanicFactory5 = (MechanicFactory)iterator2.next();
                  int value10 = 0;
                  mechanicFactory2 = mechanicFactory5;
                  if (!(mechanicFactory5 instanceof StringBlockMechanicFactory)) {
                     mechanicFactory2 = null;
                  }

                  mechanicFactory5 = (StringBlockMechanicFactory)mechanicFactory2;
                  if (mechanicFactory5 == null) {
                     continue;
                  }

                  mechanicFactory2 = mechanicFactory5;
                  break;
               }

               mechanicFactory2 = null;
               break;
            }

            return (StringBlockMechanicFactory)mechanicFactory2;
         }

         public StringBlockMechanic getMechanic(Block block) {
            StringBlockMechanicFactory stringBlockMechanicFactory = this.factory();
            StringBlockMechanic stringBlockMechanic;
            if (stringBlockMechanicFactory != null) {
               BlockData blockData3 = block.getBlockData();
               stringBlockMechanic = stringBlockMechanicFactory.getMechanic(blockData3);
            } else {
               stringBlockMechanic = null;
            }

            return stringBlockMechanic;
         }

         public StringBlockMechanic getMechanic(BlockData blockData) {
            StringBlockMechanicFactory stringBlockMechanicFactory2 = this.factory();
            return stringBlockMechanicFactory2 != null ? stringBlockMechanicFactory2.getMechanic(blockData) : null;
         }

         public List<String> toolTypes() {
            StringBlockMechanicFactory stringBlockMechanicFactory3 = this.factory();
            List list5;
            if (stringBlockMechanicFactory3 != null) {
               list5 = stringBlockMechanicFactory3.getToolTypes();
               if (list5 != null) {
                  return list5;
               }
            }

            list5 = CustomBlockFactory.this.getToolTypes();
            return list5;
         }

         public void placeCustomBlock(Location location, String itemID) {
            Block block3 = location.getBlock();
            Block block = block3;
            block3 = block.getRelative(BlockFace.UP);
            Block blockAbove = block3;
            StringBlockMechanicFactory.Companion.setBlockModel(block, itemID);
            StringBlockMechanic stringBlockMechanic2 = this.getMechanic(block);
            if (stringBlockMechanic2 != null) {
               StringBlockMechanic mechanic = stringBlockMechanic2;
               if (mechanic.getTall()) {
                  if (!BlockHelpers.REPLACEABLE_BLOCKS.contains(blockAbove.getType())) {
                     return;
                  }

                  blockAbove.setType(Material.TRIPWIRE);
               }

               if (mechanic.isSapling()) {
                  SaplingMechanic sapling = mechanic.sapling();
                  if (sapling != null) {
                     SaplingMechanic it = sapling;
                     int value11 = 0;
                     sapling = it.getCanGrowNaturally() ? it : null;
                     if (sapling != null) {
                        SaplingMechanic it_4 = sapling;
                        value11 = 0;
                        BlockHelpers.INSTANCE.getPersistentDataContainer(block).set(SaplingMechanic.Companion.getSAPLING_KEY(), PersistentDataType.INTEGER, it_4.getNaturalGrowthTime());
                     }
                  }
               }

            }
         }

         public void placeCustomBlock(Player player, EquipmentSlot hand, ItemStack item, StringBlockMechanic mechanic, Block placedAgainst, BlockFace blockFace) {
            CustomBlockHelpers.INSTANCE.makePlayerPlaceBlock(player, hand, item, placedAgainst, blockFace, mechanic, (BlockData)mechanic.getBlockData());
         }

         public boolean removeCustomBlock(Block block, Player player, Drop overrideDrop) {
            org.bukkit.inventory.ItemStack itemStack2;
            StringBlockMechanic stringBlockMechanic3 = this.getMechanic(block);
            if (stringBlockMechanic3 != null) {
               StringBlockMechanic it;
               block: {
                  it = stringBlockMechanic3;
                  int value12 = 0;
                  StackableChild stackableChild = it instanceof StackableChild ? (StackableChild)it : null;
                  if (stackableChild != null) {
                     stringBlockMechanic3 = stackableChild.getParentBlock();
                     if (stringBlockMechanic3 != null) {
                        break block;
                     }
                  }

                  stringBlockMechanic3 = it;
               }

               Pair pair2 = TuplesKt.to(stringBlockMechanic3, it.getBreakable().getDrop());
               if (pair2 != null) {
                  StringBlockMechanic mechanic;
                  Drop defaultDrop;
                  block: {
                     Pair pair3 = pair2;
                     mechanic = (StringBlockMechanic)pair3.component1();
                     defaultDrop = (Drop)pair3.component2();
                     if (player != null) {
                        PlayerInventory playerInventory = player.getInventory();
                        if (playerInventory != null) {
                           itemStack2 = playerInventory.getItemInMainHand();
                           if (itemStack2 != null) {
                              break block;
                           }
                        }
                     }

                     itemStack2 = ItemStack.of(Material.AIR);
                  }

                  ItemStack itemInHand = itemStack2;
                  Drop drop3 = overrideDrop;
                  if (overrideDrop == null) {
                     drop3 = defaultDrop;
                  }

                  Drop drop = drop3;
                  if (player != null) {
                     if (player.getGameMode() == GameMode.CREATIVE) {
                        drop = Drop.Companion.emptyDrop();
                     }

                     NexoStringBlockBreakEvent wireBlockBreakEvent = new NexoStringBlockBreakEvent(mechanic, block, player, drop);
                     if (!EventUtils.INSTANCE.call(wireBlockBreakEvent)) {
                        return false;
                     }

                     if (overrideDrop != null || player.getGameMode() != GameMode.CREATIVE) {
                        drop = wireBlockBreakEvent.getDrop();
                     }

                     block.getWorld().sendGameEvent((Entity)player, GameEvent.BLOCK_DESTROY, block.getLocation().toVector());
                  }

                  if (!drop.isEmpty()) {
                     Location location2 = block.getLocation();
                     List loots = drop.spawns(location2, itemInHand);
                     if (!((Collection)loots).isEmpty() && player != null) {
                        EventUtils.INSTANCE.call(new NexoStringBlockDropLootEvent(mechanic, block, player, loots));
                     }
                  }

                  Block relative = block.getRelative(BlockFace.UP);
                  Block blockAbove = relative;
                  if (mechanic.getTall()) {
                     blockAbove.setType(Material.AIR);
                  }

                  block.setType(Material.AIR);
                  SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocation(block.getLocation(), t -> removeCustomBlock$lambda$3(block, blockAbove, this, player, overrideDrop, t));
                  return true;
               }
            }

            return false;
         }

         public void placeWorldEdit(Location location, StringBlockMechanic mechanic) {
         }

         public void removeWorldEdit(Location location, StringBlockMechanic mechanic) {
         }

         public Class<StringBlockMechanic> getClazz() {
            return StringBlockMechanic.class;
         }

         private static final void removeCustomBlock$lambda$3(Block block5, Block block6, CustomBlockType<StringBlockMechanic> customBlockType, Player player2, Drop drop5, WrappedTask it) {
            StringMechanicHelpers stringMechanicHelpers = StringMechanicHelpers.INSTANCE;
            Location location3 = block5.getLocation();
            stringMechanicHelpers.fixClientsideUpdate(location3);
            if (block6.getType() == Material.TRIPWIRE) {
               customBlockType.removeCustomBlock(block6, player2, drop5);
            }

         }
      };
      this.CHORUSBLOCK = new CustomBlockType<ChorusBlockMechanic>() {
         public String name() {
            return "CHORUSBLOCK";
         }

         public ChorusBlockFactory factory() {
            MechanicsManager mechanicsManager3 = MechanicsManager.INSTANCE;
            Iterator iterator3 = ((Iterable)mechanicsManager3.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

            MechanicFactory mechanicFactory3;
            while(true) {
               if (iterator3.hasNext()) {
                  MechanicFactory mechanicFactory6 = (MechanicFactory)iterator3.next();
                  int value13 = 0;
                  mechanicFactory3 = mechanicFactory6;
                  if (!(mechanicFactory6 instanceof ChorusBlockFactory)) {
                     mechanicFactory3 = null;
                  }

                  mechanicFactory6 = (ChorusBlockFactory)mechanicFactory3;
                  if (mechanicFactory6 == null) {
                     continue;
                  }

                  mechanicFactory3 = mechanicFactory6;
                  break;
               }

               mechanicFactory3 = null;
               break;
            }

            return (ChorusBlockFactory)mechanicFactory3;
         }

         public ChorusBlockMechanic getMechanic(Block block) {
            ChorusBlockFactory chorusBlockFactory = this.factory();
            ChorusBlockMechanic chorusBlockMechanic;
            if (chorusBlockFactory != null) {
               BlockData blockData4 = block.getBlockData();
               chorusBlockMechanic = chorusBlockFactory.getMechanic(blockData4);
            } else {
               chorusBlockMechanic = null;
            }

            return chorusBlockMechanic;
         }

         public ChorusBlockMechanic getMechanic(BlockData blockData) {
            ChorusBlockFactory chorusBlockFactory2 = this.factory();
            return chorusBlockFactory2 != null ? chorusBlockFactory2.getMechanic(blockData) : null;
         }

         public List<String> toolTypes() {
            ChorusBlockFactory chorusBlockFactory3 = this.factory();
            List list6;
            if (chorusBlockFactory3 != null) {
               list6 = chorusBlockFactory3.getToolTypes();
               if (list6 != null) {
                  return list6;
               }
            }

            list6 = CustomBlockFactory.this.getToolTypes();
            return list6;
         }

         public void placeCustomBlock(Location location, String itemID) {
            ChorusBlockFactory.Companion companion = ChorusBlockFactory.Companion;
            Block block4 = location.getBlock();
            companion.setBlockModel(block4, itemID);
         }

         public void placeCustomBlock(Player player, EquipmentSlot hand, ItemStack item, ChorusBlockMechanic mechanic, Block placedAgainst, BlockFace blockFace) {
            CustomBlockHelpers.INSTANCE.makePlayerPlaceBlock(player, hand, item, placedAgainst, blockFace, mechanic, (BlockData)mechanic.getBlockData());
         }

         public boolean removeCustomBlock(Block block, Player player, Drop overrideDrop) {
            ItemStack itemStack3;
            block: {
               if (player != null) {
                  PlayerInventory playerInventory2 = player.getInventory();
                  if (playerInventory2 != null) {
                     itemStack3 = playerInventory2.getItemInMainHand();
                     if (itemStack3 != null) {
                        break block;
                     }
                  }
               }

               itemStack3 = ItemStack.of(Material.AIR);
            }

            Pair pair4 = TuplesKt.to(itemStack3, block.getLocation());
            ItemStack itemInHand = (ItemStack)pair4.component1();
            Object obj = pair4.component2();
            Location loc = (Location)obj;
            ChorusBlockMechanic chorusBlockMechanic2 = this.getMechanic(block);
            if (chorusBlockMechanic2 == null) {
               return false;
            } else {
               ChorusBlockMechanic mechanic = chorusBlockMechanic2;
               Drop drop4 = overrideDrop;
               if (overrideDrop == null) {
                  drop4 = mechanic.getBreakable().getDrop();
               }

               Drop drop = drop4;
               if (player != null) {
                  if (player.getGameMode() == GameMode.CREATIVE) {
                     drop = Drop.Companion.emptyDrop();
                  }

                  NexoChorusBlockBreakEvent chorusBlockBreakEvent = new NexoChorusBlockBreakEvent(mechanic, block, player, drop);
                  if (!EventUtils.INSTANCE.call(chorusBlockBreakEvent)) {
                     return false;
                  }

                  if (overrideDrop != null || player.getGameMode() != GameMode.CREATIVE) {
                     drop = chorusBlockBreakEvent.getDrop();
                  }

                  block.getWorld().sendGameEvent((Entity)player, GameEvent.BLOCK_DESTROY, loc.toVector());
                  Collection collection = loc.getNearbyPlayers((double)64.0F);
                  Iterable iterable5 = (Iterable)collection;

                  for(Object obj7 : iterable5) {
                     Player it = (Player)obj7;
                     int value14 = 0;
                     if (!Intrinsics.areEqual(it, player)) {
                        it.playEffect(loc, Effect.STEP_SOUND, block.getBlockData());
                     }
                  }
               }

               if (!drop.isEmpty()) {
                  List loots = drop.spawns(loc, itemInHand);
                  if (!((Collection)loots).isEmpty() && player != null) {
                     EventUtils.INSTANCE.call(new NexoChorusBlockDropLootEvent(mechanic, block, player, loots));
                     if (itemInHand.hasData((DataComponentType)DataComponentTypes.TOOL)) {
                        player.damageItemStack(itemInHand, 1);
                     }
                  }
               }

               block.setType(Material.AIR);
               return true;
            }
         }

         public void placeWorldEdit(Location location, ChorusBlockMechanic mechanic) {
         }

         public void removeWorldEdit(Location location, ChorusBlockMechanic mechanic) {
         }

         public int generateVariation(Set<Integer> usedVariations, int currentMax) {
            Iterable iterable6 = (Iterable)(new IntRange(49, 63));
            Iterator iterator4 = iterable6.iterator();

            Object obj2;
            while(true) {
               if (iterator4.hasNext()) {
                  Object obj8 = iterator4.next();
                  int it = ((Number)obj8).intValue();
                  int value15 = 0;
                  if (usedVariations.contains(it)) {
                     continue;
                  }

                  obj2 = obj8;
                  break;
               }

               obj2 = null;
               break;
            }

            return (Integer)obj2 != null ? (Integer)obj2 : CustomBlockType.super.generateVariation(usedVariations, currentMax);
         }

         public Class<ChorusBlockMechanic> getClazz() {
            return ChorusBlockMechanic.class;
         }
      };
      instance = this;
      CustomBlockRegistry.INSTANCE.register(this.NOTEBLOCK);
      CustomBlockRegistry.INSTANCE.register(this.STRINGBLOCK);
      CustomBlockRegistry.INSTANCE.register(this.CHORUSBLOCK);
      Listener[] listeners = new Listener[]{new CustomBlockListener(), new CustomBlockMiningListener()};
      this.registerListeners(listeners);
      CustomBlockData.Companion.registerListener((Plugin)NexoPlugin.Companion.instance());
      if (this.customSounds.getEnabled()) {
         listeners = new Listener[]{new CustomBlockSoundListener(this.customSounds)};
         this.registerListeners(listeners);
      }

   }

   @NotNull
   public final List<String> getToolTypes() {
      return this.toolTypes;
   }

   @NotNull
   public final CustomBlockSounds getCustomSounds() {
      return this.customSounds;
   }

   @NotNull
   public final CustomBlockType<NoteBlockMechanic> getNOTEBLOCK() {
      return this.NOTEBLOCK;
   }

   @NotNull
   public final CustomBlockType<StringBlockMechanic> getSTRINGBLOCK() {
      return this.STRINGBLOCK;
   }

   @NotNull
   public final CustomBlockType<ChorusBlockMechanic> getCHORUSBLOCK() {
      return this.CHORUSBLOCK;
   }

   public final void blockStates(@NotNull ResourcePack resourcePack) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory2;
      java.util.Map<java.lang.String,team.unnamed.creative.blockstate.MultiVariant> map;
      List blockStates = (List)(new ArrayList());

      for(CustomBlockType type : CustomBlockRegistry.INSTANCE.getTypes()) {
         if (type.factory() == null) {
         }
      }

      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator p0 = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory3;
      while(true) {
         if (p0.hasNext()) {
            MechanicFactory it = (MechanicFactory)p0.next();
            int value = 0;
            mechanicFactory3 = it;
            if (!(it instanceof NoteBlockMechanicFactory)) {
               mechanicFactory3 = null;
            }

            it = (NoteBlockMechanicFactory)mechanicFactory3;
            if (it == null) {
               continue;
            }

            mechanicFactory3 = it;
            break;
         }

         mechanicFactory3 = null;
         break;
      }

      mechanicFactory3 = mechanicFactory3;
      if (mechanicFactory3 != null) {
         BlockState generateBlockState = ((NoteBlockMechanicFactory)mechanicFactory3).generateBlockState();
         if (generateBlockState != null) {
            BlockState p0_3 = generateBlockState;
            int value7 = 0;
            blockStates.add(p0_3);
         }
      }

      mechanicsManager = MechanicsManager.INSTANCE;
      p0 = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      while(true) {
         if (p0.hasNext()) {
            MechanicFactory mechanicFactory6 = (MechanicFactory)p0.next();
            int value2 = 0;
            MechanicFactory mechanicFactory4 = mechanicFactory6;
            if (!(mechanicFactory6 instanceof StringBlockMechanicFactory)) {
               mechanicFactory4 = null;
            }

            mechanicFactory6 = (StringBlockMechanicFactory)mechanicFactory4;
            if (mechanicFactory6 == null) {
               continue;
            }

            mechanicFactory = mechanicFactory6;
            break;
         }

         mechanicFactory = null;
         break;
      }

      StringBlockMechanicFactory stringBlockMechanicFactory = (StringBlockMechanicFactory)mechanicFactory;
      if (stringBlockMechanicFactory != null) {
         BlockState blockState2 = stringBlockMechanicFactory.generateBlockState();
         if (blockState2 != null) {
            BlockState p0_2 = blockState2;
            int value3 = 0;
            blockStates.add(p0_2);
         }
      }

      mechanicsManager = MechanicsManager.INSTANCE;
      p0 = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      while(true) {
         if (p0.hasNext()) {
            MechanicFactory mechanicFactory7 = (MechanicFactory)p0.next();
            int value4 = 0;
            MechanicFactory mechanicFactory5 = mechanicFactory7;
            if (!(mechanicFactory7 instanceof ChorusBlockFactory)) {
               mechanicFactory5 = null;
            }

            mechanicFactory7 = (ChorusBlockFactory)mechanicFactory5;
            if (mechanicFactory7 == null) {
               continue;
            }

            mechanicFactory2 = mechanicFactory7;
            break;
         }

         mechanicFactory2 = null;
         break;
      }

      ChorusBlockFactory chorusBlockFactory = (ChorusBlockFactory)mechanicFactory2;
      if (chorusBlockFactory != null) {
         BlockState blockState3 = chorusBlockFactory.generateBlockState();
         if (blockState3 != null) {
            BlockState p0_1 = blockState3;
            int value5 = 0;
            blockStates.add(p0_1);
         }
      }

      Iterable iterable = (Iterable)blockStates;

      for(Object element : iterable) {
         BlockState blockState;
         Map map2;
         block: {
            blockState = (BlockState)element;
            int value6 = 0;
            map = blockState.variants();
            BlockState blockState4 = resourcePack.blockState(blockState.key());
            if (blockState4 != null) {
               map2 = blockState4.variants();
               if (map2 != null) {
                  break block;
               }
            }

            map2 = MapsKt.emptyMap();
         }

         Map variants = MapsKt.plus(map, map2);
         BlockState.of(blockState.key(), variants).addTo(resourcePack);
      }

   }

   public final void soundRegistries(@NotNull ResourcePack resourcePack) {

      for(CustomBlockType type : CustomBlockRegistry.INSTANCE.getTypes()) {
         if (type.factory() == null) {
         }
      }

      if (this.customSounds.getEnabled()) {
         SoundRegistry[] soundRegistries = new SoundRegistry[]{BlockSounds.NEXO_WOOD_SOUND_REGISTRY, BlockSounds.VANILLA_WOOD_SOUND_REGISTRY};
         Object[] objs = soundRegistries;
         int value = 0;

         for(int i = soundRegistries.length; value < i; ++value) {
            Object element;
            Iterable iterable;
            Collection collection;
            block: {
               element = objs[value];
               int value2 = 0;
               collection = ((SoundRegistry)element).sounds();
               SoundRegistry soundRegistry = resourcePack.soundRegistry(((SoundRegistry)element).namespace());
               if (soundRegistry != null) {
                  Collection collection2 = soundRegistry.sounds();
                  if (collection2 != null) {
                     iterable = (Iterable)collection2;
                     break block;
                  }
               }

               iterable = (Iterable)CollectionsKt.emptyList();
            }

            List sounds = CollectionsKt.plus(collection, iterable);
            SoundRegistry.soundRegistry(((SoundRegistry)element).namespace(), (Collection)sounds).addTo(resourcePack);
         }
      }

   }

   @NotNull
   public final List<String> toolTypes(@NotNull CustomBlockType<?> type) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      List list;
      if (type == this.STRINGBLOCK) {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;

         block: {
            for(MechanicFactory it : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
               int value = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof StringBlockMechanicFactory)) {
                  mechanicFactory2 = null;
               }

               it = (StringBlockMechanicFactory)mechanicFactory2;
               if (it != null) {
                  mechanicFactory = it;
                  break block;
               }
            }

            mechanicFactory = null;
         }

         StringBlockMechanicFactory stringBlockMechanicFactory = (StringBlockMechanicFactory)mechanicFactory;
         if (stringBlockMechanicFactory != null) {
            list = stringBlockMechanicFactory.getToolTypes();
            if (list != null) {
               return list;
            }
         }

         list = this.toolTypes;
      } else if (type == this.NOTEBLOCK) {
         com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory3;
         MechanicsManager mechanicsManager2 = MechanicsManager.INSTANCE;

         block: {
            for(MechanicFactory mechanicFactory7 : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager2.getFACTORIES_BY_MECHANIC_ID().values()) {
               int value2 = 0;
               MechanicFactory mechanicFactory4 = mechanicFactory7;
               if (!(mechanicFactory7 instanceof NoteBlockMechanicFactory)) {
                  mechanicFactory4 = null;
               }

               mechanicFactory7 = (NoteBlockMechanicFactory)mechanicFactory4;
               if (mechanicFactory7 != null) {
                  mechanicFactory3 = mechanicFactory7;
                  break block;
               }
            }

            mechanicFactory3 = null;
         }

         NoteBlockMechanicFactory noteBlockMechanicFactory = (NoteBlockMechanicFactory)mechanicFactory3;
         if (noteBlockMechanicFactory != null) {
            list = noteBlockMechanicFactory.getToolTypes();
            if (list != null) {
               return list;
            }
         }

         list = this.toolTypes;
      } else if (type == this.CHORUSBLOCK) {
         com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory5;
         MechanicsManager mechanicsManager3 = MechanicsManager.INSTANCE;

         block: {
            for(MechanicFactory mechanicFactory8 : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager3.getFACTORIES_BY_MECHANIC_ID().values()) {
               int value3 = 0;
               MechanicFactory mechanicFactory6 = mechanicFactory8;
               if (!(mechanicFactory8 instanceof ChorusBlockFactory)) {
                  mechanicFactory6 = null;
               }

               mechanicFactory8 = (ChorusBlockFactory)mechanicFactory6;
               if (mechanicFactory8 != null) {
                  mechanicFactory5 = mechanicFactory8;
                  break block;
               }
            }

            mechanicFactory5 = null;
         }

         ChorusBlockFactory chorusBlockFactory = (ChorusBlockFactory)mechanicFactory5;
         if (chorusBlockFactory != null) {
            list = chorusBlockFactory.getToolTypes();
            if (list != null) {
               return list;
            }
         }

         list = this.toolTypes;
      } else {
         list = CollectionsKt.emptyList();
      }

      return list;
   }

   @Nullable
   public Mechanic parse(@NotNull ConfigurationSection section) {
      CustomBlockType customBlockType = CustomBlockRegistry.INSTANCE.fromMechanicSection(section);
      if (customBlockType == null) {
         return null;
      } else {
         CustomBlockType type = customBlockType;
         if (type.factory() == null) {
            Logs.logError(NexoYamlKt.getRootId(section) + " attempted to use " + type.name() + "-type but it has been disabled", false);
            return null;
         } else {
            if (!section.isInt("custom_variation")) {
               Integer variation = CustomBlockRegistry.INSTANCE.generateVariation(section);
               if (variation != null) {
                  section.set("custom_variation", variation);
               }
            }

            MechanicFactory factory = type.factory();
            CustomBlockMechanic customBlockMechanic = factory != null ? (CustomBlockMechanic)factory.parse(section) : null;
            CustomBlockMechanic customBlockMechanic2 = customBlockMechanic instanceof CustomBlockMechanic ? (CustomBlockMechanic)customBlockMechanic : null;
            if ((customBlockMechanic instanceof CustomBlockMechanic ? (CustomBlockMechanic)customBlockMechanic : null) != null) {
               customBlockMechanic = customBlockMechanic2;
               Mechanic p0 = customBlockMechanic;
               int value = 0;
               this.addToImplemented(p0);
               customBlockMechanic2 = customBlockMechanic;
            } else {
               customBlockMechanic2 = null;
            }

            return customBlockMechanic2;
         }
      }
   }

   @Nullable
   public CustomBlockMechanic getMechanic(@Nullable String itemID) {
      Mechanic mechanic = super.getMechanic(itemID);
      return mechanic instanceof CustomBlockMechanic ? (CustomBlockMechanic)mechanic : null;
   }

   @Nullable
   public CustomBlockMechanic getMechanic(@Nullable ItemStack itemStack) {
      Mechanic mechanic = super.getMechanic(itemStack);
      return mechanic instanceof CustomBlockMechanic ? (CustomBlockMechanic)mechanic : null;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u0011\b\u0016\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\u0004\u0010\bJ\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\r\u001a\u00020\u00032\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u000f\u001a\u00020\u0010HÖ\u0081\u0004J\n\u0010\u0011\u001a\u00020\u0012HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u0013"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockFactory$CustomBlockSounds;", "", "enabled", "", "<init>", "(Z)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getEnabled", "()Z", "component1", "copy", "equals", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class CustomBlockSounds {
      private final boolean enabled;

      public CustomBlockSounds(boolean enabled) {
         this.enabled = enabled;
      }

      public final boolean getEnabled() {
         return this.enabled;
      }

      public CustomBlockSounds(@NotNull ConfigurationSection section) {
         this(section.getBoolean("enabled", true));
      }

      public final boolean component1() {
         return this.enabled;
      }

      @NotNull
      public final CustomBlockSounds copy(boolean enabled) {
         return new CustomBlockSounds(enabled);
      }

      @NotNull
      public String toString() {
         return "CustomBlockSounds(enabled=" + this.enabled + ")";
      }

      public int hashCode() {
         return Boolean.hashCode(this.enabled);
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof CustomBlockSounds)) {
            return false;
         } else {
            CustomBlockSounds customBlockSounds = (CustomBlockSounds)other;
            return this.enabled == customBlockSounds.enabled;
         }
      }

      public CustomBlockSounds() {
         this(true);
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JJ\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0007b0\b\u0006\u0012\b\b\u0007\u0012\u0004\b\b(\b\u0012\"\b\t\u0012\u001e\b\u000bB\u001a\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\f\b\r\u0012\b\b\fJ\u0004\b\b(\u000eb\f\b\u000f\u0012\b\b\u0010\u0012\u0004\b\b(\u0011R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockFactory$Companion;", "", "<init>", "()V", "instance", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockFactory;", "Lkotlin/Deprecated;", "message", "Use MechanicsManager.factory<CustomBlockFactory>() instead.", "replaceWith", "Lkotlin/ReplaceWith;", "expression", "MechanicsManager.factory<CustomBlockFactory>()", "imports", "com.nexomc.nexo.mechanics.MechanicsManager", "Lorg/jetbrains/annotations/ApiStatus$ScheduledForRemoval;", "inVersion", "1.24.0", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      /** @deprecated */
      @Deprecated(
         message = "Use MechanicsManager.factory<CustomBlockFactory>() instead.",
         replaceWith = @ReplaceWith(
   expression = "MechanicsManager.factory<CustomBlockFactory>()",
   imports = {"com.nexomc.nexo.mechanics.MechanicsManager"}
)
      )
      @ScheduledForRemoval(
         inVersion = "1.24.0"
      )
      @Nullable
      public final CustomBlockFactory instance() {
         return CustomBlockFactory.instance;
      }
   }
}

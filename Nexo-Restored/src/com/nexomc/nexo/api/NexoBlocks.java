package com.nexomc.nexo.api;

import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.breakable.Breakable;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockFactory;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockRegistry;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockType;
import com.nexomc.nexo.mechanics.custom_block.chorusblock.ChorusBlockFactory;
import com.nexomc.nexo.mechanics.custom_block.chorusblock.ChorusBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanicFactory;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanicFactory;
import com.nexomc.nexo.utils.drops.Drop;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0007b\u0002\b\b¢\u0006\u0002\u0010\u0007J\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0007b\u0002\b\b¢\u0006\u0002\u0010\u0007J\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0007b\u0002\b\b¢\u0006\u0002\u0010\u0007J\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0007b\u0002\b\b¢\u0006\u0002\u0010\u0007J\u0016\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0007b\u0002\b\bJ\u0016\u0010\f\u001a\u00020\r2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0007b\u0002\b\bJ\u0016\u0010\f\u001a\u00020\r2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b\bJ\u0014\u0010\u0013\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007b\u0002\b\bJ\u0016\u0010\u0013\u001a\u00020\r2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b\bJ\u0016\u0010\u0013\u001a\u00020\r2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0011H\u0007b\u0002\b\bJ\u0014\u0010\u0016\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007b\u0002\b\bJ\u0016\u0010\u0016\u001a\u00020\r2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b\bJ\u0014\u0010\u0017\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007b\u0002\b\bJ\u0016\u0010\u0017\u001a\u00020\r2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b\bJ\u001e\u0010\u0018\u001a\u00020\u00192\b\u0010\u0014\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u001a\u001a\u00020\u001bH\u0007b\u0002\b\bJ\u0018\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b\bJ(\u0010\u001e\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\u001b2\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010 2\u0006\u0010!\u001a\u00020\rH\u0007b\u0002\b\bJ0\u0010\u001e\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\u001b2\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010 2\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010#H\u0007b\u0002\b$b\u0002\b\bJ\u0016\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010\u001a\u001a\u00020\u001bH\u0007b\u0002\b\bJ\u0016\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007b\u0002\b\bJ\u0016\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010\u001c\u001a\u00020\u001dH\u0007b\u0002\b\bJ\u0016\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010\u0014\u001a\u00020\u0006H\u0007b\u0002\b\bJ\u0018\u0010'\u001a\u0004\u0018\u00010(2\b\u0010)\u001a\u0004\u0018\u00010\u001dH\u0007b\u0002\b\bJ\u0016\u0010'\u001a\u0004\u0018\u00010(2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007b\u0002\b\bJ\u0018\u0010'\u001a\u0004\u0018\u00010(2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b\bJ\u0018\u0010*\u001a\u0004\u0018\u00010+2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0007b\u0002\b\bJ\u0016\u0010*\u001a\u0004\u0018\u00010+2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007b\u0002\b\bJ\u0018\u0010*\u001a\u0004\u0018\u00010+2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b\bJ\u0018\u0010,\u001a\u0004\u0018\u00010-2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0007b\u0002\b\bJ\u0016\u0010,\u001a\u0004\u0018\u00010-2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007b\u0002\b\bJ\u0018\u0010,\u001a\u0004\u0018\u00010-2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b\b¨\u0006."},
   d2 = {"Lcom/nexomc/nexo/api/NexoBlocks;", "", "<init>", "()V", "blockIDs", "", "", "()[Ljava/lang/String;", "Lkotlin/jvm/JvmStatic;", "noteBlockIDs", "stringBlockIDs", "chorusBlockIDs", "isCustomBlock", "", "block", "Lorg/bukkit/block/Block;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "itemId", "isNexoNoteBlock", "itemID", "item", "isNexoStringBlock", "isNexoChorusBlock", "place", "", "location", "Lorg/bukkit/Location;", "blockData", "Lorg/bukkit/block/data/BlockData;", "remove", "player", "Lorg/bukkit/entity/Player;", "forceDrop", "overrideDrop", "Lcom/nexomc/nexo/utils/drops/Drop;", "Lkotlin/jvm/JvmOverloads;", "customBlockMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "noteBlockMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "data", "stringMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "chorusBlockMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockMechanic;", "com.nexomc:core"}
)
public final class NexoBlocks {
   @NotNull
   public static final NexoBlocks INSTANCE = new NexoBlocks();

   private NexoBlocks() {
   }

   @JvmStatic
   @NotNull
   public static final String[] blockIDs() {
      Iterable itemNames = (Iterable)NexoItems.itemNames();
      NexoBlocks nexoBlocks = INSTANCE;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : itemNames) {
         String p0 = (String)element;
         int value = 0;
         if (isCustomBlock(p0)) {
            destination.add(element);
         }
      }

      Collection collection = (Collection)((List)destination);
      return (String[])collection.toArray(new String[0]);
   }

   @JvmStatic
   @NotNull
   public static final String[] noteBlockIDs() {
      Iterable itemNames = (Iterable)NexoItems.itemNames();
      NexoBlocks nexoBlocks = INSTANCE;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : itemNames) {
         String p0 = (String)element;
         int value = 0;
         if (isNexoNoteBlock(p0)) {
            destination.add(element);
         }
      }

      Collection collection = (Collection)((List)destination);
      return (String[])collection.toArray(new String[0]);
   }

   @JvmStatic
   @NotNull
   public static final String[] stringBlockIDs() {
      Iterable itemNames = (Iterable)NexoItems.itemNames();
      NexoBlocks nexoBlocks = INSTANCE;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : itemNames) {
         String p0 = (String)element;
         int value = 0;
         if (isNexoStringBlock(p0)) {
            destination.add(element);
         }
      }

      Collection collection = (Collection)((List)destination);
      return (String[])collection.toArray(new String[0]);
   }

   @JvmStatic
   @NotNull
   public static final String[] chorusBlockIDs() {
      Iterable itemNames = (Iterable)NexoItems.itemNames();
      NexoBlocks nexoBlocks = INSTANCE;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : itemNames) {
         String p0 = (String)element;
         int value = 0;
         if (isNexoChorusBlock(p0)) {
            destination.add(element);
         }
      }

      Collection collection = (Collection)((List)destination);
      return (String[])collection.toArray(new String[0]);
   }

   @JvmStatic
   public static final boolean isCustomBlock(@Nullable Block block) {
      Material material = block != null ? block.getType() : null;
      CustomBlockMechanic customBlockMechanic;
      switch (material == null ? -1 : NexoBlocks.WhenMappings.$EnumSwitchMapping$0[material.ordinal()]) {
         case 1:
            customBlockMechanic = noteBlockMechanic(block);
            break;
         case 2:
            customBlockMechanic = stringMechanic(block);
            break;
         case 3:
            customBlockMechanic = chorusBlockMechanic(block);
            break;
         default:
            if (block != null) {
               CustomBlockRegistry customBlockRegistry = CustomBlockRegistry.INSTANCE;
               int value = 0;
               customBlockMechanic = customBlockRegistry.getMechanic(block);
            } else {
               customBlockMechanic = null;
            }
      }

      return customBlockMechanic != null;
   }

   @JvmStatic
   public static final boolean isCustomBlock(@Nullable ItemStack itemStack) {
      return isCustomBlock(NexoItems.idFromItem(itemStack));
   }

   /** Returns true if the item id belongs to a custom block factory. */
   @JvmStatic
   public static final boolean isCustomBlock(@Nullable String itemId) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof CustomBlockFactory)) {
               mechanicFactory = null;
            }

            it = (CustomBlockFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      return ((CustomBlockFactory)mechanicFactory != null ? ((CustomBlockFactory)mechanicFactory).getMechanic(itemId) : null) != null;
   }

   @JvmStatic
   public static final boolean isNexoNoteBlock(@NotNull Block block) {
      return block.getType() == Material.NOTE_BLOCK && noteBlockMechanic(block) != null;
   }

   @JvmStatic
   public static final boolean isNexoNoteBlock(@Nullable String itemID) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof NoteBlockMechanicFactory)) {
               mechanicFactory = null;
            }

            it = (NoteBlockMechanicFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      return (NoteBlockMechanicFactory)mechanicFactory != null ? !((NoteBlockMechanicFactory)mechanicFactory).isNotImplementedIn(itemID) : false;
   }

   @JvmStatic
   public static final boolean isNexoNoteBlock(@Nullable ItemStack item) {
      return isNexoNoteBlock(NexoItems.idFromItem(item));
   }

   @JvmStatic
   public static final boolean isNexoStringBlock(@NotNull Block block) {
      return block.getType() == Material.TRIPWIRE && stringMechanic(block) != null;
   }

   @JvmStatic
   public static final boolean isNexoStringBlock(@Nullable String itemID) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof StringBlockMechanicFactory)) {
               mechanicFactory = null;
            }

            it = (StringBlockMechanicFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      return (StringBlockMechanicFactory)mechanicFactory != null ? !((StringBlockMechanicFactory)mechanicFactory).isNotImplementedIn(itemID) : false;
   }

   @JvmStatic
   public static final boolean isNexoChorusBlock(@NotNull Block block) {
      return block.getType() == Material.CHORUS_PLANT && chorusBlockMechanic(block) != null;
   }

   @JvmStatic
   public static final boolean isNexoChorusBlock(@Nullable String itemID) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof ChorusBlockFactory)) {
               mechanicFactory = null;
            }

            it = (ChorusBlockFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      return (ChorusBlockFactory)mechanicFactory != null ? !((ChorusBlockFactory)mechanicFactory).isNotImplementedIn(itemID) : false;
   }

   /** Places the custom block with this item id at the location. Does nothing if the id is null or not a custom block. */
   @JvmStatic
   public static final void place(@Nullable String itemID, @NotNull Location location) {
      if (itemID != null) {
         CustomBlockRegistry customBlockRegistry = CustomBlockRegistry.INSTANCE;
         int value = 0;
         CustomBlockMechanic customBlockMechanic = customBlockRegistry.getMechanic(itemID);
         if (customBlockMechanic != null) {
            CustomBlockMechanic mechanic = customBlockMechanic;
            CustomBlockType byClass = CustomBlockRegistry.INSTANCE.getByClass(mechanic.getClass());
            if (byClass == null) {
               return;
            }

            CustomBlockType type = byClass;
            type.placeCustomBlock(location, itemID);
            return;
         }
      }

   }

   /** Returns the block data of the custom block with this item id, or null if the id is null or unknown. */
   @JvmStatic
   @Nullable
   public static final BlockData blockData(@Nullable String itemID) {
      if (itemID != null) {
         CustomBlockRegistry customBlockRegistry = CustomBlockRegistry.INSTANCE;
         int value = 0;
         CustomBlockMechanic customBlockMechanic = customBlockRegistry.getMechanic(itemID);
         if (customBlockMechanic != null) {
            CustomBlockMechanic mechanic = customBlockMechanic;
            return mechanic.getBlockData();
         }
      }

      return null;
   }

   /** Removes the custom block at the location. With {@code forceDrop} the block's own breakable drop is used as the override drop; see {@link #remove(Location, Player, Drop)}. */
   @JvmStatic
   public static final boolean remove(@NotNull Location location, @Nullable Player player, boolean forceDrop) {
      Drop drop;
      if (!forceDrop) {
         drop = null;
      } else {
         block: {
            CustomBlockMechanic customBlockMechanic2 = customBlockMechanic(location);
            if (customBlockMechanic2 != null) {
               Breakable breakable = customBlockMechanic2.getBreakable();
               if (breakable != null) {
                  drop = breakable.getDrop();
                  break block;
               }
            }

            drop = null;
         }
      }

      Drop overrideDrop = drop;
      return remove(location, player, overrideDrop);
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean remove(@NotNull Location location, @Nullable Player player, @Nullable Drop overrideDrop) {
      Block block2 = location.getBlock();
      Block block = block2;
      CustomBlockMechanic customBlockMechanic = CustomBlockRegistry.INSTANCE.getMechanic(block);
      if (customBlockMechanic == null) {
         return false;
      } else {
         CustomBlockMechanic mechanic = customBlockMechanic;
         CustomBlockType byClass = CustomBlockRegistry.INSTANCE.getByClass(mechanic.getClass());
         if (byClass == null) {
            return false;
         } else {
            CustomBlockType type = byClass;
            return type.removeCustomBlock(block, player, overrideDrop);
         }
      }
   }

   @JvmStatic
   @Nullable
   public static final CustomBlockMechanic customBlockMechanic(@NotNull Location location) {
      CustomBlockRegistry customBlockRegistry = CustomBlockRegistry.INSTANCE;
      Block block = location.getBlock();
      return customBlockRegistry.getMechanic(block);
   }

   @JvmStatic
   @Nullable
   public static final CustomBlockMechanic customBlockMechanic(@NotNull Block block) {
      return CustomBlockRegistry.INSTANCE.getMechanic(block);
   }

   @JvmStatic
   @Nullable
   public static final CustomBlockMechanic customBlockMechanic(@NotNull BlockData blockData) {
      return CustomBlockRegistry.INSTANCE.getMechanic(blockData);
   }

   @JvmStatic
   @Nullable
   public static final CustomBlockMechanic customBlockMechanic(@NotNull String itemID) {
      return CustomBlockRegistry.INSTANCE.getMechanic(itemID);
   }

   @JvmStatic
   @Nullable
   public static final NoteBlockMechanic noteBlockMechanic(@Nullable BlockData data) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof NoteBlockMechanicFactory)) {
               mechanicFactory = null;
            }

            it = (NoteBlockMechanicFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      NoteBlockMechanic noteBlockMechanic;
      if (mechanicFactory != null) {
         if (data == null) {
            return null;
         }

         noteBlockMechanic = ((NoteBlockMechanicFactory)mechanicFactory).getMechanic(data);
      } else {
         noteBlockMechanic = null;
      }

      return noteBlockMechanic;
   }

   @JvmStatic
   @Nullable
   public static final NoteBlockMechanic noteBlockMechanic(@NotNull Block block) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof NoteBlockMechanicFactory)) {
               mechanicFactory = null;
            }

            it = (NoteBlockMechanicFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      NoteBlockMechanic noteBlockMechanic;
      if (mechanicFactory != null) {
         BlockData blockData = block.getBlockData();
         noteBlockMechanic = ((NoteBlockMechanicFactory)mechanicFactory).getMechanic(blockData);
      } else {
         noteBlockMechanic = null;
      }

      return noteBlockMechanic;
   }

   @JvmStatic
   @Nullable
   public static final NoteBlockMechanic noteBlockMechanic(@Nullable String itemID) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof NoteBlockMechanicFactory)) {
               mechanicFactory = null;
            }

            it = (NoteBlockMechanicFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      return (NoteBlockMechanicFactory)mechanicFactory != null ? ((NoteBlockMechanicFactory)mechanicFactory).getMechanic(itemID) : null;
   }

   @JvmStatic
   @Nullable
   public static final StringBlockMechanic stringMechanic(@Nullable BlockData blockData) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof StringBlockMechanicFactory)) {
               mechanicFactory = null;
            }

            it = (StringBlockMechanicFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      StringBlockMechanic stringBlockMechanic;
      if (mechanicFactory != null) {
         if (blockData == null) {
            return null;
         }

         stringBlockMechanic = ((StringBlockMechanicFactory)mechanicFactory).getMechanic(blockData);
      } else {
         stringBlockMechanic = null;
      }

      return stringBlockMechanic;
   }

   @JvmStatic
   @Nullable
   public static final StringBlockMechanic stringMechanic(@NotNull Block block) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof StringBlockMechanicFactory)) {
               mechanicFactory = null;
            }

            it = (StringBlockMechanicFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      StringBlockMechanic stringBlockMechanic;
      if (mechanicFactory != null) {
         BlockData blockData = block.getBlockData();
         stringBlockMechanic = ((StringBlockMechanicFactory)mechanicFactory).getMechanic(blockData);
      } else {
         stringBlockMechanic = null;
      }

      return stringBlockMechanic;
   }

   @JvmStatic
   @Nullable
   public static final StringBlockMechanic stringMechanic(@Nullable String itemID) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof StringBlockMechanicFactory)) {
               mechanicFactory = null;
            }

            it = (StringBlockMechanicFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      return (StringBlockMechanicFactory)mechanicFactory != null ? ((StringBlockMechanicFactory)mechanicFactory).getMechanic(itemID) : null;
   }

   @JvmStatic
   @Nullable
   public static final ChorusBlockMechanic chorusBlockMechanic(@Nullable BlockData blockData) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof ChorusBlockFactory)) {
               mechanicFactory = null;
            }

            it = (ChorusBlockFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      ChorusBlockMechanic chorusBlockMechanic;
      if (mechanicFactory != null) {
         if (blockData == null) {
            return null;
         }

         chorusBlockMechanic = ((ChorusBlockFactory)mechanicFactory).getMechanic(blockData);
      } else {
         chorusBlockMechanic = null;
      }

      return chorusBlockMechanic;
   }

   @JvmStatic
   @Nullable
   public static final ChorusBlockMechanic chorusBlockMechanic(@NotNull Block block) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof ChorusBlockFactory)) {
               mechanicFactory = null;
            }

            it = (ChorusBlockFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      ChorusBlockMechanic chorusBlockMechanic;
      if (mechanicFactory != null) {
         BlockData blockData = block.getBlockData();
         chorusBlockMechanic = ((ChorusBlockFactory)mechanicFactory).getMechanic(blockData);
      } else {
         chorusBlockMechanic = null;
      }

      return chorusBlockMechanic;
   }

   @JvmStatic
   @Nullable
   public static final ChorusBlockMechanic chorusBlockMechanic(@Nullable String itemID) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof ChorusBlockFactory)) {
               mechanicFactory = null;
            }

            it = (ChorusBlockFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      return (ChorusBlockFactory)mechanicFactory != null ? ((ChorusBlockFactory)mechanicFactory).getMechanic(itemID) : null;
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean remove(@NotNull Location location, @Nullable Player player) {
      return remove(location, player, (Drop)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean remove(@NotNull Location location) {
      return remove(location, (Player)null, (Drop)null);
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.Material.values().length];
         try { a_0[org.bukkit.Material.NOTE_BLOCK.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.TRIPWIRE.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.CHORUS_PLANT.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}

package com.nexomc.nexo.compatibilities.worldedit;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanicFactory;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalDropper;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalFurnace;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalLog;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.logs.Logs;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002J\u0018\u0010\n\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\tH\u0002J.\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u0013J\u001e\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u00172\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J$\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u00172\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001b\u001a\u00020\u0013¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/worldedit/WorldEditUtils;", "", "<init>", "()V", "parseNoteBlock", "Lorg/bukkit/block/data/BlockData;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "input", "", "directionalID", "direction", "pasteSchematic", "", "loc", "Lorg/bukkit/Location;", "schematic", "Ljava/io/File;", "replaceBlocks", "", "copyBiomes", "copyEntities", "blocksInSchematic", "", "Lorg/bukkit/block/Block;", "clipboard", "Lcom/sk89q/worldedit/extent/clipboard/Clipboard;", "ignoreSelf", "com.nexomc:core"}
)
public final class WorldEditUtils {
   @NotNull
   public static final WorldEditUtils INSTANCE = new WorldEditUtils();

   private WorldEditUtils() {
   }

   private final BlockData parseNoteBlock(NoteBlockMechanic mechanic, String input) {
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
      if (mechanicFactory == null) {
         return null;
      } else {
         NoteBlockMechanicFactory factory = (NoteBlockMechanicFactory)(mechanicFactory);
         BlockData blockData;
         if (mechanic.isDirectional()) {
            String direction = StringsKt.substringBefore(StringsKt.substringAfter(StringsKt.substringAfter(input, "[", input), "=", StringsKt.substringAfter(input, "[", input)), "]", StringsKt.substringAfter(StringsKt.substringAfter(input, "[", input), "=", StringsKt.substringAfter(input, "[", input)));
            DirectionalBlock dirBlock = mechanic.getDirectional();
            NoteBlock noteBlock;
            if (!dirBlock.isParentBlock()) {
               NoteBlockMechanic parentMechanic = dirBlock.getParentMechanic();
               noteBlock = parentMechanic.getBlockData();
            } else if (dirBlock.isParentBlock() && !Intrinsics.areEqual(direction, input)) {
               NoteBlockMechanic noteBlockMechanic = factory.getMechanic(this.directionalID(mechanic, direction));
               noteBlock = noteBlockMechanic.getBlockData();
            } else {
               noteBlock = mechanic.getBlockData();
            }

            blockData = (BlockData)noteBlock;
         } else {
            blockData = (BlockData)mechanic.getBlockData();
         }

         return blockData;
      }
   }

   private final String directionalID(NoteBlockMechanic mechanic, String direction) {
      DirectionalBlock directional = mechanic.getDirectional();
      if (directional == null) {
         return mechanic.getItemID();
      } else {
         DirectionalBlock dirBlock = directional;
         String str;
         if (dirBlock instanceof DirectionalLog) {
            switch (direction) {
               case "x":
                  str = ((DirectionalLog)dirBlock).getXBlock();
                  if (str == null) {
                     str = mechanic.getItemID();
                  }

                  return str;
               case "y":
                  str = ((DirectionalLog)dirBlock).getYBlock();
                  if (str == null) {
                     str = mechanic.getItemID();
                  }

                  return str;
               case "z":
                  str = ((DirectionalLog)dirBlock).getZBlock();
                  if (str == null) {
                     str = mechanic.getItemID();
                  }

                  return str;
            }

            str = mechanic.getItemID();
         } else if (dirBlock instanceof DirectionalFurnace) {
            switch (direction) {
               case "east":
                  str = ((DirectionalFurnace)dirBlock).getEastBlock();
                  if (str == null) {
                     str = mechanic.getItemID();
                  }

                  return str;
               case "west":
                  str = ((DirectionalFurnace)dirBlock).getWestBlock();
                  if (str == null) {
                     str = mechanic.getItemID();
                  }

                  return str;
               case "north":
                  str = ((DirectionalFurnace)dirBlock).getNorthBlock();
                  if (str == null) {
                     str = mechanic.getItemID();
                  }

                  return str;
               case "south":
                  str = ((DirectionalFurnace)dirBlock).getSouthBlock();
                  if (str == null) {
                     str = mechanic.getItemID();
                  }

                  return str;
            }

            if (dirBlock instanceof DirectionalDropper) {
               if (Intrinsics.areEqual(direction, "up")) {
                  str = ((DirectionalDropper)dirBlock).getUpBlock();
                  if (str == null) {
                     str = mechanic.getItemID();
                  }
               } else if (Intrinsics.areEqual(direction, "down")) {
                  str = ((DirectionalDropper)dirBlock).getDownBlock();
                  if (str == null) {
                     str = mechanic.getItemID();
                  }
               } else {
                  str = mechanic.getItemID();
               }
            } else {
               str = mechanic.getItemID();
            }
         } else {
            str = mechanic.getItemID();
         }

         return str;
      }
   }

   public final void pasteSchematic(@NotNull Location loc, @NotNull File schematic, boolean replaceBlocks, boolean copyBiomes, boolean copyEntities) {
      ClipboardFormat clipboardFormat2 = ClipboardFormats.findByFile(schematic);
      if (clipboardFormat2 != null) {
         ClipboardFormat clipboardFormat = clipboardFormat2;
         WorldEditUtils worldEditUtils2 = this;

         Object obj;
         try {
            WorldEditUtils worldEditUtils3 = worldEditUtils2;
            int value = 0;
            Closeable closeable = (Closeable)(new FileInputStream(schematic));
            Throwable throwable = null;

            Clipboard clipboard4;
            try {
               FileInputStream inputStream = (FileInputStream)closeable;
               int it = 0;
               Closeable closeable2 = (Closeable)clipboardFormat.getReader((InputStream)inputStream);
               Throwable throwable2 = null;

               Clipboard clipboard2;
               try {
                  ClipboardReader reader = (ClipboardReader)closeable2;
                  int value2 = 0;
                  clipboard2 = reader.read();
               } catch (Throwable e) {
                  throwable2 = e;
                  throw e;
               } finally {
                  CloseableKt.closeFinally(closeable2, throwable2);
               }

               clipboard4 = clipboard2;
            } catch (Throwable ex) {
               throwable = ex;
               throw ex;
            } finally {
               CloseableKt.closeFinally(closeable, throwable);
            }

            obj = ResultShim.ok(clipboard4);
         } catch (Throwable ex2) {
            obj = ResultShim.ok(ResultKt.createFailure(ex2));
         }

         Clipboard clipboard3 = (Clipboard)(ResultShim.isFailure(obj) ? null : obj);
         if (clipboard3 != null) {
            Clipboard clipboard = clipboard3;
            WorldEditUtils worldEditUtils = this;

            Object result;
            try {
               worldEditUtils2 = worldEditUtils;
               int value3 = 0;
               World world2 = loc.getWorld();
               if (world2 == null) {
                  return;
               }

               com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(world2);
               EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).maxBlocks(-1).build();
               Operation operation = (new ClipboardHolder(clipboard)).createPaste((Extent)editSession).to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())).copyBiomes(copyBiomes).copyEntities(copyEntities).ignoreAirBlocks(true).build();
               WorldEditUtils worldEditUtils4 = worldEditUtils2;

               Object obj2;
               try {
                  int it = 0;
                  if (replaceBlocks || worldEditUtils4.blocksInSchematic(clipboard, loc).isEmpty()) {
                     Operations.complete(operation);
                  }

                  editSession.close();
                  obj2 = ResultShim.ok(Unit.INSTANCE);
               } catch (Throwable ex3) {
                  obj2 = ResultShim.ok(ResultKt.createFailure(ex3));
               }

               Object obj3 = obj2;
               Throwable throwable3 = ResultShim.exceptionOrNull(obj3);
               if (throwable3 != null) {
                  Throwable it = throwable3;
                  int value4 = 0;
                  Logs.logWarn("Could not paste schematic for sapling-mechanic", false);
                  if (Settings.DEBUG.toBool()) {
                     Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
                  }
               }

               result = ResultShim.ok(ResultShim.ok(obj3));
            } catch (Throwable ex4) {
               result = ResultShim.ok(ResultKt.createFailure(ex4));
            }

            Throwable throwable4 = ResultShim.exceptionOrNull(result);
            if (throwable4 != null) {
               Throwable it = throwable4;
               int value5 = 0;
               Logs.logWarn("Could not paste schematic", false);
               if (Settings.DEBUG.toBool()) {
                  Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
               }
            }

         }
      }
   }

   private final List<Block> blocksInSchematic(Clipboard clipboard, Location loc) {
      List list = (List)(new ArrayList());
      World world2 = loc.getWorld();
      if (world2 == null) {
         return CollectionsKt.emptyList();
      } else {
         World world = world2;
         Iterable iterable = (Iterable)(new IntRange(clipboard.getMinimumPoint().x(), clipboard.getMaximumPoint().x()));
         Iterator iterator = iterable.iterator();

         while(iterator.hasNext()) {
            int element = ((IntIterator)iterator).nextInt();
            int x = element;
            int value = 0;
            Iterable iterable2 = (Iterable)(new IntRange(clipboard.getMinimumPoint().y(), clipboard.getMaximumPoint().y()));
            Iterator iterator2 = iterable2.iterator();

            while(iterator2.hasNext()) {
               int nextInt = ((IntIterator)iterator2).nextInt();
               int y = nextInt;
               int value2 = 0;
               Iterable iterable3 = (Iterable)(new IntRange(clipboard.getMinimumPoint().z(), clipboard.getMaximumPoint().z()));
               Iterator iterator3 = iterable3.iterator();

               while(iterator3.hasNext()) {
                  int value4 = ((IntIterator)iterator3).nextInt();
                  int value3 = 0;
                  if (!clipboard.getFullBlock(BlockVector3.at(x, y, value4)).getBlockType().getMaterial().isAir()) {
                     double dx = (double)(x - clipboard.getOrigin().x());
                     double dy = (double)(y - clipboard.getOrigin().y());
                     double dz = (double)(value4 - clipboard.getOrigin().z());
                     Block blockAt = world.getBlockAt(loc.clone().add(dx, dy, dz));
                     Block block = blockAt;
                     if (!BlockHelpers.isReplaceable(block, (UUID)null)) {
                        ((Collection)list).add(block);
                     }
                  }
               }
            }
         }

         return list;
      }
   }

   @NotNull
   public final List<Block> blocksInSchematic(@NotNull Location loc, @NotNull File schematic, boolean ignoreSelf) {
      List list = (List)(new ArrayList());
      World world2 = loc.getWorld();
      if (world2 == null) {
         return CollectionsKt.emptyList();
      } else {
         World world = world2;
         ClipboardFormat clipboardFormat2 = ClipboardFormats.findByFile(schematic);
         if (clipboardFormat2 == null) {
            return list;
         } else {
            ClipboardFormat clipboardFormat = clipboardFormat2;
            WorldEditUtils worldEditUtils = this;

            Object obj;
            try {
               WorldEditUtils worldEditUtils2 = worldEditUtils;
               int value = 0;
               Closeable closeable = (Closeable)(new FileInputStream(schematic));
               Throwable throwable = null;

               Clipboard clipboard3;
               try {
                  FileInputStream inputStream = (FileInputStream)closeable;
                  int value2 = 0;
                  Closeable closeable2 = (Closeable)clipboardFormat.getReader((InputStream)inputStream);
                  Throwable throwable2 = null;

                  Clipboard y;
                  try {
                     ClipboardReader reader = (ClipboardReader)closeable2;
                     int value3 = 0;
                     y = reader.read();
                  } catch (Throwable e) {
                     throwable2 = e;
                     throw e;
                  } finally {
                     CloseableKt.closeFinally(closeable2, throwable2);
                  }

                  clipboard3 = y;
               } catch (Throwable ex) {
                  throwable = ex;
                  throw ex;
               } finally {
                  CloseableKt.closeFinally(closeable, throwable);
               }

               obj = ResultShim.ok(clipboard3);
            } catch (Throwable ex2) {
               obj = ResultShim.ok(ResultKt.createFailure(ex2));
            }

            Clipboard clipboard2 = (Clipboard)(ResultShim.isFailure(obj) ? null : obj);
            if (clipboard2 == null) {
               return list;
            } else {
               Clipboard clipboard = clipboard2;
               Iterable iterable = (Iterable)(new IntRange(clipboard.getMinimumPoint().x(), clipboard.getMaximumPoint().x()));
               Iterator iterator = iterable.iterator();

               while(iterator.hasNext()) {
                  int element = ((IntIterator)iterator).nextInt();
                  int x = element;
                  int value4 = 0;
                  Iterable iterable2 = (Iterable)(new IntRange(clipboard.getMinimumPoint().y(), clipboard.getMaximumPoint().y()));
                  Iterator iterator2 = iterable2.iterator();

                  while(iterator2.hasNext()) {
                     int nextInt = ((IntIterator)iterator2).nextInt();
                     int y = nextInt;
                     int value5 = 0;
                     Iterable iterable3 = (Iterable)(new IntRange(clipboard.getMinimumPoint().z(), clipboard.getMaximumPoint().z()));
                     Iterator iterator3 = iterable3.iterator();

                     while(iterator3.hasNext()) {
                        int value7 = ((IntIterator)iterator3).nextInt();
                        int value6 = 0;
                        if (!clipboard.getFullBlock(BlockVector3.at(x, y, value7)).getBlockType().getMaterial().isAir()) {
                           double dx = (double)(x - clipboard.getOrigin().x());
                           double dy = (double)(y - clipboard.getOrigin().y());
                           double dz = (double)(value7 - clipboard.getOrigin().z());
                           Location location = loc.clone().add(dx, dy, dz);
                           Location checkLoc = location;
                           if (!ignoreSelf || checkLoc.getBlockX() != loc.getBlockX() || checkLoc.getBlockY() != loc.getBlockY() || checkLoc.getBlockZ() != loc.getBlockZ()) {
                              Block blockAt = world.getBlockAt(checkLoc);
                              Block block = blockAt;
                              if (!BlockHelpers.isReplaceable(block, (UUID)null)) {
                                 ((Collection)list).add(block);
                              }
                           }
                        }
                     }
                  }
               }

               return list;
            }
         }
      }
   }
}

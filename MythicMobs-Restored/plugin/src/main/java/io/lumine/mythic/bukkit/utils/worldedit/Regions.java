package io.lumine.mythic.bukkit.utils.worldedit;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class Regions {
   public static WorldEdit getWorldEdit() {
      return WorldEdit.getInstance();
   }

   public static BlockVector3 toWEVector(Position from) {
      return BlockVector3.at(from.getX(), from.getY(), from.getZ());
   }

   public static BlockVector3 toWEVector(Locus from) {
      return BlockVector3.at(from.getX(), from.getY(), from.getZ());
   }

   public static Locus fromWEVector(BlockVector3 from) {
      return Locus.of(from.getX(), from.getY(), from.getZ());
   }

   public static Position fromWEVector(BlockVector3 from, World fromWorld) {
      org.bukkit.World world = BukkitAdapter.adapt(fromWorld);
      return Position.of((double)from.getX(), (double)from.getY(), (double)from.getZ(), world);
   }

   public static Region toWERegion(io.lumine.mythic.bukkit.utils.serialize.Region from) {
      World world = BukkitAdapter.adapt(from.getMin().toLocation().getWorld());
      BlockVector3 min = toWEVector(from.getMin());
      BlockVector3 max = toWEVector(from.getMax());
      return new CuboidRegion(world, min, max);
   }

   public static io.lumine.mythic.bukkit.utils.serialize.Region fromWERegion(Region from) {
      Position min = fromWEVector(from.getMinimumPoint(), from.getWorld());
      Position max = fromWEVector(from.getMaximumPoint(), from.getWorld());
      return io.lumine.mythic.bukkit.utils.serialize.Region.of(min, max);
   }

   public static World toWEWorld(Position from) {
      return BukkitAdapter.adapt(from.toLocation().getWorld());
   }

   public static Optional<io.lumine.mythic.bukkit.utils.serialize.Region> getPlayerSelection(Player player) {
      com.sk89q.worldedit.entity.Player absPlayer = BukkitAdapter.adapt(player);
      LocalSession session = WorldEdit.getInstance().getSessionManager().getIfPresent(absPlayer);
      World absWorld = session.getSelectionWorld();

      try {
         if (absWorld == null) {
            return Optional.empty();
         } else {
            Region region = session.getSelection(absWorld);
            if (region != null) {
               org.bukkit.World world = BukkitAdapter.adapt(region.getWorld());
               BlockVector3 min = region.getMinimumPoint();
               BlockVector3 max = region.getMaximumPoint();
               return Optional.of(
                  io.lumine.mythic.bukkit.utils.serialize.Region.of(
                     Position.of((double)min.getX(), (double)min.getY(), (double)min.getZ(), world),
                     Position.of((double)max.getX(), (double)max.getY(), (double)max.getZ(), world)
                  )
               );
            } else {
               return Optional.empty();
            }
         }
      } catch (Error | Exception e) {
         e.printStackTrace();
         return Optional.empty();
      }
   }

   public static Optional<Position> getPlayerSelectionPoint1(Player player) {
      com.sk89q.worldedit.entity.Player absPlayer = BukkitAdapter.adapt(player);
      LocalSession session = WorldEdit.getInstance().getSessionManager().getIfPresent(absPlayer);
      World absWorld = session.getSelectionWorld();

      try {
         if (absWorld == null) {
            return Optional.empty();
         } else {
            RegionSelector selector = session.getRegionSelector(absWorld);
            if (selector != null) {
               org.bukkit.World world = BukkitAdapter.adapt(selector.getWorld());
               CuboidRegion region = (CuboidRegion)selector.getIncompleteRegion();
               BlockVector3 min = region.getPos2();
               return Optional.of(Position.of((double)min.getX(), (double)min.getY(), (double)min.getZ(), world));
            } else {
               return Optional.empty();
            }
         }
      } catch (Error | Exception e) {
         e.printStackTrace();
         return Optional.empty();
      }
   }

   public static Optional<Position> getPlayerSelectionPoint2(Player player) {
      com.sk89q.worldedit.entity.Player absPlayer = BukkitAdapter.adapt(player);
      LocalSession session = WorldEdit.getInstance().getSessionManager().getIfPresent(absPlayer);
      World absWorld = session.getSelectionWorld();

      try {
         if (absWorld == null) {
            return Optional.empty();
         } else {
            RegionSelector selector = session.getRegionSelector(absWorld);
            if (selector != null) {
               org.bukkit.World world = BukkitAdapter.adapt(selector.getWorld());
               CuboidRegion region = (CuboidRegion)selector.getIncompleteRegion();
               BlockVector3 min = region.getPos1();
               return Optional.of(Position.of((double)min.getX(), (double)min.getY(), (double)min.getZ(), world));
            } else {
               return Optional.empty();
            }
         }
      } catch (Error | Exception e) {
         e.printStackTrace();
         return Optional.empty();
      }
   }

   public static EditSession setRegion(io.lumine.mythic.bukkit.utils.serialize.Region region, Material material) {
      Region weRegion = toWERegion(region);
      EditSession editSession = WorldEdit.getInstance().newEditSession(weRegion.getWorld());

      EditSession ex;
      block: {
         try {
            BaseBlock to = BukkitAdapter.adapt(material.createBlockData()).toBaseBlock();

            try {
               editSession.setBlocks(weRegion, to);
               editSession.flushSession();
               ex = editSession;
               break block;
            } catch (Error | Exception e) {
               e.printStackTrace();
            }
         } catch (Throwable ex2) {
            if (editSession != null) {
               try {
                  editSession.close();
               } catch (Throwable ex3) {
                  ex2.addSuppressed(ex3);
               }
            }

            throw ex2;
         }

         if (editSession != null) {
            editSession.close();
         }

         return null;
      }

      if (editSession != null) {
         editSession.close();
      }

      return ex;
   }

   public static EditSession replaceRegion(io.lumine.mythic.bukkit.utils.serialize.Region region, Collection<Material> replace, Material material) {
      Region weRegion = toWERegion(region);
      EditSession editSession = WorldEdit.getInstance().newEditSession(weRegion.getWorld());

      EditSession editSession2;
      block: {
         try {
            Set<BaseBlock> baseBlocks = new HashSet<>();

            for (Material m : replace) {
               baseBlocks.add(BukkitAdapter.adapt(m.createBlockData()).toBaseBlock());
            }

            BaseBlock to = BukkitAdapter.adapt(material.createBlockData()).toBaseBlock();

            try {
               editSession.replaceBlocks(weRegion, baseBlocks, to);
               editSession.flushSession();
               editSession2 = editSession;
               break block;
            } catch (Error | Exception e) {
               e.printStackTrace();
            }
         } catch (Throwable ex) {
            if (editSession != null) {
               try {
                  editSession.close();
               } catch (Throwable ex2) {
                  ex.addSuppressed(ex2);
               }
            }

            throw ex;
         }

         if (editSession != null) {
            editSession.close();
         }

         return null;
      }

      if (editSession != null) {
         editSession.close();
      }

      return editSession2;
   }

   public static Clipboard copyRegion(io.lumine.mythic.bukkit.utils.serialize.Region region) {
      Region weRegion = toWERegion(region);
      BlockArrayClipboard clipboard = new BlockArrayClipboard(weRegion);
      EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weRegion.getWorld(), -1);

      block: {
         BlockArrayClipboard blockArrayClipboard;
         try {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, weRegion, clipboard, weRegion.getMinimumPoint());
            forwardExtentCopy.setCopyingEntities(true);

            try {
               Operations.complete(forwardExtentCopy);
               break block;
            } catch (WorldEditException e) {
               e.printStackTrace();
               blockArrayClipboard = clipboard;
            }
         } catch (Throwable ex) {
            if (editSession != null) {
               try {
                  editSession.close();
               } catch (Throwable ex2) {
                  ex.addSuppressed(ex2);
               }
            }

            throw ex;
         }

         if (editSession != null) {
            editSession.close();
         }

         return blockArrayClipboard;
      }

      if (editSession != null) {
         editSession.close();
      }

      return clipboard;
   }

   public static Optional<Clipboard> loadSchematic(File schematicFile) {
      if (!schematicFile.exists()) {
         Log.error("Schematic file " + schematicFile.getAbsolutePath() + " doesnt exist");
         return Optional.empty();
      } else {
         ClipboardFormat schematicFormat = ClipboardFormats.findByFile(schematicFile);
         if (schematicFormat == null) {
            Log.error("Schematic file " + schematicFile.getAbsolutePath() + " is corrupt");
            return Optional.empty();
         } else {
            try {
               ClipboardReader reader = schematicFormat.getReader(new FileInputStream(schematicFile));

               Optional optional;
               try {
                  optional = Optional.ofNullable(reader.read());
               } catch (Throwable e) {
                  if (reader != null) {
                     try {
                        reader.close();
                     } catch (Throwable ex) {
                        e.addSuppressed(ex);
                     }
                  }

                  throw e;
               }

               if (reader != null) {
                  reader.close();
               }

               return optional;
            } catch (IOException ex2) {
               ex2.printStackTrace();
               return Optional.empty();
            }
         }
      }
   }

   public static Optional<Clipboard> saveSchematic(io.lumine.mythic.bukkit.utils.serialize.Region region, File schematicFile) {
      Clipboard clipboard = copyRegion(region);
      ClipboardFormat schematicFormat = ClipboardFormats.findByFile(schematicFile);
      if (schematicFormat == null) {
         schematicFormat = BuiltInClipboardFormat.SPONGE_SCHEMATIC;
      }

      try {
         ClipboardWriter reader = schematicFormat.getWriter(new FileOutputStream(schematicFile));

         Optional optional;
         try {
            reader.write(clipboard);
            optional = Optional.of(clipboard);
         } catch (Throwable e) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }
            }

            throw e;
         }

         if (reader != null) {
            reader.close();
         }

         return optional;
      } catch (IOException ex2) {
         Log.error("Schematic file " + schematicFile.getAbsolutePath() + " is corrupt");
         ex2.printStackTrace();
         return Optional.empty();
      }
   }

   public static void pasteSchematic(Clipboard schematic, io.lumine.mythic.bukkit.utils.serialize.Region pasteLocation) {
      pasteSchematic(schematic, pasteLocation.getMin(), 0, false);
   }

   public static void pasteSchematic(Clipboard schematic, Position pasteLocation) {
      pasteSchematic(schematic, pasteLocation, 0, false);
   }

   public static void pasteSchematic(Clipboard schematic, Position pasteLocation, boolean ignoreAir) {
      pasteSchematic(schematic, pasteLocation, 0, false);
   }

   public static void pasteSchematic(Clipboard schematic, Position pasteLocation, int rotation, boolean ignoreAir) {
      pasteSchematicThen(schematic, pasteLocation, rotation, ignoreAir, false, null);
   }

   public static void pasteSchematic(Clipboard schematic, Position pasteLocation, int rotation, boolean ignoreAir, boolean useOriginalOrigin) {
      pasteSchematicThen(schematic, pasteLocation, rotation, ignoreAir, useOriginalOrigin, null);
   }

   public static void pasteSchematicThen(
      Clipboard schematic, Position pasteLocation, int rotation, boolean ignoreAir, boolean useOriginalOrigin, Consumer<EditSession> then
   ) {
      BlockVector3 pastePosition = BlockVector3.at(
         Numbers.floor(pasteLocation.getX()), Numbers.floor(pasteLocation.getY()), Numbers.floor(pasteLocation.getZ())
      );
      World faweWorld = toWEWorld(pasteLocation);
      if (!useOriginalOrigin) {
         schematic.setOrigin(schematic.getMinimumPoint());
      }

      EditSession editSession = WorldEdit.getInstance().newEditSession(faweWorld);

      try {
         ClipboardHolder holder = new ClipboardHolder(schematic);
         if (rotation != 0) {
            AffineTransform tr = new AffineTransform();
            tr = tr.rotateY(rotation);
            holder.setTransform(tr);
         }

         Operation operation = holder.createPaste(editSession).to(pastePosition).ignoreAirBlocks(ignoreAir).build();

         try {
            Operations.complete(operation);
         } catch (WorldEditException e) {
            e.printStackTrace();
         }

         editSession.flushSession();
         if (then != null) {
            then.accept(editSession);
         }
      } catch (Throwable ex) {
         if (editSession != null) {
            try {
               editSession.close();
            } catch (Throwable ex2) {
               ex.addSuppressed(ex2);
            }
         }

         throw ex;
      }

      if (editSession != null) {
         editSession.close();
      }
   }

   public static Collection<Position> pasteSchematic(
      Clipboard schematic, Position pasteLocation, int rotation, boolean ignoreAir, Collection<Material> blockPositions
   ) {
      return pasteSchematic(schematic, pasteLocation, rotation, ignoreAir, false, blockPositions);
   }

   public static Collection<Position> pasteSchematic(
      Clipboard schematic, Position pasteLocation, int rotation, boolean ignoreAir, boolean useOriginalOrigin, Collection<Material> blockPositions
   ) {
      BlockVector3 pastePosition = BlockVector3.at(
         Numbers.floor(pasteLocation.getX()), Numbers.floor(pasteLocation.getY()), Numbers.floor(pasteLocation.getZ())
      );
      World faweWorld = toWEWorld(pasteLocation);
      BlockVector3 origin = schematic.getOrigin();
      if (!useOriginalOrigin) {
         schematic.setOrigin(schematic.getMinimumPoint());
      }

      EditSession editSession = WorldEdit.getInstance().newEditSession(faweWorld);

      Object obj;
      try {
         ClipboardHolder holder = new ClipboardHolder(schematic);
         if (rotation != 0) {
            AffineTransform tr = new AffineTransform();
            tr = tr.rotateY(rotation);
            holder.setTransform(tr);
         }

         Collection<Position> ret = Lists.newArrayList();

         for (int x = schematic.getMinimumPoint().getX(); x <= schematic.getMaximumPoint().getX(); x++) {
            for (int y = schematic.getMinimumPoint().getY(); y <= schematic.getMaximumPoint().getY(); y++) {
               for (int z = schematic.getMinimumPoint().getZ(); z <= schematic.getMaximumPoint().getZ(); z++) {
                  BaseBlock state = schematic.getFullBlock(BlockVector3.at(x, y, z));
                  double ax = pastePosition.getX() + x - schematic.getMinimumPoint().getBlockX();
                  double ay = pastePosition.getY() + y - schematic.getMinimumPoint().getBlockY();
                  double az = pastePosition.getZ() + z - schematic.getMinimumPoint().getBlockZ();
                  if (state.getBlockType() == BlockTypes.CHEST) {
                     ret.add(Position.of(ax, ay, az, pasteLocation.getWorld()));
                  }
               }
            }
         }

         Operation operation = holder.createPaste(editSession).to(pastePosition).ignoreAirBlocks(ignoreAir).build();

         try {
            Operations.complete(operation);
         } catch (WorldEditException e) {
            e.printStackTrace();
         }

         editSession.flushSession();
         obj = ret;
      } catch (Throwable ex) {
         if (editSession != null) {
            try {
               editSession.close();
            } catch (Throwable ex2) {
               ex.addSuppressed(ex2);
            }
         }

         throw ex;
      }

      if (editSession != null) {
         editSession.close();
      }

      return (Collection<Position>)obj;
   }

   public static void pasteSchematicByBlock(Clipboard schematic, Position pasteLocation, int rotation, boolean ignoreAir, int blocksPerTick) {
      pasteSchematicByBlock(schematic, pasteLocation, rotation, ignoreAir, false, blocksPerTick);
   }

   public static void pasteSchematicByBlock(
      Clipboard schematic, Position pasteLocation, int rotation, boolean ignoreAir, boolean useOriginalOrigin, int blocksPerTick
   ) {
      BlockVector3 pastePosition = BlockVector3.at(
         Numbers.floor(pasteLocation.getX()), Numbers.floor(pasteLocation.getY()), Numbers.floor(pasteLocation.getZ())
      );
      World faweWorld = toWEWorld(pasteLocation);
      if (!useOriginalOrigin) {
         schematic.setOrigin(schematic.getMinimumPoint());
      }

      ClipboardHolder holder = new ClipboardHolder(schematic);
      if (rotation != 0) {
         AffineTransform tr = new AffineTransform();
         tr = tr.rotateY(rotation);
         holder.setTransform(tr);
      }

      int i = 0;
      int blocks = 0;

      for (int y = schematic.getMinimumPoint().getY(); y <= schematic.getMaximumPoint().getY(); y++) {
         for (int x = schematic.getMinimumPoint().getX(); x <= schematic.getMaximumPoint().getX(); x++) {
            for (int z = schematic.getMinimumPoint().getZ(); z <= schematic.getMaximumPoint().getZ(); z++) {
               BaseBlock state = schematic.getFullBlock(BlockVector3.at(x, y, z));
               double ax = pastePosition.getX() + x - schematic.getOrigin().getBlockX();
               double ay = pastePosition.getY() + y - schematic.getOrigin().getBlockY();
               double az = pastePosition.getZ() + z - schematic.getOrigin().getBlockZ();
               if (state.getBlockType() != BlockTypes.AIR) {
                  if (++blocks % blocksPerTick == 0) {
                     i++;
                  }

                  Schedulers.sync().runLater(() -> setBlock(faweWorld, BlockVector3.at(ax, ay, az), state), i);
               }
            }
         }
      }
   }

   public static EditSession setBlock(World faweWorld, BlockVector3 position, BaseBlock to) {
      EditSession editSession = WorldEdit.getInstance().newEditSession(faweWorld);

      EditSession ex;
      block: {
         try {
            try {
               editSession.setBlock(position, to);
               editSession.flushSession();
               ex = editSession;
               break block;
            } catch (Error | Exception e) {
               e.printStackTrace();
            }
         } catch (Throwable ex2) {
            if (editSession != null) {
               try {
                  editSession.close();
               } catch (Throwable ex3) {
                  ex2.addSuppressed(ex3);
               }
            }

            throw ex2;
         }

         if (editSession != null) {
            editSession.close();
         }

         return null;
      }

      if (editSession != null) {
         editSession.close();
      }

      return ex;
   }

   public static EditSession setBlocks(Region weRegion, BaseBlock to) {
      EditSession editSession = WorldEdit.getInstance().newEditSession(weRegion.getWorld());

      EditSession ex;
      block: {
         try {
            try {
               editSession.setBlocks(weRegion, to);
               editSession.flushSession();
               ex = editSession;
               break block;
            } catch (Error | Exception e) {
               e.printStackTrace();
            }
         } catch (Throwable ex2) {
            if (editSession != null) {
               try {
                  editSession.close();
               } catch (Throwable ex3) {
                  ex2.addSuppressed(ex3);
               }
            }

            throw ex2;
         }

         if (editSession != null) {
            editSession.close();
         }

         return null;
      }

      if (editSession != null) {
         editSession.close();
      }

      return ex;
   }

   public static void undo(EditSession session) {
      EditSession editSession = WorldEdit.getInstance().newEditSession(session.getWorld());

      try {
         session.undo(editSession);
         editSession.flushSession();
         Log.info("Undo completed");
      } catch (Throwable e) {
         if (editSession != null) {
            try {
               editSession.close();
            } catch (Throwable ex) {
               e.addSuppressed(ex);
            }
         }

         throw e;
      }

      if (editSession != null) {
         editSession.close();
      }
   }
}

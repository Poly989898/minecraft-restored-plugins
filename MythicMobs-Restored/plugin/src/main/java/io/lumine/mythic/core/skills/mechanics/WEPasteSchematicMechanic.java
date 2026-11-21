package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import io.lumine.mythic.bukkit.utils.worldedit.Regions;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.droppables.CustomDrop;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "pasteSchematic",
   aliases = {"schematicPaste", "fawePaste", "wePaste"},
   premium = true,
   version = "4.11",
   description = "Pastes a schematic using Fawe"
)
public class WEPasteSchematicMechanic extends SkillMechanic implements ITargetedLocationSkill {
   @MythicField(
      name = "schematic",
      aliases = {"s"},
      description = "The name of the schematic to paste"
   )
   private PlaceholderString schematicName;
   @MythicField(
      name = "ignoreAir",
      aliases = {"pasteAir", "air"},
      description = "Whether to ignore air blocks during pasting"
   )
   private boolean ignoreAir;
   @MythicField(
      name = "xOffset",
      aliases = {"xo", "x"},
      description = "The X offset for pasting the schematic"
   )
   private PlaceholderInt x;
   @MythicField(
      name = "yOffset",
      aliases = {"yo", "y"},
      description = "The Y offset for pasting the schematic"
   )
   private PlaceholderInt y;
   @MythicField(
      name = "zOffset",
      aliases = {"zo", "z"},
      description = "The Z offset for pasting the schematic"
   )
   private PlaceholderInt z;
   @MythicField(
      name = "rotation",
      aliases = {"rot", "r"},
      description = "The rotation of the schematic (in degrees)"
   )
   private PlaceholderInt rotation;
   @MythicField(
      name = "chestDropTable",
      aliases = {"chests", "cdt"},
      description = "The drop table for chest blocks"
   )
   private PlaceholderString chestDropTable;
   @MythicField(
      name = "trapchestDropTable",
      aliases = {"trapchests", "tcdt"},
      description = "The drop table for trap chest blocks"
   )
   private PlaceholderString trapchestDropTable;
   @MythicField(
      name = "center",
      aliases = {"c"},
      description = "Whether or not to center the schematic"
   )
   private boolean center;
   @MythicField(
      name = "blocksPerTick",
      aliases = {"bpt"},
      description = "The number of blocks pasted per tick"
   )
   private PlaceholderInt blocksPerTick;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "The duration (in ticks) for pasting the schematic"
   )
   private PlaceholderDuration duration;
   private String pasteID;
   private transient File schematicFile = null;

   public WEPasteSchematicMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, mlc, "pasteSchematic mechanic requires Mythic Premium");
      } else {
         this.schematicName = mlc.getPlaceholderString(new String[]{"schematic", "s"}, null);
         this.pasteID = mlc.getString(new String[]{"id", "pasteid", "pid"}, null);
         this.ignoreAir = !mlc.getBoolean(new String[]{"pasteair", "air", "a"}, false);
         this.x = mlc.getPlaceholderInteger(new String[]{"xoffset", "xo", "x"}, 0);
         this.y = mlc.getPlaceholderInteger(new String[]{"yoffset", "yo", "y"}, 0);
         this.z = mlc.getPlaceholderInteger(new String[]{"zoffset", "zo", "z"}, 0);
         this.center = mlc.getBoolean(new String[]{"center", "c"}, true);
         this.rotation = mlc.getPlaceholderInteger(new String[]{"rotation", "rot", "r"}, 0);
         this.chestDropTable = mlc.getPlaceholderString(new String[]{"chestDropTable", "chests", "cdt"}, "");
         this.trapchestDropTable = mlc.getPlaceholderString(new String[]{"trapchestDropTable", "trapchests", "tcdt"}, "");
         this.blocksPerTick = mlc.getPlaceholderInteger(new String[]{"blocksPerTick", "bpt"}, 0);
         this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "0");
         if (this.schematicName.isStatic()) {
            this.getPlugin().getClock().queueSecondPass(() -> this.schematicFile = this.getManager().getSchematic(this.schematicName.get()));
         }
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, this.config, "pasteSchematic mechanic requires Mythic Premium");
         return SkillResult.REQUIRES_PREMIUM;
      } else {
         AtomicReference<SkillResult> result = new AtomicReference<>(SkillResult.SUCCESS);
         int duration = this.duration.get(data).getTicks();
         Schedulers.async().run(() -> {
            if (!this.schematicName.isStatic()) {
               String schematic = this.schematicName.get(data);
               this.schematicFile = this.getManager().getSchematic(schematic);
            }

            if (this.schematicFile == null || !this.schematicFile.exists()) {
               MythicLogger.errorMechanicConfig(this, this.config, "Could not find schematic file " + this.schematicFile.getAbsolutePath());
               result.set(SkillResult.INVALID_CONFIG);
            }

            Optional<Clipboard> maybeClipboard = Regions.loadSchematic(this.schematicFile);
            if (!maybeClipboard.isPresent()) {
               MythicLogger.errorMechanicConfig(this, this.config, "Could not load schematic");
               result.set(SkillResult.INVALID_CONFIG);
            }

            Clipboard clipboard = maybeClipboard.get();
            Position position = target.toPosition();
            if (this.center) {
               position = position.subtract(clipboard.getRegion().getWidth() / 2, clipboard.getRegion().getHeight() / 2, clipboard.getRegion().getLength() / 2);
            }

            position = position.add(this.x.get(data), this.y.get(data), this.z.get(data));
            int blocksPerTick = this.blocksPerTick.get(data);
            if (blocksPerTick > 0) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Pasting schematic at {0} with BPT {1}", position.toString(), blocksPerTick);
               Regions.pasteSchematicByBlock(clipboard, position, this.rotation.get(data), this.ignoreAir, !this.center, blocksPerTick);
            } else {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Pasting schematic at {0}", position.toString());
               String dropTable = this.chestDropTable.get(data);
               Position finalPosition = position;
               if (dropTable.isEmpty()) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "No chest droptables to fill");
                  Regions.pasteSchematicThen(clipboard, position, this.rotation.get(data), this.ignoreAir, !this.center, editSession -> {
                     if (this.pasteID != null) {
                        UndoPasteMechanic.addUndoableSchematic(this.pasteID, editSession);
                     } else {
                        UndoPasteMechanic.addUndoableSchematic(this.schematicName.get(data), editSession);
                     }

                     if (duration > 0) {
                        Schedulers.sync().runLater(() -> Regions.undo(editSession), duration);
                     }
                  });
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Filling chests with droptable");
                  Collection<Position> chests = Lists.newArrayList();
                  BlockVector3 minPoint = clipboard.getMinimumPoint();
                  BlockVector3 maxPoint = clipboard.getMaximumPoint();

                  for (int cx = minPoint.getX(); cx <= maxPoint.getX(); cx++) {
                     for (int cy = minPoint.getY(); cy <= maxPoint.getY(); cy++) {
                        for (int cz = minPoint.getZ(); cz <= maxPoint.getZ(); cz++) {
                           if (clipboard.getFullBlock(BlockVector3.at(cx, cy, cz)).getBlockType() == BlockTypes.CHEST) {
                              double ax = finalPosition.getX() + (cx - minPoint.getBlockX());
                              double ay = finalPosition.getY() + (cy - minPoint.getBlockY());
                              double az = finalPosition.getZ() + (cz - minPoint.getBlockZ());
                              chests.add(Position.of(ax, ay, az, finalPosition.getWorld()));
                           }
                        }
                     }
                  }

                  Regions.pasteSchematicThen(clipboard, finalPosition, this.rotation.get(data), this.ignoreAir, !this.center, editSession -> {
                     if (this.pasteID != null) {
                        UndoPasteMechanic.addUndoableSchematic(this.pasteID, editSession);
                     } else {
                        UndoPasteMechanic.addUndoableSchematic(this.schematicName.get(data), editSession);
                     }

                     if (duration > 0) {
                        Schedulers.sync().runLater(() -> Regions.undo(editSession), duration);
                     }
                  });
                  Schedulers.sync().runLater(() -> {
                     for (Position pos : chests) {
                        if (pos.toLocation().getBlock().getState() instanceof Chest chest) {
                           this.fillChest(data, chest, dropTable);
                        }
                     }
                  }, 40L);
               }
            }
         });
         return result.get();
      }
   }

   private void fillChest(SkillMetadata data, Chest chest, String dropTable) {
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "-- Filling chest ac at {0}", chest.getLocation().toString());
      chest.getInventory().clear();
      DropMetadata meta = new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null);
      Collection<ItemStack> items = Lists.newArrayList();
      this.getPlugin().getDropManager().getDropTable(dropTable).get().generate(meta).getLootTable().forEach(type -> {
         double amount = type.getAmount();
         IDrop drop;
         if (type instanceof CustomDrop) {
            if (((CustomDrop)type).getDrop().isEmpty()) {
               return;
            }

            drop = ((CustomDrop)type).getDrop().get();
         } else {
            drop = type;
         }

         if (drop instanceof IItemDrop itemDrop) {
            AbstractItemStack stack = itemDrop.getDrop(meta, amount);
            if (stack != null) {
               items.add(BukkitAdapter.adapt(stack));
            }
         }
      });

      for (ItemStack itemStack : items) {
         int slot = -1;

         do {
            slot = (int)(Math.random() * chest.getInventory().getSize());
         } while (chest.getInventory().getItem(slot) != null);

         chest.getInventory().setItem(slot, itemStack);
      }
   }
}

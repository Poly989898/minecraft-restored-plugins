package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.items.LegacyItemConverter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockBreakEvent;

@MythicMechanic(
   author = "Ashijin",
   name = "prison",
   description = "Traps the target entity in blocks"
)
public class PrisonMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderDuration duration;
   protected boolean breakable;
   protected Material material = Material.AIR;
   protected byte materialData;

   public PrisonMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "100");
      this.breakable = mlc.getBoolean(new String[]{"breakable", "b"}, false);
      String mat = mlc.getString(new String[]{"material", "m", "type", "t"}, "ICE");
      this.materialData = (byte)mlc.getInteger(new String[]{"materialdata", "md"}, 0);
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)) {
         try {
            this.material = this.materialData == 0 ? Material.valueOf(mat) : LegacyItemConverter.getMaterial(mat, this.materialData);
         } catch (Exception e) {
            this.material = LegacyItemConverter.getMaterial(mat, this.materialData);
         }
      } else {
         this.material = Material.getMaterial(mat.toUpperCase());
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.material == null) {
         return SkillResult.INVALID_CONFIG;
      } else {
         new PrisonMechanic.Prison(data, target);
         return SkillResult.SUCCESS;
      }
   }

   private class Prison implements Terminable {
      private AbstractEntity prisoner;
      private AbstractLocation location;
      private ArrayList<Block> prisonBlocks = new ArrayList<>();
      private Terminable listener;

      public Prison(SkillMetadata data, AbstractEntity prisoner) {
         this.prisoner = prisoner;
         int x = prisoner.getLocation().getBlockX();
         int y = prisoner.getLocation().getBlockY();
         int z = prisoner.getLocation().getBlockZ();
         this.location = new AbstractLocation(
            prisoner.getWorld(),
            x < 0 ? x - 0.5 : x + 0.5,
            y + 0.5,
            z < 0 ? z - 0.5 : z + 0.5,
            prisoner.getLocation().getYaw(),
            prisoner.getLocation().getPitch()
         );
         this.listener = Events.subscribe(BlockBreakEvent.class).handler(event -> {
            if (this.prisonBlocks.contains(event.getBlock())) {
               event.setCancelled(true);
               if (PrisonMechanic.this.breakable) {
                  event.getBlock().setType(Material.AIR);
               }
            }
         });
         prisoner.teleport(this.location);
         this.imprison();
         Schedulers.of(BukkitAdapter.adapt(this.location)).runLater(() -> this.terminate(), PrisonMechanic.this.duration.get(data, prisoner).getTicks());
      }

      private void imprison() {
         Entity target = BukkitAdapter.adapt(this.prisoner);
         Block feet = target.getLocation().getBlock();
         Block temp = feet.getRelative(1, 0, 0);
         if (temp.getType() == Material.AIR) {
            temp.setType(PrisonMechanic.this.material);
            this.prisonBlocks.add(temp);
         }

         temp = feet.getRelative(1, 1, 0);
         if (temp.getType() == Material.AIR) {
            temp.setType(PrisonMechanic.this.material);
            this.prisonBlocks.add(temp);
         }

         temp = feet.getRelative(-1, 0, 0);
         if (temp.getType() == Material.AIR) {
            temp.setType(PrisonMechanic.this.material);
            this.prisonBlocks.add(temp);
         }

         temp = feet.getRelative(-1, 1, 0);
         if (temp.getType() == Material.AIR) {
            temp.setType(PrisonMechanic.this.material);
            this.prisonBlocks.add(temp);
         }

         temp = feet.getRelative(0, 0, 1);
         if (temp.getType() == Material.AIR) {
            temp.setType(PrisonMechanic.this.material);
            this.prisonBlocks.add(temp);
         }

         temp = feet.getRelative(0, 1, 1);
         if (temp.getType() == Material.AIR) {
            temp.setType(PrisonMechanic.this.material);
            this.prisonBlocks.add(temp);
         }

         temp = feet.getRelative(0, 0, -1);
         if (temp.getType() == Material.AIR) {
            temp.setType(PrisonMechanic.this.material);
            this.prisonBlocks.add(temp);
         }

         temp = feet.getRelative(0, 1, -1);
         if (temp.getType() == Material.AIR) {
            temp.setType(PrisonMechanic.this.material);
            this.prisonBlocks.add(temp);
         }

         temp = feet.getRelative(0, -1, 0);
         if (temp.getType() == Material.AIR) {
            temp.setType(PrisonMechanic.this.material);
            this.prisonBlocks.add(temp);
         }

         temp = feet.getRelative(0, 2, 0);
         if (temp.getType() == Material.AIR) {
            temp.setType(PrisonMechanic.this.material);
            this.prisonBlocks.add(temp);
         }
      }

      @Override
      public void close() {
         for (Block block : this.prisonBlocks) {
            if (PrisonMechanic.this.material == block.getType()) {
               block.setType(Material.AIR);
            }
         }

         this.listener.closeAndReportException();
      }
   }
}

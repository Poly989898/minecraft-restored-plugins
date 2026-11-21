package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   name = "blockUnmask",
   aliases = {"effect:blockUnmask", "e:blockunmask"},
   description = "Unmasks any nearby blocks that have been masked"
)
public class BlockUnmaskEffect extends SkillMechanic implements ITargetedLocationSkill {
   @MythicField(
      name = "radius",
      aliases = {"r"},
      description = "The radius of the block unmask effect",
      defValue = "0"
   )
   private int radius;
   @MythicField(
      name = "shape",
      aliases = {"s"},
      description = "The shape of the block unmask effect - SPHERE/CUBE.",
      defValue = "SPHERE"
   )
   private final boolean sphere;
   private boolean noAir;
   private int radiusSq;

   public BlockUnmaskEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.radius = mlc.getInteger(new String[]{"radius", "r"}, 0);
      String shape = mlc.getString(new String[]{"shape", "s"}, "SPHERE").toUpperCase();
      if (shape.equals("SPHERE")) {
         this.sphere = true;
      } else {
         this.sphere = false;
      }

      if (this.radius < 0) {
         this.radius = 0;
      }

      this.radiusSq = this.radius * this.radius;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.playEffect(target);
      return SkillResult.SUCCESS;
   }

   public void playEffect(AbstractLocation location) {
      Location l = BukkitAdapter.adapt(location);
      if (this.radius == 0) {
         for (Player p : l.getWorld().getPlayers()) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)) {
               p.sendBlockChange(l, l.getBlock().getBlockData());
            } else {
               p.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData());
            }
         }
      } else {
         for (Location ll : this.getBlocksInRadius(l)) {
            for (Player px : l.getWorld().getPlayers()) {
               if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)) {
                  px.sendBlockChange(ll, ll.getBlock().getBlockData());
               } else {
                  px.sendBlockChange(ll, ll.getBlock().getType(), ll.getBlock().getData());
               }
            }
         }
      }
   }

   private List<Location> getBlocksInRadius(Location l) {
      List<Location> blocks = new ArrayList<>();

      for (int x = -this.radius; x <= this.radius; x++) {
         for (int y = -this.radius; y <= this.radius; y++) {
            for (int z = -this.radius; z <= this.radius; z++) {
               Location newloc = new Location(l.getWorld(), l.getX() + x, l.getY() + y, l.getZ() + z);
               if (!this.sphere || !(l.distanceSquared(newloc) > this.radiusSq)) {
                  if (this.noAir && !newloc.getBlock().getType().equals(Material.AIR)) {
                     blocks.add(newloc);
                  } else if (!this.noAir) {
                     blocks.add(newloc);
                  }
               }
            }
         }
      }

      return blocks;
   }
}

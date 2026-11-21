package io.lumine.mythic.core.mobs.totems;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitBlock;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.MobType;
import io.lumine.mythic.core.skills.SkillCondition;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

@Getter
@ToString
public class TotemConfig {
   private final MobType mobType;
   private final AbstractBlock triggerBlock;
   private final List<TotemPatternBlock> pattern;
   private final List<TotemPatternBlock> replacePattern;
   private final List<TotemPatternBlock> triggerPatternBlocks;
   private final List<SkillCondition> conditions = new ArrayList<>();

   public TotemConfig(MobType mobType, MythicConfig config) {
      this.mobType = mobType;
      String triggerBlockStr = config.getString("Head");
      List<String> listPattern = config.getStringList("Pattern");
      List<String> listReplacePattern = config.getStringList("Replacement");
      List<String> listConditions = config.getStringList("Conditions");
      this.triggerBlock = mobType.getPlugin().getBootstrap().getBlock(triggerBlockStr);
      this.pattern = this.parsePatternBlocks(listPattern);
      this.triggerPatternBlocks = this.pattern
         .stream()
         .filter(pb -> pb.material().getBlockData().getMaterial() == this.triggerBlock.getBlockData().getMaterial())
         .collect(Collectors.toList());
      if (config.isSet("Replacement")) {
         this.replacePattern = this.parsePatternBlocks(listReplacePattern);
      } else {
         this.replacePattern = this.generateDefaultReplacePattern();
      }

      for (String line : listConditions) {
         SkillCondition condition = MythicBukkit.inst().getSkillManager().getCondition(line);
         if (condition != null) {
            this.conditions.add(condition);
         }
      }
   }

   private List<TotemPatternBlock> parsePatternBlocks(List<String> config) {
      List<TotemPatternBlock> patternBlocks = new ArrayList<>();

      for (String line : config) {
         String[] parts = line.trim().split("\\s+");
         if (parts.length != 2) {
            MythicLogger.errorMobConfig(this.mobType, this.mobType.getConfig(), "Invalid totem pattern line: " + line);
         } else {
            String[] coords = parts[0].split(",");
            if (coords.length != 3) {
               MythicLogger.errorMobConfig(this.mobType, this.mobType.getConfig(), "Invalid totem coordinates in pattern line: " + line);
            } else {
               try {
                  int dx = Integer.parseInt(coords[0]);
                  int dy = Integer.parseInt(coords[1]);
                  int dz = Integer.parseInt(coords[2]);
                  AbstractBlock material = MythicBukkit.inst().getBootstrap().getBlock(parts[1]);
                  if (material != null && !(material instanceof AbstractBlock.InvalidBlock)) {
                     patternBlocks.add(new TotemPatternBlock(dx, dy, dz, material));
                  } else {
                     MythicLogger.errorMobConfig(this.mobType, this.mobType.getConfig(), "Invalid totem material in pattern line: " + line);
                  }
               } catch (NumberFormatException e) {
                  MythicLogger.errorMobConfig(this.mobType, this.mobType.getConfig(), "Invalid number in totem pattern line: " + line);
               }
            }
         }
      }

      return patternBlocks;
   }

   private List<TotemPatternBlock> generateDefaultReplacePattern() {
      AbstractBlock air = MythicBukkit.inst().getBootstrap().getBlock("air");
      return this.pattern.stream().map(pb -> new TotemPatternBlock(pb.dx(), pb.dy(), pb.dz(), air)).collect(Collectors.toList());
   }

   private TotemConfig.MatchResult findMatch(Block triggerBlock) {
      Material triggerMaterial = this.triggerBlock.getBlockData().getMaterial();

      for (TotemPatternBlock patternTriggerBlock : this.triggerPatternBlocks) {
         if (patternTriggerBlock.material().getBlockData().getMaterial() == triggerMaterial) {
            for (int rotation = 0; rotation < 4; rotation++) {
               int yawDegrees = rotation * 90;
               int[] rotatedTriggerCoords = this.rotateCoords(patternTriggerBlock.dx(), patternTriggerBlock.dz(), yawDegrees);
               Location originLocation = triggerBlock.getLocation()
                  .clone()
                  .subtract(rotatedTriggerCoords[0], patternTriggerBlock.dy(), rotatedTriggerCoords[1]);
               boolean match = true;

               for (TotemPatternBlock patternBlock : this.pattern) {
                  int[] rotatedCoords = this.rotateCoords(patternBlock.dx(), patternBlock.dz(), yawDegrees);
                  int x = originLocation.getBlockX() + rotatedCoords[0];
                  int y = originLocation.getBlockY() + patternBlock.dy();
                  int z = originLocation.getBlockZ() + rotatedCoords[1];
                  Block relativeBlock = triggerBlock.getWorld().getBlockAt(x, y, z);
                  if (patternBlock.material() instanceof BukkitBlock bukkitBlock) {
                     if (patternBlock.material().getBlockData().getMaterial() != relativeBlock.getBlockData().getMaterial()) {
                        match = false;
                        break;
                     }
                  } else if (!patternBlock.material().getBlockData().matches(relativeBlock.getBlockData())) {
                     match = false;
                     break;
                  }
               }

               if (match) {
                  return new TotemConfig.MatchResult(originLocation, yawDegrees);
               }
            }
         }
      }

      return null;
   }

   public void replacePattern(Block triggerBlock) {
      if (this.replacePattern != null && !this.replacePattern.isEmpty()) {
         TotemConfig.MatchResult match = this.findMatch(triggerBlock);
         if (match != null) {
            Location origin = match.origin;
            int rotation = match.rotation;

            for (TotemPatternBlock patternBlock : this.replacePattern) {
               int[] rotatedCoords = this.rotateCoords(patternBlock.dx(), patternBlock.dz(), rotation);
               int x = origin.getBlockX() + rotatedCoords[0];
               int y = origin.getBlockY() + patternBlock.dy();
               int z = origin.getBlockZ() + rotatedCoords[1];
               Block relativeBlock = triggerBlock.getWorld().getBlockAt(x, y, z);
               relativeBlock.setBlockData(patternBlock.material().getBlockData());
            }
         }
      }
   }

   public boolean isValidTotemStructure(Block triggerBlock) {
      AbstractLocation aLocation = BukkitAdapter.adapt(triggerBlock.getLocation());

      for (SkillCondition condition : this.conditions) {
         if (!condition.evaluateLocation(null, aLocation)) {
            return false;
         }
      }

      return this.findMatch(triggerBlock) != null;
   }

   private int[] rotateCoords(int dx, int dz, int yawDegrees) {
      switch (yawDegrees % 360) {
         case 90:
            return new int[]{-dz, dx};
         case 180:
            return new int[]{-dx, -dz};
         case 270:
            return new int[]{dz, -dx};
         default:
            return new int[]{dx, dz};
      }
   }

   public void summonMob(Block triggerBlock) {
      TotemConfig.MatchResult match = this.findMatch(triggerBlock);
      if (match != null) {
         AbstractLocation spawnLocation = BukkitAdapter.adapt(match.origin).center();
         this.mobType.spawn(spawnLocation, Double.NaN, SpawnReason.TOTEM);
      }
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TotemConfig other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$mobType = this.getMobType();
         Object other$mobType = other.getMobType();
         if (this$mobType == null ? other$mobType == null : this$mobType.equals(other$mobType)) {
            Object this$triggerBlock = this.getTriggerBlock();
            Object other$triggerBlock = other.getTriggerBlock();
            if (this$triggerBlock == null ? other$triggerBlock == null : this$triggerBlock.equals(other$triggerBlock)) {
               Object this$pattern = this.getPattern();
               Object other$pattern = other.getPattern();
               if (this$pattern == null ? other$pattern == null : this$pattern.equals(other$pattern)) {
                  Object this$replacePattern = this.getReplacePattern();
                  Object other$replacePattern = other.getReplacePattern();
                  if (this$replacePattern == null ? other$replacePattern == null : this$replacePattern.equals(other$replacePattern)) {
                     Object this$triggerPatternBlocks = this.getTriggerPatternBlocks();
                     Object other$triggerPatternBlocks = other.getTriggerPatternBlocks();
                     if (this$triggerPatternBlocks == null ? other$triggerPatternBlocks == null : this$triggerPatternBlocks.equals(other$triggerPatternBlocks)) {
                        Object this$conditions = this.getConditions();
                        Object other$conditions = other.getConditions();
                        return this$conditions == null ? other$conditions == null : this$conditions.equals(other$conditions);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof TotemConfig;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $mobType = this.getMobType();
      result = result * 59 + ($mobType == null ? 43 : $mobType.hashCode());
      Object $triggerBlock = this.getTriggerBlock();
      result = result * 59 + ($triggerBlock == null ? 43 : $triggerBlock.hashCode());
      Object $pattern = this.getPattern();
      result = result * 59 + ($pattern == null ? 43 : $pattern.hashCode());
      Object $replacePattern = this.getReplacePattern();
      result = result * 59 + ($replacePattern == null ? 43 : $replacePattern.hashCode());
      Object $triggerPatternBlocks = this.getTriggerPatternBlocks();
      result = result * 59 + ($triggerPatternBlocks == null ? 43 : $triggerPatternBlocks.hashCode());
      Object $conditions = this.getConditions();
      return result * 59 + ($conditions == null ? 43 : $conditions.hashCode());
   }

   private static class MatchResult {
      final Location origin;
      final int rotation;

      MatchResult(Location origin, int rotation) {
         this.origin = origin;
         this.rotation = rotation;
      }
   }
}

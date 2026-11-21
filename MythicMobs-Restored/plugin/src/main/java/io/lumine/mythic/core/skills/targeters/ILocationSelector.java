package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

public abstract class ILocationSelector extends SkillTargeter implements ILocationTargeter {
   protected PlaceholderDouble xoffset;
   protected PlaceholderDouble yoffset;
   protected PlaceholderDouble zoffset;
   protected PlaceholderDouble forwardOffset;
   protected PlaceholderDouble sideOffset;
   protected PlaceholderDouble upOffset;
   protected PlaceholderDouble rotateX;
   protected PlaceholderDouble rotateY;
   protected PlaceholderDouble rotateZ;
   protected PlaceholderDouble length;
   protected PlaceholderDouble coordinateX;
   protected PlaceholderDouble coordinateY;
   protected PlaceholderDouble coordinateZ;
   protected PlaceholderFloat coordinateYaw;
   protected PlaceholderFloat coordinatePitch;
   private final boolean coordinateYawConfigured;
   private final boolean coordinatePitchConfigured;
   protected PlaceholderString blockTypes;
   protected PlaceholderString blockIgnores;
   protected boolean statics = false;
   protected boolean offsets = false;
   protected boolean advOffset = false;
   protected boolean rotated = false;
   protected boolean centered;
   protected boolean surface;
   protected int surfaceTolerance = 5;
   protected boolean faulty;
   private ILocationSelector.FilterSorter sorter = ILocationSelector.FilterSorter.NONE;
   private PlaceholderInt skipTargetsUpToIndex;
   private PlaceholderInt limit;

   public ILocationSelector(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.xoffset = mlc.getPlaceholderDouble(new String[]{"xoffset", "xo", "x"}, 0.0);
      this.yoffset = mlc.getPlaceholderDouble(new String[]{"yoffset", "yo", "y"}, 0.0);
      this.zoffset = mlc.getPlaceholderDouble(new String[]{"zoffset", "zo", "z"}, 0.0);
      this.centered = mlc.getBoolean(new String[]{"blockCentered", "centered"}, false);
      this.surface = mlc.getBoolean(new String[]{"blockSurface", "surface"}, false);
      this.surfaceTolerance = mlc.getInteger(new String[]{"blockSurfaceTolerance", "surfaceTolerance"}, 5);
      this.forwardOffset = mlc.getPlaceholderDouble(new String[]{"forwardoffset", "foffset", "fo"}, 0.0);
      this.sideOffset = mlc.getPlaceholderDouble(new String[]{"sideoffset", "soffset", "so"}, 0.0);
      this.upOffset = mlc.getPlaceholderDouble(new String[]{"upoffset", "uoffset", "uo"}, 0.0);
      this.rotateX = mlc.getPlaceholderDouble(new String[]{"rotatex", "rotx"}, 0.0);
      this.rotateY = mlc.getPlaceholderDouble(new String[]{"rotatey", "roty"}, 0.0);
      this.rotateZ = mlc.getPlaceholderDouble(new String[]{"rotatez", "rotz"}, 0.0);
      this.length = mlc.getPlaceholderDouble(new String[]{"length"}, 0.0);
      this.coordinateX = mlc.getPlaceholderDouble(new String[]{"coordinatex", "cx"}, 0.0);
      this.coordinateY = mlc.getPlaceholderDouble(new String[]{"coordinatey", "cy"}, 0.0);
      this.coordinateZ = mlc.getPlaceholderDouble(new String[]{"coordinatez", "cz"}, 0.0);
      this.coordinateYaw = mlc.getPlaceholderFloat(new String[]{"coordinateyaw", "cyaw"}, 0.0F);
      this.coordinatePitch = mlc.getPlaceholderFloat(new String[]{"coordinatepitch", "cpitch"}, 0.0F);
      this.coordinateYawConfigured = mlc.getString(new String[]{"coordinateyaw", "cyaw"}) != null;
      this.coordinatePitchConfigured = mlc.getString(new String[]{"coordinatepitch", "cpitch"}) != null;
      this.blockTypes = mlc.getPlaceholderString(new String[]{"blocktypes", "blocktype", "bt"}, null);
      this.blockIgnores = mlc.getPlaceholderString(new String[]{"blockignores", "blockignore", "bi"}, null);
      this.faulty = mlc.getBoolean(new String[]{"faulty"}, false);
      if (!this.xoffset.isStaticallyEqualTo(0.0) || !this.yoffset.isStaticallyEqualTo(0.0) || !this.zoffset.isStaticallyEqualTo(0.0)) {
         this.offsets = true;
      }

      if (!this.forwardOffset.isStaticallyEqualTo(0.0) || !this.sideOffset.isStaticallyEqualTo(0.0) || !this.upOffset.isStaticallyEqualTo(0.0)) {
         this.advOffset = true;
      }

      if (!this.rotateX.isStaticallyEqualTo(0.0) || !this.rotateY.isStaticallyEqualTo(0.0) || !this.rotateZ.isStaticallyEqualTo(0.0)) {
         this.rotated = true;
      }

      if (!this.coordinateX.isStaticallyEqualTo(0.0)
         || !this.coordinateY.isStaticallyEqualTo(0.0)
         || !this.coordinateZ.isStaticallyEqualTo(0.0)
         || this.coordinateYawConfigured
         || this.coordinatePitchConfigured) {
         this.statics = true;
      }

      this.limit = mlc.getPlaceholderInteger(new String[]{"limit"}, 0);
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Setting limit of the target to " + this.limit);
      String sort = mlc.getString(new String[]{"sort", "sortby"}, "NONE").toUpperCase();

      try {
         this.sorter = ILocationSelector.FilterSorter.valueOf(sort);
      } catch (Exception e) {
         MythicLogger.errorTargeterConfig(this, mlc, "'" + sort + "' is not a valid sorter.");
      }

      this.skipTargetsUpToIndex = mlc.getPlaceholderInteger(new String[]{"skipTargetsUpToIndex", "stuti"}, 0);
   }

   @Override
   public abstract Collection<AbstractLocation> getLocations(SkillMetadata skillMetadata);

   public void filter(SkillMetadata data) {
      if (this.targetConditions != null) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Applying target conditions");

         for (SkillCondition mc : this.targetConditions) {
            if (!mc.evaluateTargets(data)) {
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill not usable: TargetConditions failed.");
               return;
            }
         }
      }

      MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Applying location target filters");
      Collection<AbstractLocation> targets = data.getLocationTargets();
      if (targets == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ No targets to filter?");
      } else {
         if (this.surface) {
            List<AbstractLocation> adjusted = new ArrayList<>(targets.size());

            for (AbstractLocation t : targets) {
               Optional<AbstractLocation> maybeBlock = t.getWorld().getSurfaceLocation(t, this.surfaceTolerance);
               if (maybeBlock.isPresent()) {
                  adjusted.add(maybeBlock.get());
               }
            }

            targets.clear();
            targets.addAll(adjusted);
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Surface filter applied, remaining targets: " + targets.size());
            if (targets.isEmpty()) {
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! No valid surface targets within tolerance.");
               data.setLocationTargets(targets);
               return;
            }
         }

         if (this.blockIgnores != null) {
            List<AbstractLocation> tempTargets = new ArrayList<>(targets);
            List<String> ignoreList = List.of(this.blockIgnores.get(data).split(","));

            for (AbstractLocation target : tempTargets) {
               Material mat = BukkitAdapter.adapt(target).getBlock().getType();
               if (blockMatches(mat, ignoreList)) {
                  targets.remove(target);
               }
            }
         }

         if (this.blockTypes != null) {
            List<AbstractLocation> tempTargets = new ArrayList<>(targets);
            List<String> wantList = List.of(this.blockTypes.get(data).split(","));

            for (AbstractLocation targetx : tempTargets) {
               Material mat = BukkitAdapter.adapt(targetx).getBlock().getType();
               if (!blockMatches(mat, wantList)) {
                  targets.remove(targetx);
               }
            }
         }

         int limit = this.limit.get(data);
         int skipTargetsUpToIndex = this.skipTargetsUpToIndex.get(data);
         if (limit <= 0 && skipTargetsUpToIndex <= 0) {
            List<AbstractLocation> result = new ArrayList<>(targets);
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Returning {0} targets", result.size());
            data.setLocationTargets(result);
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Limit of " + limit + " being checked for with " + this.sorter.name() + " sort being applied.");
            List<AbstractLocation> sorted = new ArrayList<>(targets);
            switch (this.sorter) {
               case RANDOM:
                  Collections.shuffle(sorted);
                  break;
               case NEAREST: {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Sorting from nearest... thanks josh.");
                  AbstractLocation origin = data.getOrigin();
                  sorted.sort((o1, o2) -> Double.compare(origin.distanceSquared(o1), origin.distanceSquared(o2)));
                  break;
               }
               case FURTHEST: {
                  AbstractLocation origin = data.getOrigin();
                  sorted.sort((o1, o2) -> Double.compare(origin.distanceSquared(o2), origin.distanceSquared(o1)));
               }
            }

            int size = sorted.size();
            int from = skipTargetsUpToIndex > 0 ? Math.min(skipTargetsUpToIndex, size) : 0;
            int to = limit > 0 ? Math.min(from + limit, size) : size;
            List<AbstractLocation> result = from >= to ? new ArrayList<>() : new ArrayList<>(sorted.subList(from, to));
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Returning {0} targets", result.size());
            data.setLocationTargets(result);
         }
      }
   }

   public AbstractLocation mutate(SkillMetadata data, AbstractLocation location) {
      if (this.offsets) {
         location = location.clone().add(this.xoffset.get(data), this.yoffset.get(data), this.zoffset.get(data));
      }

      if (this.advOffset) {
         location = MythicUtil.move(this.faulty, location, this.forwardOffset.get(data), this.upOffset.get(data), this.sideOffset.get(data));
      }

      double len = this.length.get(data);
      if (len != 0.0) {
         location = location.add(location.getDirection().clone().multiply(len));
      }

      if (this.statics) {
         double cX = this.coordinateX.get(data);
         if (cX != 0.0) {
            location.setX(cX);
         }

         double cY = this.coordinateY.get(data);
         if (cY != 0.0) {
            location.setY(cY);
         }

         double cZ = this.coordinateZ.get(data);
         if (cZ != 0.0) {
            location.setZ(cZ);
         }

         if (this.coordinateYawConfigured) {
            location.setYaw(this.coordinateYaw.get(data));
         }

         if (this.coordinatePitchConfigured) {
            location.setPitch(this.coordinatePitch.get(data));
         }
      }

      if (this.centered) {
         double bX = location.getBlockX();
         double bZ = location.getBlockZ();
         location.setX(bX + 0.5);
         location.setZ(bZ + 0.5);
      }

      return location;
   }

   public static boolean blockMatches(Material mat, List<String> list) {
      for (String wantType : list) {
         wantType = wantType.toUpperCase();
         switch (wantType.charAt(0)) {
            case '#':
               if (mat.toString().contains(wantType.substring(1))) {
                  return true;
               }
               break;
            case '*':
               NamespacedKey tagKey = NamespacedKey.minecraft(wantType.toLowerCase().substring(1));
               Tag<Material> tag = Bukkit.getTag("blocks", tagKey, Material.class);
               if (tag == null) {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ BlockTag '" + tagKey + "' does not exist, returning true");
                  return true;
               }

               return tag.isTagged(mat);
            case '@':
               if (mat.toString().startsWith(wantType.substring(1))) {
                  return true;
               }
               break;
            default:
               if (mat == Material.valueOf(wantType)) {
                  return true;
               }
         }
      }

      return false;
   }

   private static enum FilterSorter {
      NONE,
      RANDOM,
      NEAREST,
      FURTHEST;
   }
}

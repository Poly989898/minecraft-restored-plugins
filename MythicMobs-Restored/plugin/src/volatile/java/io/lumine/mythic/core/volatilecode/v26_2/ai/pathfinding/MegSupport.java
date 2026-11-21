package io.lumine.mythic.core.volatilecode.v26_2.ai.pathfinding;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.function.BiPredicate;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

final class MegSupport {
   private static final MethodHandle OVERRIDE_MOVE_TO = resolveOverrideMoveTo();

   private MegSupport() {
   }

   static boolean isWrapper(Class<?> type, String simpleName) {
      return type.getName().endsWith(".entity.navigation." + simpleName);
   }

   static boolean overrideMoveTo() {
      if (OVERRIDE_MOVE_TO == null) {
         return true;
      } else {
         try {
            return (boolean)OVERRIDE_MOVE_TO.invoke();
         } catch (Throwable var1) {
            return true;
         }
      }
   }

   static void snapToClosestNode(Path newPath, Path current, Mob mob) {
      if (!newPath.sameAs(current)) {
         int closest = newPath.getNextNodeIndex();
         double best = distanceSquared(newPath.getNextEntityPos(mob), mob);

         for (int i = closest + 1; i < newPath.getNodeCount(); i++) {
            double distance = distanceSquared(newPath.getEntityPosAtNode(mob, i), mob);
            if (distance < best) {
               best = distance;
               closest = i;
            }
         }

         newPath.setNextNodeIndex(closest);
      }
   }

   static boolean shouldAdvance(Path path, Mob mob, Vec3 mobPos, float maxDistanceToWaypoint, boolean canCutCorner, BiPredicate<Vec3, Vec3> canMoveDirectly) {
      Vec3 target = path.getNextEntityPos(mob);
      boolean arrived = Math.abs(mob.getX() - target.x) < maxDistanceToWaypoint
         && Math.abs(mob.getZ() - target.z) < maxDistanceToWaypoint
         && Math.abs(mob.getY() - target.y) < 1.0;
      return arrived || canCutCorner && shouldTargetNextNode(path, mob, mobPos, canMoveDirectly);
   }

   private static boolean shouldTargetNextNode(Path path, Mob mob, Vec3 mobPos, BiPredicate<Vec3, Vec3> canMoveDirectly) {
      if (path.getNextNodeIndex() + 1 >= path.getNodeCount()) {
         return false;
      } else {
         Vec3 target = path.getNextEntityPos(mob);
         if (!mobPos.closerThan(target, 2.0)) {
            return false;
         } else if (canMoveDirectly.test(mobPos, target)) {
            return true;
         } else {
            Vec3 next = path.getEntityPosAtNode(mob, path.getNextNodeIndex() + 1);
            return next.subtract(target).dot(mobPos.subtract(target)) > 0.0;
         }
      }
   }

   private static double distanceSquared(Vec3 pos, Mob mob) {
      double x = pos.x - mob.getX();
      double y = pos.y - mob.getY();
      double z = pos.z - mob.getZ();
      return x * x + y * y + z * z;
   }

   private static MethodHandle resolveOverrideMoveTo() {
      try {
         Plugin meg = Bukkit.getPluginManager().getPlugin("ModelEngine");
         if (meg == null) {
            return null;
         } else {
            ClassLoader loader = meg.getClass().getClassLoader();
            Class<?> api = loader.loadClass("com.ticxo.modelengine.api.ModelEngineAPI");
            Class<?> cache = loader.loadClass("com.ticxo.modelengine.api.utils.config.ConfigCache");
            Lookup lookup = MethodHandles.publicLookup();
            MethodHandle getCache = lookup.unreflect(api.getMethod("getConfigCache"));
            MethodHandle isOverride = lookup.unreflect(cache.getMethod("isOverrideMoveTo"));
            return MethodHandles.filterReturnValue(getCache, isOverride);
         }
      } catch (RuntimeException | ReflectiveOperationException var7) {
         return null;
      }
   }
}

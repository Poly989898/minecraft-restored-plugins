package io.lumine.mythic.core.volatilecode.v1_21_R6.ai.pathfinding;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.function.BiPredicate;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;
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

   static void snapToClosestNode(PathEntity newPath, PathEntity current, EntityInsentient mob) {
      if (!newPath.a(current)) {
         int closest = newPath.f();
         double best = distanceSquared(newPath.a(mob), mob);

         for (int i = closest + 1; i < newPath.e(); i++) {
            double distance = distanceSquared(newPath.a(mob, i), mob);
            if (distance < best) {
               best = distance;
               closest = i;
            }
         }

         newPath.c(closest);
      }
   }

   static boolean shouldAdvance(
      PathEntity path, EntityInsentient mob, Vec3D mobPos, float maxDistanceToWaypoint, boolean canCutCorner, BiPredicate<Vec3D, Vec3D> canMoveDirectly
   ) {
      Vec3D target = path.a(mob);
      boolean arrived = Math.abs(mob.dK() - target.g) < maxDistanceToWaypoint
         && Math.abs(mob.dQ() - target.i) < maxDistanceToWaypoint
         && Math.abs(mob.dM() - target.h) < 1.0;
      return arrived || canCutCorner && shouldTargetNextNode(path, mob, mobPos, canMoveDirectly);
   }

   private static boolean shouldTargetNextNode(PathEntity path, EntityInsentient mob, Vec3D mobPos, BiPredicate<Vec3D, Vec3D> canMoveDirectly) {
      if (path.f() + 1 >= path.e()) {
         return false;
      } else {
         Vec3D target = path.a(mob);
         if (!mobPos.a(target, 2.0)) {
            return false;
         } else if (canMoveDirectly.test(mobPos, target)) {
            return true;
         } else {
            Vec3D next = path.a(mob, path.f() + 1);
            return next.d(target).b(mobPos.d(target)) > 0.0;
         }
      }
   }

   private static double distanceSquared(Vec3D pos, EntityInsentient mob) {
      double x = pos.g - mob.dK();
      double y = pos.h - mob.dM();
      double z = pos.i - mob.dQ();
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

package io.lumine.mythic.core.volatilecode.v1_21_R4;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MobEars;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileAIHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfinderAdapter;
import io.lumine.mythic.core.mobs.ai.WrappedPathfinder;
import io.lumine.mythic.core.utils.annotations.AnnotationUtil;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.CustomAIAdapter;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.EntityHearingAid;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.PathfinderHolder;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.navigators.StayInLavaNavigator;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.pathfinding.AsyncPathfinding;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.monster.EntitySkeletonAbstract;
import net.minecraft.world.level.pathfinder.PathType;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

public class VolatileAIHandlerImpl implements VolatileAIHandler {
   private Reflector<EntityInsentient> refEntityInsentient = new Reflector<>(EntityInsentient.class, "bE", "bF", "bG");
   private Reflector<PathfinderGoalSelector> refGoalSelector = new Reflector<>(PathfinderGoalSelector.class, "b", "c");
   private Reflector<EntitySkeletonAbstract> refAbstractSkeleton = new Reflector<>(EntitySkeletonAbstract.class, "c", "d");
   private final Map<String, Class<? extends PathfinderAdapter>> AI_GOALS = new ConcurrentHashMap<>();
   private final Map<String, Class<? extends PathfinderAdapter>> AI_TARGETS = new ConcurrentHashMap<>();
   private final Map<UUID, AtomicInteger> aiGoalCounters = new ConcurrentHashMap<>();

   public VolatileAIHandlerImpl(VolatileCodeHandler handler) {
      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(MythicBukkit.inst(), "io.lumine.mythic.core.mobs.ai.goals", MythicAIGoal.class)) {
         try {
            String name = clazz.getAnnotation(MythicAIGoal.class).name();
            String[] aliases = clazz.getAnnotation(MythicAIGoal.class).aliases();
            if (PathfinderAdapter.class.isAssignableFrom(clazz)) {
               this.AI_GOALS.put(name.toUpperCase(), (Class<? extends PathfinderAdapter>)clazz);

               for (String alias : aliases) {
                  this.AI_GOALS.put(alias.toUpperCase(), (Class<? extends PathfinderAdapter>)clazz);
               }
            }
         } catch (Exception var17) {
            MythicLogger.error("Failed to load custom AI goal {0}", clazz.getCanonicalName());
         }
      }

      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(MythicBukkit.inst(), "io.lumine.mythic.core.volatilecode.v1_21_R4.ai.goals", MythicAIGoal.class)) {
         try {
            String name = clazz.getAnnotation(MythicAIGoal.class).name();
            String[] aliases = clazz.getAnnotation(MythicAIGoal.class).aliases();
            if (PathfinderAdapter.class.isAssignableFrom(clazz)) {
               this.AI_GOALS.put(name.toUpperCase(), (Class<? extends PathfinderAdapter>)clazz);

               for (String alias : aliases) {
                  this.AI_GOALS.put(alias.toUpperCase(), (Class<? extends PathfinderAdapter>)clazz);
               }
            }
         } catch (Exception var16) {
            MythicLogger.error("Failed to load wrapped AI goal {0}", clazz.getCanonicalName());
         }
      }

      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(MythicBukkit.inst(), "io.lumine.mythic.core.mobs.ai.targeters", MythicAIGoal.class)) {
         try {
            String name = clazz.getAnnotation(MythicAIGoal.class).name();
            String[] aliases = clazz.getAnnotation(MythicAIGoal.class).aliases();
            if (PathfinderAdapter.class.isAssignableFrom(clazz)) {
               this.AI_TARGETS.put(name.toUpperCase(), (Class<? extends PathfinderAdapter>)clazz);

               for (String alias : aliases) {
                  this.AI_TARGETS.put(alias.toUpperCase(), (Class<? extends PathfinderAdapter>)clazz);
               }
            }
         } catch (Exception var15) {
            MythicLogger.error("Failed to load custom AI targeter {0}", clazz.getCanonicalName());
         }
      }

      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(
         MythicBukkit.inst(), "io.lumine.mythic.core.volatilecode.v1_21_R4.ai.targeters", MythicAIGoal.class
      )) {
         try {
            String name = clazz.getAnnotation(MythicAIGoal.class).name();
            String[] aliases = clazz.getAnnotation(MythicAIGoal.class).aliases();
            if (PathfinderAdapter.class.isAssignableFrom(clazz)) {
               this.AI_TARGETS.put(name.toUpperCase(), (Class<? extends PathfinderAdapter>)clazz);

               for (String alias : aliases) {
                  this.AI_TARGETS.put(alias.toUpperCase(), (Class<? extends PathfinderAdapter>)clazz);
               }
            }
         } catch (Exception var14) {
            MythicLogger.error("Failed to load wrapped AI targeter {0}", clazz.getCanonicalName());
         }
      }
   }

   @Override
   public MobEars createEars(ActiveMob activeMob) {
      return new EntityHearingAid(activeMob);
   }

   @Override
   public void navigateToLocation(AbstractEntity entity, AbstractLocation destination, double speedModifier) {
      if (entity.isLiving()) {
         EntityInsentient e = (EntityInsentient)((CraftLivingEntity)BukkitAdapter.adapt(entity)).getHandle();
         e.N().a(destination.getX(), destination.getY(), destination.getZ(), speedModifier);
      }
   }

   @Override
   public void clearPath(AbstractEntity entity) {
      if (entity.isLiving()) {
         EntityInsentient e = (EntityInsentient)((CraftLivingEntity)BukkitAdapter.adapt(entity)).getHandle();
         e.N().m();
      }
   }

   public void clearPathfinderGoals(AbstractEntity entity) {
      if (entity.isLiving()) {
         EntityInsentient e = (EntityInsentient)((CraftLivingEntity)BukkitAdapter.adapt(entity)).getHandle();
         this.clearGoalSelector(e);
      }
   }

   public void clearPathfinderTargets(AbstractEntity entity) {
      if (entity.isLiving()) {
         EntityInsentient e = (EntityInsentient)((CraftLivingEntity)BukkitAdapter.adapt(entity)).getHandle();
         e.bG = new PathfinderGoalSelector();
      }
   }

   public void addPathfindersGoal(int index, AbstractEntity entity, PathfinderGoal goal, Predicate<AbstractEntity> validator) {
      if (validator == null || validator.test(entity)) {
         EntityInsentient nmsEntity = (EntityInsentient)((CraftLivingEntity)BukkitAdapter.adapt(entity)).getHandle();
         PathfinderGoalSelector goals = (PathfinderGoalSelector)this.refEntityInsentient.get(nmsEntity, "");

         try {
            goals.a(index, goal);
         } catch (Exception var8) {
            MythicLogger.error("Failed to apply pathfinder goal");
            MythicLogger.handleMinorError(var8);
         }
      }
   }

   @Override
   public void addPathfinderGoals(LivingEntity entity, List<String> aiMods) {
      try {
         EntityInsentient e = (EntityInsentient)((CraftLivingEntity)entity).getHandle();
         PathfinderGoalSelector goals = (PathfinderGoalSelector)this.refEntityInsentient.get(e, "bF");
         UUID entityId = entity.getUniqueId();
         int j = 0;

         for (String str : aiMods) {
            String[] split = str.split(" ");
            String goal;
            if (split[0].matches("[0-9]+")) {
               j = Integer.parseInt(split[0]);
               goal = split[1];
               if (split.length > 2) {
                  String data = split[2];
               } else {
                  String data = null;
               }

               if (split.length > 3) {
                  String data2 = split[3];
               } else {
                  String data2 = null;
               }
            } else {
               j = this.aiGoalCounters.computeIfAbsent(entityId, k -> new AtomicInteger(0)).incrementAndGet();
               goal = split[0];
               if (split.length > 1) {
                  String data = split[1];
               } else {
                  String data = null;
               }

               if (split.length > 2) {
                  String data2 = split[2];
               } else {
                  String data2 = null;
               }
            }

            MythicLineConfig mlc = new MythicLineConfigImpl(MythicLineConfigImpl.unparseBlock(goal));
            goal = mlc.getKey();
            if (this.AI_GOALS.containsKey(goal.toUpperCase())) {
               Class<? extends PathfinderAdapter> clazz = this.AI_GOALS.get(goal.toUpperCase());

               try {
                  if (Pathfinder.class.isAssignableFrom(clazz)) {
                     Pathfinder pathfinder = (Pathfinder)clazz.getConstructor(AbstractEntity.class, String.class, MythicLineConfig.class)
                        .newInstance(BukkitAdapter.adapt(entity), str, mlc);
                     goals.a(j, CustomAIAdapter.create(pathfinder));
                  } else {
                     WrappedPathfinder wrappedPathfinder = (WrappedPathfinder)clazz.getConstructor(AbstractEntity.class, String.class, MythicLineConfig.class)
                        .newInstance(BukkitAdapter.adapt(entity), str, mlc);
                     PathfinderHolder holder = (PathfinderHolder)wrappedPathfinder;
                     if (holder.isValid()) {
                        PathfinderGoal pathfinder = holder.create();
                        goals.a(j, pathfinder);
                     } else {
                        MythicLogger.error("AI pathfinder {0} is not valid for mob type {1}", goal, entity.getType());
                     }
                  }
                  continue;
               } catch (Error | Exception var19) {
                  MythicLogger.error("Failed to construct AI pathfinder {0}", goal);
                  var19.printStackTrace();
               }
            }

            String var29 = goal.toLowerCase();
            byte ex = -1;
            switch (var29.hashCode()) {
               case 94746189:
                  if (var29.equals("clear")) {
                     ex = 0;
                  }
               default:
                  switch (ex) {
                     case 0:
                        goals = this.clearGoalSelector(e);
                        this.aiGoalCounters.put(entityId, new AtomicInteger(0));
                  }
            }
         }
      } catch (Exception var20) {
         MythicLogger.error("An error occurred while adding an AIGoalSelector, enable debugging for a stack trace.");
         MythicLogger.handleMinorError(var20);
      }
   }

   private PathfinderGoalSelector clearGoalSelector(EntityInsentient mob) {
      mob.bF.b().stream().filter(goal -> goal.h()).forEach(goal -> goal.e());
      return mob instanceof EntitySkeletonAbstract skeleton
         ? (
            mob.bF = new VolatileAIHandlerImpl.SkeletonGoalSelector(
               (PathfinderGoal)this.refAbstractSkeleton.get(skeleton, "c"), (PathfinderGoal)this.refAbstractSkeleton.get(skeleton, "d")
            )
         )
         : (mob.bF = new PathfinderGoalSelector());
   }

   @Override
   public void addTargetGoals(LivingEntity entity, List<String> aiMods) {
      try {
         EntityInsentient e = (EntityInsentient)((CraftLivingEntity)entity).getHandle();
         PathfinderGoalSelector goals = (PathfinderGoalSelector)this.refEntityInsentient.get(e, "bG");
         int i = 0;
         int j = 0;

         for (String str : aiMods) {
            i++;
            String[] split = str.split(" ");
            String goal;
            if (split[0].matches("[0-9]+")) {
               j = Integer.parseInt(split[0]);
               goal = split[1];
               if (split.length > 2) {
                  String data = split[2];
               } else {
                  String data = "";
               }
            } else {
               j = i;
               goal = split[0];
               if (split.length > 1) {
                  String data = split[1];
               } else {
                  String data = "";
               }
            }

            MythicLineConfig mlc = new MythicLineConfigImpl(goal);
            goal = mlc.getKey();
            if (this.AI_TARGETS.containsKey(goal.toUpperCase())) {
               Class<? extends PathfinderAdapter> clazz = this.AI_TARGETS.get(goal.toUpperCase());

               try {
                  if (clazz.isAssignableFrom(Pathfinder.class)) {
                     Pathfinder pathfinder = (Pathfinder)clazz.getConstructor(AbstractEntity.class, String.class, MythicLineConfig.class)
                        .newInstance(BukkitAdapter.adapt(entity), str, mlc);
                     goals.a(j, CustomAIAdapter.create(pathfinder));
                  } else {
                     WrappedPathfinder wrappedPathfinder = (WrappedPathfinder)clazz.getConstructor(AbstractEntity.class, String.class, MythicLineConfig.class)
                        .newInstance(BukkitAdapter.adapt(entity), str, mlc);
                     PathfinderHolder holder = (PathfinderHolder)wrappedPathfinder;
                     if (holder.isValid()) {
                        PathfinderGoal pathfinder = holder.create();
                        goals.a(j, pathfinder);
                     } else {
                        MythicLogger.error("AI pathfinder {0} is not valid for mob type {1}", goal, entity.getType());
                     }
                  }
                  continue;
               } catch (Error | Exception var19) {
                  MythicLogger.error("Failed to construct AI pathfinder {0}", goal);
                  var19.printStackTrace();
               }
            }

            String var26 = goal.toLowerCase();
            switch (var26) {
               case "reset":
               case "clear":
                  e.bG = goals = new PathfinderGoalSelector();
                  e.g((net.minecraft.world.entity.EntityLiving)null);
            }
         }
      } catch (Exception var20) {
         MythicLogger.error("An error has occurred, enable debugging for a stack trace.");
         MythicLogger.handleMinorError(var20);
      }
   }

   @Override
   public void setNavigationController(AbstractEntity entity, String type) {
      if (entity.isLiving()) {
         if (type != null) {
            String normalizedType = type.trim().toLowerCase(Locale.ROOT);
            if (!normalizedType.isEmpty() && !normalizedType.equals("default") && !normalizedType.equals("vanilla") && !normalizedType.equals("none")) {
               if (!normalizedType.equals("stayinlava") && !normalizedType.equals("lava") && !normalizedType.equals("strider")) {
                  MythicLogger.error("Invalid Navigation Controller supplied: {0}", type);
               } else {
                  try {
                     EntityInsentient mob = (EntityInsentient)((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
                     StayInLavaNavigator navigator = new StayInLavaNavigator(mob, mob.dV());
                     this.refEntityInsentient.set(mob, "bE", navigator);
                  } catch (Error | Exception var6) {
                     MythicLogger.error("Invalid Navigation Controller supplied: {0}", type);
                     var6.printStackTrace();
                  }
               }
            }
         }
      }
   }

   @Override
   public void enableAsyncPathfinding(AbstractEntity entity) {
      if (entity.isLiving()) {
         if (((CraftLivingEntity)entity.getBukkitEntity()).getHandle() instanceof EntityInsentient mob) {
            try {
               AsyncPathfinding.install(mob);
            } catch (Error | Exception var4) {
               MythicLogger.error("Failed to enable async pathfinding for {0}", entity.getUniqueId());
               var4.printStackTrace();
            }
         }
      }
   }

   @Override
   public void shutdownAsyncPathfinding() {
      AsyncPathfinding.shutdown();
   }

   @Override
   public void setPathfindingMalus(AbstractEntity entity, String malus, float value) {
      if (entity.isLiving()) {
         try {
            EntityInsentient mob = (EntityInsentient)((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
            PathType type = PathType.valueOf(malus.toUpperCase());
            mob.a(type, value);
         } catch (Error | Exception var6) {
            MythicLogger.error("Invalid PathfindingMalus BlockPathTypes supplied: {0}", malus);
            var6.printStackTrace();
         }
      }
   }

   @Override
   public void resetAI(AbstractEntity entity) {
      if (entity.isLiving()) {
         EntityInsentient mob = (EntityInsentient)((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
         PathfinderGoalSelector goalSelector = mob.bF;
         PathfinderGoalSelector targetSelector = mob.bG;
         ((Map)this.refGoalSelector.get(goalSelector, "b")).clear();
         goalSelector.b().clear();
         if (goalSelector instanceof VolatileAIHandlerImpl.SkeletonGoalSelector) {
            mob.bF = new PathfinderGoalSelector();
         }

         ((Map)this.refGoalSelector.get(targetSelector, "b")).clear();
         targetSelector.b().clear();

         try {
            Method registeredGoalsMethod = EntityInsentient.class.getDeclaredMethod("C");
            registeredGoalsMethod.setAccessible(true);
            registeredGoalsMethod.invoke(mob);
         } catch (NoSuchMethodException var6) {
            MythicLogger.error("EntityType {0} does not support resetting AI selectors", mob.an());
         } catch (Exception var7) {
            var7.printStackTrace();
         }

         if (mob instanceof EntitySkeletonAbstract skeleton) {
            skeleton.u();
         }
      }
   }

   private static final class SkeletonGoalSelector extends PathfinderGoalSelector {
      private final PathfinderGoal bowGoal;
      private final PathfinderGoal meleeGoal;

      private SkeletonGoalSelector(PathfinderGoal bowGoal, PathfinderGoal meleeGoal) {
         this.bowGoal = bowGoal;
         this.meleeGoal = meleeGoal;
      }

      public void a(int priority, PathfinderGoal goal) {
         if (goal != this.bowGoal && goal != this.meleeGoal) {
            super.a(priority, goal);
         }
      }
   }
}

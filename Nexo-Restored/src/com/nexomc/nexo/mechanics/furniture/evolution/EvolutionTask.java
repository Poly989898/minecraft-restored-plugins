package com.nexomc.nexo.mechanics.furniture.evolution;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.drops.Drop;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.random.Random;
import kotlin.time.Duration;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\b\u001a\u00020\tH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/evolution/EvolutionTask;", "Lorg/bukkit/scheduler/BukkitRunnable;", "furnitureFactory", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureFactory;", "delay", "", "<init>", "(Lcom/nexomc/nexo/mechanics/furniture/FurnitureFactory;I)V", "run", "", "com.nexomc:core"}
)
public final class EvolutionTask extends BukkitRunnable {
   @NotNull
   private final FurnitureFactory furnitureFactory;
   private final int delay;

   public EvolutionTask(@NotNull FurnitureFactory furnitureFactory, int delay) {
      super();
      this.furnitureFactory = furnitureFactory;
      this.delay = delay;
   }

   public void run() {
      SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
      Function1 task = (a0_ -> EvolutionTask.run$lambda$0(this, (ItemDisplay)a0_));
      List worlds = Bukkit.getWorlds();
      Iterable iterable = (Iterable)worlds;

      for(Object element : iterable) {
         World world = (World)element;
         int value = 0;
         if (VersionUtil.INSTANCE.isFoliaServer()) {
            Chunk[] loadedChunks = world.getLoadedChunks();
            Object[] objs = loadedChunks;
            int value2 = 0;

            for(int i = objs.length; value2 < i; ++value2) {
               Object obj = objs[value2];
               Chunk chunk = (Chunk)obj;
               int value3 = 0;
               SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocation(new Location(world, (double)chunk.getX() * (double)16.0F, (double)100.0F, (double)chunk.getZ() * (double)16.0F), new EvolutionTask$run$$inlined$runAtWorldEntities$1(chunk, task));
            }
         } else {
            Collection entitiesByClass = world.getEntitiesByClass(ItemDisplay.class);
            Iterable iterable2 = (Iterable)entitiesByClass;

            for(Object obj2 : iterable2) {
               Entity p0 = (Entity)obj2;
               int value4 = 0;
               task.invoke(p0);
            }
         }
      }

   }

   private static final Unit run$lambda$0(EvolutionTask evolutionTask, ItemDisplay entity) {
      Pair pair = TuplesKt.to(entity.getLocation(), entity.getLocation().getBlock());
      Object component1 = pair.component1();
      Location entityLoc = (Location)component1;
      component1 = pair.component2();
      Block entityBlock = (Block)component1;
      Pair pair2 = TuplesKt.to(entity.getWorld(), entity.getPersistentDataContainer());
      component1 = pair2.component1();
      World world = (World)component1;
      component1 = pair2.component2();
      PersistentDataContainer pdc = (PersistentDataContainer)component1;
      if (!pdc.has(FurnitureMechanic.Companion.getEVOLUTION_KEY(), PersistentDataType.INTEGER)) {
         return Unit.INSTANCE;
      } else {
         FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)entity);
         if (furnitureMechanic == null) {
            return Unit.INSTANCE;
         } else {
            FurnitureMechanic mechanic = furnitureMechanic;
            if (mechanic.getFarmlandRequired() && entityBlock.getRelative(BlockFace.DOWN).getType() != Material.FARMLAND) {
               NexoFurniture.remove((Entity)((Entity)entity), (Player)null, (Drop)null);
               return Unit.INSTANCE;
            } else {
               EvolvingFurniture evolvingFurniture = mechanic.getEvolution();
               if (evolvingFurniture == null) {
                  return Unit.INSTANCE;
               } else {
                  EvolvingFurniture evolution = evolvingFurniture;
                  Integer number = evolution.getLightBoostTick();
                  int it = ((Number)number).intValue();
                  int value = 0;
                  Integer number2 = entityBlock.getLightLevel() >= evolution.getMinimumLightLevel() ? number : null;
                  int lightBoostTick = number2 != null ? number2 : 0;
                  Integer nextMechanic = evolution.getRainBoostTick();
                  value = ((Number)nextMechanic).intValue();
                  int value2 = 0;
                  boolean flag = world.hasStorm() && (double)world.getHighestBlockAt(entityLoc).getY() > entityLoc.getY();
                  int rainBoostTick = (flag ? nextMechanic : null) != null ? flag ? nextMechanic : null : 0;
                  Integer number3 = (Integer)pdc.get(FurnitureMechanic.Companion.getEVOLUTION_KEY(), DataType.INTEGER);
                  int evolutionStep = (number3 != null ? number3 + evolutionTask.delay : 1) + lightBoostTick + rainBoostTick;
                  if (Duration.compareTo_LRDsOJo(DurationUtilsKt.getTicks(evolutionStep), evolution.getDelay_UwyO8pc()) < 0) {
                     pdc.set(FurnitureMechanic.Companion.getEVOLUTION_KEY(), DataType.INTEGER, evolutionStep);
                  } else {
                     if (evolution.getNextStage() == null || Random.Default.nextInt(evolution.getProbability()) != 0) {
                        return Unit.INSTANCE;
                     }

                     FurnitureMechanic furnitureMechanic2 = evolutionTask.furnitureFactory.getMechanic(evolution.getNextStage());
                     if (furnitureMechanic2 == null) {
                        return Unit.INSTANCE;
                     }

                     FurnitureMechanic nextMechanic_1 = furnitureMechanic2;
                     Location location2 = entity.getLocation();
                     Location location = location2;
                     NexoFurniture.remove((Entity)((Entity)entity), (Player)null, (Drop)null);
                     nextMechanic_1.place(location, location.getYaw(), BlockFace.UP, false);
                  }

                  return Unit.INSTANCE;
               }
            }
         }
      }
   }
}

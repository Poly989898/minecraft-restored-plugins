package com.nexomc.nexo.mechanics.custom_block.stringblock.sapling;

import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.LocationUtils;
import com.nexomc.nexo.utils.SchedulerUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u000b\u001a\u00020\fH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/sapling/SaplingTask;", "Ljava/lang/Runnable;", "delay", "", "<init>", "(I)V", "tasks", "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", "Lcom/nexomc/libs/folialib/wrapper/task/WrappedTask;", "getTasks", "()Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", "run", "", "com.nexomc:core"}
)
public final class SaplingTask implements Runnable {
   private final int delay;
   @NotNull
   private final ObjectArrayList<WrappedTask> tasks;

   public SaplingTask(int delay) {
      this.delay = delay;
      this.tasks = new ObjectArrayList();
   }

   @NotNull
   public final ObjectArrayList<WrappedTask> getTasks() {
      return this.tasks;
   }

   public void run() {
      Iterable iterable = (Iterable)this.tasks;
      int value = 0;

      for(Object element : iterable) {
         WrappedTask it = (WrappedTask)element;
         int value2 = 0;
         it.cancel();
      }

      ((ObjectArrayList)iterable).clear();

      for(World world : Bukkit.getWorlds()) {
         LocationUtils locationUtils = LocationUtils.INSTANCE;
         locationUtils.blocksWithCustomData(world, (a0_ -> SaplingTask.run$lambda$1(this, (Block)a0_)));
      }

   }

   private static final void run$lambda$1$0(Location location, Block block, SaplingTask saplingTask) {
      if (BlockHelpers.INSTANCE.isLoaded(location)) {
         Pair pair = TuplesKt.to(block.getType(), BlockHelpers.INSTANCE.getPersistentDataContainer(block));
         Object component1 = pair.component1();
         Material type = (Material)component1;
         PersistentDataContainer pdc = (PersistentDataContainer)pair.component2();
         if (pdc.has(SaplingMechanic.Companion.getSAPLING_KEY(), PersistentDataType.INTEGER) || type == Material.TRIPWIRE) {
            StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(block);
            if (stringBlockMechanic != null ? stringBlockMechanic.isSapling() : false) {
               stringBlockMechanic = NexoBlocks.stringMechanic(block);
               if (stringBlockMechanic == null) {
                  return;
               }

               SaplingMechanic saplingMechanic = stringBlockMechanic.sapling();
               if (saplingMechanic == null) {
                  return;
               }

               SaplingMechanic sapling = saplingMechanic;
               if (!sapling.hasSchematic() || !sapling.getCanGrowNaturally()) {
                  return;
               }

               if (sapling.getRequiresWaterSource() && !sapling.isUnderWater(block)) {
                  return;
               }

               if (sapling.requiresLight() && block.getLightLevel() < sapling.getMinLightLevel()) {
                  return;
               }

               File selectSchematic = sapling.selectSchematic();
               if (selectSchematic == null) {
                  return;
               }

               File selectedSchematic = selectSchematic;
               if (!sapling.canPlaceSchematic(location, selectedSchematic)) {
                  return;
               }

               Integer number = (Integer)pdc.get(SaplingMechanic.Companion.getSAPLING_KEY(), PersistentDataType.INTEGER);
               int growthTimeRemains = (number != null ? number : sapling.getNaturalGrowthTime()) - saplingTask.delay;
               if (growthTimeRemains <= 0) {
                  sapling.placeSchematic(location, selectedSchematic);
               } else {
                  pdc.set(SaplingMechanic.Companion.getSAPLING_KEY(), PersistentDataType.INTEGER, growthTimeRemains);
               }

               return;
            }
         }

         if (type != Material.TRIPWIRE) {
            pdc.remove(SaplingMechanic.Companion.getSAPLING_KEY());
         }

      }
   }

   private static final Unit run$lambda$1(SaplingTask saplingTask, Block block) {
      Location location2 = block.getLocation();
      Location location = location2;
      ((Collection)saplingTask.tasks).add(SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocationTimer(location, (() -> SaplingTask.run$lambda$1$0(location, block, saplingTask)), 1L, (long)saplingTask.delay));
      return Unit.INSTANCE;
   }
}

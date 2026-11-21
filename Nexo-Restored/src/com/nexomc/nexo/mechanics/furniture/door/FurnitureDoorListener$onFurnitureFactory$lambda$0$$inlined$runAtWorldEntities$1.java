package com.nexomc.nexo.mechanics.furniture.door;

import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.utils.SchedulerUtils;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class FurnitureDoorListener$onFurnitureFactory$lambda$0$$inlined$runAtWorldEntities$1 implements Consumer<WrappedTask> {
   private final Chunk $chunk;
   private final Function1 $task;
   public FurnitureDoorListener$onFurnitureFactory$lambda$0$$inlined$runAtWorldEntities$1(Chunk chunk, Function1 task) {
      this.$chunk = chunk;
      this.$task = task;
   }

   public final void accept(WrappedTask it) {
      Entity[] entities = this.$chunk.getEntities();
      Object[] objs = entities;
      Function1 function1 = this.$task;
      int value = 0;

      for(int i = objs.length; value < i; ++value) {
         Object element = objs[value];
         Entity entity = (Entity)element;
         int value2 = 0;
         if (entity instanceof ItemDisplay) {
            SchedulerUtils.INSTANCE.getFoliaScheduler().runAtEntity(entity, new FurnitureDoorListener$onFurnitureFactory$lambda$0$$inlined$runAtWorldEntities$1$1(function1, entity));
         }
      }

   }
}

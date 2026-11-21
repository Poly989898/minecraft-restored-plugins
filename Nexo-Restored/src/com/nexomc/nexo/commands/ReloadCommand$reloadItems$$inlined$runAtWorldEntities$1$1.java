package com.nexomc.nexo.commands;

import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import org.bukkit.entity.Entity;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class ReloadCommand$reloadItems$$inlined$runAtWorldEntities$1$1 implements Consumer<WrappedTask> {
   private final Entity $entity;
   private final Function1 $task;
   public ReloadCommand$reloadItems$$inlined$runAtWorldEntities$1$1(Function1 task, Entity entity) {
      this.$task = task;
      this.$entity = entity;
   }

   public final void accept(WrappedTask it) {
      this.$task.invoke(this.$entity);
   }
}

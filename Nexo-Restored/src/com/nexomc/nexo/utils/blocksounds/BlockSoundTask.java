package com.nexomc.nexo.utils.blocksounds;

import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.utils.SchedulerUtils;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.time.Duration;
import kotlin.time.DurationKt;
import kotlin.time.DurationUnit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003¢\u0006\u0004\b\b\u0010\tJ\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0014\u001a\u00020\u0015J\u0016\u0010\u0016\u001a\u0004\u0018\u00010\u00112\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00150\u0017R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000bR\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/nexo/utils/blocksounds/BlockSoundTask;", "", "timeStarted", "", "location", "Lorg/bukkit/Location;", "delay", "repeat", "<init>", "(JLorg/bukkit/Location;JJ)V", "getTimeStarted", "()J", "getLocation", "()Lorg/bukkit/Location;", "getDelay", "getRepeat", "task", "Lcom/nexomc/libs/folialib/wrapper/task/WrappedTask;", "isCancelled", "", "cancel", "", "runTask", "Lkotlin/Function0;", "com.nexomc:core"}
)
public final class BlockSoundTask {
   private final long timeStarted;
   @NotNull
   private final Location location;
   private final long delay;
   private final long repeat;
   @Nullable
   private WrappedTask task;

   public BlockSoundTask(long timeStarted, @NotNull Location location, long delay, long repeat) {
      super();
      this.timeStarted = timeStarted;
      this.location = location;
      this.delay = delay;
      this.repeat = repeat;
   }

   public final long getTimeStarted() {
      return this.timeStarted;
   }

   @NotNull
   public final Location getLocation() {
      return this.location;
   }

   public final long getDelay() {
      return this.delay;
   }

   public final long getRepeat() {
      return this.repeat;
   }

   public final boolean isCancelled() {
      WrappedTask wrappedTask = this.task;
      return wrappedTask != null ? wrappedTask.isCancelled() : false;
   }

   public final void cancel() {
      WrappedTask wrappedTask = this.task;
      if (wrappedTask != null) {
         wrappedTask.cancel();
      }

      this.task = null;
   }

   @Nullable
   public final WrappedTask runTask(@NotNull Function0<Unit> task) {
      WrappedTask wrappedTask = this.task;
      if (wrappedTask != null) {
         wrappedTask.cancel();
      }

      this.task = SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocationTimer(this.location, (() -> BlockSoundTask.runTask$lambda$0(this, task)), this.delay, this.repeat);
      return this.task;
   }

   private static final void runTask$lambda$0(BlockSoundTask blockSoundTask, Function0 function0) {
      long value = System.currentTimeMillis() - blockSoundTask.timeStarted;
      Duration.Companion companion = Duration.Companion;
      if (value >= Duration.getInWholeMilliseconds_impl(DurationKt.toDuration(1, DurationUnit.MINUTES))) {
         blockSoundTask.cancel();
      } else {
         function0.invoke();
      }

   }
}

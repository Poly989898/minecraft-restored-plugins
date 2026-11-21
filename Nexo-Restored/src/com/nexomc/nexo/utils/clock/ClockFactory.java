package com.nexomc.nexo.utils.clock;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000fR\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/utils/clock/ClockFactory;", "", "delay", "Lkotlin/time/Duration;", "<init>", "(JLkotlin/jvm/internal/DefaultConstructorMarker;)V", "getDelay-UwyO8pc", "()J", "J", "clocksByUUID", "", "Ljava/util/UUID;", "Lcom/nexomc/nexo/utils/clock/Clock;", "clock", "player", "Lorg/bukkit/entity/Player;", "com.nexomc:core"}
)
public final class ClockFactory {
   private final long delay;
   @NotNull
   private final Map<UUID, Clock> clocksByUUID;

   public ClockFactory(long delay) {
      this.delay = delay;
      this.clocksByUUID = (Map)(new LinkedHashMap());
   }

   public final long getDelay_UwyO8pc/* $FF was: getDelay-UwyO8pc*/() {
      return this.delay;
   }

   @NotNull
   public final Clock clock(@NotNull Player player) {
      Object obj = this.clocksByUUID.computeIfAbsent(player.getUniqueId(), (a0_ -> ClockFactory.clock$lambda$1((b0_ -> ClockFactory.clock$lambda$0(this, (UUID)b0_)), a0_)));
      return (Clock)obj;
   }

   private static final Clock clock$lambda$0(ClockFactory clockFactory, UUID it) {
      return new Clock(clockFactory.delay, 0L);
   }

   private static final Clock clock$lambda$1(Function1 tmp0, Object p0) {
      return (Clock)tmp0.invoke(p0);
   }
}

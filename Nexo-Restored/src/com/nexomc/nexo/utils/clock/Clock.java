package com.nexomc.nexo.utils.clock;

import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.utils.AdventureUtils;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.time.Duration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0006\u0010\t\u001a\u00020\nJ\u0010\u0010\u0015\u001a\u00020\n2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017R\u0010\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u000b\u001a\u00020\f8F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\rR\u0011\u0010\u000e\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u00128F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/nexo/utils/clock/Clock;", "", "delay", "Lkotlin/time/Duration;", "lastUsage", "", "<init>", "(JJLkotlin/jvm/internal/DefaultConstructorMarker;)V", "J", "reset", "", "isFinished", "", "()Z", "remainingTime", "getRemainingTime", "()J", "string", "", "getString", "()Ljava/lang/String;", "sendToPlayer", "player", "Lorg/bukkit/entity/Player;", "com.nexomc:core"}
)
public final class Clock {
   private final long delay;
   private long lastUsage;

   public Clock(long delay, long lastUsage) {
      this.delay = delay;
      this.lastUsage = lastUsage;
   }

   public final void reset() {
      this.lastUsage = System.currentTimeMillis();
   }

   public final boolean isFinished() {
      return System.currentTimeMillis() >= this.lastUsage + Duration.getInWholeMilliseconds_impl(this.delay);
   }

   public final long getRemainingTime() {
      return this.lastUsage + Duration.getInWholeMilliseconds_impl(this.delay) - System.currentTimeMillis();
   }

   @NotNull
   public final String getString() {
      String str = "%.2f";
      Object[] objs = new Object[]{(float)this.getRemainingTime() / 1000.0F};
      String str2 = String.format(str, Arrays.copyOf(objs, objs.length));
      return str2;
   }

   public final void sendToPlayer(@Nullable Player player) {
      Message message = Message.COOLDOWN;
      CommandSender commandSender = (CommandSender)player;
      TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("time", this.getString())};
      message.send(commandSender, tagResolvers);
   }
}

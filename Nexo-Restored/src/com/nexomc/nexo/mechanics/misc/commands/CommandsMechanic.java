package com.nexomc.nexo.mechanics.misc.commands;

import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.clock.Clock;
import com.nexomc.nexo.utils.clock.ClockFactory;
import com.nexomc.nexo.utils.commands.CommandsParser;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u0017J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0016\u001a\u00020\u0017R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0010R\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u001a"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/commands/CommandsMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/misc/commands/CommandsMechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/misc/commands/CommandsMechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "commands", "Lcom/nexomc/nexo/utils/commands/CommandsParser;", "getCommands", "()Lcom/nexomc/nexo/utils/commands/CommandsParser;", "clockFactory", "Lcom/nexomc/nexo/utils/clock/ClockFactory;", "isOneUsage", "", "()Z", "permission", "", "getPermission", "()Ljava/lang/String;", "hasPermission", "player", "Lorg/bukkit/entity/Player;", "clock", "Lcom/nexomc/nexo/utils/clock/Clock;", "com.nexomc:core"}
)
public final class CommandsMechanic extends Mechanic {
   @NotNull
   private final CommandsParser commands;
   @NotNull
   private final ClockFactory clockFactory;
   private final boolean isOneUsage;
   @Nullable
   private final String permission;

   public CommandsMechanic(@NotNull CommandsMechanicFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      this.commands = new CommandsParser(section, (TagResolver)null);
      Duration duration = NexoYamlKt.durationOrNull(section, "cooldown", (DurationUnit)null);
      this.clockFactory = new ClockFactory(duration != null ? duration.unbox_impl() : Duration.Companion.getZERO_UwyO8pc());
      this.isOneUsage = section.getBoolean("one_usage");
      this.permission = NexoYamlKt.stringOrNull(section, "permission");
   }

   @NotNull
   public final CommandsParser getCommands() {
      return this.commands;
   }

   public final boolean isOneUsage() {
      return this.isOneUsage;
   }

   @Nullable
   public final String getPermission() {
      return this.permission;
   }

   public final boolean hasPermission(@NotNull Player player) {
      return this.permission == null || player.hasPermission(this.permission);
   }

   @NotNull
   public final Clock clock(@NotNull Player player) {
      return this.clockFactory.clock(player);
   }
}

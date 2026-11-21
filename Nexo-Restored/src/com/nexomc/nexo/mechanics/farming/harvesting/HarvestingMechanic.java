package com.nexomc.nexo.mechanics.farming.harvesting;

import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.clock.Clock;
import com.nexomc.nexo.utils.clock.ClockFactory;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.RangesKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/nexo/mechanics/farming/harvesting/HarvestingMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/farming/harvesting/HarvestingMechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/farming/harvesting/HarvestingMechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "radius", "", "getRadius", "()I", "height", "getHeight", "lowerItemDurability", "", "getLowerItemDurability", "()Z", "clockFactory", "Lcom/nexomc/nexo/utils/clock/ClockFactory;", "clock", "Lcom/nexomc/nexo/utils/clock/Clock;", "player", "Lorg/bukkit/entity/Player;", "com.nexomc:core"}
)
public final class HarvestingMechanic extends Mechanic {
   private final int radius;
   private final int height;
   private final boolean lowerItemDurability;
   @NotNull
   private final ClockFactory clockFactory;

   public HarvestingMechanic(@NotNull HarvestingMechanicFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      this.radius = section.getInt("radius");
      this.height = section.getInt("height");
      this.lowerItemDurability = section.getBoolean("lower_item_durability", true);
      this.clockFactory = new ClockFactory(((Duration)RangesKt.coerceAtLeast(Duration.box_impl(NexoYamlKt.duration(section, "cooldown", (DurationUnit)null)), Duration.box_impl(Duration.Companion.getZERO_UwyO8pc()))).unbox_impl());
   }

   public final int getRadius() {
      return this.radius;
   }

   public final int getHeight() {
      return this.height;
   }

   public final boolean getLowerItemDurability() {
      return this.lowerItemDurability;
   }

   @NotNull
   public final Clock clock(@NotNull Player player) {
      return this.clockFactory.clock(player);
   }
}

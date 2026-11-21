package com.nexomc.nexo.mechanics.repair;

import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import kotlin.Metadata;
import kotlin.ranges.RangesKt;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0016\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000bR\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/repair/RepairMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/MechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "ratio", "", "fixedAmount", "", "finalDamage", "maxDurability", "damage", "com.nexomc:core"}
)
public final class RepairMechanic extends Mechanic {
   private final double ratio;
   private final int fixedAmount;

   public RepairMechanic(@NotNull MechanicFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      this.ratio = section.getDouble("ratio", (double)-1.0F);
      this.fixedAmount = section.getInt("fixed_amount", -1);
   }

   public final int finalDamage(int maxDurability, int damage) {
      int amountToRepair = this.ratio != (double)-1.0F ? (int)(this.ratio * (double)maxDurability) : this.fixedAmount;
      return RangesKt.coerceAtLeast(damage - amountToRepair, 0);
   }
}

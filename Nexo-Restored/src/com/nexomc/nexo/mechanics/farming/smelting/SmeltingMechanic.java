package com.nexomc.nexo.mechanics.farming.smelting;

import com.nexomc.nexo.mechanics.Mechanic;
import kotlin.Metadata;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/mechanics/farming/smelting/SmeltingMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/farming/smelting/SmeltingMechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "playSound", "", "<init>", "(Lcom/nexomc/nexo/mechanics/farming/smelting/SmeltingMechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;Z)V", "com.nexomc:core"}
)
public final class SmeltingMechanic extends Mechanic {
   private final boolean playSound;

   public SmeltingMechanic(@NotNull SmeltingMechanicFactory factory, @NotNull ConfigurationSection section, boolean playSound) {
      super(factory, section);
      this.playSound = playSound;
   }

   public final boolean playSound() {
      return this.playSound;
   }
}

package com.nexomc.nexo.mechanics.misc.soulbound;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.mechanics.Mechanic;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\u0018\u0000 \f2\u00020\u0001:\u0001\fB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/soulbound/SoulBoundMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/misc/soulbound/SoulBoundMechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/misc/soulbound/SoulBoundMechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "loseChance", "", "getLoseChance", "()D", "Companion", "com.nexomc:core"}
)
public final class SoulBoundMechanic extends Mechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   private final double loseChance;
   @NotNull
   private static final NamespacedKey NAMESPACED_KEY;

   public SoulBoundMechanic(@NotNull SoulBoundMechanicFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      this.loseChance = section.getDouble("lose_chance");
   }

   public final double getLoseChance() {
      return this.loseChance;
   }

   static {
      NAMESPACED_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "soulbound");
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/misc/soulbound/SoulBoundMechanic$Companion;", "", "<init>", "()V", "NAMESPACED_KEY", "Lorg/bukkit/NamespacedKey;", "getNAMESPACED_KEY", "()Lorg/bukkit/NamespacedKey;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getNAMESPACED_KEY() {
         return SoulBoundMechanic.NAMESPACED_KEY;
      }
   }
}

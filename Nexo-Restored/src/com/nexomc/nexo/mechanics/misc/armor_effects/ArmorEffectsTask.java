package com.nexomc.nexo.mechanics.misc.armor_effects;

import java.util.Collection;
import kotlin.Metadata;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0006"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsTask;", "Lorg/bukkit/scheduler/BukkitRunnable;", "<init>", "()V", "run", "", "com.nexomc:core"}
)
public final class ArmorEffectsTask extends BukkitRunnable {
   public void run() {
      Collection onlinePlayers = Bukkit.getOnlinePlayers();
      Iterable iterable = (Iterable)onlinePlayers;
      ArmorEffectsMechanic.Companion companion = ArmorEffectsMechanic.Companion;

      for(Object element : iterable) {
         Player p0 = (Player)element;
         int value = 0;
         companion.addEffects(p0);
      }

   }
}

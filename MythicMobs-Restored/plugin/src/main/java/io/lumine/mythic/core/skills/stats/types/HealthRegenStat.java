package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.IntProp;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;

public class HealthRegenStat extends StatType {
   private static final IntProp FREQUENCY = Property.Int(Scope.STATS, "Frequency", 60);

   public HealthRegenStat(String key) {
      super(key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      int frequency = FREQUENCY.fget(this.getScope(), this);
      Schedulers.sync().runRepeating(this::doHealing, (long)frequency, (long)frequency).bindWith(this);
   }

   @Override
   public boolean isApplicable(AbstractEntity entity) {
      return entity.isPlayer();
   }

   private void doHealing() {
      for (PlayerData profile : MythicBukkit.inst().getPlayerManager().getKnownProfiles()) {
         AbstractPlayer player = profile.getEntity();
         if (player != null && player.isOnline() && !player.isDead()) {
            StatRegistry registry = profile.getStatRegistry();
            if (registry != null) {
               double amount = registry.get(this);
               if (!(amount <= 0.0)) {
                  double health = player.getHealth();
                  double max = player.getMaxHealth();
                  if (!(health >= max)) {
                     player.setHealth(Math.min(health + amount, max));
                  }
               }
            }
         }
      }
   }
}

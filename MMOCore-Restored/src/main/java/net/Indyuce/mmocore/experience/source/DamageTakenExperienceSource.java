package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.util.Lazy;
import io.lumine.mythic.lib.version.Attributes;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageTakenExperienceSource extends SpecificExperienceSource<DamageCause> {
   private final DamageCause cause;

   public DamageTakenExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser, config);
      if (!config.contains("type")) {
         this.cause = null;
      } else {
         String str = config.getString("type").toUpperCase().replace("-", "_");
         Validate.isTrue(
            Arrays.stream(DamageCause.values()).map(Objects::toString).collect(Collectors.toList()).contains(str),
            "Cause not allowed. Go check at all the Damage Causes in EntityDamageEvent.DamageCause enum."
         );
         this.cause = DamageCause.valueOf(str);
      }
   }

   @Override
   public ExperienceSourceManager<DamageTakenExperienceSource> newManager() {
      return new DamageTakenExperienceSource.Manager();
   }

   public boolean matchesParameter(PlayerData player, DamageCause damageCause) {
      return player.getPlayer().isDead() ? false : this.cause == null || damageCause.equals(this.cause);
   }

   private static class Manager extends ExperienceSourceManager<DamageTakenExperienceSource> {
      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void onDamageTaken(final EntityDamageEvent event) {
         if (UtilityMethods.isRealPlayer(event.getEntity())) {
            final PlayerData playerData = PlayerData.get((Player)event.getEntity());
            final Lazy<Double> effectiveDamage = Lazy.of(() -> {
               double eventDamage = event.getDamage();
               double maxHealth = ((Player)event.getEntity()).getAttribute(Attributes.MAX_HEALTH).getValue();
               return Math.min(eventDamage, maxHealth);
            });
            (new BukkitRunnable() {
               public void run() {
                  for (DamageTakenExperienceSource source : Manager.this.getSources()) {
                     if (source.matchesParameter(playerData, event.getCause())) {
                        source.giveExperience(playerData, (Double)effectiveDamage.get(), null);
                     }
                  }
               }
            }).runTaskLater(MMOCore.plugin, 2L);
         }
      }
   }
}

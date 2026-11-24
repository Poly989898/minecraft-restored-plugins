package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.version.Attributes;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.apache.commons.lang.Validate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class DamageDealtExperienceSource extends SpecificExperienceSource<Void> {
   private final DamageType type;

   public DamageDealtExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser, config);
      if (!config.contains("type")) {
         this.type = null;
      } else {
         String str = UtilityMethods.enumName(config.getString("type"));
         Validate.isTrue(
            Arrays.stream(DamageType.values()).map(Objects::toString).collect(Collectors.toList()).contains(str),
            "Type value not allowed. Type value allowed: magic, physical, weapon, skill, projectile, unarmed, on-hit, minion, dot."
         );
         this.type = DamageType.valueOf(str);
      }
   }

   @Override
   public ExperienceSourceManager<DamageDealtExperienceSource> newManager() {
      return new DamageDealtExperienceSource.Manager();
   }

   public boolean matchesParameter(PlayerData player, Void v) {
      return true;
   }

   private static class Manager extends ExperienceSourceManager<DamageDealtExperienceSource> {
      @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
      public void onDamageDealt(PlayerAttackEvent event) {
         PlayerData playerData = PlayerData.get(event.getPlayer());

         for (DamageDealtExperienceSource source : this.getSources()) {
            if (source.matches(playerData, null)) {
               double value = source.type == null ? event.getDamage().getDamage() : event.getDamage().getDamage(source.type);
               if (value != 0.0) {
                  double enemyMaxHealth = event.getEntity().getAttribute(Attributes.MAX_HEALTH).getValue();
                  value = Math.min(value, enemyMaxHealth);
                  source.giveExperience(playerData, value, null);
               }
            }
         }
      }
   }
}

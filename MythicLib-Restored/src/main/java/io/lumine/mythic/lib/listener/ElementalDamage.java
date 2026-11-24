package io.lumine.mythic.lib.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.Skill;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.util.DefenseFormula;
import java.util.Random;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ElementalDamage implements Listener {
   private static final Random RANDOM = new Random();

   @EventHandler(ignoreCancelled = true)
   public void applyElementalDamage(AttackEvent event) {
      if (event.getAttack().getAttacker() != null) {
         StatProvider attacker = event.getAttack().getAttacker();
         double critChanceCoef = attacker.getStat("CRITICAL_STRIKE_CHANCE") / 100.0;
         if (event.getDamage().hasType(DamageType.WEAPON)) {
            double attackCharge = attacker instanceof PlayerMetadata
               ? MythicLib.plugin.getVersion().getWrapper().getAttackCooldown(((PlayerMetadata)attacker).getPlayer())
               : 1.0;

            for (Element element : MythicLib.plugin.getElements().getAll()) {
               double damage = attacker.getStat(element.getId() + "_DAMAGE") * attackCharge;
               if (damage != 0.0) {
                  if (MythicLib.plugin.getMMOConfig().skipElementalDamageApplication) {
                     ((PlayerMetadata)attacker).setStat(element.getId() + "_DAMAGE", damage);
                     this.applyElementalScripts(event, element, attacker, critChanceCoef);
                  } else {
                     event.getDamage().add(damage, element);
                  }
               }
            }
         }

         for (Element element : MythicLib.plugin.getElements().getAll()) {
            if (event.getDamage().hasElement(element)) {
               event.getDamage().multiplicativeModifier(1.0 + Math.max(-1.0, attacker.getStat(element.getId() + "_DAMAGE_PERCENT") / 100.0), element);
               StatProvider opponent = StatProvider.get(event.getEntity(), null, false);
               event.getDamage().multiplicativeModifier(1.0 + Math.max(-1.0, opponent.getStat(element.getId() + "_WEAKNESS") / 100.0), element);
               double defense = opponent.getStat(element.getId() + "_DEFENSE")
                  * (1.0 + Math.max(-1.0, opponent.getStat(element.getId() + "_DEFENSE_PERCENT") / 100.0));
               double initialDamage = event.getDamage().getDamage(element);
               if (initialDamage != 0.0) {
                  double finalDamage = DefenseFormula.calculateDamage(true, defense, initialDamage);
                  event.getDamage().multiplicativeModifier(finalDamage / initialDamage, element);
                  if (!MythicLib.plugin.getMMOConfig().skipElementalDamageApplication) {
                     this.applyElementalScripts(event, element, attacker, critChanceCoef);
                  }
               }
            }
         }
      }
   }

   private void applyElementalScripts(AttackEvent event, Element element, StatProvider attacker, double critChanceCoef) {
      boolean crit = RANDOM.nextDouble() < critChanceCoef;
      Skill skill = element.getSkill(crit);
      if (skill != null && attacker instanceof PlayerMetadata) {
         skill.cast(new TriggerMetadata((PlayerMetadata)attacker, TriggerType.API, event.getEntity(), event.getAttack()));
      }

      if (crit) {
         event.getDamage().registerElementalCriticalStrike(element);
      }
   }
}

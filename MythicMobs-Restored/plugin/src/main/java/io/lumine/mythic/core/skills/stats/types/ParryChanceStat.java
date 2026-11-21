package io.lumine.mythic.core.skills.stats.types;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.BooleanProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.IntProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringListProp;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.stats.PercentModifyer;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.Stats;
import io.lumine.mythic.core.skills.stats.TriggerModifyingStat;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class ParryChanceStat extends StatType implements PercentModifyer, TriggerModifyingStat {
   private static final IntProp DEGREES = Property.Int(Scope.STATS, "FrontAngle", 180);
   private static final BooleanProp OFFHAND_PARRY = Property.Boolean(Scope.STATS, "OffhandParrying", false);
   private static final StringListProp MATERIALS = Property.StringList(Scope.STATS, "UsableMaterials");
   private int degrees = 180;
   private boolean offhandParrying;
   private final Collection<Material> materials = Sets.newConcurrentHashSet();

   public ParryChanceStat(String key) {
      super(key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      this.executionPoint = StatExecution.PRE;
      this.getApplicableTriggers().add(SkillTriggers.DAMAGED);
      this.degrees = DEGREES.fget(this.getScope(), this);
      this.offhandParrying = OFFHAND_PARRY.fget(this.getScope(), this);
      List<String> mats = MATERIALS.fget(this.getScope(), this);
      if (mats.isEmpty()) {
         this.materials.add(Material.WOODEN_SWORD);
         this.materials.add(Material.IRON_SWORD);
         this.materials.add(Material.GOLDEN_SWORD);
         this.materials.add(Material.DIAMOND_SWORD);
         this.materials.add(Material.NETHERITE_SWORD);
      } else {
         for (String mat : mats) {
            try {
               this.materials.add(Material.valueOf(mat.toUpperCase()));
            } catch (Throwable e) {
            }
         }
      }
   }

   @Override
   public void processTrigger(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata, StatSnapshot statRegistry, double value) {
      if (triggerMetadata instanceof EntityAttackMetadata attackMetadata) {
         if (attackMetadata.getEvent().getCause() != DamageCause.ENTITY_ATTACK) {
            return;
         }

         AbstractEntity damagedEntity = attackMetadata.getDamaged();
         if (!damagedEntity.isLiving()) {
            return;
         }

         Optional<StatRegistry> maybeRegistry = this.getManager().getStatRegistry(attackMetadata.getDamager());
         double accuracy;
         if (maybeRegistry.isPresent()) {
            accuracy = maybeRegistry.get().get(Stats.PARRY_NEGATION);
         } else {
            accuracy = 0.0;
         }

         double roll = Numbers.randomDouble();
         if (value - accuracy >= roll) {
            boolean usedMainHand = true;
            if (damagedEntity.isPlayer()) {
               LivingEntity damagedBukkitEntity = (LivingEntity)damagedEntity.getBukkitEntity();
               ItemStack mainHand = damagedBukkitEntity.getEquipment().getItemInMainHand();
               if (mainHand == null || !this.materials.contains(mainHand.getType())) {
                  if (!this.offhandParrying) {
                     return;
                  }

                  ItemStack offhand = damagedBukkitEntity.getEquipment().getItemInOffHand();
                  if (offhand == null || !this.materials.contains(offhand.getType())) {
                     return;
                  }

                  usedMainHand = false;
               }
            }

            AbstractLocation damagedLocation = damagedEntity.getLocation();
            AbstractLocation damagerLocation = attackMetadata.getDamager().getLocation();
            if (!this.isDamagerInFront(damagedLocation, damagerLocation)) {
               return;
            }

            if (damagedEntity.isPlayer() && ServerVersion.isPaper()) {
               if (usedMainHand) {
                  ((Player)damagedEntity.getBukkitEntity()).swingMainHand();
               } else {
                  ((Player)damagedEntity.getBukkitEntity()).swingOffHand();
               }
            }

            double parryPower = statRegistry.get(Stats.PARRY_POWER);
            double parryCounterattack = statRegistry.get(Stats.PARRY_COUNTERATTACK);
            attackMetadata.setDamage(attackMetadata.getDamage() - attackMetadata.getDamage() * parryPower);
            attackMetadata.putBoolean("parry", true);
            this.runProcSkills(skillMetadata, triggerMetadata);
            if (parryCounterattack > 0.0) {
               float counterDamage = (float)(attackMetadata.getDamage() * parryCounterattack);
               attackMetadata.getDamager().damage(counterDamage);
            }
         }
      }
   }

   private boolean isDamagerInFront(AbstractLocation damagedLocation, AbstractLocation damagerLocation) {
      AbstractVector entityVector = damagedLocation.toVector();
      AbstractVector targetVector = damagerLocation.toVector();
      AbstractVector headDirection = damagedLocation.getDirection();
      AbstractVector targetDirection = targetVector.subtract(entityVector).normalize();
      double targetAngle = Math.toDegrees(targetDirection.angle(headDirection));
      return targetAngle <= this.degrees / 2;
   }
}

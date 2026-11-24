package io.lumine.mythic.lib.comp.mythicmobs.mechanic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.logging.MythicLogger.DebugLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.damage.DamagingMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import java.io.File;
import java.util.logging.Level;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MythicMechanic(author = "Indyuce", name = "mmodamage", aliases = {"mmod", "mmodmg"}, description = "Deals damage to the target (compatible with MMO plugins)")
public class MMODamageMechanic extends DamagingMechanic implements ITargetedEntitySkill {
   protected final PlaceholderDouble amount;
   protected final boolean ignoreMMOAttack;
   @Nullable
   private final PlaceholderString damageTypes;
   @Nullable
   private final PlaceholderString elementName;

   public MMODamageMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.amount = PlaceholderDouble.of(mlc.getString(new String[]{"amount", "a"}, "1", new String[0]));
      this.ignoreMMOAttack = mlc.getBoolean(new String[]{"ignoreMMOAttack", "immo", "immoa"}, false);
      this.damageTypes = this.placeholders(mlc.getString(new String[]{"type", "t", "types"}, null, new String[0]));
      this.elementName = this.placeholders(mlc.getString(new String[]{"element", "el", "e"}, null, new String[0]));
   }

   @Nullable
   private PlaceholderString placeholders(@Nullable String input) {
      return input == null ? null : PlaceholderString.of(input);
   }

   @NotNull
   private DamageType[] findDamageTypes(SkillCaster caster) {
      String format = this.damageTypes.get(caster);
      if (!format.isEmpty() && !format.equals("NONE")) {
         String[] split = format.split("\\,");
         DamageType[] array = new DamageType[split.length];

         for (int i = 0; i < array.length; i++) {
            array[i] = DamageType.valueOf(UtilityMethods.enumName(split[i]));
         }

         return array;
      } else {
         return new DamageType[0];
      }
   }

   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isDead() && target.getBukkitEntity() instanceof LivingEntity && !data.getCaster().isUsingDamageSkill() && !(target.getHealth() <= 0.0)) {
         double damage = this.amount.get(data, target) * data.getPower();
         Element element = this.elementName == null
            ? null
            : MythicLib.plugin.getElements().get(UtilityMethods.enumName(this.elementName.get(data.getCaster())));
         DamageType[] damageTypes = this.damageTypes == null ? new DamageType[0] : this.findDamageTypes(data.getCaster());
         AttackMetadata currentAttack;
         if (!this.ignoreMMOAttack && (currentAttack = MythicLib.plugin.getDamage().getRegisteredAttackMetadata(target.getBukkitEntity())) != null) {
            if (element == null) {
               currentAttack.getDamage().add(damage, damageTypes);
            } else {
               currentAttack.getDamage().add(damage, element, damageTypes);
            }

            return SkillResult.SUCCESS;
         } else {
            Entity attackerBukkit = data.getCaster().getEntity().getBukkitEntity();
            StatProvider attacker = data.getVariables().has("MMOSkillMetadata")
               ? ((io.lumine.mythic.lib.skill.SkillMetadata)data.getVariables().get("MMOSkillMetadata").get()).getCaster()
               : (attackerBukkit instanceof LivingEntity ? StatProvider.get((LivingEntity)attackerBukkit, EquipmentSlot.MAIN_HAND, true) : null);
            DamageMetadata damageMeta = element == null ? new DamageMetadata(damage, damageTypes) : new DamageMetadata(damage, element, damageTypes);
            AttackMetadata attackMeta = new AttackMetadata(damageMeta, (LivingEntity)target.getBukkitEntity(), attacker);
            MythicLib.plugin.getDamage().markAsMetadata(attackMeta);

            try {
               this.doDamage(data, target, damage);
               MythicLogger.debug(DebugLevel.MECHANIC, "+ MMODamageMechanic fired for {0} with {1} power", new Object[]{damage, data.getPower()});
            } catch (Exception exception) {
               MythicLib.plugin.getLogger().log(Level.SEVERE, "Caught an exception (4) while damaging entity '" + target.getUniqueId() + "':");
               exception.printStackTrace();
            } finally {
               MythicLib.plugin.getDamage().unmarkAsMetadata(target.getBukkitEntity());
            }

            return SkillResult.SUCCESS;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}

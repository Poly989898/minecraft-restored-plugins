package io.lumine.mythic.lib.comp.mythicmobs;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.MythicLibSupport;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.comp.mythicmobs.condition.CanTargetCondition;
import io.lumine.mythic.lib.comp.mythicmobs.mechanic.MMODamageMechanic;
import io.lumine.mythic.lib.comp.mythicmobs.mechanic.MultiplyDamageMechanic;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.manager.StatManager;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.player.cooldown.CooldownInfo;
import io.lumine.mythic.lib.player.cooldown.CooldownMap;
import java.util.Optional;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class MythicLibSupportImpl extends ReloadableModule<MythicBukkit> implements MythicLibSupport {
   public MythicLibSupportImpl() {
      super(MythicBukkit.inst());
   }

   public void load(MythicBukkit plugin) {
      Events.subscribe(MythicMechanicLoadEvent.class)
         .handler(
            event -> {
               if (event.getMechanicName().equalsIgnoreCase("mmodamage") || event.getMechanicName().equalsIgnoreCase("mmod")) {
                  event.register(
                     new MMODamageMechanic(event.getContainer().getManager(), event.getContainer().getFile(), event.getMechanicName(), event.getConfig())
                  );
               }

               if (event.getMechanicName().equalsIgnoreCase("multiplydamage")) {
                  event.register(new MultiplyDamageMechanic(event.getConfig()));
               }
            }
         );
      Events.subscribe(MythicConditionLoadEvent.class).handler(event -> {
         if (event.getConditionName().equalsIgnoreCase("canTarget")) {
            event.register(new CanTargetCondition(event.getConditionName(), event.getConfig()));
         }
      });
      Events.subscribe(MythicReloadedEvent.class).handler(event -> this.registerPlaceholders());
      this.registerPlaceholders();
   }

   public void unload() {
   }

   private void registerPlaceholders() {
      MythicBukkit.inst().getPlaceholderManager().register("modifier", Placeholder.meta((metadata, arg) -> {
         if (!(metadata instanceof SkillMetadata)) {
            throw new RuntimeException("Cannot use this placeholder outside of skill");
         }

         Variable var = ((SkillMetadata)metadata).getVariables().get("MMOSkillMetadata");
         Validate.notNull(var, "Could not find MythicLib skill variable");
         io.lumine.mythic.lib.skill.SkillMetadata mmoMetadata = (io.lumine.mythic.lib.skill.SkillMetadata)var.get();
         return String.valueOf(mmoMetadata.getParameter(arg));
      }));
      MythicBukkit.inst().getPlaceholderManager().register("modifier.int", Placeholder.meta((metadata, arg) -> {
         if (!(metadata instanceof SkillMetadata)) {
            throw new RuntimeException("Cannot use this placeholder outside of skill");
         }

         Variable var = ((SkillMetadata)metadata).getVariables().get("MMOSkillMetadata");
         Validate.notNull(var, "Could not find MythicLib skill variable");
         io.lumine.mythic.lib.skill.SkillMetadata mmoMetadata = (io.lumine.mythic.lib.skill.SkillMetadata)var.get();
         return String.valueOf((int)mmoMetadata.getParameter(arg));
      }));
      MythicBukkit.inst().getPlaceholderManager().register("mmodamage", Placeholder.meta((metadata, arg) -> {
         if (!(metadata instanceof SkillMetadata)) {
            throw new RuntimeException("Cannot use this placeholder outside of skill");
         } else {
            SkillMetadata skillMeta = (SkillMetadata)metadata;
            Optional<AbstractEntity> damagedOpt = skillMeta.getEntityTargets().stream().findFirst();
            Validate.isTrue(damagedOpt.isPresent(), "Could not find target entity");
            Entity damaged = damagedOpt.get().getBukkitEntity();
            AttackMetadata attackMeta = MythicLib.plugin.getDamage().findAttack(new EntityDamageEvent(damaged, DamageCause.CUSTOM, 0.0));
            Validate.notNull(attackMeta, "Entity not being attacked");
            if (arg != null && !arg.isEmpty()) {
               DamageType type = DamageType.valueOf(UtilityMethods.enumName(arg));
               return String.valueOf(attackMeta.getDamage().getDamage(type));
            } else {
               return String.valueOf(attackMeta.getDamage().getDamage());
            }
         }
      }));
      MythicBukkit.inst().getPlaceholderManager().register("mmostat", Placeholder.meta((metadata, arg) -> {
         if (metadata instanceof SkillMetadata && ((SkillMetadata)metadata).getVariables().has("MMOSkillMetadata")) {
            Variable var = ((SkillMetadata)metadata).getVariables().get("MMOSkillMetadata");
            io.lumine.mythic.lib.skill.SkillMetadata mmoMetadata = (io.lumine.mythic.lib.skill.SkillMetadata)var.get();
            PlayerMetadata caster = mmoMetadata.getCaster();
            String statName = arg.toUpperCase();
            return StatManager.format(statName, caster.getStat(statName));
         }

         SkillCaster caster = metadata.getCaster();
         if (!caster.getEntity().isPlayer()) {
            return "0";
         }

         MMOPlayerData playerData = MMOPlayerData.get(caster.getEntity().getUniqueId());
         return MythicLib.inst().getMMOConfig().decimals.format(playerData.getStatMap().getStat(arg.toUpperCase()));
      }));
      MythicBukkit.inst().getPlaceholderManager().register("target.stat", Placeholder.entity((entity, arg) -> {
         if (!entity.isPlayer()) {
            return "0";
         }

         StatMap statMap = MMOPlayerData.get(entity.getUniqueId()).getStatMap();
         String statName = arg.toUpperCase();
         return StatManager.format(statName, statMap.getStat(statName));
      }));
      MythicBukkit.inst().getPlaceholderManager().register("cooldown", Placeholder.meta((metadata, arg) -> {
         CooldownMap cooldownMap = MMOPlayerData.get(metadata.getCaster().getEntity().getUniqueId()).getCooldownMap();
         CooldownInfo info = cooldownMap.getInfo(arg);
         return String.valueOf(info == null ? 0.0 : info.getRemaining() / 1000.0);
      }));
   }
}

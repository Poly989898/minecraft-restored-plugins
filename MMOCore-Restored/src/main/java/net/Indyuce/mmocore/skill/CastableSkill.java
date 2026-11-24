package net.Indyuce.mmocore.skill;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import io.lumine.mythic.lib.player.cooldown.CooldownInfo;
import io.lumine.mythic.lib.skill.Skill;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import javax.inject.Provider;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.player.PlayerActivity;
import net.Indyuce.mmocore.api.player.PlayerData;

public class CastableSkill extends Skill {
   private final ClassSkill skill;
   private final Provider<Integer> skillLevel;

   @Deprecated
   public CastableSkill(ClassSkill skill, int fixedLevel) {
      this.skill = skill;
      this.skillLevel = () -> fixedLevel;
   }

   public CastableSkill(ClassSkill skill, PlayerData playerData) {
      this.skill = skill;
      this.skillLevel = () -> playerData.getSkillLevel(skill.getSkill());
   }

   public TriggerType getTrigger() {
      return this.skill.getSkill().getTrigger();
   }

   public ClassSkill getSkill() {
      return this.skill;
   }

   public boolean getResult(SkillMetadata skillMeta) {
      PlayerData playerData = PlayerData.get(skillMeta.getCaster().getData());
      boolean loud = !this.getTrigger().isSilent();
      if (!playerData.hasUnlockedLevel(this.skill)) {
         if (loud) {
            ConfigMessage.fromKey("skill-level-not-met").send(playerData.getPlayer());
         }

         return false;
      } else {
         if (!this.getTrigger().isPassive() && playerData.getActivityTimeOut(PlayerActivity.CAST_SKILL) > 0L) {
            return false;
         }

         if (skillMeta.getCaster().getData().getCooldownMap().isOnCooldown(this)) {
            if (loud) {
               ConfigMessage.fromKey(
                     "casting.on-cooldown",
                     "cooldown",
                     MythicLib.plugin.getMMOConfig().decimal.format(skillMeta.getCaster().getData().getCooldownMap().getCooldown(this))
                  )
                  .send(playerData.getPlayer());
            }

            return false;
         } else if (playerData.getMana() < skillMeta.getParameter("mana")) {
            if (loud) {
               ConfigMessage.fromKey(
                     "casting.no-mana",
                     "mana-required",
                     MythicLib.plugin.getMMOConfig().decimal.format(skillMeta.getParameter("mana") - playerData.getMana()),
                     "mana",
                     playerData.getProfess().getManaDisplay().getName()
                  )
                  .send(playerData.getPlayer());
            }

            return false;
         } else if (playerData.getStamina() < skillMeta.getParameter("stamina")) {
            if (loud) {
               ConfigMessage.fromKey("casting.no-stamina").send(playerData.getPlayer());
            }

            return false;
         } else {
            return MythicLib.plugin.getFlags().isFlagAllowed(skillMeta.getCaster().getPlayer(), CustomFlag.MMO_ABILITIES);
         }
      }
   }

   public void whenCast(SkillMetadata skillMeta) {
      PlayerData casterData = PlayerData.get(skillMeta.getCaster().getData());
      if (!casterData.noCooldown) {
         double flatCooldownReduction = Math.max(0.0, Math.min(1.0, skillMeta.getCaster().getStat("COOLDOWN_REDUCTION") / 100.0));
         CooldownInfo cooldownHandler = skillMeta.getCaster().getData().getCooldownMap().applyCooldown(this, skillMeta.getParameter("cooldown"));
         cooldownHandler.reduceInitialCooldown(flatCooldownReduction);
         casterData.giveMana(-skillMeta.getParameter("mana"), PlayerResourceUpdateEvent.UpdateReason.SKILL_COST);
         casterData.giveStamina(-skillMeta.getParameter("stamina"), PlayerResourceUpdateEvent.UpdateReason.SKILL_COST);
      }

      if (!this.getTrigger().isPassive()) {
         casterData.setLastActivity(PlayerActivity.CAST_SKILL);
      }
   }

   public SkillHandler<?> getHandler() {
      return this.skill.getSkill().getHandler();
   }

   public double getParameter(String mod) {
      return this.skill.getParameter(mod, (Integer)this.skillLevel.get());
   }
}

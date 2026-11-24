package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.trigger.api.Removable;
import net.Indyuce.mmocore.skill.RegisteredSkill;

public class LevelUpSkillTrigger extends Trigger implements Removable {
   private final RegisteredSkill skill;
   private final int amount;

   public LevelUpSkillTrigger(MMOLineConfig config) {
      super(config);
      config.validateKeys(new String[]{"skill", "amount"});
      this.amount = config.getInt("amount");
      this.skill = MMOCore.plugin.skillManager.getSkillOrThrow(config.getString("skill"));
   }

   @Override
   public void apply(PlayerData playerData) {
      playerData.setSkillLevel(this.skill, playerData.getSkillLevel(this.skill) + this.amount);
   }

   @Override
   public void remove(PlayerData playerData) {
      playerData.setSkillLevel(this.skill, Math.max(0, playerData.getSkillLevel(this.skill) - this.amount));
   }
}

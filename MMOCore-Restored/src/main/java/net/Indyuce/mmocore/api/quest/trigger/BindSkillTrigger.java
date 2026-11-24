package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.trigger.api.Removable;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;

public class BindSkillTrigger extends Trigger implements Removable {
   private final RegisteredSkill skill;
   private final int slot;

   public BindSkillTrigger(MMOLineConfig config) {
      super(config);
      config.validateKeys(new String[]{"skill", "slot"});
      this.slot = config.getInt("slot");
      this.skill = MMOCore.plugin.skillManager.getSkillOrThrow(config.getString("skill"));
   }

   @Override
   public void apply(PlayerData playerData) {
      ClassSkill found = playerData.getProfess().getSkill(this.skill);
      if (found != null) {
         playerData.bindSkill(this.slot, found);
      }
   }

   @Override
   public void remove(PlayerData playerData) {
      playerData.unbindSkill(this.slot);
   }
}

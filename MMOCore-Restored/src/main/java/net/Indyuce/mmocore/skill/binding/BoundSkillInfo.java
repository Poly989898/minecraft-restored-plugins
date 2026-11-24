package net.Indyuce.mmocore.skill.binding;

import io.lumine.mythic.lib.player.skill.PassiveSkill;
import io.lumine.mythic.lib.util.Closeable;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.trigger.SkillModifierTrigger;
import net.Indyuce.mmocore.skill.ClassSkill;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoundSkillInfo implements Closeable {
   private final SkillSlot skillSlot;
   private final PlayerData playerData;
   private final ClassSkill classSkill;
   public int skillBarCastSlot;
   @Nullable
   private final PassiveSkill registered;
   private boolean open = true;

   public BoundSkillInfo(@NotNull SkillSlot skillSlot, @NotNull ClassSkill classSkill, @NotNull PlayerData playerData) {
      this.skillSlot = skillSlot;
      this.classSkill = classSkill;
      this.playerData = playerData;

      for (SkillModifierTrigger skillModifierTrigger : skillSlot.getSkillModifierTriggers()) {
         if (skillModifierTrigger.getTargetSkills().contains(classSkill.getSkill().getHandler())) {
            skillModifierTrigger.apply(playerData, classSkill.getSkill().getHandler());
         }
      }

      if (classSkill.getSkill().getTrigger().isPassive() && !classSkill.isPermanent()) {
         this.registered = classSkill.toPassive(playerData);
         this.registered.register(playerData.getMMOPlayerData());
      } else {
         this.registered = null;
      }
   }

   @NotNull
   public ClassSkill getClassSkill() {
      return this.classSkill;
   }

   @NotNull
   public PlayerData getPlayerData() {
      return this.playerData;
   }

   @NotNull
   public SkillSlot getSkillSlot() {
      return this.skillSlot;
   }

   public boolean isPassive() {
      return this.classSkill.getSkill().getTrigger().isPassive();
   }

   public void close() {
      Validate.isTrue(this.open, "BoundSkillInfo has already been closed");
      this.open = false;
      if (this.registered != null) {
         this.registered.unregister(this.playerData.getMMOPlayerData());
      }

      this.skillSlot.getSkillModifierTriggers().forEach(skillBuffTrigger -> skillBuffTrigger.remove(this.playerData, this.classSkill.getSkill().getHandler()));
   }
}

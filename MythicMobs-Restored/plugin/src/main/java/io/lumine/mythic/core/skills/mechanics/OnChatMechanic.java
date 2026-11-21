package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPromptImpl;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   name = "onchat",
   aliases = {"chatprompt"},
   description = "Applies an aura to the target that triggers a skill when they type a chat message",
   deprecated = true
)
public class OnChatMechanic extends Aura implements ITargetedEntitySkill {
   @MythicField(
      name = "onChat",
      aliases = {"oc", "then"},
      version = "4.14",
      description = "Skill to execute when a block is placed"
   )
   protected Optional<Skill> onChatSkill = Optional.empty();
   protected String onChatSkillName;
   @MythicField(
      name = "conditions",
      aliases = {"cond", "c"},
      defValue = "NONE",
      version = "5.1",
      description = "Conditions applied to the caster when triggered"
   )
   protected List<SkillCondition> inputConditions = null;

   public OnChatMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onChatSkillName = mlc.getString(new String[]{"onchatskill", "onchat", "oc", "then"});
      this.getManager().queueSecondPass(() -> {
         if (this.onChatSkillName != null) {
            this.onChatSkill = this.getManager().getSkill(file, this, this.onChatSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         new OnChatMechanic.Tracker(data, target);
         return SkillResult.SUCCESS;
      }
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         this.start();
      }

      @Override
      public void auraStart() {
         Player player = (Player)this.entity.get().getBukkitEntity();
         ChatPromptImpl prompt = new ChatPromptImpl(player, input -> {
            SkillMetadata meta = this.skillMetadata.deepClone();
            meta.getVariables().putString("input", input);
            meta.setEntityTarget(this.entity.get());
            if (this.executeAuraSkill(OnChatMechanic.this.onChatSkill, meta)) {
               this.consumeCharge();
               return ChatPrompt.Response.ACCEPTED;
            } else {
               return ChatPrompt.Response.TRY_AGAIN;
            }
         });
         this.registerAuraComponent(prompt);
         prompt.get().thenAcceptSync(in -> this.terminate());
         this.executeAuraSkill(OnChatMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}

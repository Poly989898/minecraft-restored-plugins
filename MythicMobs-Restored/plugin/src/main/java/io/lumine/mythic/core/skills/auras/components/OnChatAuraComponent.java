package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPromptImpl;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.Optional;
import org.bukkit.entity.Player;

public class OnChatAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private final Optional<Skill> onChatSkill;

   public OnChatAuraComponent(Aura.AuraTracker tracker, Optional<Skill> onChatSkill) {
      this.tracker = tracker;
      this.onChatSkill = onChatSkill;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty() && maybeEntity.get().isPlayer()) {
         Player player = (Player)maybeEntity.get().getBukkitEntity();
         ChatPromptImpl prompt = new ChatPromptImpl(player, input -> {
            SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
            meta.getVariables().putString("input", input);
            meta.setEntityTarget(maybeEntity.get());
            if (this.tracker.executeAuraSkill(this.onChatSkill, meta)) {
               this.tracker.consumeCharge();
               return ChatPrompt.Response.ACCEPTED;
            } else {
               return ChatPrompt.Response.TRY_AGAIN;
            }
         });
         this.tracker.registerAuraComponent(prompt);
         prompt.get().thenAcceptSync(in -> this.tracker.terminate());
      }
   }

   @MythicAuraComponent(
      name = "onchat",
      aliases = {"chatprompt"},
      description = "Triggers a skill from chat input"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onChatSkillName;
      private Optional<Skill> onChatSkill = Optional.empty();

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onChatSkillName = mlc.getString(new String[]{"onchatskill", "onchat", "oc", "then"});
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onChatSkillName != null) {
               this.onChatSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onChatSkillName);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new OnChatAuraComponent(tracker, this.onChatSkill);
      }
   }
}

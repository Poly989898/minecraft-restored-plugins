package io.lumine.mythic.core.skills.triggers.meta;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import java.util.function.Consumer;

public final class SimpleSkillMetadata extends SkillTriggerMetadata {
   private final Consumer<SkillMetadata> consumer;

   public SimpleSkillMetadata(Consumer<SkillMetadata> consumer) {
      this.consumer = consumer;
   }

   @Override
   public void applyToSkillMetadata(SkillMetadata skillMetadata) {
      this.consumer.accept(skillMetadata);
   }
}

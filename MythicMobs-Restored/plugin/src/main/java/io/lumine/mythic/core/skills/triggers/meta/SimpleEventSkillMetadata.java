package io.lumine.mythic.core.skills.triggers.meta;

import io.lumine.mythic.api.skills.SkillMetadata;
import java.util.function.BiConsumer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public final class SimpleEventSkillMetadata<E extends Event> extends EmptyMetadata {
   private final BiConsumer<E, SkillMetadata> eventSkillMetadataConsumer;

   public SimpleEventSkillMetadata(@NotNull BiConsumer<E, SkillMetadata> skillMetadataBiConsumer) {
      this.eventSkillMetadataConsumer = skillMetadataBiConsumer;
   }

   public void applyToSkillMetadata(E event, SkillMetadata skillMetadata) {
      CancellableTriggerMetadata.captureEvent(skillMetadata, event);
      this.eventSkillMetadataConsumer.accept(event, skillMetadata);
   }
}

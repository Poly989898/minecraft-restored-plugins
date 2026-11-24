package io.lumine.mythic.lib.comp.interaction.relation;

import io.lumine.mythic.lib.comp.interaction.InteractionType;
import org.jetbrains.annotations.NotNull;

public class EmptyPvPInteractionRules extends InteractionRules {
   public EmptyPvPInteractionRules() {
      super(null);
   }

   @Override
   public boolean isEnabled(boolean pvpEnabled, @NotNull InteractionType interaction, @NotNull Relationship relationship) {
      return true;
   }
}

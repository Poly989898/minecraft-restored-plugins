package io.lumine.mythic.core.skills.triggers.meta;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import lombok.Getter;

@Getter
public class PlayerInputMeta extends SkillTriggerMetadata {
   private final InputState state;

   public PlayerInputMeta(InputState state) {
      this.state = state == null ? InputState.EMPTY : state;
   }

   @Override
   public void applyToSkillMetadata(SkillMetadata skillMetadata) {
      VariableRegistry variables = skillMetadata.getVariables();
      variables.putInt("input-forward", this.state.forward() ? 1 : 0);
      variables.putInt("input-backward", this.state.backward() ? 1 : 0);
      variables.putInt("input-left", this.state.left() ? 1 : 0);
      variables.putInt("input-right", this.state.right() ? 1 : 0);
      variables.putInt("input-jump", this.state.jump() ? 1 : 0);
      variables.putInt("input-sneak", this.state.sneak() ? 1 : 0);
      variables.putInt("input-sprint", this.state.sprint() ? 1 : 0);
      variables.putString("input", this.describeActive());
   }

   private String describeActive() {
      StringBuilder activeKeys = new StringBuilder();
      if (this.state.forward()) {
         append(activeKeys, "forward");
      }

      if (this.state.backward()) {
         append(activeKeys, "backward");
      }

      if (this.state.left()) {
         append(activeKeys, "left");
      }

      if (this.state.right()) {
         append(activeKeys, "right");
      }

      if (this.state.jump()) {
         append(activeKeys, "jump");
      }

      if (this.state.sneak()) {
         append(activeKeys, "sneak");
      }

      if (this.state.sprint()) {
         append(activeKeys, "sprint");
      }

      return activeKeys.isEmpty() ? "none" : activeKeys.toString();
   }

   private static void append(StringBuilder keys, String key) {
      if (!keys.isEmpty()) {
         keys.append(',');
      }

      keys.append(key);
   }
}

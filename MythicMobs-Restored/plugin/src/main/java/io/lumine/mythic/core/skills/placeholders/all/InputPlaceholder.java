package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.skills.triggers.meta.InputState;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "input",
   usedPlaceholderArguments = -1
)
public class InputPlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> inputKey = this.getPlaceholderString(0, new String[0]);

   public InputPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
      this.initializeMetaKeywords();
   }

   @Nullable
   public String applyToScope(PlaceholderContext context) {
      AbstractEntity entity = this.getEntity.get(context);
      if (entity != null && entity.getBukkitEntity() instanceof Player player) {
         try {
            InputState state = InputState.from(player.getCurrentInput());
            return state.isPressed(this.inputKey.getOrDefault(context, PlaceholderString::get, "forward")) ? "1" : "0";
         } catch (UnsupportedOperationException | NoSuchMethodError e) {
            return "0";
         }
      } else {
         return "0";
      }
   }
}

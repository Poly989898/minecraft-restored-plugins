package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "level",
   aliases = {"lvl"}
)
public class LevelPlaceholder extends NumericEntityScopedPlaceholder {
   public LevelPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context, 0);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      return this.manager.getPlugin().getSkillManager().getCaster(this.getEntity.get(placeholderContext)).getLevel();
   }
}

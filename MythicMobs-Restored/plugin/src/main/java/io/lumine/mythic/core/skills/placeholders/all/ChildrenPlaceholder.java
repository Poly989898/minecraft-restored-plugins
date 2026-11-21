package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.ListPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "children"
)
public class ChildrenPlaceholder extends EntityScopedPlaceholder<List> implements ListPlaceholder {
   public ChildrenPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public List<String> applyToScope(PlaceholderContext placeholderContext) {
      ActiveMob am = this.getActiveMob(placeholderContext);
      return am == null ? null : am.getChildrenUUID().stream().map(UUID::toString).toList();
   }
}

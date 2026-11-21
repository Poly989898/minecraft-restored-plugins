package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(
   placeholder = "caster.children.size",
   aliases = {}
)
public class ChildrenSizePlaceholder extends GenericPlaceholder<Integer> implements IntegerPlaceholder {
   public ChildrenSizePlaceholder(GenericPlaceholder.GenericPlaceholderArguments arguments) {
      super(arguments);
   }

   public Integer applyWithMetaKeywords(PlaceholderContext context) {
      return context.meta().getCaster().getChildren().size();
   }
}

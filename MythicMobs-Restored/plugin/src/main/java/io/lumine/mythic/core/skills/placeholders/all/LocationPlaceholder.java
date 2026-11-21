package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;

public abstract class LocationPlaceholder extends NumericEntityScopedPlaceholder {
   protected boolean centered = false;

   public LocationPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
      GenericPlaceholderSegment maybeArg = this.getPlaceholderSegmentsManager().getExtraArg(0);
      if (maybeArg == null) {
         this.initializeMetaKeywords();
      } else {
         String arg = maybeArg.key();
         if (arg.equals("double")) {
            this.usedPlaceholderArguments.set(1);
         } else if (arg.equals("centered")) {
            this.usedPlaceholderArguments.set(1);
            this.centered = true;
         }

         this.initializeMetaKeywords();
      }
   }
}

package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public interface PlaceholderDouble extends IPlaceholder {
   static PlaceholderDouble of(String in) {
      return MythicProvider.get().getConfiguration().createPlaceholderDouble(in);
   }

   double get();

   /** @deprecated */
   double get(PlaceholderMeta placeholderMeta);

   /** @deprecated */
   double get(AbstractEntity abstractEntity);

   /** @deprecated */
   double get(PlaceholderMeta placeholderMeta, AbstractEntity abstractEntity);

   /** @deprecated */
   double get(SkillCaster skillCaster);

   double get(PlaceholderContext placeholderContext);

   boolean isStaticallyEqualTo(double value);
}

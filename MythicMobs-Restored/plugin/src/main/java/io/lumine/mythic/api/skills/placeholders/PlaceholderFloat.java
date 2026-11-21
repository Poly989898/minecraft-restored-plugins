package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public interface PlaceholderFloat extends IPlaceholder {
   static PlaceholderFloat of(String in) {
      return MythicProvider.get().getConfiguration().createPlaceholderFloat(in);
   }

   float get();

   /** @deprecated */
   float get(PlaceholderMeta placeholderMeta);

   /** @deprecated */
   float get(AbstractEntity abstractEntity);

   /** @deprecated */
   float get(PlaceholderMeta placeholderMeta, AbstractEntity abstractEntity);

   /** @deprecated */
   float get(SkillCaster skillCaster);

   float get(PlaceholderContext placeholderContext);

   boolean isStaticallyEqualTo(float value);
}

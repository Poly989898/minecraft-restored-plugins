package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public interface PlaceholderString extends IPlaceholder {
   static PlaceholderString of(String in) {
      return MythicProvider.get().getConfiguration().createPlaceholderString(in);
   }

   String get();

   String get(PlaceholderContext placeholderContext);

   /** @deprecated */
   String get(AbstractEntity abstractEntity);

   /** @deprecated */
   String get(PlaceholderMeta placeholderMeta, AbstractEntity abstractEntity);

   /** @deprecated */
   String get(PlaceholderMeta placeholderMeta, AbstractLocation abstractLocation);

   /** @deprecated */
   String get(SkillCaster skillCaster);

   /** @deprecated */
   String get(PlaceholderMeta placeholderMeta, AbstractItemStack abstractItemStack);

   /** @deprecated */
   String get(PlaceholderMeta placeholderMeta);

   boolean isStaticallyEqualTo(String str);
}

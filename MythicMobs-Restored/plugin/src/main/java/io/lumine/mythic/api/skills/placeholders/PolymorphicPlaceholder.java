package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.VariableType;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface PolymorphicPlaceholder extends IPlaceholder {
   static PolymorphicPlaceholder of(String input) {
      return MythicProvider.get().getConfiguration().createPolymorphicPlaceholder(input);
   }

   String originalValue();

   String getString(PlaceholderContext placeholderContext);

   int getInt(PlaceholderContext placeholderContext);

   float getFloat(PlaceholderContext placeholderContext);

   double getDouble(PlaceholderContext placeholderContext);

   Long getLong(PlaceholderContext placeholderContext);

   boolean getBoolean(PlaceholderContext placeholderContext);

   List<String> getList(PlaceholderContext placeholderContext);

   Map<String, String> getMap(PlaceholderContext placeholderContext);

   AbstractVector getVector(PlaceholderContext placeholderContext);

   Instant getTime(PlaceholderContext placeholderContext);

   PolymorphicPlaceholder cache(VariableType variableType);
}

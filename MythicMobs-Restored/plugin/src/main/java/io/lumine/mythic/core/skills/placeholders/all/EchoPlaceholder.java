package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "echo",
   version = "5.2",
   usedPlaceholderArguments = -1
)
public class EchoPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> arg = this.getPlaceholderString(0, new String[]{"echo"});

   public EchoPlaceholder(GenericPlaceholder.GenericPlaceholderArguments arguments) {
      super(arguments);
      this.initializeMetaKeywords();
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return this.arg.getOrDefault(placeholderContext, PlaceholderString::get, "UNDEFINED");
   }
}

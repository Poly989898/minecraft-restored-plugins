package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.SegmentSource;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "char",
   usedPlaceholderArguments = -1
)
public class CharPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   final ResolvedPlaceholderSegment<PlaceholderString> character = this.getPlaceholderString(
      0, SegmentSource.EXTRA_ARGS, new String[]{"character", "char", "c"}
   );

   public CharPlaceholder(@NotNull GenericPlaceholder.GenericPlaceholderArguments arguments) {
      super(arguments);
      this.initializeMetaKeywords();
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      String code = this.character.get(placeholderContext, PlaceholderString::get);
      if (code == null) {
         return null;
      } else {
         if (code.startsWith("\\")) {
            code = code.substring(1);
         }

         if (code.startsWith("u")) {
            code = code.substring(1);
         }

         int codePoint = Integer.parseInt(code, 16);
         return Character.toString(codePoint);
      }
   }
}

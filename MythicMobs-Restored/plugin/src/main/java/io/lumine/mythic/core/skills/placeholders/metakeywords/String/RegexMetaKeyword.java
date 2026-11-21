package io.lumine.mythic.core.skills.placeholders.metakeywords.String;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@MythicMetaKeyword(
   name = "regex"
)
public class RegexMetaKeyword extends AbstractMetaKeyword<String, Boolean> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute REGEX = CommonAttributes.REGEX;
   private static final int CACHE_MAX = 256;
   private static final ConcurrentHashMap<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

   public Boolean extract(MetaKeywords.ExtractionHandler<String, Boolean> handler) {
      String source = handler.getString(REGEX);
      Pattern pattern = PATTERN_CACHE.get(source);
      if (pattern == null) {
         pattern = Pattern.compile(source);
         if (PATTERN_CACHE.size() >= 256) {
            PATTERN_CACHE.clear();
         }

         PATTERN_CACHE.put(source, pattern);
      }

      return pattern.matcher(handler.value()).find();
   }
}

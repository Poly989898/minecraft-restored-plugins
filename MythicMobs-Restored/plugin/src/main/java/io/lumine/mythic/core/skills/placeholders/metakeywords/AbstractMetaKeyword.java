package io.lumine.mythic.core.skills.placeholders.metakeywords;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractMetaKeyword<T, K> {
   public Collection<MetaKeywords.MetaKeywordsAttribute> getAttributes() {
      List<MetaKeywords.MetaKeywordsAttribute> attributes = new ArrayList<>();

      for (Field field : this.getClass().getDeclaredFields()) {
         if (MetaKeywords.MetaKeywordsAttribute.class.isAssignableFrom(field.getType())) {
            MythicMetaKeywordAttribute annotation = field.getAnnotation(MythicMetaKeywordAttribute.class);
            if (annotation != null) {
               field.setAccessible(true);

               try {
                  MetaKeywords.MetaKeywordsAttribute base = (MetaKeywords.MetaKeywordsAttribute)field.get(null);
                  attributes.add(base.toBuilder().index(annotation.index()).build());
               } catch (IllegalAccessException e) {
                  throw new RuntimeException(e);
               }
            }
         }
      }

      return attributes;
   }

   public abstract K extract(MetaKeywords.ExtractionHandler<T, K> extractionHandler);
}

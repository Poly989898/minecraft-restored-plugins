package io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes;

public interface LongPlaceholder extends GenericPlaceholderTyping<Long> {
   default String convertType(Long parsedPlaceholder) {
      return String.valueOf(parsedPlaceholder);
   }

   @Override
   default Class<Long> getParsedValueClass() {
      return Long.class;
   }
}

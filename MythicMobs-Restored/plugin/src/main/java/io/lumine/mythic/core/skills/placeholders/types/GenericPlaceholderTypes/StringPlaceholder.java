package io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes;

public interface StringPlaceholder extends GenericPlaceholderTyping<String> {
   default String convertType(String parsedPlaceholder) {
      return String.valueOf(parsedPlaceholder);
   }

   @Override
   default Class<String> getParsedValueClass() {
      return String.class;
   }
}

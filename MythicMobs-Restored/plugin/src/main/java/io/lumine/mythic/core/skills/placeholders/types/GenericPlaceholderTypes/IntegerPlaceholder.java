package io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes;

public interface IntegerPlaceholder extends GenericPlaceholderTyping<Integer> {
   default String convertType(Integer parsedPlaceholder) {
      return Integer.toString(parsedPlaceholder);
   }

   @Override
   default Class<Integer> getParsedValueClass() {
      return Integer.class;
   }
}

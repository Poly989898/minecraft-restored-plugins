package io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes;

public interface FloatPlaceholder extends GenericPlaceholderTyping<Float> {
   @Override
   default Class<Float> getParsedValueClass() {
      return Float.class;
   }

   default String convertType(Float parsedPlaceholder) {
      return parsedPlaceholder == null ? null : parsedPlaceholder.toString();
   }
}

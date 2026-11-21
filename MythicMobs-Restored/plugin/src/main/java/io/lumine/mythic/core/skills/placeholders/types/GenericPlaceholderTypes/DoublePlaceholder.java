package io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes;

public interface DoublePlaceholder extends GenericPlaceholderTyping<Double> {
   @Override
   default Class<Double> getParsedValueClass() {
      return Double.class;
   }

   default String convertType(Double parsedPlaceholder) {
      return parsedPlaceholder == null ? null : parsedPlaceholder.toString();
   }
}

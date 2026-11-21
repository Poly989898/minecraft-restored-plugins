package io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes;

public interface GenericPlaceholderTyping<T> {
   String convertType(T t);

   Class<T> getParsedValueClass();
}

package io.lumine.mythic.core.skills.placeholders;

public abstract class GenericPlaceholderParser<T> extends PlaceholderParser {
   public GenericPlaceholderParser(String string) {
      super(string);
   }

   public T get(PlaceholderContext context) {
      String string = this.parse(context);
      return this.handleParsing(string);
   }

   protected abstract T handleParsing(String str);
}

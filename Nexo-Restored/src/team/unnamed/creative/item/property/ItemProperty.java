package team.unnamed.creative.item.property;

public interface ItemProperty {
   default boolean isBoolean() {
      return false;
   }

   default boolean isNumeric() {
      return false;
   }

   default boolean isString() {
      return false;
   }
}

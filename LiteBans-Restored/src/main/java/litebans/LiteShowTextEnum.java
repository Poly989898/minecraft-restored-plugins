package litebans;

public enum LiteShowTextEnum {
   SHOW_TEXT;

   public static LiteShowTextEnum computeLiteShowTextEnum(String text) {
      return Enum.valueOf(LiteShowTextEnum.class, text);
   }

   private static final LiteShowTextEnum[] obtainLiteShowTextEnumArray() {
      return new LiteShowTextEnum[]{SHOW_TEXT};
   }
}

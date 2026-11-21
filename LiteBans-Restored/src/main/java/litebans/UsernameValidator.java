package litebans;

public final class UsernameValidator {
   public static final MojangProfileLookup KOTLIN_DATA = new MojangProfileLookup(null);
   private static final Regex return2 = new Regex("^\\w{2,16}$");
   private static final JSONParser return3 = new JSONParser();

   public static final Regex obtainRegex() {
      return return2;
   }

   public static final JSONParser obtainJsonParser() {
      return return3;
   }
}

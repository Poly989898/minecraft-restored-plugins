package it.ytnoos.lpx.api.check;

public class FlagInfo {
   private final String name;
   private final Object value;

   public FlagInfo(String string, Object object) {
      this.name = string;
      this.value = object;
   }

   public String getName() {
      return this.name;
   }

   public Object getValue() {
      return this.value;
   }

   public static FlagInfo of(String string, Object object) {
      return new FlagInfo(string, object);
   }

   // State hooks of the obfuscator: the bundled PacketEvents/Adventure bytecode calls these by name (see README, "Hooks").
   // Default state (null) is the normal control flow of those classes.
   private static int[] hookState;

   public static int[] z9() {
      return hookState;
   }

   public static void WX(int[] state) {
      hookState = state;
   }

   @Override
   public String toString() {
      return this.name + "=" + this.value;
   }
}

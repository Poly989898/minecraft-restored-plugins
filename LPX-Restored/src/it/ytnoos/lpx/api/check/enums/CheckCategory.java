package it.ytnoos.lpx.api.check.enums;


public enum CheckCategory {
   NETTY("netty"),
   WINDOW("window"),
   ITEM("item"),
   CREATIVE("creative"),
   PLACE("place"),
   FLOOD("flood"),
   POSITION("position"),
   PAYLOAD("payload"),
   BOOK("book"),
   TAB("tab"),
   COMMAND("command"),
   RECIPE("recipe");

   private final String name;

   CheckCategory(String string) {
      this.name = string;
   }

   @Override
   public String toString() {
      return this.name;
   }

   public String getFormattedName() {
      return capitalise(this.toString());
   }

   private static String capitalise(String string) {
      if (string == null) {
         return null;
      } else {
         return string.isEmpty() ? "" : Character.toTitleCase(string.charAt(0)) + string.substring(1);
      }
   }

}

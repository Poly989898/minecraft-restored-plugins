package me.TechsCode.UltraPermissions.base.visual;

public enum MinecraftColor {
   DARK_RED("&4", "#AA0000"),
   RED("&c", "#FF5555"),
   GOLD("&6", "#FFAA00"),
   YELLOW("&e", "#FFFF55"),
   DARK_GREEN("&2", "#00AA00"),
   GREEN("&a", "#55FF55"),
   AQUA("&b", "#55FFFF"),
   DARK_AQUA("&3", "#00AAAA"),
   DARK_BLUE("&1", "#0000AA"),
   BLUE("&9", "#5555FF"),
   LIGHT_PURPLE("&d", "#FF55FF"),
   DARK_PURPLE("&5", "#AA00AA"),
   WHITE("&f", "#FFFFFF"),
   GRAY("&7", "#AAAAAA"),
   DARK_GRAY("&8", "#555555"),
   BLACK("&0", "#000000");

   private final String chatColor;
   private final Color color;

   public static MinecraftColor getClosest(Color color) {
      MinecraftColor minecraftColor = null;
      int count = 0;

      for (MinecraftColor candidate : values()) {
         int distance = Color.difference(color, candidate.getColor());
         if (minecraftColor == null || count > distance) {
            count = distance;
            minecraftColor = candidate;
         }
      }

      return minecraftColor;
   }

   MinecraftColor(String text, String hex) {
      this.chatColor = text;
      this.color = Color.from(hex);
   }

   public String getName() {
      return this.name().toLowerCase();
   }

   public Color getColor() {
      return this.color;
   }

   public String getTag() {
      return this.chatColor;
   }

   public String getAppliedTag() {
      return this.chatColor.replace("&", "\u00a7");
   }

   @Override
   public String toString() {
      return this.getAppliedTag();
   }
}

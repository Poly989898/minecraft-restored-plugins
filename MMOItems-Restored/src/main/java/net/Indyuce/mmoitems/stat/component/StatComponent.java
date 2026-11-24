package net.Indyuce.mmoitems.stat.component;

@Deprecated
public abstract class StatComponent {
   private final String path;

   public StatComponent(String text) {
      this.path = text;
   }

   public String getPath() {
      return this.path;
   }
}

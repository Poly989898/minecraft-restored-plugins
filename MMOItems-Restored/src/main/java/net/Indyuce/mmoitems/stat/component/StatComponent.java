package net.Indyuce.mmoitems.stat.component;

@Deprecated
public abstract class StatComponent {
   private final String path;

   public StatComponent(String var1) {
      this.path = var1;
   }

   public String getPath() {
      return this.path;
   }
}

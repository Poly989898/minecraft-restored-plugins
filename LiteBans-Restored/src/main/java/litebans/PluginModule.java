package litebans;

@PluginMarker
public abstract class PluginModule {
   public final Platform platform;

   public PluginModule(Platform platform) {
      if (platform == null) {
         throw new NullPointerException();
      }

      this.platform = platform;
   }

   public void disable() {
   }

   public void reloadConfig() {
   }

   public void load() {
   }

   public boolean obtainFlag() {
      return true;
   }

   public abstract void enable();
}

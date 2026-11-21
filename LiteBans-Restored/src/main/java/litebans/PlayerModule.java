package litebans;

public abstract class PlayerModule extends PluginModule {
   public PlayerModule(Platform platform) {
      super(platform);
   }

   public abstract LiteSender[] obtainLiteSenderArray();

   public abstract int obtainIntValue();

   @Override
   public void enable() {
   }
}

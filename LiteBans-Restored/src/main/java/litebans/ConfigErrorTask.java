package litebans;

public final class ConfigErrorTask implements Runnable {
   final ConfigModule configModule;
   final String text2;

   public ConfigErrorTask(ConfigModule configModule, String text) {
      this.configModule = configModule;
      this.text2 = text;
   }

   @Override
   public final void run() {
      this.configModule.platform.getLogger().info(this.text2);
   }
}

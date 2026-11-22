package litebans;

public final class ConfigErrorTask implements Runnable {
   final ConfigModule configModule;
   final String message;

   public ConfigErrorTask(ConfigModule configModule, String text) {
      this.configModule = configModule;
      this.message = text;
   }

   @Override
   public final void run() {
      this.configModule.platform.getLogger().info(this.message);
   }
}

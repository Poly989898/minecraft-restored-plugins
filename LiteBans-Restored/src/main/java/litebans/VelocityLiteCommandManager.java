package litebans;

import com.velocitypowered.api.command.CommandManager;

@PlatformMarker(obtainByteValue = 2)
public class VelocityLiteCommandManager extends LiteCommandManager {
   public VelocityLiteCommandManager(Platform platform) {
      super(platform);
   }

   @Override
   public boolean obtainFlag() {
      return this.platform.getPlatformId() == 2;
   }

   @Override
   public void enable() {
   }

   @Override
   public void load() {
      super.load();

      for (LiteCommand liteCommand : (Iterable<LiteCommand>) this.getCollection4()) {
         this.handleLiteCommand(new VelocityCommandWrapper(liteCommand, liteCommand.getPermission(), this.platform, liteCommand.getAliases()));
      }
   }

   public void handleLiteCommand(LiteCommand liteCommand) {
      VelocityPlugin velocityPlugin = (VelocityPlugin)this.platform;
      CommandManager commandManager = velocityPlugin.proxyServer.getCommandManager();
      commandManager.register(liteCommand.getName(), (VelocityCommandWrapper)liteCommand, liteCommand.getAliases());
   }
}

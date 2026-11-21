package litebans;

import net.md_5.bungee.api.plugin.Plugin;

@PlatformMarker(obtainByteValue = 1)
public abstract class BungeeLiteCommandManager extends LiteCommandManager {
   public BungeeLiteCommandManager(Platform platform) {
      super(platform);
   }

   @Override
   public boolean obtainFlag() {
      return this.platform.getPlatformId() == 1;
   }

   @Override
   public void load() {
      super.load();

      for (LiteCommand liteCommand : (Iterable<LiteCommand>) this.getCollection4()) {
         this.handleLiteCommand3(new BungeeCommandWrapper(liteCommand, liteCommand.getPermission(), this.platform, new String[0]));
      }
   }

   public void handleLiteCommand3(LiteCommand liteCommand) {
      Plugin plugin = (Plugin)this.platform;
      plugin.getProxy().getPluginManager().registerCommand(plugin, (BungeeCommandWrapper)liteCommand);
   }
}

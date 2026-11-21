package litebans;

import com.velocitypowered.api.proxy.Player;
import java.util.Collection;

@PlatformMarker(obtainByteValue = 2)
public class VelocityPlayerModule extends PlayerModule {
   public VelocityPlayerModule(Platform platform) {
      super(platform);
   }

   @Override
   public int obtainIntValue() {
      Collection collection = this.obtainCollection();
      return collection.size();
   }

   private final Collection obtainCollection() {
      return ((VelocityPlugin)this.platform).proxyServer.getAllPlayers();
   }

   @Override
   public LiteSender[] obtainLiteSenderArray() {
      Platform platform = this.platform;
      Collection collection = this.obtainCollection();
      if (collection.isEmpty()) {
         return LiteSender.liteSenderArray;
      }

      LiteSender[] liteSenders = new LiteSender[collection.size()];
      int value = 0;

      for (Player player : (Iterable<Player>) collection) {
         liteSenders[value++] = platform.wrapSender(player);
      }

      return liteSenders;
   }
}

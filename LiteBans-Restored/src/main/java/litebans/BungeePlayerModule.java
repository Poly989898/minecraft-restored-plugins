package litebans;

import java.util.Collection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PlatformMarker(obtainByteValue = 1)
public class BungeePlayerModule extends PlayerModule {
   public BungeePlayerModule(Platform platform) {
      super(platform);
   }

   @Override
   public int obtainIntValue() {
      Collection collection = this.obtainCollection3();
      return collection.size();
   }

   private final Collection obtainCollection3() {
      return ((BungeePlugin)this.platform).getProxy().getPlayers();
   }

   @Override
   public LiteSender[] obtainLiteSenderArray() {
      Platform platform = this.platform;
      Collection collection = this.obtainCollection3();
      if (collection.isEmpty()) {
         return LiteSender.liteSenderArray;
      }

      LiteSender[] liteSenders = new LiteSender[collection.size()];
      int value = 0;

      for (ProxiedPlayer proxiedPlayer : (Iterable<ProxiedPlayer>) collection) {
         liteSenders[value++] = platform.wrapSender(proxiedPlayer);
      }

      return liteSenders;
   }
}

package litebans;

import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.entity.Player;

@PlatformMarker(obtainByteValue = 0)
public class BukkitPlayerModule extends PlayerModule {
   public BukkitPlayerModule(Platform platform) {
      super(platform);
   }

   @Override
   public int obtainIntValue() {
      Collection onlinePlayers = ((BukkitPlugin)this.platform).getServer().getOnlinePlayers();
      return onlinePlayers.size();
   }

   @Override
   public LiteSender[] obtainLiteSenderArray() {
      Collection onlinePlayers = ((BukkitPlugin)this.platform).getServer().getOnlinePlayers();
      if (onlinePlayers.isEmpty()) {
         return LiteSender.liteSenderArray;
      }

      int value = onlinePlayers.size();
      ArrayList arrayList = new ArrayList(value);

      for (Player player : (Iterable<Player>) onlinePlayers) {
         arrayList.add(this.platform.wrapSender(player));
      }

      return (LiteSender[]) arrayList.toArray(LiteSender.liteSenderArray);
   }
}

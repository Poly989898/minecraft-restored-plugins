package it.ytnoos.lpx.check.checks.place;

import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlaceC extends PlayerCheck<WrapperPlayClientPlayerDigging> {
   private double max;

   public PlaceC(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.max = this.getDouble("max", 50.0);
   }

   protected CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPlayerDigging wrapperPlayClientPlayerDigging) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 == null) {
         return CheckResult.ALLOW;
      } else if (wrapperPlayClientPlayerDigging.getAction().ordinal() > 2) {
         return CheckResult.ALLOW;
      } else {
         Location location = player2.getLocation();
         Vector3i vector3i = wrapperPlayClientPlayerDigging.getBlockPosition();
         double number = location.getX() - (vector3i.getX() + 0.5);
         double number2 = location.getY() - (vector3i.getY() + 0.5) + 1.5;
         double number3 = location.getZ() - (vector3i.getZ() + 0.5);
         double number4 = number * number + number2 * number2 + number3 * number3;
         if (number4 > this.max) {
            this.incrementBuffer(
               wrapperPlayClientPlayerDigging,
               new FlagInfo[]{
                  FlagInfo.of("distanceX", number), FlagInfo.of("distanceY", number2), FlagInfo.of("distanceZ", number3), FlagInfo.of("distance", (int)number4)
               }
            );
            return CheckResult.DENY;
         } else {
            this.decayBuffer();
            return CheckResult.ALLOW;
         }
      }
   }

}

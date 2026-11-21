package it.ytnoos.lpx.check.checks.place;

import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.common.utils.TimeUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlaceA extends PlayerCheck<WrapperPlayClientPlayerBlockPlacement> {
   private double maxDistance;
   private long teleportGrace;

   public PlaceA(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.maxDistance = this.getDouble("max", 100.0);
      this.teleportGrace = this.getLong("teleport-grace", 100L);
   }

   protected CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPlayerBlockPlacement wrapperPlayClientPlayerBlockPlacement) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 == null) {
         return CheckResult.ALLOW;
      } else {
         long longValue = this.teleportGrace + Math.max(0, (int)protocolPlayer.getPlayerPing());
         if (TimeUtils.difference(protocolPlayer.getLastTeleport()) < longValue) {
            return CheckResult.ALLOW;
         } else {
            Vector3i vector3i = wrapperPlayClientPlayerBlockPlacement.getBlockPosition();
            double number = vector3i.getX();
            double number2 = vector3i.getZ();
            if (number == -1.0 && number2 == -1.0) {
               return CheckResult.ALLOW;
            } else {
               Location location = player2.getLocation();
               double number3 = Math.abs(number - location.getX()) + Math.abs(number2 - location.getZ());
               if (number3 > this.maxDistance) {
                  this.incrementBuffer(
                     wrapperPlayClientPlayerBlockPlacement,
                     new FlagInfo[]{
                        FlagInfo.of("x", number),
                        FlagInfo.of("z", number2),
                        FlagInfo.of("player-x", location.getX()),
                        FlagInfo.of("player-z", location.getZ()),
                        FlagInfo.of("distance", (int)number3)
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
   }

}

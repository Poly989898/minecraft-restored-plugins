package it.ytnoos.lpx.check.checks.position;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class PositionC extends PlayerCheck<WrapperPlayClientPlayerFlying> {
   private int max;
   private long lastTeleport;
   private static final String MAX_PATH = "max";

   public PositionC(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.max = this.getConfig().getInt(this.getPath(MAX_PATH), 300000);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPlayerFlying wrapperPlayClientPlayerFlying) {
      if (wrapperPlayClientPlayerFlying.hasPositionChanged() && Math.abs(wrapperPlayClientPlayerFlying.getLocation().getY()) > this.max) {
         this.teleportBelow(protocolPlayer);
         this.raiseViolation(wrapperPlayClientPlayerFlying, new FlagInfo[]{FlagInfo.of("y", wrapperPlayClientPlayerFlying.getLocation().getY())});
         return CheckResult.DENY;
      } else {
         return CheckResult.ALLOW;
      }
   }

   private void teleportBelow(ProtocolPlayer protocolPlayer) {
      if (System.currentTimeMillis() - this.lastTeleport >= 1000L) {
         this.lastTeleport = System.currentTimeMillis();
         protocolPlayer.getPlayer().ifPresent(player2 -> FoliaScheduler.getEntityScheduler().run(player2, this.getOptions().getCheckOptions().getPlugin(), object -> {
            if (player2.getLocation().getY() > this.max) {
               player2.teleport(player2.getLocation().clone().subtract(0.0, this.max - 5, 0.0));
            }
         }, () -> {}));
      }
   }

}

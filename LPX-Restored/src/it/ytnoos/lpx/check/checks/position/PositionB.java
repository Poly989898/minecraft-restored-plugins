package it.ytnoos.lpx.check.checks.position;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.common.utils.TimeUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PositionB extends PlayerCheck<WrapperPlayClientPlayerFlying> {
   private int delay;
   private boolean hard;
   private boolean ignoreFly;
   private int lastChunkX;
   private int lastChunkZ;
   private String lastWorld = "";

   public PositionB(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.delay = this.getInt("delay", 2000);
      this.hard = this.getBoolean("hard", false);
      this.ignoreFly = this.getBoolean("ignore-fly", true);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPlayerFlying wrapperPlayClientPlayerFlying) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 == null) {
         return CheckResult.ALLOW;
      }

      if (!wrapperPlayClientPlayerFlying.hasPositionChanged()) {
         return CheckResult.ALLOW;
      }

      if ((!this.ignoreFly || !player2.isFlying()) && TimeUtils.difference(protocolPlayer.getLastTeleport()) >= this.delay) {
         int value = (int)wrapperPlayClientPlayerFlying.getLocation().getX() / 16;
         int value2 = (int)wrapperPlayClientPlayerFlying.getLocation().getZ() / 16;
         World world = player2.getWorld();
         String string = world.getName();
         boolean flag = true;
         if (!world.isChunkLoaded(value, value2)) {
            if ((this.lastChunkX != value || this.lastChunkZ != value2) && this.lastWorld.equals(string)) {
               this.incrementBuffer(
                  wrapperPlayClientPlayerFlying,
                  new FlagInfo[]{
                     FlagInfo.of("x", wrapperPlayClientPlayerFlying.getLocation().getX()),
                     FlagInfo.of("z", wrapperPlayClientPlayerFlying.getLocation().getZ()),
                     FlagInfo.of("chunk-x", value),
                     FlagInfo.of("chunk-z", value2),
                     FlagInfo.of("last-chunk-x", this.lastChunkX),
                     FlagInfo.of("last-chunk-z", this.lastChunkZ),
                     FlagInfo.of("world", string)
                  }
               );
               if (this.hard) {
                  flag = false;
               }
            } else {
               this.decayBuffer();
            }
         }

         this.updateChunk(player2, wrapperPlayClientPlayerFlying);
         return flag ? CheckResult.ALLOW : CheckResult.DENY;
      } else {
         this.updateChunk(player2, wrapperPlayClientPlayerFlying);
         return CheckResult.ALLOW;
      }
   }

   private void updateChunk(Player player2, WrapperPlayClientPlayerFlying wrapperPlayClientPlayerFlying) {
      this.lastWorld = player2.getWorld().getName();
      this.lastChunkX = (int)wrapperPlayClientPlayerFlying.getLocation().getX() / 16;
      this.lastChunkZ = (int)wrapperPlayClientPlayerFlying.getLocation().getZ() / 16;
   }

}

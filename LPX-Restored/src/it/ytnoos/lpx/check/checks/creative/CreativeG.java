package it.ytnoos.lpx.check.checks.creative;

import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CreativeG extends PlayerCheck<WrapperPlayClientCreativeInventoryAction> {
   public CreativeG(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientCreativeInventoryAction wrapperPlayClientCreativeInventoryAction) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 == null) {
         return CheckResult.ALLOW;
      }

      NBTCompound nbtCompound = wrapperPlayClientCreativeInventoryAction.getItemStack().getNBT();
      if (nbtCompound == null) {
         return CheckResult.ALLOW;
      }

      NBTCompound nbtCompound2 = nbtCompound.getCompoundTagOrNull("BlockEntityTag");
      if (nbtCompound2 == null) {
         return CheckResult.ALLOW;
      }

      NBTNumber nbtNumber = nbtCompound2.getNumberTagOrNull("x");
      if (nbtNumber == null) {
         return CheckResult.ALLOW;
      }

      NBTNumber nbtNumber2 = nbtCompound2.getNumberTagOrNull("z");
      if (nbtNumber2 == null) {
         return CheckResult.ALLOW;
      }

      int value;
      int value2;
      try {
         value = nbtNumber.getAsInt();
         value2 = nbtNumber2.getAsInt();
      } catch (Exception exception) {
         return CheckResult.ALLOW;
      }

      int value3 = value / 16;
      int value4 = value2 / 16;
      World world = player2.getWorld();
      if (!world.isChunkLoaded(value3, value4)) {
         this.raiseViolation(
            wrapperPlayClientCreativeInventoryAction,
            new FlagInfo[]{
               FlagInfo.of("world", world.getName()),
               FlagInfo.of("x", value),
               FlagInfo.of("z", value2),
               FlagInfo.of("chunk-x", value3),
               FlagInfo.of("chunk-z", value4)
            }
         );
      }

      return CheckResult.ALLOW;
   }
}

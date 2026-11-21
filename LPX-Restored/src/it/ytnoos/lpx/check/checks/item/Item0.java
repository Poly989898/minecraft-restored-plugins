package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.NBTDecodeEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import it.ytnoos.lpx.iy;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.RollingIntervalCounter;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.api.protocol.ReceivedPacket;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Item0 extends PlayerCheck<NBTDecodeEvent> {
   private static final int J = 32768;
   private static final int q = 262144;
   private DisableDecodingType disableItemDecoding;
   private NbtSizeLimit nbtSizeLimit;
   private int dialogPayloadMaxBytes;
   private RollingIntervalCounter rollingIntervalCounter;

   public Item0(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.disableItemDecoding = DisableDecodingType.CD(
         this.getOptions().getCheckOptions().getConfig().getString("options.disable-item-decoding", DisableDecodingType.DEFAULT.name())
      );
      this.nbtSizeLimit = NbtSizeLimit.defaults();
      this.dialogPayloadMaxBytes = this.getOptions().getCheckOptions().getConfig().getInt("options.dialog-payload-max-bytes", 32768);
      this.rollingIntervalCounter = new RollingIntervalCounter(
         this.getOptions().getCheckOptions().getConfig().getInt("options.dialog-payload-bytes-per-second", 262144)
      );
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, NBTDecodeEvent nbtDecodeEvent) {
      if (protocolPlayer.isProcessingPayload()) {
         return CheckResult.ALLOW;
      }

      Player player2 = protocolPlayer.getPlayer().orElse(null);
      int value = nbtDecodeEvent.getNbt().getDecodedSizeBytes();
      if (this.disableItemDecoding != DisableDecodingType.NONE && this.isCustomClickAction(protocolPlayer)) {
         if (value < this.dialogPayloadMaxBytes) {
            this.rollingIntervalCounter.increment(value);
            if (!this.rollingIntervalCounter.isOverLimit()) {
               return CheckResult.ALLOW;
            }
         }

         return this.replaceNbt(nbtDecodeEvent, true);
      } else {
         boolean flag = player2 != null && player2.getGameMode() == GameMode.CREATIVE;
         boolean flag2 = value >= this.nbtSizeLimit.getLimit(protocolPlayer);
         switch (this.disableItemDecoding) {
            case NONE:
               return CheckResult.ALLOW;
            case SMART:
               if (!flag2 || flag) {
                  return CheckResult.ALLOW;
               }
               break;
            case ALL:
               if (flag) {
                  return CheckResult.ALLOW;
               }
            case CREATIVE:
         }

         boolean flag3 = !flag || this.disableItemDecoding != DisableDecodingType.CREATIVE;
         return this.replaceNbt(nbtDecodeEvent, flag3);
      }
   }

   private boolean isCustomClickAction(ProtocolPlayer protocolPlayer) {
      ReceivedPacket receivedPacket = protocolPlayer.getLastReceivedPacket();
      if (receivedPacket == null) {
         return false;
      }

      PacketTypeCommon packetTypeCommon = receivedPacket.getPacketType();
      return packetTypeCommon == Client.CUSTOM_CLICK_ACTION
         || packetTypeCommon == com.github.retrooper.packetevents.protocol.packettype.PacketType.Configuration.Client.CUSTOM_CLICK_ACTION;
   }

   private CheckResult replaceNbt(NBTDecodeEvent nbtDecodeEvent, boolean flag) {
      NBT nbt = nbtDecodeEvent.getNbt();
      ReceivedPacket receivedPacket = this.player.getLastReceivedPacket();
      Object object = null;
      if (receivedPacket != null && this.player.isLegacyClientOnHashedServer()) {
         receivedPacket.addOriginalNbt(nbt instanceof NBTList ? nbt.copy() : nbt);
      }

      if (nbt instanceof NBTCompound) {
         nbtDecodeEvent.setNbt(new NBTCompound());
      } else if (nbt instanceof NBTString) {
         if (flag || ((NBTString)nbt).getValue().length() >= 256) {
            nbtDecodeEvent.setNbt(new NBTString(""));
         }
      } else if (nbt instanceof NBTList nbtList) {
         if (flag) {
            nbtDecodeEvent.setNbt(new NBTList(nbtList.getType(), 0));
         } else {
            int value = nbtList.size() - 1;

            while (value >= 0) {
               NBT nbt2 = nbtList.getTag(value);
               if (nbt2 instanceof NBTString) {
                  if (((NBTString)nbt2).getValue().length() >= 256) {
                     nbtList.removeTag(value);
                  }
               } else {
                  nbtList.removeTag(value);
               }

               value--;
               if (object != null) {
                  break;
               }
            }

            nbtDecodeEvent.setNbt(nbtList);
         }
      }

      return CheckResult.CHANGE;
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}

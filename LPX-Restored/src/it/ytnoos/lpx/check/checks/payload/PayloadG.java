package it.ytnoos.lpx.check.checks.payload;

import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import com.github.retrooper.packetevents.event.simple.NBTDecodeEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import io.netty.buffer.Unpooled;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.check.BukkitCheckManager;
import it.ytnoos.lpx.check.WrapperUtils;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class PayloadG extends PlayerCheck<WrapperPlayClientPluginMessage> {
   private final boolean preVia;
   private static final String EXCEPTION = "exception";

   public PayloadG(ProtocolPlayer protocolPlayer, CheckOptions checkOptions, boolean flag) {
      super(protocolPlayer, checkOptions);
      this.preVia = flag;
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPluginMessage wrapperPlayClientPluginMessage) {
      if (!BukkitCheckManager.BOOK_CHANNELS.contains(wrapperPlayClientPluginMessage.getChannelName())) {
         return CheckResult.ALLOW;
      }

      if (!protocolPlayer.hasAnyCheck(ItemStackDecodeEvent.class, this.preVia)) {
         return CheckResult.ALLOW;
      }

      PacketWrapper packetWrapper = null;
      PacketWrapper packetWrapper2 = null;

      try {
         packetWrapper = WrapperUtils.createWrapper(protocolPlayer, Unpooled.copiedBuffer(wrapperPlayClientPluginMessage.getData()), this.preVia);
         ItemStack itemStack = packetWrapper.readItemStack();
         NBTCompound nbtCompound = itemStack.getNBT();
         protocolPlayer.setProcessingPayload(true);
         CheckResult checkResult = CheckResult.ALLOW;
         if (nbtCompound != null) {
            checkResult = protocolPlayer.handleChecks(NBTDecodeEvent.class, new NBTDecodeEvent(wrapperPlayClientPluginMessage, null, nbtCompound), this.preVia).result();
            if (checkResult == CheckResult.DENY) {
               return checkResult;
            }
         }

         checkResult = checkResult.merge(protocolPlayer.handleChecks(ItemStackDecodeEvent.class, new ItemStackDecodeEvent(wrapperPlayClientPluginMessage, null, itemStack), this.preVia).result());
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }

         if (checkResult == CheckResult.CHANGE) {
            packetWrapper2 = WrapperUtils.createWrapper(protocolPlayer, Unpooled.buffer(), this.preVia);
            packetWrapper2.writeItemStack(itemStack);
            ByteBufHelper.capacity(packetWrapper2.buffer, ByteBufHelper.writerIndex(packetWrapper2.buffer));
            wrapperPlayClientPluginMessage.setData(ByteBufHelper.array(packetWrapper2.buffer));
         }

         return checkResult;
      } catch (Throwable throwable) {
         this.raiseViolation(wrapperPlayClientPluginMessage, new FlagInfo[]{FlagInfo.of(EXCEPTION, throwable.getMessage())});
         return CheckResult.DENY;
      } finally {
         protocolPlayer.setProcessingPayload(false);
         WrapperUtils.releaseWrapper(packetWrapper);
         WrapperUtils.releaseWrapper(packetWrapper2);
      }
   }

   @Override
   public boolean isPreVia() {
      return this.preVia;
   }
}

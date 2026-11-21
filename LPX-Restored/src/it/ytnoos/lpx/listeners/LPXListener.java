package it.ytnoos.lpx.listeners;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import com.github.retrooper.packetevents.event.simple.NBTDecodeEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.ReadSlotsEvent;
import com.github.retrooper.packetevents.exception.PacketLimitException;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCraftRecipeRequest;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCustomClickAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPickItemFromBlock;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPickItemFromEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSelectBundleItem;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSetDisplayedRecipe;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSetTestBlock;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTestInstanceBlockAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import io.netty.buffer.ByteBuf;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.WrappedRuntimeException;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.CheckResultInfo;
import it.ytnoos.lpx.api.check.abstraction.Check;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.api.protocol.ReceivedPacket;
import it.ytnoos.lpx.common.utils.NettyUtils;
import it.ytnoos.lpx.common.utils.TimeUtils;
import it.ytnoos.lpx.logger.PacketEventsExceptionHandler;
import it.ytnoos.lpx.player.BukkitProtocolPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class LPXListener {
   protected static final Map<PacketTypeCommon, Function<PacketReceiveEvent, PacketWrapper<?>>> WRAPPER_FACTORIES = new HashMap<>();
   private static final long RESYNC_INTERVAL_MS = 1000L;
   private final LPXBukkit lpxBukkit;
   private final Map<String, Long> WRAPPER_FACTORIES2 = new ConcurrentHashMap<>();

   public LPXListener(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   void handleReceive(PacketReceiveEvent packetReceiveEvent, boolean flag) {
      User user = packetReceiveEvent.getUser();
      ProtocolPlayer protocolPlayer = this.lpxBukkit.getRegistry().getPlayer(user).orElse(null);
      if (protocolPlayer != null) {
         try {
            this.processReceivedPacket(packetReceiveEvent, protocolPlayer, flag);
         } catch (RuntimeException runtimeException) {
            protocolPlayer.setLastReceivedPacket(null);
            String string = packetReceiveEvent.getPacketType().getName();
            String string2 = protocolPlayer.getUsername();
            this.lpxBukkit.getAlertManager().getLogWriter().execute(() -> {
               if (PacketEventsExceptionHandler.shouldLog(this.WRAPPER_FACTORIES2, string2, System.currentTimeMillis())) {
                  this.lpxBukkit.getAlertManager().getLogWriter().writeError(() -> "Error while handling " + string + " sent by " + string2, runtimeException);
               }
            });
         }
      }
   }

   private void processReceivedPacket(PacketReceiveEvent packetReceiveEvent, ProtocolPlayer protocolPlayer, boolean flag) {
      User user = packetReceiveEvent.getUser();
      if (flag && packetReceiveEvent.getPacketType() == Client.KEEP_ALIVE) {
         protocolPlayer.setPlayerPing(System.currentTimeMillis() - protocolPlayer.getLastKeepAliveSent());
      }

      if (protocolPlayer.getPacketCount() < 10L) {
         protocolPlayer.incrementPacketCount();
      }

      if (flag || protocolPlayer.getLastReceivedPacket() == null) {
         protocolPlayer.setLastReceivedPacket(
            new ReceivedPacket(
               user.getName(),
               user.getClientVersion(),
               this.lpxBukkit.getPacketTickMonitor().getCurrentTick(),
               packetReceiveEvent.getPacketType(),
               NettyUtils.getSize(packetReceiveEvent.getByteBuf()),
               null
            )
         );
      }

      if (this.handleChecks(packetReceiveEvent, protocolPlayer, ByteBuf.class, packetReceiveEvent.getByteBuf(), flag) == CheckResult.DENY) {
         protocolPlayer.setLastReceivedPacket(null);
      } else if (this.handleChecks(packetReceiveEvent, protocolPlayer, PacketPlayReceiveEvent.class, packetReceiveEvent, flag) == CheckResult.DENY) {
         protocolPlayer.setLastReceivedPacket(null);
      } else {
         Object object = packetReceiveEvent.getPacketType();
         if (object == Client.PLAYER_POSITION || object == Client.PLAYER_POSITION_AND_ROTATION || object == Client.PLAYER_ROTATION) {
            object = Client.PLAYER_FLYING;
         }

         Function function = WRAPPER_FACTORIES.get(object);
         if (function != null) {
            PacketWrapper packetWrapper;
            try {
               packetWrapper = (PacketWrapper)function.apply(packetReceiveEvent);
            } catch (RuntimeException runtimeException) {
               throw new WrappedRuntimeException(runtimeException);
            }

            if (protocolPlayer.getLastReceivedPacket().getPacket() == null) {
               protocolPlayer.getLastReceivedPacket().setPacket(packetWrapper);
            }

            if (packetReceiveEvent.isCancelled()) {
               protocolPlayer.setLastReceivedPacket(null);
            } else {
               if (this.handleChecks(packetReceiveEvent, protocolPlayer, object, packetWrapper, flag) == CheckResult.DENY) {
                  protocolPlayer.setLastReceivedPacket(null);
               }
            }
         }
      }
   }

   CheckResult handleDecode(PacketEvent packetEvent, boolean flag) {
      ProtocolPacketEvent protocolPacketEvent;
      if (packetEvent instanceof ItemStackDecodeEvent) {
         protocolPacketEvent = ((ItemStackDecodeEvent)packetEvent).getEvent();
      } else if (packetEvent instanceof NBTDecodeEvent) {
         protocolPacketEvent = ((NBTDecodeEvent)packetEvent).getEvent();
      } else {
         if (!(packetEvent instanceof ReadSlotsEvent)) {
            return CheckResult.ALLOW;
         }

         protocolPacketEvent = ((ReadSlotsEvent)packetEvent).getEvent();
      }

      if (!(protocolPacketEvent instanceof PacketReceiveEvent packetReceiveEvent)) {
         return CheckResult.ALLOW;
      } else {
         if (packetReceiveEvent.isCancelled()) {
            return CheckResult.ALLOW;
         }

         if (!(packetReceiveEvent instanceof PacketReceiveEvent)) {
            return CheckResult.ALLOW;
         }

         User user = packetReceiveEvent.getUser();
         if (user != null && user.getUUID() != null) {
            ProtocolPlayer protocolPlayer = this.lpxBukkit.getRegistry().getPlayer(user).orElse(null);
            if (protocolPlayer == null) {
               return CheckResult.ALLOW;
            } else if (packetEvent instanceof ItemStackDecodeEvent) {
               return this.handleChecks(packetReceiveEvent, protocolPlayer, ItemStackDecodeEvent.class, packetEvent, flag);
            } else {
               return packetEvent instanceof NBTDecodeEvent
                  ? this.handleChecks(packetReceiveEvent, protocolPlayer, NBTDecodeEvent.class, packetEvent, flag)
                  : this.handleChecks(packetReceiveEvent, protocolPlayer, ReadSlotsEvent.class, packetEvent, flag);
            }
         } else {
            return CheckResult.ALLOW;
         }
      }
   }

   public CheckResult handleChecks(PacketReceiveEvent packetReceiveEvent, ProtocolPlayer protocolPlayer, Object object, Object object2, boolean flag) {
      CheckResultInfo checkResultInfo = protocolPlayer.handleChecks(object, object2, flag);
      CheckResult checkResult = checkResultInfo.result();
      boolean flag2 = false;
      if (checkResult == CheckResult.DENY) {
         packetReceiveEvent.setCancelled(true);
         PacketTypeCommon packetTypeCommon = packetReceiveEvent.getPacketType();
         if (packetTypeCommon == Client.CREATIVE_INVENTORY_ACTION || packetTypeCommon == Client.CLICK_WINDOW) {
            this.resyncInventory(protocolPlayer);
         }

         if (this.lpxBukkit.getConfigManager().getOptionsConfig().isLogCancelledPackets()) {
            Check check = checkResultInfo.lastCheck();
            String string = check != null ? "[" + check.getType().getId() + "] " : "";
            this.lpxBukkit
               .getAlertManager()
               .getLogWriter()
               .writeString(() -> string + "Cancelling " + packetReceiveEvent.getPacketType().getName() + " sent by " + protocolPlayer.getUsername());
         }

         if (!flag2) {
            return checkResult;
         }
      }

      if (checkResult == CheckResult.CHANGE) {
         packetReceiveEvent.markForReEncode(true);
      }

      return checkResult;
   }

   private void handleDecodeFailure(PacketReceiveEvent packetReceiveEvent, ProtocolPlayer protocolPlayer, RuntimeException runtimeException, boolean flag) {
      packetReceiveEvent.setCancelled(true);
      protocolPlayer.setLastReceivedPacket(null);
      if (!protocolPlayer.isKicked()) {
         PacketLimitException packetLimitException = findPacketLimitException(runtimeException);
         if (packetLimitException != null) {
            this.handleChecks(packetReceiveEvent, protocolPlayer, PacketLimitException.class, packetLimitException, flag);
         } else {
            String string = packetReceiveEvent.getPacketType().getName();
            String string2 = protocolPlayer.getUsername();
            boolean flag2 = this.lpxBukkit.getConfigManager().getOptionsConfig().isKickOnException();
            if (flag2) {
               protocolPlayer.kick(this.lpxBukkit.getConfigManager().getMessagesConfig().getKick());
            }

            this.lpxBukkit.getAlertManager().getLogWriter().execute(() -> {
               if (flag2 || PacketEventsExceptionHandler.shouldLog(this.WRAPPER_FACTORIES2, string2, System.currentTimeMillis())) {
                  Logger logger = this.lpxBukkit.getLogger();
                  String string3 = flag2 ? ", kicking" : ", cancelled";
                  String string4 = runtimeException.getMessage();
                  String string5 = string3;
                  String string6 = string2;
                  String string7 = string;
                  logger.warning("Invalid " + string7 + " sent by " + string6 + string5 + ": " + string4 + " (full stacktrace saved in LPX logs)");
                  this.lpxBukkit.getAlertManager().getLogWriter().writeError(() -> "Invalid " + string + " sent by " + string2, runtimeException);
               }
            });
         }
      }
   }

   @Nullable
   private static PacketLimitException findPacketLimitException(Throwable throwable) {
      for (Throwable throwable2 = throwable; throwable2 != null; throwable2 = throwable2.getCause()) {
         if (throwable2 instanceof PacketLimitException packetLimitException) {
            return packetLimitException;
         }
      }

      return null;
   }

   private void resyncInventory(ProtocolPlayer protocolPlayer) {
      if (protocolPlayer instanceof BukkitProtocolPlayer bukkitProtocolPlayer) {
         Player player = protocolPlayer.getPlayer().orElse(null);
         if (player != null) {
            if (bukkitProtocolPlayer.tryBeginInventoryResync()) {
               long longValue = 1000L - TimeUtils.difference(bukkitProtocolPlayer.getLastInventoryResync());
               Runnable runnable = () -> bukkitProtocolPlayer.finishInventoryResync(false);
               if (longValue <= 0L) {
                  FoliaScheduler.getEntityScheduler().run(player, this.lpxBukkit.getPlugin(), object -> {
                     player.updateInventory();
                     bukkitProtocolPlayer.finishInventoryResync(true);
                  }, runnable);
               } else {
                  FoliaScheduler.getEntityScheduler().runDelayed(player, this.lpxBukkit.getPlugin(), object2 -> {
                     player.updateInventory();
                     bukkitProtocolPlayer.finishInventoryResync(true);
                  }, runnable, (longValue + 49L) / 50L);
               }
            }
         }
      }
   }

   static {
      WRAPPER_FACTORIES.put(Client.CLICK_WINDOW, WrapperPlayClientClickWindow::new);
      WRAPPER_FACTORIES.put(Client.CREATIVE_INVENTORY_ACTION, WrapperPlayClientCreativeInventoryAction::new);
      WRAPPER_FACTORIES.put(Client.PLAYER_BLOCK_PLACEMENT, WrapperPlayClientPlayerBlockPlacement::new);
      WRAPPER_FACTORIES.put(Client.UPDATE_SIGN, WrapperPlayClientUpdateSign::new);
      WRAPPER_FACTORIES.put(Client.PLAYER_FLYING, WrapperPlayClientPlayerFlying::new);
      WRAPPER_FACTORIES.put(Client.PLAYER_POSITION, WrapperPlayClientPlayerFlying::new);
      WRAPPER_FACTORIES.put(Client.PLAYER_POSITION_AND_ROTATION, WrapperPlayClientPlayerFlying::new);
      WRAPPER_FACTORIES.put(Client.PLAYER_ROTATION, WrapperPlayClientPlayerFlying::new);
      WRAPPER_FACTORIES.put(Client.PLUGIN_MESSAGE, WrapperPlayClientPluginMessage::new);
      WRAPPER_FACTORIES.put(Client.TAB_COMPLETE, WrapperPlayClientTabComplete::new);
      WRAPPER_FACTORIES.put(Client.EDIT_BOOK, WrapperPlayClientEditBook::new);
      WRAPPER_FACTORIES.put(Client.PLAYER_DIGGING, WrapperPlayClientPlayerDigging::new);
      WRAPPER_FACTORIES.put(Client.CRAFT_RECIPE_REQUEST, WrapperPlayClientCraftRecipeRequest::new);
      WRAPPER_FACTORIES.put(Client.SET_DISPLAYED_RECIPE, WrapperPlayClientSetDisplayedRecipe::new);
      WRAPPER_FACTORIES.put(Client.SELECT_BUNDLE_ITEM, WrapperPlayClientSelectBundleItem::new);
      WRAPPER_FACTORIES.put(Client.PICK_ITEM_FROM_BLOCK, WrapperPlayClientPickItemFromBlock::new);
      WRAPPER_FACTORIES.put(Client.PICK_ITEM_FROM_ENTITY, WrapperPlayClientPickItemFromEntity::new);
      WRAPPER_FACTORIES.put(Client.TEST_INSTANCE_BLOCK_ACTION, WrapperPlayClientTestInstanceBlockAction::new);
      WRAPPER_FACTORIES.put(Client.SET_TEST_BLOCK, WrapperPlayClientSetTestBlock::new);
      WRAPPER_FACTORIES.put(Client.CUSTOM_CLICK_ACTION, WrapperPlayClientCustomClickAction::new);
   }
}

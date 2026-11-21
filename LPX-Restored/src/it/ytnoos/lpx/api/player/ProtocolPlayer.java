package it.ytnoos.lpx.api.player;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import it.ytnoos.lpx.api.LPX;
import it.ytnoos.lpx.api.check.CheckResultInfo;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.abstraction.Check;
import it.ytnoos.lpx.api.check.enums.CheckType;
import it.ytnoos.lpx.api.protocol.CheckContainer;
import it.ytnoos.lpx.api.protocol.ReceivedPacket;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;

public interface ProtocolPlayer {
   Optional<Player> getPlayer();

   User getUser();

   String getUsername();

   UUID getUniqueId();

   PrinterData getPrinterData();

   boolean isBedrock();

   void setBedrock(boolean flag2);

   boolean isProcessingPayload();

   void setProcessingPayload(boolean flag2);

   Map<Object, CheckContainer<?>> getChecks();

   CheckContainer<?> getChecks(Object object);

   boolean hasAnyCheck(Object object, boolean flag2);

   CheckResultInfo handleChecks(Object object, Object object2);

   CheckResultInfo handleChecks(Object object, Object object2, boolean flag2);

   <T> Check<T> getCheck(Object object, CheckType checkType, LPX lpx2);

   ReceivedPacket getLastReceivedPacket();

   long getLastAlert();

   long getLastPunish();

   long getLastTeleport();

   void updateLastAlert();

   long getLastPortalTeleport();

   long getLastShears();

   long getLastContainerInteract();

   void setLastReceivedPacket(ReceivedPacket receivedPacket);

   void updateLastPunish();

   void updateLastTeleport();

   void updateLastPortalTeleport();

   void updateLastShears();

   void updateLastContainerInteract();

   long incrementPacketCount();

   long getPacketCount();

   long getLastKeepAliveSent();

   void setLastKeepAliveSent(long longValue);

   double getPlayerPing();

   void setPlayerPing(double number);

   void flag(Check<?> check, Object object, FlagInfo... flagInfos);

   void punish(Check<?> check);

   void kick(String string);

   boolean isKicked();

   boolean ignoreChecks();

   boolean hasExemptPermission();

   default boolean usesComponents() {
      return this.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5);
   }

   default boolean usesHashedStacks() {
      return this.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_5);
   }

   default boolean serverUsesHashedStacks() {
      return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5);
   }

   default boolean isLegacyClientOnLegacyServer() {
      return !this.usesHashedStacks() && !this.serverUsesHashedStacks();
   }

   default boolean isLegacyClientOnHashedServer() {
      return !this.usesHashedStacks() && this.serverUsesHashedStacks();
   }

   default boolean isHashedClientOnLegacyServer() {
      return this.usesHashedStacks() && !this.serverUsesHashedStacks();
   }

   default boolean isHashedClientOnHashedServer() {
      return this.usesHashedStacks() && this.serverUsesHashedStacks();
   }

   default double itemFloodMultiplier() {
      if (this.isLegacyClientOnHashedServer()) {
         return 1.5;
      } else {
         return this.isHashedClientOnHashedServer() ? 0.5 : 1.0;
      }
   }

   LPX lpx();

}

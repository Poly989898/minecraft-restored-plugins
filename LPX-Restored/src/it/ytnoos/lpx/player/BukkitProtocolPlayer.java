package it.ytnoos.lpx.player;

import com.github.retrooper.packetevents.protocol.player.User;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.LPX;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.CheckResultInfo;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.abstraction.Check;
import it.ytnoos.lpx.api.check.abstraction.CheckManager;
import it.ytnoos.lpx.api.check.enums.CheckType;
import it.ytnoos.lpx.api.player.PrinterData;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.api.protocol.CheckContainer;
import it.ytnoos.lpx.api.protocol.ReceivedPacket;
import it.ytnoos.lpx.common.utils.TimeUtils;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class BukkitProtocolPlayer implements ProtocolPlayer {
   private final LPXBukkit lpxBukkit;
   private final Map<Object, CheckContainer<?>> checks;
   private final User user;
   private final PrinterData printerData;
   @Nullable
   private Player player;
   private boolean hasBypassPermission = false;
   private boolean kicked = false;
   private boolean bedrock = false;
   private boolean processingPayload = false;
   private ReceivedPacket receivedPacket;
   private long lastAlert;
   private long lastPunish;
   private long lastTeleport;
   private long lastPortalTeleport;
   private long lastShears;
   private long lastContainerInteract;
   private long packetCount = 0L;
   private long lastKeepAliveSent = 0L;
   private double playerPing = 0.0;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private volatile long lastInventoryResync;
   private static final String BYPASS_PERMISSION = "lpx.bypass";

   public BukkitProtocolPlayer(LPXBukkit lpxBukkit2, User user2) {
      this.lpxBukkit = lpxBukkit2;
      this.user = user2;
      this.checks = lpxBukkit2.getCheckManager().createPlayerChecks(this);
      this.printerData = new PrinterDataImpl(lpxBukkit2, this);
   }

   @Override
   public Optional<Player> getPlayer() {
      if (this.player != null) {
         return Optional.of(this.player);
      }

      UUID uuid = this.user.getUUID();
      if (uuid == null) {
         return Optional.empty();
      }

      Player player2 = Bukkit.getPlayer(uuid);
      if (player2 == null) {
         return Optional.empty();
      }

      this.player = player2;
      this.hasBypassPermission = player2.hasPermission(BYPASS_PERMISSION);
      return Optional.of(player2);
   }

   @Override
   public User getUser() {
      return this.user;
   }

   @Override
   public String getUsername() {
      return this.user.getName();
   }

   @Override
   public UUID getUniqueId() {
      return this.user.getUUID();
   }

   @Override
   public PrinterData getPrinterData() {
      return this.printerData;
   }

   @Override
   public boolean isBedrock() {
      return this.bedrock;
   }

   @Override
   public void setBedrock(boolean flag2) {
      this.bedrock = flag2;
   }

   @Override
   public boolean isProcessingPayload() {
      return this.processingPayload;
   }

   @Override
   public void setProcessingPayload(boolean flag2) {
      this.processingPayload = flag2;
   }

   public LPXBukkit getLpxBukkit() {
      return this.lpxBukkit;
   }

   @Override
   public Map<Object, CheckContainer<?>> getChecks() {
      return this.checks;
   }

   @Override
   public CheckContainer<?> getChecks(Object object) {
      CheckContainer checkContainer = this.checks.get(object);
      return checkContainer != null ? checkContainer : CheckManager.EMPTY_CONTAINER;
   }

   @Override
   public boolean hasAnyCheck(Object object, boolean flag2) {
      for (Check check : this.getChecks(object)) {
         if (check.isPreVia() == flag2) {
            return true;
         }
      }

      return false;
   }

   @Override
   public CheckResultInfo handleChecks(Object object, Object object2) {
      return this.handleChecks(object, object2, false);
   }

   @Override
   public CheckResultInfo handleChecks(Object object, Object object2, boolean flag2) {
      CheckResultInfo checkResultInfo = new CheckResultInfo(CheckResult.ALLOW);

      for (Check check : this.getChecks(object)) {
         if (check.isPreVia() == flag2) {
            CheckResult checkResult = check.handle(object2);
            CheckResult checkResult2 = checkResult.merge(checkResultInfo.result());
            if (checkResult2 != checkResultInfo.result()) {
               checkResultInfo = new CheckResultInfo(checkResult2, check);
            }

            if (checkResult2 == CheckResult.DENY) {
               return checkResultInfo;
            }
         }
      }

      return checkResultInfo;
   }

   @Override
   public <T> Check<T> getCheck(Object object, CheckType checkType, LPX lpx2) {
      for (Check check : this.checks.get(object)) {
         if (check.getType() == checkType) {
            return check;
         }
      }

      return null;
   }

   @Override
   public ReceivedPacket getLastReceivedPacket() {
      return this.receivedPacket;
   }

   @Override
   public long getLastAlert() {
      return this.lastAlert;
   }

   @Override
   public long getLastPunish() {
      return this.lastPunish;
   }

   @Override
   public long getLastTeleport() {
      return this.lastTeleport;
   }

   @Override
   public long getLastPortalTeleport() {
      return this.lastPortalTeleport;
   }

   @Override
   public long getLastShears() {
      return this.lastShears;
   }

   @Override
   public long getLastContainerInteract() {
      return this.lastContainerInteract;
   }

   @Override
   public void setLastReceivedPacket(ReceivedPacket receivedPacket2) {
      this.receivedPacket = receivedPacket2;
   }

   @Override
   public void updateLastPunish() {
      this.lastPunish = TimeUtils.current();
   }

   public boolean tryBeginInventoryResync() {
      return this.atomicBoolean.compareAndSet(false, true);
   }

   public long getLastInventoryResync() {
      return this.lastInventoryResync;
   }

   public void finishInventoryResync(boolean flag2) {
      if (flag2) {
         this.lastInventoryResync = TimeUtils.current();
      }

      this.atomicBoolean.set(false);
   }

   @Override
   public void updateLastAlert() {
      this.lastAlert = TimeUtils.current();
   }

   @Override
   public void updateLastTeleport() {
      this.lastTeleport = TimeUtils.current();
   }

   @Override
   public void updateLastPortalTeleport() {
      this.lastPortalTeleport = TimeUtils.current();
   }

   @Override
   public void updateLastShears() {
      this.lastShears = TimeUtils.current();
   }

   @Override
   public void updateLastContainerInteract() {
      this.lastContainerInteract = TimeUtils.current();
   }

   @Override
   public long incrementPacketCount() {
      return ++this.packetCount;
   }

   @Override
   public long getPacketCount() {
      return this.packetCount;
   }

   @Override
   public long getLastKeepAliveSent() {
      return this.lastKeepAliveSent;
   }

   @Override
   public void setLastKeepAliveSent(long longValue) {
      this.lastKeepAliveSent = longValue;
   }

   @Override
   public double getPlayerPing() {
      return this.playerPing;
   }

   @Override
   public void setPlayerPing(double number) {
      this.playerPing = number;
   }

   @Override
   public void flag(Check<?> check, Object object, FlagInfo... flagInfos) {
      this.lpxBukkit.getAlertManager().flag(this, check, flagInfos);
   }

   @Override
   public void punish(Check<?> check) {
      this.lpxBukkit.getAlertManager().punish(this, check);
   }

   @Override
   public void kick(String string) {
      if (!this.isKicked()) {
         this.kicked = true;
         this.lpxBukkit.forceKick(this, string);
      }
   }

   @Override
   public boolean isKicked() {
      return this.kicked || !this.getPlayer().isPresent();
   }

   @Override
   public boolean ignoreChecks() {
      return this.bedrock || this.lpxBukkit.getConfigManager().getOptionsConfig().isBypassPermission() && this.hasExemptPermission();
   }

   @Override
   public boolean hasExemptPermission() {
      return this.hasBypassPermission;
   }

   @Override
   public LPX lpx() {
      return this.lpxBukkit;
   }

}

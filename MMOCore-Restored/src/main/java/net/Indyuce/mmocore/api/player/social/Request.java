package net.Indyuce.mmocore.api.player.social;

import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.Validate;

public abstract class Request {
   private final UUID uuid = UUID.randomUUID();
   private final long date = System.currentTimeMillis();
   private final PlayerData creator;
   private final PlayerData target;
   private static final long TIME_OUT = 120000L;

   public Request(PlayerData creator, PlayerData target) {
      this.creator = creator;
      this.target = target;
   }

   public PlayerData getCreator() {
      return this.creator;
   }

   public PlayerData getTarget() {
      return this.target;
   }

   public UUID getUniqueId() {
      return this.uuid;
   }

   public boolean isTimedOut() {
      return this.date + 120000L < System.currentTimeMillis();
   }

   public void accept() {
      Validate.isTrue(this.target.isOnline(), "Target must be online");
      this.whenAccepted();
      MMOCore.plugin.requestManager.unregisterRequest(this.getUniqueId());
   }

   public abstract void whenAccepted();

   public void deny() {
      Validate.isTrue(this.target.isOnline(), "Target must be online");
      this.whenDenied();
      MMOCore.plugin.requestManager.unregisterRequest(this.getUniqueId());
   }

   public abstract void whenDenied();
}

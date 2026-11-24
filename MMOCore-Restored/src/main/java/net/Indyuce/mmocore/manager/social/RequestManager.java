package net.Indyuce.mmocore.manager.social;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.social.Request;
import net.Indyuce.mmocore.manager.MMOCoreManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequestManager implements MMOCoreManager {
   private final Map<UUID, Request> requests = new HashMap<>();
   private boolean ENABLED;

   @Override
   public void initialize(boolean clearBefore) {
      if (!this.ENABLED) {
         Bukkit.getScheduler().runTaskTimer(MMOCore.plugin, this::flushRequests, 1200L, 6000L);
         this.ENABLED = true;
      }
   }

   @NotNull
   public Request getRequest(UUID uuid) {
      return Objects.requireNonNull(this.requests.get(uuid), "Could not find request with UUID '" + uuid.toString() + "'");
   }

   public void registerRequest(Request request) {
      this.requests.put(request.getUniqueId(), request);
   }

   @Nullable
   public <T extends Request> T findRequest(PlayerData target, Class<T> givenClass) {
      for (Request req : this.requests.values()) {
         if (!req.isTimedOut() && req.getTarget().equals(target) && req.getClass().equals(givenClass)) {
            return (T)req;
         }
      }

      return null;
   }

   @Nullable
   public Request unregisterRequest(UUID uuid) {
      return this.requests.remove(uuid);
   }

   private void flushRequests() {
      Iterator<Request> iterator = this.requests.values().iterator();

      while (iterator.hasNext()) {
         Request next = iterator.next();
         if (next.isTimedOut()) {
            iterator.remove();
         }
      }
   }
}

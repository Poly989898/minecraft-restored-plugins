package io.lumine.mythic.lib.data;

import fr.phoenixdevt.profiles.ProfileDataModule;
import fr.phoenixdevt.profiles.ProfileProvider;
import fr.phoenixdevt.profiles.placeholder.PlaceholderProcessor;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.SynchronizedDataLoadEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.comp.profile.LegacyProfiles;
import io.lumine.mythic.lib.comp.profile.ProfileMode;
import io.lumine.mythic.lib.util.Autosaveable;
import io.lumine.mythic.lib.util.Closeable;
import io.lumine.mythic.lib.util.Tasks;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SynchronizedDataManager<H extends SynchronizedDataHolder, O extends OfflineDataHolder> implements Closeable {
   private final JavaPlugin owning;
   private final Map<UUID, H> activeData = Collections.synchronizedMap(new HashMap<>());
   private final boolean profilePlugin;
   @NotNull
   private SynchronizedDataHandler<H, O> dataHandler;
   private static final Listener FICTIVE_LISTENER = new Listener() {};

   public SynchronizedDataManager(@NotNull JavaPlugin owning, @NotNull SynchronizedDataHandler<H, O> dataHandler) {
      this(owning, dataHandler, false);
   }

   public SynchronizedDataManager(@NotNull JavaPlugin owning, @NotNull SynchronizedDataHandler<H, O> dataHandler, boolean profilePlugin) {
      this.owning = Objects.requireNonNull(owning, "Plugin cannot be null");
      this.dataHandler = Objects.requireNonNull(dataHandler, "Data handler cannot be null");
      this.profilePlugin = profilePlugin;
   }

   public void setDataHandler(@NotNull SynchronizedDataHandler<H, O> dataHandler) {
      this.dataHandler = Objects.requireNonNull(dataHandler, "Data handler cannot be null");
      dataHandler.setup();
   }

   @NotNull
   public SynchronizedDataHandler<H, O> getDataHandler() {
      return this.dataHandler;
   }

   @NotNull
   public JavaPlugin getOwningPlugin() {
      return this.owning;
   }

   @NotNull
   public H get(OfflinePlayer player) {
      return this.get(player.getUniqueId());
   }

   @NotNull
   public H get(UUID uuid) {
      return Objects.requireNonNull(this.activeData.get(uuid), "Player data is not loaded");
   }

   @Nullable
   public H getOrNull(OfflinePlayer player) {
      return this.getOrNull(player.getUniqueId());
   }

   @Nullable
   public H getOrNull(UUID uuid) {
      return this.activeData.get(uuid);
   }

   @NotNull
   public O getOffline(UUID uuid) {
      return (O)(this.isLoaded(uuid) ? this.activeData.get(uuid) : this.dataHandler.getOffline(uuid));
   }

   public void autosave() {
      for (H holder : this.getLoaded()) {
         if (holder.isSynchronized()) {
            if (holder instanceof Autosaveable) {
               ((Autosaveable)holder).prepareSaving(true);
            }

            Tasks.runAsync(this.owning, () -> this.getDataHandler().saveData(holder, true));
         }
      }
   }

   public void initialize(@NotNull EventPriority joinEventPriority, @NotNull EventPriority quitEventPriority) {
      Bukkit.getOnlinePlayers().forEach(this::setup);
      if (this.owning.getConfig().getBoolean("auto-save.enabled")) {
         new AutoSaveRunnable(this);
      }

      UtilityMethods.registerEvent(PlayerJoinEvent.class, FICTIVE_LISTENER, joinEventPriority, event -> this.setup(event.getPlayer()), this.owning, false);
      if (this.profilePlugin || MythicLib.plugin.getProfileMode() != ProfileMode.LEGACY) {
         UtilityMethods.registerEvent(
            PlayerQuitEvent.class, FICTIVE_LISTENER, quitEventPriority, event -> this.unregister(event.getPlayer()), this.owning, false
         );
      }

      if (!this.profilePlugin && MythicLib.plugin.hasProfiles()) {
         this.owning.getLogger().log(Level.INFO, "Hooked onto ProfileAPI");
         ProfileProvider profilePlugin = (ProfileProvider)Bukkit.getServicesManager().getRegistration(ProfileProvider.class).getProvider();
         ProfileDataModule module = (ProfileDataModule)this.newProfileDataModule();
         if (module instanceof PlaceholderProcessor) {
            profilePlugin.registerPlaceholders((PlaceholderProcessor)module);
         }

         if (MythicLib.plugin.getProfileMode() == ProfileMode.LEGACY) {
            LegacyProfiles.hook(profilePlugin, module, this, FICTIVE_LISTENER, joinEventPriority, quitEventPriority);
         }
      }
   }

   @Override
   public void close() {
      for (H holder : this.getLoaded()) {
         if (holder.isSynchronized()) {
            if (holder instanceof Closeable) {
               ((Closeable)holder).close();
            }

            this.getDataHandler().saveData(holder, false);
         }
      }

      this.owning.getLogger().log(Level.INFO, "Waiting for other threads, please wait...");
      Tasks.executePendingSafe(this.owning);
      Tasks.waitSafe(this.owning);
      this.dataHandler.close();
   }

   @NotNull
   public CompletableFuture<Void> loadData(@NotNull H playerData) {
      CompletableFuture<Void> future = new CompletableFuture<>();
      AtomicBoolean success = new AtomicBoolean();
      Tasks.runAsync(this.owning, () -> success.set(this.dataHandler.loadData(playerData))).thenAccept(Tasks.sync(this.owning, ignored -> {
         if (success.get()) {
            if (!playerData.getMMOPlayerData().isLookup() && !playerData.getMMOPlayerData().isOnline()) {
               this.saveData(playerData);
            } else {
               future.complete(null);
            }
         }
      }));
      return future;
   }

   public CompletableFuture<Void> saveData(@NotNull H playerData) {
      return Tasks.runSafeAsync(this.owning, () -> this.dataHandler.saveData(playerData, false));
   }

   public H setup(@NotNull Player player) {
      H playerData = this.activeData.computeIfAbsent(player.getUniqueId(), uuid -> this.newPlayerData(MMOPlayerData.get(player.getUniqueId())));
      if (this.requiresSynchronizationOnLogin(playerData)) {
         this.loadData(playerData).thenAccept(Tasks.sync(this.owning, v -> {
            playerData.markAsSynchronized();
            Bukkit.getPluginManager().callEvent(new SynchronizedDataLoadEvent(this, playerData));
         }));
      }

      return playerData;
   }

   private boolean requiresSynchronizationOnLogin(@NotNull H holder) {
      if (holder.isSynchronized()) {
         return false;
      } else if (this.profilePlugin) {
         return true;
      } else if (MythicLib.plugin.getProfileMode() == null) {
         return true;
      } else if (MythicLib.plugin.getProfileMode() == ProfileMode.LEGACY) {
         return false;
      } else if (MythicLib.plugin.getProfileMode() == ProfileMode.PROXY) {
         return holder.getMMOPlayerData().hasProfile();
      } else {
         throw new RuntimeException("Unhandled profile mode");
      }
   }

   @NotNull
   public CompletableFuture<Void> unregister(@NotNull Player player) {
      H playerData = this.activeData.remove(player.getUniqueId());
      Validate.notNull(playerData, "Could not find player data of player '" + player.getUniqueId() + "'");
      if (playerData instanceof Closeable) {
         ((Closeable)playerData).close();
      }

      return playerData.isSynchronized() ? this.saveData(playerData) : CompletableFuture.completedFuture(null);
   }

   public abstract H newPlayerData(@NotNull MMOPlayerData var1);

   public abstract Object newProfileDataModule();

   public boolean isLoaded(UUID uuid) {
      return this.activeData.containsKey(uuid);
   }

   public Collection<H> getLoaded() {
      return this.activeData.values();
   }

   @Deprecated
   public void saveAll(boolean autosave) {
      if (autosave) {
         this.autosave();
      } else {
         this.close();
      }
   }

   @Deprecated
   public void unregisterSafely(H playerData) {
      this.unregister(playerData.getPlayer());
   }
}

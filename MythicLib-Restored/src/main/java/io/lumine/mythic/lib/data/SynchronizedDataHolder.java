package io.lumine.mythic.lib.data;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.util.MMOPlugin;
import java.util.UUID;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SynchronizedDataHolder implements OfflineDataHolder {
   private final MMOPlayerData playerData;
   @Nullable
   private final MMOPlugin mmoPlugin;
   private boolean sync;

   @Deprecated
   public SynchronizedDataHolder(@NotNull MMOPlayerData playerData) {
      this(null, playerData);
   }

   public SynchronizedDataHolder(@NotNull MMOPlugin mmoPlugin, @NotNull MMOPlayerData playerData) {
      this.mmoPlugin = mmoPlugin;
      this.playerData = playerData;
   }

   @NotNull
   public MMOPlayerData getMMOPlayerData() {
      return this.playerData;
   }

   @NotNull
   @Override
   public UUID getUniqueId() {
      return this.playerData.getUniqueId();
   }

   @NotNull
   public UUID getProfileId() {
      return this.playerData.getProfileId();
   }

   @NotNull
   public UUID getOfficialId() {
      return this.playerData.getOfficialId();
   }

   @NotNull
   public Player getPlayer() {
      return this.playerData.getPlayer();
   }

   @NotNull
   public UUID getEffectiveId() {
      if (MythicLib.plugin.getProfileMode() == null) {
         return this.getUniqueId();
      } else if (this.mmoPlugin != null && this.mmoPlugin.hasProfiles()) {
         return this.playerData.hasOfficialId() ? this.getOfficialId() : this.getUniqueId();
      } else {
         return this.playerData.hasProfile() ? this.getProfileId() : this.getUniqueId();
      }
   }

   @Deprecated
   public boolean shouldBeSaved() {
      return this.isSynchronized();
   }

   public boolean isSynchronized() {
      return this.sync;
   }

   public void markAsSynchronized() {
      Validate.isTrue(!this.sync, "Data holder already synchronized");
      Validate.isTrue(this.playerData.isOnline() || this.playerData.isLookup(), "Cannot synchronize non-lookup offline player data");
      this.sync = true;
      if (this.mmoPlugin != null) {
         this.playerData.markAsSynchronized(this.mmoPlugin, this);
      }
   }
}

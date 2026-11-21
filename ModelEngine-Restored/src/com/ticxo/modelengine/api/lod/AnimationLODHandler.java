package com.ticxo.modelengine.api.lod;

import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.math.Box;
import com.ticxo.modelengine.api.utils.math.TMath;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class AnimationLODHandler {
   private static final Set<UUID> ACTIVE = new ObjectOpenHashSet();
   private static final Callback<Consumer<Boolean>> TOGGLE_CALLBACK = new Callback<>(
      consumers -> flag -> consumers.forEach(booleanConsumer -> booleanConsumer.accept(flag))
   );
   private static final Callback<BiConsumer<UUID, Boolean>> PLAYER_CALLBACK = new Callback<>(
      biConsumers -> (uuid, flag) -> biConsumers.forEach(uuidBooleanBiConsumer -> uuidBooleanBiConsumer.accept(uuid, flag))
   );
   private static boolean ENABLED;
   private static boolean DEFAULT;
   private static double FALLOFF_LENGTH;
   private static double FALLOFF_MULTIPLIER;
   private static double IGNORE_DISTANCE;
   private static double IGNORE_DISTANCE_SQR;
   private static double IGNORE_MULTIPLIER;
   private static double MAX_RATE_MULTIPLIER;
   private static double MIN_RATE_MULTIPLIER;
   private final ModeledEntity modeledEntity;
   private final UUID toggleCallback;
   private final UUID playerCallback;
   private final Object2ObjectMap<UUID, AnimationLODHandler.LODTracker> trackers = new Object2ObjectOpenHashMap();
   private final AnimationLODHandler.LODTracker fullUpdate;
   @Getter
   private Boolean enabled;

   public static void updateConfig() {
      ENABLED = ConfigProperty.ANIMATION_LOD_ENABLED.getBoolean();
      DEFAULT = ConfigProperty.ANIMATION_LOD_DEFAULT.getBoolean();
      FALLOFF_LENGTH = ConfigProperty.ANIMATION_LOD_FALLOFF_LENGTH.getDouble();
      FALLOFF_MULTIPLIER = ConfigProperty.ANIMATION_LOD_FALLOFF_MULTIPLIER.getDouble();
      IGNORE_DISTANCE = ConfigProperty.ANIMATION_LOD_IGNORE_DISTANCE.getDouble();
      IGNORE_DISTANCE_SQR = IGNORE_DISTANCE * IGNORE_DISTANCE;
      IGNORE_MULTIPLIER = ConfigProperty.ANIMATION_LOD_IGNORE_MULTIPLIER.getDouble();
      MAX_RATE_MULTIPLIER = ConfigProperty.ANIMATION_LOD_MAX_RATE_MULTIPLIER.getDouble();
      MIN_RATE_MULTIPLIER = ConfigProperty.ANIMATION_LOD_MIN_RATE_MULTIPLIER.getDouble();
   }

   public static void setGlobalEnabled(boolean flag) {
      if (ENABLED != flag) {
         ENABLED = flag;
         TOGGLE_CALLBACK.invoker().accept(flag);
      }
   }

   public static boolean isGlobalEnabled() {
      return ENABLED;
   }

   public static void registerPlayer(UUID uuid) {
      setPlayerActive(uuid, DEFAULT);
   }

   public static void setPlayerActive(UUID uuid, boolean flag) {
      if (flag && !ACTIVE.contains(uuid)) {
         ACTIVE.add(uuid);
         PLAYER_CALLBACK.invoker().accept(uuid, true);
      } else if (!flag) {
         ACTIVE.remove(uuid);
         PLAYER_CALLBACK.invoker().accept(uuid, false);
      }
   }

   public AnimationLODHandler(ModeledEntity modeledEntity) {
      this.modeledEntity = modeledEntity;
      this.fullUpdate = new AnimationLODHandler.LODTracker(UUID.randomUUID(), modeledEntity);
      this.fullUpdate.canSkip = false;
      this.toggleCallback = TOGGLE_CALLBACK.subscribe(flag -> {
         if (!flag) {
            this.trackers.clear();
         }
      });
      this.playerCallback = PLAYER_CALLBACK.subscribe(this::updatePlayer);
   }

   public AnimationLODHandler.LODTracker tick(UUID uuid) {
      if (this.enabled() && ACTIVE.contains(uuid)) {
         AnimationLODHandler.LODTracker tracker = this.trackers
            .computeIfAbsent(uuid, uuid1 -> new AnimationLODHandler.LODTracker(uuid, this.modeledEntity));
         if (tracker.tick(ServerInfo.CURRENT_TICK)) {
            this.trackers.remove(uuid);
            tracker.canSkip = true;
         }

         return tracker;
      } else {
         return this.fullUpdate;
      }
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
      if (!this.enabled()) {
         this.trackers.clear();
      }
   }

   public boolean enabled() {
      return this.enabled == null ? ENABLED : this.enabled;
   }

   public void destroy() {
      TOGGLE_CALLBACK.unsubscribe(this.toggleCallback);
      PLAYER_CALLBACK.unsubscribe(this.playerCallback);
   }

   private void updatePlayer(UUID uuid, boolean flag) {
      if (flag) {
         this.trackers.computeIfAbsent(uuid, uuid1 -> new AnimationLODHandler.LODTracker(uuid, this.modeledEntity));
      } else {
         this.trackers.remove(uuid);
      }
   }

   @RequiredArgsConstructor
   public class LODTracker {
      private final UUID uuid;
      private final ModeledEntity modeledEntity;
      private int lastCheckedTick = ServerInfo.CURRENT_TICK;
      @Getter
      private final DataTracker<Integer> tickDuration = new DataTracker<>(1);
      private double distanceSqr = -1.0;
      private double tickRate = 1.0;
      private double cumulatedTick = 0.0;
      private int lastSuccessfulTick = 0;
      @Getter
      private boolean canSkip;

      public boolean tick(int tick) {
         if (this.canRemoveSelf()) {
            this.canSkip = true;
            return true;
         }

         if (this.lastCheckedTick == tick) {
            return false;
         }

         this.lastCheckedTick = tick;
         this.calculateTickRate();
         this.cumulatedTick = this.cumulatedTick + this.tickRate;
         int floorTick = TMath.floor(this.cumulatedTick);
         this.canSkip = this.lastSuccessfulTick == floorTick;
         this.lastSuccessfulTick = floorTick;
         if (!this.canSkip) {
            this.tickDuration.clearDirty();
            this.tickDuration.set(Math.max(TMath.ceil((Math.ceil(this.cumulatedTick) - this.cumulatedTick) / this.tickRate), 1));
         }

         return false;
      }

      public boolean canRemoveSelf() {
         return !AnimationLODHandler.this.enabled() || !AnimationLODHandler.ACTIVE.contains(this.uuid) || Bukkit.getPlayer(this.uuid) == null;
      }

      public void calculateTickRate() {
         if (AnimationLODHandler.this.enabled() && AnimationLODHandler.ACTIVE.contains(this.uuid)) {
            Player player = Bukkit.getPlayer(this.uuid);
            if (player != null) {
               BaseEntity<?> base = this.modeledEntity.getBase();
               Location loc = base.getLocation();
               if (loc.getWorld() == player.getWorld()) {
                  double dSqr = loc.distanceSquared(player.getLocation());
                  if (!TMath.isSimilar(dSqr, this.distanceSqr)) {
                     this.distanceSqr = dSqr;
                     if (AnimationLODHandler.IGNORE_DISTANCE_SQR >= dSqr) {
                        this.tickRate = 1.0;
                     } else {
                        Box cullBox = base.getData().getCullHitbox();
                        BoundingBox box = cullBox == null ? base.getBoundingBox() : cullBox.createBoundingBox(loc.toVector());
                        double ignoreMul = (box.getWidthX() + box.getHeight() + box.getWidthZ()) / 3.0 * AnimationLODHandler.IGNORE_MULTIPLIER;
                        float distance = (float)Math.max(0.0, Math.sqrt(dSqr) - AnimationLODHandler.IGNORE_DISTANCE * ignoreMul);
                        if (TMath.isSimilar(distance, 0.0F)) {
                           this.tickRate = 1.0;
                        } else {
                           this.tickRate = Math.pow(AnimationLODHandler.FALLOFF_MULTIPLIER, distance / AnimationLODHandler.FALLOFF_LENGTH);
                        }
                     }

                     this.tickRate = TMath.clamp(this.tickRate, AnimationLODHandler.MIN_RATE_MULTIPLIER, AnimationLODHandler.MAX_RATE_MULTIPLIER);
                  }
               }
            }
         }
      }
   }
}

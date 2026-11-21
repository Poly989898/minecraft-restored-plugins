package com.ticxo.modelengine.api.entity.data;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.math.Box;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface IEntityData extends DataIO {
   SavedData.DataSaver<CullType> CULL_TYPE_DATA_SAVER = (data, s, cullType) -> data.putString(s, cullType.name());
   SavedData.DataLoader<CullType> CULL_TYPE_DATA_LOADER = (data, key) -> {
      String s = data.getString(key);
      return s == null ? null : CullType.get(s);
   };

   void asyncUpdate();

   void syncUpdate();

   void cullUpdate();

   void cleanup();

   void destroy();

   BaseEntity<?> getBaseEntity();

   boolean isDataValid();

   Location getLocation();

   List<Entity> getPassengers();

   default void markViewersDirty() {
   }

   Set<UUID> getStartTracking();

   @Internal
   Map<UUID, CullType> getMutableTracking();

   Map<UUID, CullType> getTracking();

   Set<UUID> getStopTracking();

   boolean hasTracking();

   Box getCullHitbox();

   void setCullHitbox(Box cullHitbox);

   Integer getCullInterval();

   void setCullInterval(Integer cullInterval);

   int cullInterval();

   Boolean getVerticalCull();

   void setVerticalCull(Boolean verticalCull);

   boolean verticalCull();

   Double getVerticalCullDistance();

   void setVerticalCullDistance(Double verticalCullDistance);

   double verticalCullDistance();

   CullType getVerticalCullType();

   void setVerticalCullType(CullType verticalCullType);

   CullType verticalCullType();

   Boolean getBackCull();

   void setBackCull(Boolean backCull);

   boolean backCull();

   Double getBackCullAngle();

   void setBackCullAngle(Double backCullAngle);

   double backCullAngle();

   Double getBackCullIgnoreRadius();

   void setBackCullIgnoreRadius(Double backCullIgnoreRadius);

   double backCullIgnoreRadius();

   CullType getBackCullType();

   void setBackCullType(CullType backCullType);

   CullType backCullType();

   Boolean getBlockedCull();

   void setBlockedCull(Boolean blockedCull);

   boolean blockedCull();

   Double getBlockedCullIgnoreRadius();

   void setBlockedCullIgnoreRadius(Double blockedCullIgnoreRadius);

   double blockedCullIgnoreRadius();

   CullType getBlockedCullType();

   void setBlockedCullType(CullType blockedCullType);

   CullType blockedCullType();

   void setCullExempt(UUID uuid, boolean exempt);

   boolean isCullExempt(UUID uuid);

   Set<UUID> getCullExempt();

   void markModelGlowing(ActiveModel model, boolean flag);

   void markBoneGlowing(ModelBone bone, boolean flag);

   boolean isModelGlowing();

   boolean isBaseGlowing();

   @Override
   default void save(SavedData data) {
      data.saveIfExist("cull_interval", this::getCullInterval, SavedData::putInt);
      data.saveIfExist("vertical_cull", this::getVerticalCull, SavedData::putBoolean);
      data.saveIfExist("vertical_cull_distance", this::getVerticalCullDistance, SavedData::putDouble);
      data.saveIfExist("vertical_cull_type", this::getVerticalCullType, CULL_TYPE_DATA_SAVER);
      data.saveIfExist("back_cull", this::getBackCull, SavedData::putBoolean);
      data.saveIfExist("back_cull_angle", this::getBackCullAngle, SavedData::putDouble);
      data.saveIfExist("back_cull_ignore_radius", this::getBackCullIgnoreRadius, SavedData::putDouble);
      data.saveIfExist("back_cull_type", this::getBackCullType, CULL_TYPE_DATA_SAVER);
      data.saveIfExist("blocked_cull", this::getBlockedCull, SavedData::putBoolean);
      data.saveIfExist("blocked_cull_ignore_radius", this::getBlockedCullIgnoreRadius, SavedData::putDouble);
      data.saveIfExist("blocked_cull_type", this::getBlockedCullType, CULL_TYPE_DATA_SAVER);
   }

   @Override
   default void load(SavedData data) {
      data.loadIfExist("cull_interval", SavedData::getInt, this::setCullInterval);
      data.loadIfExist("vertical_cull", SavedData::getBoolean, this::setVerticalCull);
      data.loadIfExist("vertical_cull_distance", SavedData::getDouble, this::setVerticalCullDistance);
      data.loadIfExist("vertical_cull_type", CULL_TYPE_DATA_LOADER, this::setVerticalCullType);
      data.loadIfExist("back_cull", SavedData::getBoolean, this::setBackCull);
      data.loadIfExist("back_cull_angle", SavedData::getDouble, this::setBackCullAngle);
      data.loadIfExist("back_cull_ignore_radius", SavedData::getDouble, this::setBackCullIgnoreRadius);
      data.loadIfExist("back_cull_type", CULL_TYPE_DATA_LOADER, this::setBackCullType);
      data.loadIfExist("blocked_cull", SavedData::getBoolean, this::setBlockedCull);
      data.loadIfExist("blocked_cull_ignore_radius", SavedData::getDouble, this::setBlockedCullIgnoreRadius);
      data.loadIfExist("blocked_cull_type", CULL_TYPE_DATA_LOADER, this::setBlockedCullType);
   }

   @FunctionalInterface
   interface CullCameraOverride {
      Location calculate(IEntityData iEntityData, Player player, Location location);
   }
}

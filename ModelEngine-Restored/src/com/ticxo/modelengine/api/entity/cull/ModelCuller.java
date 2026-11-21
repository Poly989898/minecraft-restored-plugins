package com.ticxo.modelengine.api.entity.cull;

import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.utils.callback.Callback;
import java.util.UUID;
import org.bukkit.Location;

public interface ModelCuller {
   Callback<IEntityData.CullCameraOverride> PLAYER_CALLBACK = new Callback<>(cullCameraOverrides -> (data, player, lastLocation) -> {
      Location location = lastLocation;

      for (IEntityData.CullCameraOverride override : cullCameraOverrides) {
         location = override.calculate(data, player, location);
      }

      return location;
   });

   void setData(IEntityData data);

   IEntityData getData();

   void updateCulledPlayer();

   CullType put(UUID uuid, CullType type);

   CullType remove(UUID uuid);

   int getCulledCount();
}

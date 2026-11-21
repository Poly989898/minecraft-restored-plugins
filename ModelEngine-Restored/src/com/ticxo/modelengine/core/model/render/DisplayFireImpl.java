package com.ticxo.modelengine.core.model.render;

import com.ticxo.modelengine.api.model.render.DisplayFire;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

@Getter
@AllArgsConstructor
public class DisplayFireImpl implements DisplayFire {
   private final int id;
   private final UUID uuid = UUID.randomUUID();
   private final DataTracker<Vector3f> position = new UpdateDataTracker<>(new Vector3f(), Vector3f::set, Vector3f::new);
   private final DataTracker<Vector3f> scale = new UpdateDataTracker<>(new Vector3f(), Vector3f::set, Vector3f::new);
   private final DataTracker<ItemStack> fireModel = new DataTracker<>(new ItemStack(Material.AIR), ItemStack::isSimilar);
   private final DataTracker<Boolean> visible = new DataTracker<>(false);

   @Override
   public void clearDirty() {
      this.position.clearDirty();
      this.scale.clearDirty();
      this.fireModel.clearDirty();
      this.visible.clearDirty();
   }
}

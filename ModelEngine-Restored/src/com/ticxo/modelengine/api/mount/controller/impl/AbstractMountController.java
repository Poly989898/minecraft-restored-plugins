package com.ticxo.modelengine.api.mount.controller.impl;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@Getter
@Setter
public abstract class AbstractMountController implements MountController {
   protected final Entity entity;
   protected final Mount mount;
   protected MountController.MountInput input;
   protected boolean canDamageMount;
   protected boolean canInteractMount;

   @Override
   public boolean canDamageMount() {
      return this.canDamageMount;
   }

   @Override
   public boolean canInteractMount() {
      return this.canInteractMount;
   }

   @Override
   public void updateDirection(LookController controller, ActiveModel model) {
      Location location = this.getEntity().getLocation();
      controller.setHeadYaw(location.getYaw());
      controller.setPitch(location.getPitch() * 0.5F);
   }

   @Generated
   public AbstractMountController(Entity entity, Mount mount) {
      this.entity = entity;
      this.mount = mount;
   }
}

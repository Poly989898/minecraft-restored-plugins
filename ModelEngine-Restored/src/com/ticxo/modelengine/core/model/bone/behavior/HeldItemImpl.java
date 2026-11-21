package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.api.model.render.DisplayRenderer;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class HeldItemImpl extends AbstractBoneBehavior<HeldItemImpl> implements HeldItem {
   private final Vector3f location = new Vector3f();
   private final Quaternionf rotation = new Quaternionf();
   @Setter
   private ItemDisplayTransform display;
   private HeldItem.ItemStackSupplier itemProvider;

   public HeldItemImpl(ModelBone bone, BoneBehaviorType<HeldItemImpl> type, BoneBehaviorData data) {
      super(bone, type, data);
      this.display = data.get("display");
   }

   @Override
   public void onApply() {
      if (this.bone.getActiveModel().getModelRenderer() instanceof DisplayRenderer) {
         this.bone.setRenderer(true);
         this.bone.setModel(EMPTY);
      }
   }

   @Override
   public void onFinalize() {
      float yaw = -this.bone.getYaw() * (float) (Math.PI / 180.0);
      this.bone.getGlobalTransform().mutatePosition(pos -> pos.rotateY(yaw, this.location));
      this.bone.getGlobalTransform().mutateLeftQuaternion(q -> q.premul(this.rotation.rotationZYX(0.0F, yaw, 0.0F), this.rotation));
      if (this.itemProvider == null) {
         this.bone.setModel(EMPTY);
      } else {
         this.bone.setModel(this.itemProvider.supply());
      }
   }

   @Override
   public void save(SavedData data) {
      if (this.itemProvider != null) {
         this.itemProvider.save().ifPresent(data1 -> data.putData("supplier", data1));
      }
   }

   @Override
   public void load(SavedData data) {
      data.getData("supplier").ifPresent(data1 -> {
         this.itemProvider = switch (data1.getString("type", "")) {
            case "static" -> new HeldItem.StaticItemStackSupplier();
            case "equipment" -> new HeldItem.EquipmentSupplier();
            default -> null;
         };
         if (this.itemProvider != null) {
            this.itemProvider.load(data1);
         }
      });
   }

   @Override
   public void clearItemProvider() {
      this.itemProvider = null;
   }

   @Override
   public void setItemProvider(Supplier<ItemStack> stackSupplier) {
      this.setItemProvider(new HeldItem.TemporaryItemStackSupplier(stackSupplier));
   }

   @Override
   public void setItemProvider(HeldItem.ItemStackSupplier stackSupplier) {
      this.itemProvider = stackSupplier;
   }

   @Override
   public ItemStack getItem() {
      return this.bone.getModel();
   }
}

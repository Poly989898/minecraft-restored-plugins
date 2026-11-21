package com.ticxo.modelengine.core.model.bone.behavior;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.skin.LimbData;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.UserLimb;
import com.ticxo.modelengine.api.utils.MojangAPI;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class UserLimbImpl extends AbstractBoneBehavior<UserLimbImpl> implements UserLimb {
   private final String limbType;
   private final DataTracker<ItemStack> generatedStack = new DataTracker<>(this.createHead(), ItemStack::isSimilar);
   private LimbData limbData;
   private boolean slim;
   private final Matrix4f originalTransform = new Matrix4f();
   private boolean generating;
   private volatile int generation;
   @Setter
   private String placeholder;

   public UserLimbImpl(ModelBone bone, BoneBehaviorType<UserLimbImpl> type, BoneBehaviorData data) {
      super(bone, type, data);
      this.limbType = data.get("type");
   }

   @Override
   public void onApply() {
      this.bone.setRenderer(true);
      this.bone.setModel(this.generatedStack.get());
   }

   @Override
   public void preTransformDecompose() {
      if (this.limbData != null) {
         this.originalTransform.set(this.bone.getTransformMatrix());
         this.bone.getTransformMatrix().translate(this.limbData.translation()).rotate(new Quaternionf().rotateY((float) Math.PI)).scale(this.limbData.scale());
      }
   }

   @Override
   public void postTransformRecompose() {
      this.bone.getTransformMatrix().set(this.originalTransform);
   }

   @Override
   public void onFinalize() {
      this.generatedStack.ifDirty(this.bone::setModel);
      this.generatedStack.clearDirty();
   }

   @Override
   public void setTexture(@Nullable Player player) {
      if (player == null) {
         this.generatedStack.set(this.createHead());
      } else {
         this.setTexture(player.getPlayerProfile());
      }
   }

   @Override
   public String getLimbType() {
      return this.limbType;
   }

   @Override
   public void setStaticSkin(boolean slim, @Nullable String url) {
      this.generation++;
      this.slim = slim;
      this.limbData = ModelEngineAPI.getAPI().getSkinSplitter().getLimb(this.limbType, slim);
      if (url != null && this.limbData != null) {
         this.generatedStack.set(this.limbData.createItem(url));
      } else {
         this.generatedStack.set(this.createHead());
      }
   }

   @Override
   public void setTexture(@Nullable PlayerProfile profile) {
      int gen = ++this.generation;
      this.generatedStack.set(this.createHead());
      if (profile != null) {
         switch (profile.getTextures().getSkinModel()) {
            case CLASSIC:
               this.slim = false;
               break;
            case SLIM:
               this.slim = true;
         }

         this.limbData = ModelEngineAPI.getAPI().getSkinSplitter().getLimb(this.limbType, this.slim);
         if (this.limbData.isHead()) {
            ItemStack head = this.createHead();
            head.editMeta(itemMeta -> ((SkullMeta)itemMeta).setPlayerProfile(profile));
            this.generatedStack.set(head);
         } else {
            this.generating = true;
            ModelEngineAPI.getUserLimbRegistry()
               .getSkinUrlFromKey(this.getPlaceholder(), this.limbType, this.slim)
               .thenApply(url -> this.limbData.createItem(url))
               .handle((stack, throwable) -> {
                  if (throwable != null) {
                     throwable.printStackTrace();
                     return null;
                  }

                  if (this.generating && gen == this.generation) {
                     this.generatedStack.set(stack);
                  }

                  return null;
               });
            MojangAPI.getRawSkinData(profile)
               .thenCompose(base64 -> ModelEngineAPI.getUserLimbRegistry().getSkinUrlFromSource(base64, this.limbType, this.slim))
               .thenApply(url -> this.limbData.createItem(url))
               .handle((stack, throwable) -> {
                  if (throwable != null) {
                     throwable.printStackTrace();
                     return null;
                  }

                  if (gen != this.generation) {
                     return null;
                  }

                  this.generatedStack.set(stack);
                  this.generating = false;
                  return null;
               });
         }
      }
   }

   @Override
   public String getPlaceholder() {
      return this.placeholder != null
         ? this.placeholder
         : (this.slim ? ModelEngineAPI.getConfigCache().getSlim() : ModelEngineAPI.getConfigCache().getClassic());
   }

   private ItemStack createHead() {
      return new ItemStack(Material.PLAYER_HEAD);
   }
}

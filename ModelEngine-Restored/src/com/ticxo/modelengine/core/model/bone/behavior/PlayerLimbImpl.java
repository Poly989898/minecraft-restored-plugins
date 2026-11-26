package com.ticxo.modelengine.core.model.bone.behavior;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.events.BoneTransformReadEvent;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures.SkinModel;
import org.jetbrains.annotations.Nullable;

public class PlayerLimbImpl extends AbstractBoneBehavior<PlayerLimbImpl> implements PlayerLimb {
   @Getter
   private final PlayerLimb.Limb limbType;
   private boolean isSlim;

   public PlayerLimbImpl(ModelBone bone, BoneBehaviorType<PlayerLimbImpl> type, BoneBehaviorData data) {
      super(bone, type, data);
      this.limbType = data.get("limb");
   }

   @Override
   public void onApply() {
      this.bone.setRenderer(true);
      this.bone.setModel(new ItemStack(Material.AIR));
   }

   @Override
   public void modifyTransform(BoneTransformReadEvent event) {
      event.getPosition().add(0.0F, this.isSlim ? this.limbType.slimYOffset : this.limbType.defaultYOffset, 0.0F);
   }

   @Override
   public void setTexture(@Nullable Player player) {
      if (player == null) {
         this.bone.setModel(new ItemStack(Material.AIR));
      } else {
         this.setTexture(player.getPlayerProfile());
      }
   }

   @Override
   public void setTexture(@Nullable PlayerProfile profile) {
      if (profile == null) {
         this.bone.setModel(new ItemStack(Material.AIR));
      } else {
         ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
         ItemMeta meta = playerHead.getItemMeta();
         ((SkullMeta)meta).setPlayerProfile(profile);

         meta.setCustomModelData(switch (shownSkinModel(profile)) {
            case CLASSIC -> {
               this.isSlim = false;
               yield this.limbType.defaultId;
            }
            case SLIM -> {
               this.isSlim = true;
               yield this.limbType.slimId;
            }
            default -> throw new IncompatibleClassChangeError();
         });
         playerHead.setItemMeta(meta);
         ModelEngineAPI.getNMSHandler().applyPlayerHeadItemModel(playerHead);
         this.bone.setModel(playerHead);
      }
   }

   /**
    * Arm width the client will actually draw. A profile without a skin reports CLASSIC, but the client then picks one of
    * the 18 default skins by UUID, and the first 9 of them are slim: wide limbs on a slim texture leave see-through pixels.
    */
   private static SkinModel shownSkinModel(PlayerProfile profile) {
      if (profile.getTextures().getSkin() == null && profile.getUniqueId() != null) {
         return Math.floorMod(profile.getUniqueId().hashCode(), 18) < 9 ? SkinModel.SLIM : SkinModel.CLASSIC;
      }

      return profile.getTextures().getSkinModel();
   }
}

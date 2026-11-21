package com.ticxo.modelengine.core21.mythic.mechanics.bone;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import com.ticxo.modelengine.api.model.bone.type.UserLimb;
import com.ticxo.modelengine.api.utils.MojangAPI;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.profile.PlayerTextures.SkinModel;

@MythicMechanic(name = "modelplayerskin", aliases = "modelskin")
public class ModelSkinMechanic implements ITargetedEntitySkill {
   private final PlaceholderString modelId;
   private final PlaceholderString pbone;
   private final PlaceholderString username;
   private final PlaceholderString uuid;
   private final PlaceholderString skin;
   private final Boolean isSlim;

   public ModelSkinMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.pbone = mlc.getPlaceholderString(new String[]{"p", "pbone", "limbs"}, null, new String[0]);
      this.uuid = mlc.getPlaceholderString(new String[]{"uuid"}, null, new String[0]);
      this.username = mlc.getPlaceholderString(new String[]{"user", "name", "username"}, null, new String[0]);
      this.skin = mlc.getPlaceholderString(new String[]{"s", "skin"}, null, new String[0]);
      String slim = mlc.getString(new String[]{"slim"}, null, new String[0]);
      this.isSlim = slim == null ? null : Boolean.parseBoolean(slim);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      AbstractEntity caster = meta.getCaster().getEntity();
      ModeledEntity model = ModelEngineAPI.getModeledEntity(caster.getUniqueId());
      if (model == null) {
         return SkillResult.CONDITION_FAILED;
      }

      String username = MythicUtils.getOrNull(this.username, meta, target);
      if (username != null) {
         MojangAPI.getUUIDFromUsernamePromise(username)
            .thenApplyAsync(MojangAPI::fromUUID)
            .thenAcceptAsync(playerProfile -> this.apply(model, playerProfile, meta, target));
         return SkillResult.SUCCESS;
      }

      String uuidString = MythicUtils.getOrNull(this.uuid, meta, target);
      if (uuidString != null) {
         UUID uuid = TMath.parseUUID(uuidString);
         MojangAPI.fromUUIDPromise(uuid).thenAcceptAsync(playerProfile2 -> this.apply(model, playerProfile2, meta, target));
         return SkillResult.SUCCESS;
      }

      String skin = MythicUtils.getOrNull(this.skin, meta, target);
      PlayerProfile profile;
      if (skin == null) {
         if (!(target.getBukkitEntity() instanceof Player player)) {
            return SkillResult.INVALID_TARGET;
         }

         profile = player.getPlayerProfile();
      } else {
         profile = MojangAPI.fromBase64(skin);
      }

      return this.apply(model, profile, meta, target);
   }

   public boolean getTargetsCreatives() {
      return true;
   }

   private SkillResult apply(ModeledEntity model, PlayerProfile profile, SkillMetadata meta, AbstractEntity target) {
      PlayerProfile finalProfile = this.forceModel(profile);
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      String pbone = MythicUtils.getOrNullLowercase(this.pbone, meta, target);
      if (modelId == null && pbone == null) {
         for (ActiveModel activeModel : model.getModels().values()) {
            for (ModelBone modelBone : activeModel.getBones().values()) {
               modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)playerLimb).setTexture(finalProfile));
               modelBone.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)playerLimb).setTexture(finalProfile));
            }
         }

         return SkillResult.SUCCESS;
      } else {
         List<String> limbs = pbone == null ? null : List.of(pbone.split(","));
         if (modelId == null) {
            for (ActiveModel activeModel : model.getModels().values()) {
               for (String limb : limbs) {
                  activeModel.getBone(limb).ifPresent(modelBone -> {
                     modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)playerLimb).setTexture(finalProfile));
                     modelBone.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)playerLimb).setTexture(finalProfile));
                  });
               }
            }
         } else {
            model.getModel(modelId).ifPresent(activeModel2 -> {
               if (limbs == null) {
                  for (ModelBone modelBone : activeModel2.getBones().values()) {
                     modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)playerLimb).setTexture(finalProfile));
                     modelBone.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)playerLimb).setTexture(finalProfile));
                  }
               } else {
                  for (String seat : limbs) {
                     activeModel2.getBone(seat).ifPresent(modelBone -> {
                        modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)playerLimb).setTexture(finalProfile));
                        modelBone.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)playerLimb).setTexture(finalProfile));
                     });
                  }
               }
            });
         }

         return SkillResult.SUCCESS;
      }
   }

   private PlayerProfile forceModel(PlayerProfile profile) {
      if (this.isSlim == null) {
         return profile;
      }

      PlayerTextures texture = profile.getTextures();
      SkinModel model = this.isSlim ? SkinModel.SLIM : SkinModel.CLASSIC;
      if (model != texture.getSkinModel()) {
         texture.setSkin(texture.getSkin(), model);
         profile.setTextures(texture);
      }

      return profile;
   }
}

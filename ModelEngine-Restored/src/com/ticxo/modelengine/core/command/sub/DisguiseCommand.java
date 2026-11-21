package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import com.ticxo.modelengine.api.model.bone.type.UserLimb;
import com.ticxo.modelengine.core.command.MECommand;
import com.ticxo.modelengine.core.command.ModelOptionParser;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisguiseCommand extends AbstractCommand {
   public DisguiseCommand(AbstractCommand parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      if (args.length < 1) {
         return false;
      }

      ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(args[0]);
      if (blueprint == null) {
         return false;
      }

      ModelOptionParser options = ModelOptionParser.parse(1, args);
      ModeledEntity modeledEntity = ModelEngineAPI.getOrCreateModeledEntity(player);
      modeledEntity.getBase().getBodyRotationController().setPlayerMode(true);
      modeledEntity.setBaseEntityVisible(!options.hideSelf);
      if (modeledEntity.getBase().getData() instanceof BukkitEntityData data && (args.length < 2 || !options.hideSelfDisguise)) {
         data.getTracked().addForcedPairing(player.getUniqueId());
      }

      if (options.hideSelf) {
         ModelEngineAPI.getEntityHandler().setForcedInvisible(player, true);
      }

      if (modeledEntity.getModel(args[0]).isEmpty()) {
         ActiveModel activeModel = ModelEngineAPI.createActiveModel(blueprint);
         options.applyDisguiseOptions(activeModel);
         modeledEntity.addModel(activeModel, false).ifPresent(ActiveModel::destroy);
         if (options.pivotOverride) {
            activeModel.setPivotOverride(ModelEngineAPI.getPivotOverrideRegistry().getOrCreate(player));
         }

         activeModel.getBones().values().forEach(modelBone -> {
            modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)playerLimb).setTexture(player));
            modelBone.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)playerLimb).setTexture(player));
         });
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      List<String> list = new ArrayList<>();
      switch (args.length) {
         case 1:
            MECommand.getModelIdTabComplete(list, args[0]);
            break;
         default:
            list.addAll(ModelOptionParser.getTabCompletion(1, args));
      }

      return list;
   }

   @Override
   public String getPermissionNode() {
      return "modelengine.command.disguise";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "disguise";
   }
}

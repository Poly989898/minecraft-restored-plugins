package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.command.MECommand;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndisguiseCommand extends AbstractCommand {
   public UndisguiseCommand(AbstractCommand parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(player.getUniqueId());
      if (modeledEntity == null) {
         return true;
      }

      if (args.length == 0) {
         modeledEntity.setBaseEntityVisible(true);
         modeledEntity.markRemoved();
         ModelEngineAPI.getEntityHandler().setForcedInvisible(player, false);
      } else {
         for (String modelId : args) {
            modeledEntity.removeModel(modelId).ifPresent(ActiveModel::destroy);
         }

         if (modeledEntity.getModels().isEmpty()) {
            modeledEntity.setBaseEntityVisible(true);
            modeledEntity.markRemoved();
            ModelEngineAPI.getEntityHandler().setForcedInvisible(player, false);
         }
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      List<String> list = new ArrayList<>();
      Player player = (Player)sender;
      ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(player.getUniqueId());
      if (modeledEntity == null) {
         return list;
      }

      if (args.length > 0) {
         String arg = args[args.length - 1];
         MECommand.getModelIdTabComplete(list, arg, modeledEntity);
      }

      return list;
   }

   @Override
   public String getPermissionNode() {
      return "modelengine.command.undisguise";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "undisguise";
   }
}

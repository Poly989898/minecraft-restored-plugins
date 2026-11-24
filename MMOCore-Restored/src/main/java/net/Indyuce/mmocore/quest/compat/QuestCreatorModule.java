package net.Indyuce.mmocore.quest.compat;

import com.guillaumevdn.questcreator.ConfigQC;
import com.guillaumevdn.questcreator.data.user.QuestHistoryElement;
import com.guillaumevdn.questcreator.data.user.UserQC;
import com.guillaumevdn.questcreator.lib.model.ElementModel;
import com.guillaumevdn.questcreator.lib.quest.QuestEndType;
import java.util.Arrays;
import net.Indyuce.mmocore.quest.AbstractQuest;
import org.bukkit.entity.Player;

public class QuestCreatorModule implements QuestModule<QuestCreatorModule.QuestCreatorQuest> {
   public QuestCreatorModule.QuestCreatorQuest getQuestOrThrow(String id) {
      return new QuestCreatorModule.QuestCreatorQuest(id);
   }

   @Override
   public boolean hasCompletedQuest(String questId, Player player) {
      UserQC playerData = UserQC.cachedOrNull(player);
      if (playerData == null) {
         return false;
      }

      for (QuestHistoryElement el : playerData.getQuestHistory().getElements(questId, Arrays.asList(QuestEndType.SUCCESS), 0L)) {
         if (el.getModelId().equals(questId)) {
            return true;
         }
      }

      return false;
   }

   public class QuestCreatorQuest implements AbstractQuest {
      ElementModel questModel;

      public QuestCreatorQuest(String modelId) {
         this.questModel = (ElementModel)ConfigQC.models.getElement(modelId).orNull();
      }

      @Override
      public String getName() {
         return this.questModel.getDisplayName().getId();
      }

      @Override
      public String getId() {
         return this.questModel.getId();
      }
   }
}

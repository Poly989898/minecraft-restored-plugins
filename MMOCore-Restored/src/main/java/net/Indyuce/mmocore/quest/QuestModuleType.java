package net.Indyuce.mmocore.quest;

import javax.inject.Provider;
import net.Indyuce.mmocore.quest.compat.BeautyQuestModule;
import net.Indyuce.mmocore.quest.compat.BlackVeinQuestsModule;
import net.Indyuce.mmocore.quest.compat.QuestCreatorModule;
import net.Indyuce.mmocore.quest.compat.QuestModule;
import org.bukkit.Bukkit;

public enum QuestModuleType {
   MMOCORE("MMOCore", MMOCoreQuestModule::new),
   QUESTS("Quests", BlackVeinQuestsModule::new),
   BEAUTY_QUEST("BeautyQuests", BeautyQuestModule::new),
   QUEST_CREATOR("QuestCreator", QuestCreatorModule::new);

   private final String pluginName;
   private final Provider<QuestModule> provider;

   QuestModuleType(String pluginName, Provider<QuestModule> provider) {
      this.pluginName = pluginName;
      this.provider = provider;
   }

   public boolean isValid() {
      return Bukkit.getPluginManager().getPlugin(this.pluginName) != null;
   }

   public QuestModule provideModule() {
      return (QuestModule)this.provider.get();
   }
}

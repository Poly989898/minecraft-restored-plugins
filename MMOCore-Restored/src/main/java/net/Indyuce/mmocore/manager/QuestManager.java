package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.util.FileUtils;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.quest.Quest;
import org.apache.commons.lang.Validate;

public class QuestManager implements MMOCoreManager {
   private final Map<String, Quest> quests = new LinkedHashMap<>();

   public void register(Quest quest) {
      this.quests.put(quest.getId(), quest);
   }

   public Quest get(String id) {
      return this.quests.get(id);
   }

   public Quest getOrThrow(String id) {
      Validate.isTrue(this.quests.containsKey(id), "Could not find quest with ID '" + id + "'");
      return this.get(id);
   }

   public Collection<Quest> getAll() {
      return this.quests.values();
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.quests.clear();
      }

      FileUtils.loadObjectsFromFolder(
         MMOCore.plugin, "quests", true, (key, config) -> this.register(new Quest(key, config)), "Could not load quest from file '%s': %s"
      );

      for (Quest quest : this.quests.values()) {
         try {
            quest.getPostLoadAction().performAction();
         } catch (IllegalArgumentException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not post-load quest '" + quest.getId() + "': " + exception.getMessage());
         }
      }
   }
}

package io.lumine.mythic.api.skills;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;

public interface ISkillMechanic {
   default MythicPlugin getPlugin() {
      return MythicProvider.get();
   }

   default ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.EITHER;
   }

   default boolean getTargetsSpectators() {
      return false;
   }

   default boolean getTargetsCreatives() {
      return false;
   }
}

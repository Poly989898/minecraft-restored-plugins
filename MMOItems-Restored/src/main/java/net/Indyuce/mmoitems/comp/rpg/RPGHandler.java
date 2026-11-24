package net.Indyuce.mmoitems.comp.rpg;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.comp.mmocore.MMOCoreHook;

public interface RPGHandler {
   RPGPlayer getInfo(PlayerData var1);

   void refreshStats(PlayerData var1);

   enum PluginEnum {
      MMOCORE("MMOCore", MMOCoreHook.class),
      HEROES("Heroes", HeroesHook.class),
      FABLED("Fabled", FabledHook.class),
      RPGPLAYERLEVELING("RPGPlayerLeveling", RPGPlayerLevelingHook.class),
      RACESANDCLASSES("RacesAndClasses", RacesAndClassesHook.class),
      BATTLELEVELS("BattleLevels", BattleLevelsHook.class),
      MCMMO("mcMMO", McMMOHook.class),
      MCRPG("McRPG", McRPGHook.class),
      AURELIUM_SKILLS("AureliumSkills", AureliumSkillsHook.class),
      AURA_SKILLS("AuraSkills", AuraSkillsHook.class),
      SKILLS("Skills", SkillsHook.class),
      SKILLSPRO("SkillsPro", SkillsProHook.class);

      private final Class<? extends RPGHandler> pluginClass;
      private final String name;

      PluginEnum(String nullxx, Class<? extends RPGHandler> nullxxx) {
         this.pluginClass = nullxxx;
         this.name = nullxx;
      }

      /** The original rethrows the reflective exceptions unchecked (sneaky throws). */
      public RPGHandler load() {
         try {
            return this.pluginClass.getDeclaredConstructor().newInstance();
         } catch (ReflectiveOperationException var1) {
            PluginEnum.<RuntimeException>sneakyThrow(var1);
            return null;
         }
      }

      @SuppressWarnings("unchecked")
      private static <E extends Throwable> void sneakyThrow(Throwable var0) throws E {
         throw (E)var0;
      }

      public String getName() {
         return this.name;
      }
   }
}

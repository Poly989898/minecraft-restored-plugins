package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigFile;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.Indyuce.mmocore.skill.list.Ambers;
import net.Indyuce.mmocore.skill.list.Neptune_Gift;
import net.Indyuce.mmocore.skill.list.Sneaky_Picky;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillManager implements MMOCoreManager {
   private final Map<String, RegisteredSkill> skills = new LinkedHashMap<>();

   public void registerSkill(RegisteredSkill skill) {
      this.skills.put(skill.getHandler().getId().toUpperCase(), skill);
   }

   @Nullable
   public RegisteredSkill getSkill(String id) {
      return this.skills.get(id.toUpperCase());
   }

   @NotNull
   public RegisteredSkill getSkillOrThrow(String id) {
      return Objects.requireNonNull(this.skills.get(id), "Could not find skill with ID '" + id + "'");
   }

   public Collection<RegisteredSkill> getAll() {
      return this.skills.values();
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         MythicLib.plugin.getSkills().initialize(true);
         this.skills.clear();
      }

      MythicLib.plugin.getSkills().registerSkillHandler(new Ambers());
      MythicLib.plugin.getSkills().registerSkillHandler(new Neptune_Gift());
      MythicLib.plugin.getSkills().registerSkillHandler(new Sneaky_Picky());
      File skillFolder = FileUtils.getFile(MMOCore.plugin, "skills");
      if (!skillFolder.exists()) {
         try {
            skillFolder.mkdir();

            for (SkillHandler<?> handler : MythicLib.plugin.getSkills().getHandlers()) {
               InputStream res = MMOCore.plugin.getResource("default/skills/" + handler.getLowerCaseId() + ".yml");
               if (res != null) {
                  Files.copy(res, new File(MMOCore.plugin.getDataFolder() + "/skills/" + handler.getLowerCaseId() + ".yml").getAbsoluteFile().toPath());
               }
            }
         } catch (IOException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not save default skill configs: " + exception.getMessage());
         }
      }

      ConfigFile generated = new ConfigFile(MMOCore.plugin, "", "_generated_skill_configs");
      List<String> generatedSkillHandlerIds = generated.getConfig().getStringList("list");

      for (SkillHandler<?> handler : MythicLib.plugin.getSkills().getHandlers()) {
         if (!generatedSkillHandlerIds.contains(handler.getId())) {
            generatedSkillHandlerIds.add(handler.getId());
            ConfigFile config = new ConfigFile("/skills", handler.getLowerCaseId());
            if (!config.exists()) {
               config.getConfig().set("name", UtilityMethods.caseOnWords(handler.getId().replace("_", " ").replace("-", " ").toLowerCase()));
               config.getConfig()
                  .set("lore", Arrays.asList("This is the default skill description", "", "&e{cooldown}s Cooldown", "&9Costs {mana} {mana_name}"));
               config.getConfig().set("material", "BOOK");

               for (Object param : handler.getParameters()) {
                  config.getConfig().set(param + ".base", 0);
                  config.getConfig().set(param + ".per-level", 0);
                  config.getConfig().set(param + ".min", 0);
                  config.getConfig().set(param + ".max", 0);
               }

               config.save();
            }
         }
      }

      generated.getConfig().set("list", generatedSkillHandlerIds);
      generated.save();
      FileUtils.loadObjectsFromFolder(MMOCore.plugin, "skills", true, (name, configx) -> {
         SkillHandler<?> handlerx = MythicLib.plugin.getSkills().getHandler(UtilityMethods.enumName(name));
         Validate.notNull(handlerx, "Could not find skill handler with ID '" + UtilityMethods.enumName(name) + "'");
         RegisteredSkill skill = new RegisteredSkill(handlerx, configx);
         this.skills.put(handlerx.getId(), skill);
      }, "Could not load skill from file '%s': %s");
   }
}

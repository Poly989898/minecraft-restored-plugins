package net.Indyuce.mmoitems.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillManager {
   private final Map<String, RegisteredSkill> skills = new HashMap<>();

   @Nullable
   public RegisteredSkill getSkill(String text) {
      return this.skills.get(text);
   }

   @NotNull
   public RegisteredSkill getSkillOrThrow(String text) {
      return Objects.requireNonNull(this.skills.get(text), "Could not find skill with ID '" + text + "'");
   }

   public void registerSkill(RegisteredSkill registeredSkill) {
      this.skills.put(Objects.requireNonNull(registeredSkill, "Skill cannot be null").getHandler().getId(), registeredSkill);
   }

   public boolean hasSkill(String text) {
      return this.skills.containsKey(text);
   }

   @NotNull
   public Collection<RegisteredSkill> getAll() {
      return this.skills.values();
   }

   public void initialize(boolean flag) {
      if (flag) {
         this.skills.clear();
      }

      File file = new File(MMOItems.plugin.getDataFolder() + "/skill");
      if (!file.exists()) {
         try {
            file.mkdir();

            for (SkillHandler handler : MythicLib.plugin.getSkills().getHandlers()) {
               InputStream resource = MMOItems.plugin.getResource("default/skill/" + handler.getLowerCaseId() + ".yml");
               if (resource != null) {
                  Files.copy(resource, new File(MMOItems.plugin.getDataFolder() + "/skill/" + handler.getLowerCaseId() + ".yml").getAbsoluteFile().toPath());
               }
            }
         } catch (IOException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not save default ability configs: " + exception.getMessage());
         }
      }

      for (SkillHandler<?> handler : MythicLib.plugin.getSkills().getHandlers()) {
         ConfigFile configFile = new ConfigFile("/skill", handler.getLowerCaseId());
         if (!configFile.exists()) {
            configFile.getConfig().set("name", UtilityMethods.caseOnWords(handler.getId().replace("_", " ").replace("-", " ").toLowerCase()));

            for (String modifier : handler.getModifiers()) {
               configFile.getConfig().set("modifier." + modifier + ".name", UtilityMethods.caseOnWords(modifier.replace("-", " ").toLowerCase()));
               configFile.getConfig().set("modifier." + modifier + ".default-value", 0);
            }

            configFile.save();
         }

         try {
            this.skills.put(handler.getId(), new RegisteredSkill(handler, configFile.getConfig()));
         } catch (RuntimeException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load skill '" + handler.getId() + "': " + exception.getMessage());
         }
      }
   }
}

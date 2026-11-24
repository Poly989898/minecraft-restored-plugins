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
   public RegisteredSkill getSkill(String var1) {
      return this.skills.get(var1);
   }

   @NotNull
   public RegisteredSkill getSkillOrThrow(String var1) {
      return Objects.requireNonNull(this.skills.get(var1), "Could not find skill with ID '" + var1 + "'");
   }

   public void registerSkill(RegisteredSkill var1) {
      this.skills.put(Objects.requireNonNull(var1, "Skill cannot be null").getHandler().getId(), var1);
   }

   public boolean hasSkill(String var1) {
      return this.skills.containsKey(var1);
   }

   @NotNull
   public Collection<RegisteredSkill> getAll() {
      return this.skills.values();
   }

   public void initialize(boolean var1) {
      if (var1) {
         this.skills.clear();
      }

      File var2 = new File(MMOItems.plugin.getDataFolder() + "/skill");
      if (!var2.exists()) {
         try {
            var2.mkdir();

            for (SkillHandler var4 : MythicLib.plugin.getSkills().getHandlers()) {
               InputStream var5 = MMOItems.plugin.getResource("default/skill/" + var4.getLowerCaseId() + ".yml");
               if (var5 != null) {
                  Files.copy(var5, new File(MMOItems.plugin.getDataFolder() + "/skill/" + var4.getLowerCaseId() + ".yml").getAbsoluteFile().toPath());
               }
            }
         } catch (IOException var9) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not save default ability configs: " + var9.getMessage());
         }
      }

      for (SkillHandler<?> var11 : MythicLib.plugin.getSkills().getHandlers()) {
         ConfigFile var12 = new ConfigFile("/skill", var11.getLowerCaseId());
         if (!var12.exists()) {
            var12.getConfig().set("name", UtilityMethods.caseOnWords(var11.getId().replace("_", " ").replace("-", " ").toLowerCase()));

            for (String var7 : var11.getModifiers()) {
               var12.getConfig().set("modifier." + var7 + ".name", UtilityMethods.caseOnWords(var7.replace("-", " ").toLowerCase()));
               var12.getConfig().set("modifier." + var7 + ".default-value", 0);
            }

            var12.save();
         }

         try {
            this.skills.put(var11.getId(), new RegisteredSkill(var11, var12.getConfig()));
         } catch (RuntimeException var8) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load skill '" + var11.getId() + "': " + var8.getMessage());
         }
      }
   }
}

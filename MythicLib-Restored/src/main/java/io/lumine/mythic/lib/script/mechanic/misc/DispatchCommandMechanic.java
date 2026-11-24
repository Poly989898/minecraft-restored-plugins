package io.lumine.mythic.lib.script.mechanic.misc;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

@MechanicMetadata
public class DispatchCommandMechanic extends TargetMechanic {
   private final String format;
   private final boolean fromConsole;
   private final boolean asOperator;

   public DispatchCommandMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("format");
      this.format = config.getString("format");
      this.asOperator = config.getBoolean("op", false);
      this.fromConsole = config.getBoolean("from_console", true);
   }

   @Override
   public void cast(SkillMetadata meta, Entity target) {
      String command = meta.parseString(this.format);
      if (this.asOperator && !target.isOp()) {
         try {
            target.setOp(true);
            Bukkit.dispatchCommand(target, meta.parseString(this.format));
         } catch (Exception exception) {
            MythicLib.plugin
               .getLogger()
               .log(Level.WARNING, "Could not run command '" + command + "' as entity '" + target.getUniqueId() + "': " + exception.getMessage());
         } finally {
            target.setOp(false);
         }
      }

      Bukkit.dispatchCommand((CommandSender)(this.fromConsole ? Bukkit.getConsoleSender() : target), meta.parseString(this.format));
   }
}

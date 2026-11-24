package io.lumine.mythic.lib.script;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.mechanic.misc.DelayMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import java.util.Iterator;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class MechanicQueue {
   private final Iterator<Mechanic> queue;
   private final SkillMetadata meta;
   private final Script script;
   private int counter = 0;

   public MechanicQueue(@NotNull SkillMetadata meta, @NotNull Script script) {
      this.meta = meta;
      this.queue = script.getMechanics().iterator();
      this.script = script;
   }

   public boolean next() {
      while (this.queue.hasNext()) {
         this.counter++;
         Mechanic mechanic = this.queue.next();
         if (mechanic instanceof DelayMechanic) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(MythicLib.plugin, this::next, ((DelayMechanic)mechanic).getDelay(this.meta));
            return false;
         }

         try {
            mechanic.cast(this.meta);
         } catch (RuntimeException exception) {
            MythicLib.plugin
               .getLogger()
               .log(Level.WARNING, "Could not execute mechanic n" + this.counter + " from script '" + this.script.getId() + "': " + exception.getMessage());
            return true;
         }
      }

      return true;
   }
}

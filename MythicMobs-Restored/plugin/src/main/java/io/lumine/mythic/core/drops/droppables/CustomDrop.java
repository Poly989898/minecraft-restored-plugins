package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Optional;
import org.bukkit.Bukkit;

public class CustomDrop extends Drop {
   protected final String dropName;
   protected IDrop drop = null;
   protected boolean loaded = false;

   public CustomDrop(String drop, String line, MythicLineConfig mlc) {
      super(line, mlc);
      this.dropName = drop;
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Loading CustomDrop with name " + drop);
      Schedulers.sync().runLater(() -> {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Attempting to Register CustomDrop: " + drop);
         MythicDropLoadEvent event = new MythicDropLoadEvent(this, this.dropName, this.getDropArgument(), mlc);
         Bukkit.getServer().getPluginManager().callEvent(event);
         if (event.getDrop().isPresent()) {
            this.drop = event.getDrop().get();
            this.loaded = true;
         } else {
            MythicLogger.errorDropConfig(this, mlc, "Drop type not found.");
         }
      }, 1L);
   }

   public String getConfigLine() {
      return this.getLine();
   }

   public Optional<IDrop> getDrop() {
      return Optional.ofNullable(this.drop);
   }

   public String getDropArgument() {
      return this.dropVar;
   }
}

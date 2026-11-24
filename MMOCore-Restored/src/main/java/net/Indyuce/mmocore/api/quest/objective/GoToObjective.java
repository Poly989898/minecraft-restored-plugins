package net.Indyuce.mmocore.api.quest.objective;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.quest.ObjectiveProgress;
import net.Indyuce.mmocore.api.quest.QuestProgress;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class GoToObjective extends Objective {
   private final Location loc;
   private final double range;

   public GoToObjective(ConfigurationSection section, MMOLineConfig config) {
      super(section);
      config.validate(new String[]{"range", "world", "x", "y", "z"});
      this.range = config.getDouble("range");
      World world = Bukkit.getWorld(config.getString("world"));
      Validate.notNull(world, "Could not find world " + config.getString("world"));
      this.loc = new Location(world, config.getInt("x"), config.getInt("y"), config.getInt("z"));
   }

   @Override
   public ObjectiveProgress newProgress(QuestProgress questProgress) {
      return new GoToObjective.GotoProgress(questProgress, this);
   }

   public class GotoProgress extends ObjectiveProgress implements Listener {
      public GotoProgress(QuestProgress questProgress, Objective objective) {
         super(questProgress, objective);
      }

      @EventHandler
      public void a(PlayerMoveEvent event) {
         if (event.getFrom().getBlockX() != event.getTo().getBlockX()
            || event.getFrom().getBlockY() != event.getTo().getBlockY()
            || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            Player player = event.getPlayer();
            if (this.getPlayer().isOnline()
               && player.equals(this.getPlayer().getPlayer())
               && player.getWorld().equals(GoToObjective.this.loc.getWorld())
               && player.getLocation().distance(GoToObjective.this.loc) - 0.5 < GoToObjective.this.range) {
               this.getQuestProgress().completeObjective();
            }
         }
      }

      @Override
      public String formatLore(String lore) {
         return lore;
      }
   }
}

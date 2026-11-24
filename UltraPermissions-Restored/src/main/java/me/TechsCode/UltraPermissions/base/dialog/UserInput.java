package me.TechsCode.UltraPermissions.base.dialog;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class UserInput extends Dialog {
   private static final Phrase LEFT_CLICK_CLOSE = Phrase.create("userInput.leftClickClose", "**Left Click** to close", Colors.GRAY, Colors.YELLOW);
   private final Player p;
   private final String mainTitle;
   private final String subTitle;
   private String actionbar;
   private long lastTimeMoved = 0L;

   public UserInput(Player player, SpigotTechPlugin spigotTechPlugin, Object obj, Object subTitleValue) {
      super(player, spigotTechPlugin);
      this.p = player;
      this.mainTitle = obj.toString();
      this.subTitle = subTitleValue.toString();
   }

   public UserInput(Player player, SpigotTechPlugin spigotTechPlugin, Object obj, Object subTitleValue, Object actionbarValue) {
      super(player, spigotTechPlugin);
      this.p = player;
      this.mainTitle = obj.toString();
      this.subTitle = subTitleValue.toString();
      this.actionbar = actionbarValue.toString();
   }

   public abstract boolean onResult(String text);

   @Override
   public boolean onInput(String text) {
      boolean flag = this.onResult(text);
      if (flag) {
         this.close(true);
      }

      return true;
   }

   @EventHandler
   public void move(PlayerMoveEvent event) {
      if (event.getPlayer().equals(this.p)) {
         this.lastTimeMoved = System.currentTimeMillis();
      }
   }

   @EventHandler
   public void close(PlayerInteractEvent event) {
      if (event.getPlayer().equals(this.p) && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
         this.close(false);
      }
   }

   private boolean hasMoved() {
      return this.lastTimeMoved != 0L && System.currentTimeMillis() - this.lastTimeMoved < 3000L;
   }

   @Override
   public String getMainTitle() {
      return this.mainTitle;
   }

   @Override
   public String getSubTitle() {
      return this.hasMoved() ? LEFT_CLICK_CLOSE.get() : this.subTitle;
   }

   @Override
   public String getActionBar() {
      return this.actionbar;
   }
}

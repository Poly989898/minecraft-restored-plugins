package me.TechsCode.UltraPermissions.base.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateEvent;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateTime;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Color;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class RestartView extends GUI {
   private static final Phrase TITLE = Phrase.create("restartView.title", "Restart the Server");
   private static final Phrase RESTARTING = Phrase.create("restartView.restarting", "Restarting...");
   private static final Phrase RESTART_BUTTON_TITLE = Phrase.create("restartView.button.title", "Restart");
   private static final Phrase RESTART_BUTTON_ACTION = Phrase.create("restartView.button.action", "Click to restart the Server", Colors.GRAY);
   private boolean restarting = false;
   private final List<Integer> red = new ArrayList<>();
   private final List<Integer> orange = new ArrayList<>();
   private final List<Integer> yellow = new ArrayList<>();
   private final List<Integer> green = new ArrayList<>();

   public RestartView(Player player, SpigotTechPlugin spigotTechPlugin) {
      super(player, spigotTechPlugin);
   }

   @Override
   protected void construct(Model model) {
      model.setSlots(27);
      if (!this.restarting) {
         model.setTitle(TITLE.get());
         model.button(14, this::RestartButton);
      } else {
         model.setTitle(RESTARTING.get());

         for (int count : this.red) {
            model.button(count, button -> this.TileButton(button, XMaterial.RED_STAINED_GLASS_PANE, Colors.RED));
         }

         for (int count : this.orange) {
            model.button(count, button -> this.TileButton(button, XMaterial.ORANGE_STAINED_GLASS_PANE, Colors.GOLD));
         }

         for (int count : this.yellow) {
            model.button(count, button -> this.TileButton(button, XMaterial.YELLOW_STAINED_GLASS_PANE, Colors.YELLOW));
         }

         for (int count : this.green) {
            model.button(count, button -> this.TileButton(button, XMaterial.LIME_STAINED_GLASS_PANE, Colors.GREEN));
         }
      }
   }

   private void TileButton(Button button, XMaterial xMaterial, Color color) {
      button.material(xMaterial).name(Animation.wave(RESTARTING.get(), color, Colors.WHITE));
   }

   private void RestartButton(Button button) {
      button.material(XMaterial.EMERALD_BLOCK).name(Animation.fading(RESTART_BUTTON_TITLE.get(), Colors.GREEN, Colors.WHITE)).lore(RESTART_BUTTON_ACTION.get());
      button.action(actionType -> this.restarting = true);
   }

   @EventHandler
   public void update(UpdateEvent event) {
      if (event.getUpdateTime() == UpdateTime.TICK) {
         if (this.restarting) {
            if (this.green.size() == this.getCurrentSlots()) {
               Bukkit.shutdown();
            } else {
               int count = 0;

               while (count == 0 || this.green.contains(count)) {
                  count = new Random().nextInt(this.getCurrentSlots()) + 1;
               }

               if (this.yellow.contains(count)) {
                  this.yellow.remove(Integer.valueOf(count));
                  this.green.add(count);
               } else if (this.orange.contains(count)) {
                  this.orange.remove(Integer.valueOf(count));
                  this.yellow.add(count);
               } else if (this.red.contains(count)) {
                  this.red.remove(Integer.valueOf(count));
                  this.orange.add(count);
               } else {
                  this.red.add(count);
               }
            }
         }
      }
   }
}

package me.TechsCode.UltraPermissions.base.legacy.clickGUI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateEvent;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateTime;
import me.TechsCode.UltraPermissions.base.legacy.utils.Armorstands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public abstract class ClickGUI implements Listener {
   protected Player p;
   private SpigotTechPlugin plugin;
   private HashMap<FloatingElement, ArmorStand> items;
   private boolean opened;
   private double offsetDegree;
   private FloatingElement currentlyHovering;

   public ClickGUI(Player player, SpigotTechPlugin plugin) {
      this.p = player;
      this.plugin = plugin;
      this.items = new HashMap<>();
      this.opened = false;
   }

   @EventHandler
   public void draw(UpdateEvent event) {
      if (event.getUpdateTime() == UpdateTime.TWOTICKS) {
         this.redraw();
      }
   }

   protected void redraw() {
      if (this.isFixed()) {
         this.offsetDegree = this.p.getLocation().getYaw();
      }

      double closestDistance = 0.0;
      FloatingElement closestElement = null;

      for (Entry entry : this.items.entrySet()) {
         FloatingElement element = (FloatingElement)entry.getKey();
         Location target = this.p.getEyeLocation();
         Vector direction = this.p.getEyeLocation().getDirection().multiply(element.getRadius());
         target.add(direction);
         double angle = this.offsetDegree + 90.0 + element.getX() * 10.0;
         double radians = Math.toRadians(angle);
         double cos = Math.cos(radians);
         double yOffset = element.getY();
         double sin = Math.sin(radians);
         Vector offset = new Vector(cos, 0.0, sin);
         offset.multiply(element.getRadius());
         Location location = this.p.getEyeLocation();
         location.add(offset);
         location.subtract(0.0, yOffset, 0.0);
         if (element.isShown()) {
            double distance = location.distance(target);
            if (closestElement == null || distance < closestDistance) {
               closestDistance = distance;
               closestElement = (FloatingElement)entry.getKey();
            }
         }

         location.subtract(0.0, element.getYOffset(), 0.0);
         ArmorStand armorStand = (ArmorStand)entry.getValue();
         if (((FloatingElement)entry.getKey()).isShown()) {
            if (armorStand == null) {
               armorStand = (ArmorStand)location.getWorld().spawn(location, ArmorStand.class);
               armorStand.setGravity(false);
               armorStand.setVisible(false);
               armorStand.setBasePlate(false);
               entry.setValue(armorStand);
            } else {
               Armorstands.move(armorStand, location);
            }
         } else if (armorStand != null) {
            if (armorStand.getPassenger() != null) {
               armorStand.getPassenger().remove();
            }

            armorStand.remove();
            entry.setValue(null);
         }

         ((FloatingElement)entry.getKey()).apply(armorStand);
      }

      if (closestDistance < 0.5) {
         this.currentlyHovering = closestElement;
         this.currentlyHovering.hovering(true);
      } else {
         this.currentlyHovering = null;
      }

      this.items
         .keySet()
         .stream()
         .filter(floatingElement -> this.currentlyHovering == null || !floatingElement.equals(this.currentlyHovering))
         .forEach(floatingElement -> floatingElement.hovering(false));
   }

   protected void addItem(FloatingElement floatingElement) {
      this.items.put(floatingElement, null);
   }

   protected void removeItem(FloatingElement floatingElement) {
      ArmorStand armorStand = this.items.get(floatingElement);
      if (armorStand != null) {
         if (armorStand.getPassenger() != null) {
            armorStand.getPassenger().remove();
         }

         armorStand.remove();
      }

      this.items.remove(floatingElement);
   }

   protected FloatingElement[] getItems() {
      return this.items.keySet().toArray(new FloatingElement[this.items.size()]);
   }

   protected void clear() {
      Arrays.stream(this.getItems()).forEach(item -> this.removeItem(item));
   }

   protected void open() {
      if (!this.opened) {
         Bukkit.getPluginManager().registerEvents(this, (Plugin)this.plugin.getBootstrap());
         this.opened = true;
         this.offsetDegree = this.p.getLocation().getYaw();
      }
   }

   protected void close() {
      if (this.opened) {
         HandlerList.unregisterAll(this);
         Arrays.stream(this.getItems()).forEach(item -> this.removeItem(item));
      }
   }

   @EventHandler
   public void interact(PlayerInteractEvent event) {
      if (event.getPlayer().equals(this.p)) {
         if (event.getAction() == Action.LEFT_CLICK_AIR && this.currentlyHovering != null) {
            this.currentlyHovering.click();
         }
      }
   }

   protected FloatingElement getCursor() {
      return this.currentlyHovering;
   }

   public abstract boolean isFixed();
}

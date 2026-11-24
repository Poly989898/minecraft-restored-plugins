package net.Indyuce.mmocore.gui;

import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.EditableInventory;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.NextPageItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.PreviousPageItem;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import io.lumine.mythic.lib.util.lang3.Validate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerActivity;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.waypoint.Waypoint;
import net.Indyuce.mmocore.waypoint.WaypointPath;
import net.Indyuce.mmocore.waypoint.WaypointPathCalculation;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaypointViewer extends EditableInventory {
   private static final NamespacedKey WAYPOINT_ID_KEY = new NamespacedKey(MMOCore.plugin, "waypointId");

   public WaypointViewer() {
      super("waypoints");
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      if (function.equals("waypoint")) {
         return new WaypointViewer.WaypointItem(config);
      } else if (function.equals("previous")) {
         return new PreviousPageItem(config);
      } else {
         return function.equals("next") ? new NextPageItem(config) : null;
      }
   }

   public GeneratedInventory newInventory(PlayerData data) {
      return this.newInventory(data, null);
   }

   public GeneratedInventory newInventory(PlayerData data, Waypoint waypoint) {
      return new WaypointViewer.WaypointViewerInventory(data, waypoint);
   }

   public class WaypointItem extends InventoryItem<WaypointViewer.WaypointViewerInventory> {
      private final SimpleItem<WaypointViewer.WaypointViewerInventory> noWaypoint;
      private final SimpleItem<WaypointViewer.WaypointViewerInventory> locked;
      private final WaypointViewer.WaypointItemHandler availWaypoint;
      private final WaypointViewer.WaypointItemHandler noStellium;
      private final WaypointViewer.WaypointItemHandler notLinked;
      private final WaypointViewer.WaypointItemHandler currentWayPoint;

      public WaypointItem(ConfigurationSection config) {
         super(config);
         Validate.notNull(config.getConfigurationSection("no-waypoint"), "Could not load 'no-waypoint' config", new Object[0]);
         Validate.notNull(config.getConfigurationSection("locked"), "Could not load 'locked' config", new Object[0]);
         Validate.notNull(config.getConfigurationSection("not-a-destination"), "Could not load 'not-a-destination' config", new Object[0]);
         Validate.notNull(config.getConfigurationSection("current-waypoint"), "Could not load 'current-waypoint' config", new Object[0]);
         Validate.notNull(config.getConfigurationSection("not-enough-stellium"), "Could not load 'not-enough-stellium' config", new Object[0]);
         Validate.notNull(config.getConfigurationSection("display"), "Could not load 'display' config", new Object[0]);
         this.noWaypoint = new SimpleItem(config.getConfigurationSection("no-waypoint"));
         this.locked = new SimpleItem(config.getConfigurationSection("locked"));
         this.notLinked = WaypointViewer.this.new WaypointItemHandler(config.getConfigurationSection("not-a-destination"), true);
         this.currentWayPoint = WaypointViewer.this.new WaypointItemHandler(config.getConfigurationSection("current-waypoint"), true);
         this.noStellium = WaypointViewer.this.new WaypointItemHandler(config.getConfigurationSection("not-enough-stellium"), false);
         this.availWaypoint = WaypointViewer.this.new WaypointItemHandler(config.getConfigurationSection("display"), false);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public ItemStack getDisplayedItem(WaypointViewer.WaypointViewerInventory inv, int n) {
         int index = inv.getPageIndex(n);
         if (index >= inv.waypoints.size()) {
            return this.noWaypoint.getDisplayedItem(inv, n);
         } else {
            Waypoint waypoint = inv.waypoints.get(index);
            if (inv.current != null && inv.current.equals(waypoint)) {
               return this.currentWayPoint.getDisplayedItem(inv, n);
            } else if (!inv.playerData.hasWaypoint(waypoint)) {
               return this.locked.getDisplayedItem(inv, n);
            } else if (!inv.paths.containsKey(waypoint)) {
               return this.notLinked.getDisplayedItem(inv, n);
            } else {
               return inv.paths.get(waypoint).getCost() > inv.playerData.getStellium()
                  ? this.noStellium.getDisplayedItem(inv, n)
                  : this.availWaypoint.getDisplayedItem(inv, n);
            }
         }
      }

      public void onClick(@NotNull WaypointViewer.WaypointViewerInventory inv, @NotNull InventoryClickEvent event) {
         PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
         String tag = container.has(WaypointViewer.WAYPOINT_ID_KEY, PersistentDataType.STRING)
            ? (String)container.get(WaypointViewer.WAYPOINT_ID_KEY, PersistentDataType.STRING)
            : "";
         if (!tag.isEmpty()) {
            Waypoint waypoint = MMOCore.plugin.waypointManager.get(tag);
            if (!inv.playerData.hasWaypoint(waypoint)) {
               ConfigMessage.fromKey("not-unlocked-waypoint").send(inv.playerData);
            } else if (waypoint.equals(inv.current)) {
               ConfigMessage.fromKey("standing-on-waypoint").send(inv.playerData);
            } else if (inv.paths.get(waypoint) == null) {
               ConfigMessage.fromKey("cannot-teleport-to").send(inv.playerData);
            } else {
               double withdraw = inv.paths.get(waypoint).getCost();
               double left = withdraw - inv.playerData.getStellium();
               if (left > 0.0) {
                  ConfigMessage.fromKey("not-enough-stellium", "more", this.ONE_DIGIT.format(left)).send(inv.playerData);
               } else if (inv.playerData.getActivityTimeOut(PlayerActivity.USE_WAYPOINT) <= 0L) {
                  inv.getPlayer().closeInventory();
                  inv.playerData.warp(waypoint, withdraw);
               }
            }
         }
      }
   }

   public class WaypointItemHandler extends PhysicalItem<WaypointViewer.WaypointViewerInventory> {
      private final boolean onlyName;
      private final String splitter;
      private final String none;

      public WaypointItemHandler(ConfigurationSection config, boolean onlyName) {
         super(config);
         this.onlyName = onlyName;
         this.splitter = config.getString("format_path.splitter", ", ");
         this.none = config.getString("format_path.none", "None");
      }

      public void preprocessLore(@NotNull WaypointViewer.WaypointViewerInventory inv, int n, @NotNull List<String> lore) {
         Waypoint waypoint = inv.waypoints.get(inv.getPageIndex(n));
         if (waypoint != null) {
            int loreIdx = lore.indexOf("{lore}");
            if (loreIdx != -1) {
               lore.remove(loreIdx);
               lore.addAll(waypoint.getLore());
            }
         }
      }

      public ItemStack getDisplayedItem(WaypointViewer.WaypointViewerInventory inv, int n) {
         ItemStack item = super.getDisplayedItem(inv, n);
         ItemMeta meta = item.getItemMeta();
         Waypoint waypoint = inv.waypoints.get(inv.getPageIndex(n));
         PersistentDataContainer container = meta.getPersistentDataContainer();
         container.set(WaypointViewer.WAYPOINT_ID_KEY, PersistentDataType.STRING, waypoint.getId());
         item.setItemMeta(meta);
         return item;
      }

      public Placeholders getPlaceholders(WaypointViewer.WaypointViewerInventory inv, int n) {
         Placeholders holders = new Placeholders();
         Waypoint waypoint = inv.waypoints.get(inv.getPageIndex(n));
         holders.register("name", waypoint.getName());
         if (!this.onlyName) {
            holders.register("current_cost", inv.paths.get(waypoint).getCost());
            holders.register(
               "normal_cost", this.ONE_DIGIT.format(inv.paths.containsKey(waypoint) ? inv.paths.get(waypoint).getCost() : Double.POSITIVE_INFINITY)
            );
            holders.register("dynamic_cost", this.ONE_DIGIT.format(waypoint.getDynamicCost()));
            holders.register(
               "intermediary_waypoints",
               inv.paths.containsKey(waypoint) ? inv.paths.get(waypoint).displayIntermediaryWayPoints(this.splitter, this.none) : this.none
            );
         }

         return holders;
      }
   }

   public class WaypointViewerInventory extends GeneratedInventory {
      private final List<Waypoint> waypoints = new ArrayList<>(MMOCore.plugin.waypointManager.getAll());
      @Nullable
      private final Waypoint current;
      private final PlayerData playerData;
      private Map<Waypoint, WaypointPath> paths;

      public WaypointViewerInventory(PlayerData playerData, Waypoint current) {
         super(new Navigator(playerData.getMMOPlayerData()), WaypointViewer.this);
         this.playerData = playerData;
         this.current = current;
         this.paths = new WaypointPathCalculation(playerData).run(current).getPaths();
         this.enablePagination(WaypointViewer.this.getByFunction("waypoint").getSlots().size());
      }

      public int getMaxPage() {
         return this.computeMaxPage(this.waypoints.size());
      }

      @Deprecated
      public boolean isDynamicUse() {
         return this.current == null;
      }
   }
}

package net.Indyuce.mmocore.gui.skilltree;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.EditableInventory;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.ItemOptions;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.SoundEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.skilltree.IntegerCoordinates;
import net.Indyuce.mmocore.skilltree.NodeState;
import net.Indyuce.mmocore.skilltree.NodeType;
import net.Indyuce.mmocore.skilltree.ParentType;
import net.Indyuce.mmocore.skilltree.PathType;
import net.Indyuce.mmocore.skilltree.SkillTreeNode;
import net.Indyuce.mmocore.skilltree.SkillTreePath;
import net.Indyuce.mmocore.skilltree.display.DisplayInfo;
import net.Indyuce.mmocore.skilltree.display.NodeDisplayInfo;
import net.Indyuce.mmocore.skilltree.display.PathDisplayInfo;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;
import net.Indyuce.mmocore.util.Icon;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillTreeViewer extends EditableInventory {
   protected final Map<DisplayInfo, Icon> icons = new HashMap<>();
   protected final Map<NodeState, String> statusNames = new HashMap<>();
   @Nullable
   private final SkillTree defaultSkillTree;

   public SkillTreeViewer() {
      super("skill-tree");
      this.defaultSkillTree = null;
   }

   public SkillTreeViewer(SkillTree initialSkillTree, boolean isDefault) {
      super("specific-skill-tree-" + (isDefault ? "default" : UtilityMethods.ymlName(initialSkillTree.getId())));
      this.defaultSkillTree = initialSkillTree;
   }

   public void reload(@NotNull JavaPlugin plugin, @NotNull ConfigurationSection config) {
      super.reload(plugin, config);
      if (config.contains("status-names")) {
         for (NodeState nodeState : NodeState.values()) {
            this.statusNames.put(nodeState, config.getString("status-names." + UtilityMethods.ymlName(nodeState.name()), nodeState.name()));
         }
      }

      for (NodeState status : NodeState.values()) {
         for (PathType pathType : PathType.values()) {
            String configPath = "display.paths." + MMOCoreUtils.ymlName(status.name()) + "." + MMOCoreUtils.ymlName(pathType.name());
            if (!config.contains(configPath)) {
               MMOCore.log(
                  Level.WARNING,
                  "An error occurred while loading skill tree GUI: Missing path type: "
                     + MMOCoreUtils.ymlName(pathType.name())
                     + " for status: "
                     + MMOCoreUtils.ymlName(status.name())
               );
            } else {
               this.icons.put(new PathDisplayInfo(pathType, status), Icon.from(config.get(configPath)));
            }
         }
      }

      for (NodeState status : NodeState.values()) {
         for (NodeType nodeType : NodeType.values()) {
            String configPath = "display.nodes." + MMOCoreUtils.ymlName(status.name()) + "." + MMOCoreUtils.ymlName(nodeType.name());
            if (!config.contains(configPath)) {
               MMOCore.log(
                  Level.WARNING,
                  "An error occurred while loading skill tree GUI: Missing node type: "
                     + MMOCoreUtils.ymlName(nodeType.name())
                     + " for status: "
                     + MMOCoreUtils.ymlName(status.name())
               );
            } else {
               this.icons.put(new NodeDisplayInfo(nodeType, status), Icon.from(config.get(configPath)));
            }
         }
      }
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      if (function.equals("skill-tree")) {
         return new SkillTreeViewer.SkillTreeItem(config);
      } else if (function.equals("up")) {
         return new SkillTreeViewer.UpArrow(config);
      } else if (function.equals("left")) {
         return new SkillTreeViewer.LeftArrow(config);
      } else if (function.equals("down")) {
         return new SkillTreeViewer.DownArrow(config);
      } else if (function.equals("right")) {
         return new SkillTreeViewer.RightArrow(config);
      } else if (function.equals("reallocation")) {
         return new SkillTreeViewer.ReallocateButton(config);
      } else if (function.equals("skill-tree-node")) {
         return new SkillTreeViewer.SkillTreeNodeItem(config);
      } else if (function.equals("next-tree-list-page")) {
         return new SkillTreeViewer.NextTreeListPageItem(config);
      } else {
         return function.equals("previous-tree-list-page") ? new SkillTreeViewer.PreviousTreeListPageItem(config) : null;
      }
   }

   public SkillTreeViewer.SkillTreeInventory newInventory(PlayerData playerData) {
      return new SkillTreeViewer.SkillTreeInventory(playerData, this.defaultSkillTree);
   }

   public class DownArrow extends SimpleItem<SkillTreeViewer.SkillTreeInventory> {
      public DownArrow(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull SkillTreeViewer.SkillTreeInventory inv, @NotNull InventoryClickEvent event) {
         inv.y = inv.y + MMOCore.plugin.configManager.skillTreeScrollStepY;
         inv.open();
      }
   }

   public class LeftArrow extends SimpleItem<SkillTreeViewer.SkillTreeInventory> {
      public LeftArrow(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull SkillTreeViewer.SkillTreeInventory inv, @NotNull InventoryClickEvent event) {
         inv.x = inv.x - MMOCore.plugin.configManager.skillTreeScrollStepX;
         inv.open();
      }
   }

   public class NextTreeListPageItem extends SimpleItem<SkillTreeViewer.SkillTreeInventory> {
      public NextTreeListPageItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull SkillTreeViewer.SkillTreeInventory inv, @NotNull InventoryClickEvent event) {
         inv.treeListPage++;
         inv.open();
      }

      public boolean isDisplayed(SkillTreeViewer.SkillTreeInventory inv) {
         return inv.getTreeListPage() < inv.getMaxTreeListPage();
      }
   }

   public class PreviousTreeListPageItem extends SimpleItem<SkillTreeViewer.SkillTreeInventory> {
      public PreviousTreeListPageItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull SkillTreeViewer.SkillTreeInventory inv, @NotNull InventoryClickEvent event) {
         inv.treeListPage--;
         inv.open();
      }

      public boolean isDisplayed(SkillTreeViewer.SkillTreeInventory inv) {
         return inv.getTreeListPage() > 0;
      }
   }

   public class ReallocateButton extends PhysicalItem<SkillTreeViewer.SkillTreeInventory> {
      public ReallocateButton(ConfigurationSection config) {
         super(config);
      }

      public Placeholders getPlaceholders(SkillTreeViewer.SkillTreeInventory inv, int n) {
         Placeholders holders = new Placeholders();
         holders.register("skill-tree-points", inv.playerData.getSkillTreePoints(inv.getSkillTree().getId()));
         holders.register("global-points", inv.playerData.getSkillTreePoints("global"));
         holders.register("realloc-points", inv.playerData.getSkillTreeReallocationPoints());
         int maxPointSpent = inv.getSkillTree().getMaxPointSpent();
         holders.register("max-point-spent", maxPointSpent == Integer.MAX_VALUE ? "∞" : maxPointSpent);
         holders.register("point-spent", inv.playerData.getPointsSpent(inv.getSkillTree()));
         return holders;
      }

      public void onClick(@NotNull SkillTreeViewer.SkillTreeInventory inv, @NotNull InventoryClickEvent event) {
         int spent = inv.playerData.getPointsSpent(inv.skillTree);
         if (spent < 1) {
            ConfigMessage.fromKey("no-skill-tree-points-spent").send(inv.playerData);
            MMOCore.plugin.soundManager.getSound(SoundEvent.NOT_ENOUGH_POINTS).playTo(inv.getPlayer());
         } else if (inv.playerData.getSkillTreeReallocationPoints() <= 0) {
            ConfigMessage.fromKey("not-skill-tree-reallocation-point").send(inv.playerData);
            MMOCore.plugin.soundManager.getSound(SoundEvent.NOT_ENOUGH_POINTS).playTo(inv.getPlayer());
         } else {
            int reallocated = inv.playerData.getPointsSpent(inv.skillTree);
            inv.playerData.giveSkillTreePoints(inv.skillTree.getId(), reallocated);
            inv.playerData.giveSkillTreeReallocationPoints(-1);
            inv.playerData.resetSkillTree(inv.skillTree);
            inv.skillTree.setupNodeStates(inv.playerData);
            ConfigMessage.fromKey(
                  "reallocated-points", "points", inv.playerData.getSkillTreePoints(inv.skillTree.getId()), "skill-tree", inv.skillTree.getName()
               )
               .send(inv.playerData);
            MMOCore.plugin.soundManager.getSound(SoundEvent.RESET_SKILL_TREE).playTo(inv.getPlayer());
            inv.open();
         }
      }
   }

   public class RightArrow extends SimpleItem<SkillTreeViewer.SkillTreeInventory> {
      public RightArrow(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull SkillTreeViewer.SkillTreeInventory inv, @NotNull InventoryClickEvent event) {
         inv.x = inv.x + MMOCore.plugin.configManager.skillTreeScrollStepX;
         inv.open();
      }
   }

   public class SkillTreeInventory extends GeneratedInventory {
      private final int width;
      private final int height;
      private final int maxTreeListPage;
      private final List<SkillTree> skillTrees;
      private final List<Integer> slots;
      private final PlayerData playerData;
      @NotNull
      private SkillTree skillTree;
      private int treeListPage;
      private int x;
      private int y;
      private int minSlot;
      private int maxSlot;

      public SkillTreeInventory(PlayerData playerData, SkillTree skillTree) {
         super(new Navigator(playerData.getMMOPlayerData()), SkillTreeViewer.this);
         this.playerData = playerData;
         this.skillTrees = playerData.getProfess().getSkillTrees();
         this.skillTree = skillTree == null ? this.skillTrees.get(0) : skillTree;
         if (skillTree == null) {
            this.maxTreeListPage = (this.skillTrees.size() - 1) / SkillTreeViewer.this.getByFunction("skill-tree").getSlots().size();
         } else {
            this.maxTreeListPage = 0;
         }

         this.slots = SkillTreeViewer.this.getByFunction("skill-tree-node").getSlots();
         this.minSlot = 64;
         this.maxSlot = 0;

         for (int slot : this.slots) {
            if (slot < this.minSlot) {
               this.minSlot = slot;
            }

            if (slot > this.maxSlot) {
               this.maxSlot = slot;
            }
         }

         this.width = (this.maxSlot - this.minSlot) % 9;
         this.height = (this.maxSlot - this.minSlot) / 9;
         this.x = this.x - this.width / 2;
         this.y = this.y - this.height / 2;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getTreeListPage() {
         return this.treeListPage;
      }

      public int getMaxTreeListPage() {
         return this.maxTreeListPage;
      }

      public PlayerData getPlayerData() {
         return this.playerData;
      }

      public Icon getIcon(IntegerCoordinates coordinates) {
         if (this.skillTree.isNode(coordinates)) {
            SkillTreeNode node = this.skillTree.getNode(coordinates);
            NodeType nodeType = node.getNodeType();
            NodeState nodeState = this.playerData.getNodeState(node);
            if (node.hasIcon(nodeState)) {
               return node.getIcon(nodeState);
            }

            DisplayInfo displayInfo = new NodeDisplayInfo(nodeType, nodeState);
            if (this.skillTree.hasIcon(displayInfo)) {
               return this.skillTree.getIcon(displayInfo);
            }

            Icon icon = SkillTreeViewer.this.icons.get(displayInfo);
            Validate.notNull(icon, "The node " + node.getFullId() + " has no icon for the type " + nodeType + " and the status " + nodeState);
            return icon;
         } else {
            SkillTreePath path = this.skillTree.getPath(coordinates);
            PathType pathType = path.getPathType();
            NodeState pathStatus = path.getStatus(this.playerData);
            DisplayInfo displayInfo = new PathDisplayInfo(pathType, pathStatus);
            if (this.skillTree.hasIcon(displayInfo)) {
               return this.skillTree.getIcon(displayInfo);
            }

            Icon icon = SkillTreeViewer.this.icons.get(displayInfo);
            Validate.notNull(icon, "There is no icon for the path type " + pathType + " and the status " + pathStatus);
            return icon;
         }
      }

      @NotNull
      public String getRawName() {
         return this.guiName.replace("{skill-tree-name}", this.skillTree.getName()).replace("{skill-tree-id}", this.skillTree.getId());
      }

      public IntegerCoordinates getCoordinates(int n) {
         int slot = this.slots.get(n);
         int deltaX = (slot - this.minSlot) % 9;
         int deltaY = (slot - this.minSlot) / 9;
         return new IntegerCoordinates(this.getX() + deltaX, this.getY() + deltaY);
      }

      public SkillTreeNode getNode(int n) {
         return this.getSkillTree().getNode(this.getCoordinates(n));
      }

      public SkillTree getSkillTree() {
         return this.skillTree;
      }

      public int getMinSlot() {
         return this.minSlot;
      }
   }

   public class SkillTreeItem extends PhysicalItem<SkillTreeViewer.SkillTreeInventory> {
      public SkillTreeItem(ConfigurationSection config) {
         super(config);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public void preprocessLore(@NotNull SkillTreeViewer.SkillTreeInventory inv, int n, @NotNull List<String> lore) {
         int index = inv.getEditable().getByFunction("skill-tree").getSlots().size() * inv.treeListPage + n;
         if (inv.skillTrees.size() > index) {
            SkillTree skillTree = inv.skillTrees.get(index);
            int loreIdx = lore.indexOf("{tree-lore}");
            if (loreIdx != -1) {
               lore.remove(loreIdx);
               lore.addAll(loreIdx, skillTree.getLore());
            }
         }
      }

      public ItemStack getDisplayedItem(SkillTreeViewer.SkillTreeInventory inv, int n) {
         int index = inv.getEditable().getByFunction("skill-tree").getSlots().size() * inv.treeListPage + n;
         if (inv.skillTrees.size() <= index) {
            return null;
         }

         SkillTree skillTree = inv.skillTrees.get(index);
         ItemStack item = super.getDisplayedItem(inv, ItemOptions.material(n, skillTree.getItem()));
         ItemMeta meta = item.getItemMeta();
         meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         meta.setDisplayName(skillTree.getName());
         meta.setCustomModelData(skillTree.getCustomModelData());
         PersistentDataContainer container = meta.getPersistentDataContainer();
         container.set(new NamespacedKey(MMOCore.plugin, "skill-tree-id"), PersistentDataType.STRING, skillTree.getId());
         item.setItemMeta(meta);
         return item;
      }

      public Placeholders getPlaceholders(SkillTreeViewer.SkillTreeInventory inv, int n) {
         int index = inv.getEditable().getByFunction("skill-tree").getSlots().size() * inv.treeListPage + n;
         SkillTree skillTree = inv.skillTrees.get(index);
         Placeholders holders = new Placeholders();
         holders.register("name", skillTree.getName());
         holders.register("id", skillTree.getId());
         int maxPointSpent = inv.getSkillTree().getMaxPointSpent();
         holders.register("max-point-spent", maxPointSpent == Integer.MAX_VALUE ? "∞" : maxPointSpent);
         holders.register("point-spent", inv.playerData.getPointsSpent(inv.getSkillTree()));
         holders.register("skill-tree-points", inv.playerData.getSkillTreePoints(inv.getSkillTree().getId()));
         holders.register("global-points", inv.playerData.getSkillTreePoints("global"));
         return holders;
      }

      public void onClick(@NotNull SkillTreeViewer.SkillTreeInventory inv, @NotNull InventoryClickEvent event) {
         String id = (String)event.getCurrentItem()
            .getItemMeta()
            .getPersistentDataContainer()
            .get(new NamespacedKey(MMOCore.plugin, "skill-tree-id"), PersistentDataType.STRING);
         MMOCore.plugin.soundManager.getSound(SoundEvent.CHANGE_SKILL_TREE).playTo(inv.getPlayer());
         inv.skillTree = MMOCore.plugin.skillTreeManager.get(id);
         inv.open();
      }
   }

   public class SkillTreeNodeItem extends PhysicalItem<SkillTreeViewer.SkillTreeInventory> {
      private final List<String> pathLore = new ArrayList<>();

      public SkillTreeNodeItem(ConfigurationSection config) {
         super(config);
         if (config.isList("path-lore")) {
            this.pathLore.addAll(config.getStringList("path-lore"));
         }
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public void preprocessLore(@NotNull SkillTreeViewer.SkillTreeInventory inv, int n, @NotNull List<String> lore) {
         IntegerCoordinates coordinates = inv.getCoordinates(n);
         if (inv.getSkillTree().isNode(coordinates)) {
            SkillTreeNode node = inv.getSkillTree().getNode(coordinates);
            int i = 0;

            while (i < lore.size()) {
               String str = lore.get(i);
               if (str.contains("{node-lore}")) {
                  lore.remove(i);
                  List<String> shaded = node.getLore(inv.playerData);
                  int _i = i;
                  shaded.forEach(s -> lore.add(_i, str.replace("{node-lore}", s)));
                  i += shaded.size();
               } else if (str.contains("{strong-parents}")) {
                  lore.remove(i);
                  List<String> shaded = this.getParentsLore(inv, node, node.getParents(ParentType.STRONG));
                  lore.addAll(i, shaded);
                  i += shaded.size();
               } else if (str.contains("{soft-parents}")) {
                  lore.remove(i);
                  List<String> shaded = this.getParentsLore(inv, node, node.getParents(ParentType.SOFT));
                  lore.addAll(i, shaded);
                  i += shaded.size();
               } else if (str.contains("{incompatible-parents}")) {
                  lore.remove(i);
                  List<String> shaded = this.getParentsLore(inv, node, node.getParents(ParentType.INCOMPATIBLE));
                  lore.addAll(i, shaded);
                  i += shaded.size();
               } else {
                  i++;
               }
            }
         }
      }

      public ItemStack getDisplayedItem(SkillTreeViewer.SkillTreeInventory inv, int n) {
         IntegerCoordinates coordinates = inv.getCoordinates(n);
         if (!inv.getSkillTree().isPathOrNode(coordinates)) {
            return new ItemStack(Material.AIR);
         }

         Icon icon = inv.getIcon(coordinates);
         ItemStack item = super.getDisplayedItem(inv, icon.toItemOptions(n));
         ItemMeta meta = item.getItemMeta();
         if (inv.getSkillTree().isNode(coordinates)) {
            SkillTreeNode node = inv.getSkillTree().getNode(coordinates);
            if (!meta.hasDisplayName() || meta.getDisplayName().isEmpty()) {
               meta.setDisplayName(node.getName());
            }
         } else {
            meta.setLore(new ArrayList<>(this.pathLore));
            meta.setDisplayName(" ");
         }

         meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         PersistentDataContainer container = meta.getPersistentDataContainer();
         container.set(new NamespacedKey(MMOCore.plugin, "coordinates.x"), PersistentDataType.INTEGER, coordinates.getX());
         container.set(new NamespacedKey(MMOCore.plugin, "coordinates.y"), PersistentDataType.INTEGER, coordinates.getY());
         item.setItemMeta(meta);
         return item;
      }

      public List<String> getParentsLore(SkillTreeViewer.SkillTreeInventory inv, SkillTreeNode node, Collection<SkillTreeNode> parents) {
         List<String> lore = new ArrayList<>();

         for (SkillTreeNode parent : parents) {
            int level = inv.playerData.getNodeLevel(parent);
            ChatColor color = level >= node.getParentNeededLevel(parent) ? ChatColor.GREEN : ChatColor.RED;
            lore.add(ChatColor.GRAY + "◆" + parent.getName() + ": " + color + node.getParentNeededLevel(parent));
         }

         return lore;
      }

      public Placeholders getPlaceholders(SkillTreeViewer.SkillTreeInventory inv, int n) {
         Placeholders holders = new Placeholders();
         holders.register("skill-tree", inv.getSkillTree().getName());
         boolean isNode = inv.getSkillTree().isNode(inv.getCoordinates(n));
         if (isNode) {
            SkillTreeNode node = inv.getNode(n);
            holders.register("current-level", inv.playerData.getNodeLevel(node));
            NodeState status = inv.playerData.getNodeState(node);
            holders.register("current-state", SkillTreeViewer.this.statusNames.getOrDefault(status, status.name()));
            holders.register("max-level", node.getMaxLevel());
            holders.register("name", node.getName());
            holders.register("max-children", node.getMaxChildren());
            holders.register("point-consumed", node.getPointConsumption());
            holders.register("display-type", node.getNodeType());
         } else {
            holders.register("display-type", inv.skillTree.getPath(inv.getCoordinates(n)).getPathType());
         }

         int maxPointSpent = inv.getSkillTree().getMaxPointSpent();
         holders.register("max-point-spent", maxPointSpent == Integer.MAX_VALUE ? "∞" : maxPointSpent);
         holders.register("point-spent", inv.playerData.getPointsSpent(inv.getSkillTree()));
         holders.register("skill-tree-points", inv.playerData.getSkillTreePoints(inv.getSkillTree().getId()));
         holders.register("global-points", inv.playerData.getSkillTreePoints("global"));
         return holders;
      }

      public void onClick(@NotNull SkillTreeViewer.SkillTreeInventory inv, @NotNull InventoryClickEvent event) {
         if (event.getClick() == ClickType.LEFT) {
            PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
            int x = (Integer)container.get(new NamespacedKey(MMOCore.plugin, "coordinates.x"), PersistentDataType.INTEGER);
            int y = (Integer)container.get(new NamespacedKey(MMOCore.plugin, "coordinates.y"), PersistentDataType.INTEGER);
            if (inv.skillTree.isNode(new IntegerCoordinates(x, y))) {
               SkillTreeNode node = inv.skillTree.getNode(new IntegerCoordinates(x, y));
               if (inv.playerData.getPointsSpent(inv.skillTree) >= inv.skillTree.getMaxPointSpent()) {
                  ConfigMessage.fromKey("max-points-reached").send(inv.playerData);
                  MMOCore.plugin.soundManager.getSound(SoundEvent.NOT_ENOUGH_POINTS).playTo(inv.getPlayer());
               } else {
                  switch (inv.playerData.canIncrementNodeLevel(node)) {
                     case SUCCESS:
                        inv.playerData.incrementNodeLevel(node);
                        ConfigMessage.fromKey("upgrade-skill-node", "skill-node", node.getName(), "level", inv.playerData.getNodeLevel(node))
                           .send(inv.playerData);
                        MMOCore.plugin.soundManager.getSound(SoundEvent.LEVEL_SKILL_TREE_NODE).playTo(inv.getPlayer());
                        inv.open();
                        break;
                     case PERMISSION_DENIED:
                        ConfigMessage.fromKey("missing-skill-node-permission").send(inv.playerData);
                        MMOCore.plugin.soundManager.getSound(SoundEvent.NOT_ENOUGH_POINTS).playTo(inv.getPlayer());
                        break;
                     case LOCKED_NODE:
                        ConfigMessage.fromKey("locked-node").send(inv.playerData);
                        MMOCore.plugin.soundManager.getSound(SoundEvent.NOT_ENOUGH_POINTS).playTo(inv.getPlayer());
                        break;
                     case MAX_LEVEL_REACHED:
                        ConfigMessage.fromKey("skill-node-max-level-hit").send(inv.playerData);
                        MMOCore.plugin.soundManager.getSound(SoundEvent.NOT_ENOUGH_POINTS).playTo(inv.getPlayer());
                        break;
                     case NOT_ENOUGH_POINTS:
                        ConfigMessage.fromKey("not-enough-skill-tree-points", "point", node.getPointConsumption()).send(inv.playerData);
                        MMOCore.plugin.soundManager.getSound(SoundEvent.NOT_ENOUGH_POINTS).playTo(inv.getPlayer());
                  }
               }
            }
         }
      }
   }

   public class UpArrow extends SimpleItem<SkillTreeViewer.SkillTreeInventory> {
      public UpArrow(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull SkillTreeViewer.SkillTreeInventory inv, @NotNull InventoryClickEvent event) {
         inv.y = inv.y - MMOCore.plugin.configManager.skillTreeScrollStepY;
         inv.open();
      }
   }
}

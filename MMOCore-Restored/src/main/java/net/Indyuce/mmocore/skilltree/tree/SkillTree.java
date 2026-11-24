package net.Indyuce.mmocore.skilltree.tree;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.manager.registry.RegisteredObject;
import net.Indyuce.mmocore.skilltree.IntegerCoordinates;
import net.Indyuce.mmocore.skilltree.NodeState;
import net.Indyuce.mmocore.skilltree.NodeType;
import net.Indyuce.mmocore.skilltree.ParentInformation;
import net.Indyuce.mmocore.skilltree.ParentType;
import net.Indyuce.mmocore.skilltree.PathType;
import net.Indyuce.mmocore.skilltree.SkillTreeNode;
import net.Indyuce.mmocore.skilltree.SkillTreePath;
import net.Indyuce.mmocore.skilltree.display.DisplayInfo;
import net.Indyuce.mmocore.skilltree.display.NodeDisplayInfo;
import net.Indyuce.mmocore.skilltree.display.PathDisplayInfo;
import net.Indyuce.mmocore.util.Icon;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SkillTree implements RegisteredObject {
   private final String id;
   private final String name;
   private final List<String> lore = new ArrayList<>();
   private final Material item;
   private final int customModelData;
   protected final Map<String, SkillTreeNode> nodes = new HashMap<>();
   protected final int maxPointSpent;
   protected final List<SkillTreeNode> roots = new ArrayList<>();
   protected final Map<DisplayInfo, Icon> icons = new HashMap<>();
   protected final Map<IntegerCoordinates, SkillTreeNode> coordNodes = new HashMap<>();
   protected final Map<IntegerCoordinates, SkillTreePath> coordPaths = new HashMap<>();

   public SkillTree(@NotNull ConfigurationSection config) {
      this.id = Objects.requireNonNull(config.getString("id"), "Could not find skill tree id");
      this.name = MythicLib.plugin.parseColors(Objects.requireNonNull(config.getString("name"), "Could not find skill tree name"));
      Objects.requireNonNull(config.getStringList("lore"), "Could not find skill tree lore").forEach(str -> this.lore.add(MythicLib.plugin.parseColors(str)));
      this.item = Material.valueOf(UtilityMethods.enumName(Objects.requireNonNull(config.getString("item"), "Could not find item")));
      this.customModelData = config.getInt("custom-model-data", 0);
      Validate.isTrue(config.isConfigurationSection("nodes"), "Could not find any nodes in the tree");
      this.maxPointSpent = config.getInt("max-point-spent", Integer.MAX_VALUE);

      for (String key : config.getConfigurationSection("nodes").getKeys(false)) {
         try {
            ConfigurationSection section = config.getConfigurationSection("nodes." + key);
            SkillTreeNode node = new SkillTreeNode(this, section);
            this.nodes.put(node.getId(), node);
            this.coordNodes.put(node.getCoordinates(), node);
            if (node.isRoot()) {
               this.roots.add(node);
            }
         } catch (Exception e) {
            MMOCore.log("Couldn't load skill tree node " + this.id + "." + key + ": " + e.getMessage());
         }
      }

      for (String from : config.getConfigurationSection("nodes").getKeys(false)) {
         ConfigurationSection section = config.getConfigurationSection("nodes." + from);
         if (section.contains("paths")) {
            for (String to : section.getConfigurationSection("paths").getKeys(false)) {
               SkillTreeNode toNode = this.nodes.get(to);
               if (toNode == null) {
                  MMOCore.log("Couldn't find node " + to + " for path in node " + from + ".");
               } else {
                  for (String pathKey : section.getConfigurationSection("paths." + to).getKeys(false)) {
                     IntegerCoordinates coordinates = IntegerCoordinates.from(section.get("paths." + to + "." + pathKey));
                     this.coordPaths.put(coordinates, new SkillTreePath(this, coordinates, this.nodes.get(from), toNode));
                  }
               }
            }
         }
      }

      for (NodeState status : NodeState.values()) {
         for (PathType pathType : PathType.values()) {
            try {
               String configPath = "display.paths." + MMOCoreUtils.ymlName(status.name()) + "." + MMOCoreUtils.ymlName(pathType.name());
               this.icons.put(new PathDisplayInfo(pathType, status), Icon.from(config.get(configPath)));
            } catch (Exception exception) {
            }
         }
      }

      for (NodeState status : NodeState.values()) {
         Object anyType = config.get("display.nodes." + MMOCoreUtils.ymlName(status.name()));
         if (anyType != null) {
            for (NodeType nodeType : NodeType.values()) {
               this.icons.put(new NodeDisplayInfo(nodeType, status), Icon.from(anyType));
            }
         } else {
            for (NodeType nodeType : NodeType.values()) {
               try {
                  String configPath = "display.nodes." + MMOCoreUtils.ymlName(status.name()) + "." + MMOCoreUtils.ymlName(nodeType.name());
                  this.icons.put(new NodeDisplayInfo(nodeType, status), Icon.from(config.get(configPath)));
               } catch (Exception exception) {
               }
            }
         }
      }

      for (SkillTreeNode node : this.nodes.values()) {
         try {
            if (config.isConfigurationSection("nodes." + node.getId() + ".parents")) {
               for (String key : config.getConfigurationSection("nodes." + node.getId() + ".parents").getKeys(false)) {
                  ConfigurationSection section = config.getConfigurationSection("nodes." + node.getId() + ".parents." + key);
                  if (section != null) {
                     ParentType parentType = ParentType.valueOf(UtilityMethods.enumName(key));

                     for (String parentId : section.getKeys(false)) {
                        SkillTreeNode parent = this.getNode(parentId);
                        int level = section.getInt(parentId);
                        node.addParent(parent, parentType, level);
                        parent.addChild(node, parentType, level);
                     }
                  }
               }
            }
         } catch (RuntimeException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load parents of skill tree node '" + node.getId() + "': " + exception.getMessage());
         }
      }
   }

   public List<String> getLore() {
      return this.lore;
   }

   public int getMaxPointSpent() {
      return this.maxPointSpent;
   }

   public int getCustomModelData() {
      return this.customModelData;
   }

   @Deprecated
   public static SkillTree loadSkillTree(ConfigurationSection config) {
      return MMOCore.plugin.skillTreeManager.loadSkillTree(config);
   }

   public void addRoot(@NotNull SkillTreeNode node) {
      this.roots.add(node);
   }

   @NotNull
   public List<SkillTreeNode> getRoots() {
      return this.roots;
   }

   public void setupNodeStates(@NotNull PlayerData playerData) {
      playerData.clearNodeStates(this);
      boolean skillTreeLocked = playerData.getPointsSpent(this) >= this.maxPointSpent;
      NodeState lockState = skillTreeLocked ? NodeState.FULLY_LOCKED : NodeState.LOCKED;

      for (SkillTreeNode node : this.nodes.values()) {
         playerData.setNodeState(node, playerData.getNodeLevel(node) > 0 ? NodeState.UNLOCKED : lockState);
      }

      if (!skillTreeLocked) {
         Stack<SkillTreeNode> unreachable = new Stack<>();
         Set<SkillTreeNode> updated = new HashSet<>();

         for (SkillTreeNode node : this.nodes.values()) {
            for (ParentInformation parent : node.getParents()) {
               if (parent.getType() == ParentType.INCOMPATIBLE && playerData.getNodeState(parent.getNode()) == NodeState.UNLOCKED) {
                  unreachable.add(node);
                  break;
               }
            }

            int maxChildren = node.getMaxChildren();
            if (maxChildren > 0) {
               int unlocked = 0;
               List<SkillTreeNode> locked = new ArrayList<>();

               for (ParentInformation child : node.getChildren()) {
                  switch (playerData.getNodeState(child.getNode())) {
                     case LOCKED:
                        locked.add(child.getNode());
                        break;
                     case UNLOCKED:
                        unlocked++;
                  }
               }

               if (unlocked >= maxChildren) {
                  unreachable.addAll(locked);
               }
            }
         }

         while (!unreachable.empty()) {
            SkillTreeNode node = unreachable.pop();
            updated.add(node);
            playerData.setNodeState(node, NodeState.FULLY_LOCKED);

            for (ParentInformation child : node.getChildren()) {
               if (!updated.contains(child.getNode()) && this.isUnreachable(child.getNode(), playerData)) {
                  unreachable.push(child.getNode());
               }
            }
         }

         label114:
         for (SkillTreeNode node : this.nodes.values()) {
            if (playerData.getNodeState(node) == NodeState.LOCKED) {
               if (node.isRoot()) {
                  playerData.setNodeState(node, NodeState.UNLOCKABLE);
               } else {
                  boolean soft = false;
                  boolean hasSoft = false;

                  for (ParentInformation parent : node.getParents()) {
                     if (parent.getType() == ParentType.STRONG && playerData.getNodeLevel(parent.getNode()) < parent.getLevel()) {
                        continue label114;
                     }

                     if (!soft && parent.getType() == ParentType.SOFT) {
                        hasSoft = true;
                        if (playerData.getNodeLevel(parent.getNode()) >= parent.getLevel()) {
                           soft = true;
                        }
                     }
                  }

                  if (!hasSoft || soft) {
                     playerData.setNodeState(node, NodeState.UNLOCKABLE);
                  }
               }
            }
         }

         for (SkillTreeNode node : this.nodes.values()) {
            if (playerData.getNodeState(node) == NodeState.UNLOCKED && playerData.getNodeLevel(node) >= node.getMaxLevel()) {
               playerData.setNodeState(node, NodeState.MAXED_OUT);
            }
         }
      }
   }

   private boolean isUnreachable(@NotNull SkillTreeNode node, @NotNull PlayerData playerData) {
      boolean soft = false;
      boolean hasSoft = false;

      for (ParentInformation parent : node.getParents()) {
         if (parent.getType() == ParentType.STRONG && playerData.getNodeState(parent.getNode()) == NodeState.FULLY_LOCKED) {
            return true;
         }

         if (!soft && parent.getType() == ParentType.SOFT) {
            hasSoft = true;
            if (playerData.getNodeState(parent.getNode()) != NodeState.FULLY_LOCKED) {
               soft = true;
            }
         }
      }

      return hasSoft && !soft;
   }

   public boolean isNode(@NotNull IntegerCoordinates coordinates) {
      return this.coordNodes.containsKey(coordinates);
   }

   public boolean isPath(@NotNull IntegerCoordinates coordinates) {
      return this.coordPaths.containsKey(coordinates);
   }

   public boolean isPathOrNode(IntegerCoordinates coordinates) {
      return this.isNode(coordinates) || this.isPath(coordinates);
   }

   public Material getItem() {
      return this.item;
   }

   @Override
   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Collection<SkillTreeNode> getNodes() {
      return this.nodes.values();
   }

   @NotNull
   public SkillTreeNode getNode(@NotNull IntegerCoordinates coords) {
      return Objects.requireNonNull(this.coordNodes.get(coords), "Could not find node in tree '" + this.id + "' with coordinates '" + coords + "'");
   }

   @Nullable
   public SkillTreeNode getNodeOrNull(@NotNull IntegerCoordinates coords) {
      return this.coordNodes.get(coords);
   }

   @NotNull
   public SkillTreePath getPath(@NotNull IntegerCoordinates coords) {
      return Objects.requireNonNull(this.coordPaths.get(coords), "Could not find path in tree '" + this.id + "' with coordinates '" + coords + "'");
   }

   @NotNull
   public SkillTreeNode getNode(@NotNull String name) {
      return Objects.requireNonNull(this.nodes.get(name), "Could not find node in tree '" + this.id + "' with name '" + name + "'");
   }

   public boolean hasIcon(DisplayInfo displayInfo) {
      return this.icons.containsKey(displayInfo);
   }

   public Icon getIcon(DisplayInfo displayInfo) {
      return this.icons.get(displayInfo);
   }

   public boolean isNode(String name) {
      return this.nodes.containsKey(name);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SkillTree skillTree = (SkillTree)o;
         return this.id.equals(skillTree.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}

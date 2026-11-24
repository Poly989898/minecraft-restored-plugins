package net.Indyuce.mmocore.skilltree;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.ExpCurve;
import net.Indyuce.mmocore.experience.ExperienceObject;
import net.Indyuce.mmocore.experience.droptable.ExperienceTable;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;
import net.Indyuce.mmocore.util.Icon;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillTreeNode implements ExperienceObject {
   private final SkillTree tree;
   private final String name;
   private final String id;
   private final String permissionRequired;
   private final int pointConsumption;
   private final Map<NodeState, Icon> icons = new HashMap<>();
   private final IntegerCoordinates coordinates;
   private final int maxLevel;
   private final int maxChildren;
   private final ExperienceTable experienceTable;
   private final List<ParentInformation> children = new ArrayList<>();
   private final List<ParentInformation> parents = new ArrayList<>();
   private final Map<Integer, List<String>> lores = new HashMap<>();
   private boolean root;
   public static final String KEY_PREFIX = "node";

   public SkillTreeNode(SkillTree tree, ConfigurationSection config) {
      Validate.notNull(config, "Config cannot be null");
      this.id = config.getName();
      this.tree = tree;
      if (config.isConfigurationSection("display")) {
         for (NodeState state : NodeState.values()) {
            String ymlStatus = MMOCoreUtils.ymlName(state.name());
            if (config.isConfigurationSection("display." + ymlStatus)) {
               this.icons.put(state, Icon.from(config.get("display." + MMOCoreUtils.ymlName(state.name()))));
            } else {
               MMOCore.log(
                  "Could not find node display for state " + ymlStatus + " of node " + this.id + " in tree " + tree.getId() + ". Using default display."
               );
            }
         }
      }

      this.name = Objects.requireNonNull(config.getString("name"), "Could not find node name");
      this.root = config.getBoolean("root", config.getBoolean("is-root"));
      this.pointConsumption = config.getInt("point-consumed", 1);
      this.permissionRequired = config.getString("permission-required");
      Validate.isTrue(this.pointConsumption > 0, "The skill tree points consumed by a node must be greater than 0.");
      if (config.contains("lores")) {
         for (String key : config.getConfigurationSection("lores").getKeys(false)) {
            try {
               this.lores.put(Integer.parseInt(key), config.getStringList("lores." + key));
            } catch (NumberFormatException exception) {
               throw new RuntimeException("You shall only specify integers in the 'lores' config section");
            }
         }
      }

      try {
         Validate.isTrue(config.contains("experience-table"), "You must specify an exp table");
         this.experienceTable = MMOCore.plugin.experience.loadExperienceTable(config.get("experience-table"));
      } catch (RuntimeException exception) {
         throw new RuntimeException("Could not load experience table: " + exception.getMessage());
      }

      this.maxLevel = config.getInt("max-level", 1);
      Validate.isTrue(this.maxLevel > 0, "Max level must be positive");
      this.maxChildren = config.getInt("max-children", 0);
      Validate.isTrue(this.maxChildren >= 0, "Max children must positive or zero");
      this.coordinates = IntegerCoordinates.from(config.get("coordinates"));
   }

   public SkillTree getTree() {
      return this.tree;
   }

   public boolean hasIcon(NodeState status) {
      return this.icons.containsKey(status);
   }

   public Icon getIcon(NodeState status) {
      return this.icons.get(status);
   }

   public boolean isRoot() {
      return this.root;
   }

   public void addParent(@NotNull SkillTreeNode parent, @NotNull ParentType parentType, int requiredLevel) {
      this.parents.add(new ParentInformation(parent, parentType, requiredLevel));
   }

   public void addChild(@NotNull SkillTreeNode child, @NotNull ParentType parentType, int requiredLevel) {
      this.children.add(new ParentInformation(child, parentType, requiredLevel));
   }

   public void setRoot() {
      this.root = true;
   }

   public int getPointConsumption() {
      return this.pointConsumption;
   }

   public int getParentNeededLevel(SkillTreeNode parent) {
      for (ParentInformation entry : this.parents) {
         if (entry.getNode().equals(parent)) {
            return entry.getLevel();
         }
      }

      throw new RuntimeException("Could not find parent " + parent.getId() + " for node " + this.id);
   }

   @Deprecated
   public int getParentNeededLevel(SkillTreeNode parent, ParentType parentType) {
      for (ParentInformation entry : this.parents) {
         if (entry.getNode().equals(parent) && entry.getType() == parentType) {
            return entry.getLevel();
         }
      }

      return 0;
   }

   public boolean hasParent(SkillTreeNode parent) {
      for (ParentInformation entry : this.parents) {
         if (entry.getNode() == parent) {
            return true;
         }
      }

      return false;
   }

   public int getMaxLevel() {
      return this.maxLevel;
   }

   public int getMaxChildren() {
      return this.maxChildren;
   }

   public boolean hasPermissionRequirement(@NotNull PlayerData playerData) {
      return this.permissionRequired == null || playerData.getPlayer().hasPermission(this.permissionRequired);
   }

   @NotNull
   public List<ParentInformation> getParents() {
      return this.parents;
   }

   @Deprecated
   @NotNull
   public List<SkillTreeNode> getParents(ParentType parentType) {
      return this.parents.stream().filter(integer -> integer.getType() == parentType).map(ParentInformation::getNode).collect(Collectors.toList());
   }

   @NotNull
   public List<ParentInformation> getChildren() {
      return this.children;
   }

   public String getId() {
      return this.id;
   }

   @NotNull
   public String getFullId() {
      return this.tree.getId() + "_" + this.id;
   }

   @NotNull
   public String getName() {
      return MythicLib.plugin.parseColors(this.name);
   }

   @NotNull
   public IntegerCoordinates getCoordinates() {
      return this.coordinates;
   }

   @Override
   public String getKey() {
      return "node:" + this.getFullId().replace("-", "_");
   }

   @NotNull
   @Override
   public ExperienceTable getExperienceTable() {
      return Objects.requireNonNull(this.experienceTable, "Skill tree has no exp table");
   }

   @Override
   public boolean hasExperienceTable() {
      return this.experienceTable != null;
   }

   public NodeType getNodeType() {
      boolean up = this.tree.isPathOrNode(new IntegerCoordinates(this.coordinates.getX(), this.coordinates.getY() - 1));
      boolean down = this.tree.isPathOrNode(new IntegerCoordinates(this.coordinates.getX(), this.coordinates.getY() + 1));
      boolean right = this.tree.isPathOrNode(new IntegerCoordinates(this.coordinates.getX() + 1, this.coordinates.getY()));
      boolean left = this.tree.isPathOrNode(new IntegerCoordinates(this.coordinates.getX() - 1, this.coordinates.getY()));
      if (up && right && down && left) {
         return NodeType.UP_RIGHT_DOWN_LEFT;
      } else if (up && right && down) {
         return NodeType.UP_RIGHT_DOWN;
      } else if (up && right && left) {
         return NodeType.UP_RIGHT_LEFT;
      } else if (up && down && left) {
         return NodeType.UP_DOWN_LEFT;
      } else if (down && right && left) {
         return NodeType.DOWN_RIGHT_LEFT;
      } else if (up && right) {
         return NodeType.UP_RIGHT;
      } else if (up && down) {
         return NodeType.UP_DOWN;
      } else if (up && left) {
         return NodeType.UP_LEFT;
      } else if (down && right) {
         return NodeType.DOWN_RIGHT;
      } else if (down && left) {
         return NodeType.DOWN_LEFT;
      } else if (right && left) {
         return NodeType.RIGHT_LEFT;
      } else if (up) {
         return NodeType.UP;
      } else if (down) {
         return NodeType.DOWN;
      } else if (right) {
         return NodeType.RIGHT;
      } else {
         return left ? NodeType.LEFT : NodeType.NO_PATH;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SkillTreeNode that = (SkillTreeNode)o;
         return this.tree.equals(that.tree) && this.id.equals(that.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.tree, this.id);
   }

   public List<String> getLore(PlayerData playerData) {
      int nodeLevel = playerData.getNodeLevel(this);
      List<String> parsedLore = new ArrayList<>();

      for (int i = nodeLevel; i >= 0; i--) {
         List<String> found = this.lores.get(i);
         if (found != null) {
            Placeholders holders = this.getPlaceholders(playerData);
            found.forEach(string -> parsedLore.add(MythicLib.plugin.parseColors(holders.apply(playerData.getPlayer(), string))));
            break;
         }
      }

      return parsedLore;
   }

   private Placeholders getPlaceholders(@NotNull PlayerData playerData) {
      Placeholders holders = new Placeholders();
      holders.register("name", this.getName());
      holders.register("node-state", playerData.getNodeState(this));
      holders.register("level", playerData.getNodeLevel(this));
      holders.register("max-level", this.getMaxLevel());
      holders.register("max-children", this.getMaxChildren());
      return holders;
   }

   @Override
   public void giveExperience(PlayerData playerData, double experience, @Nullable Location hologramLocation, @NotNull EXPSource source) {
      throw new RuntimeException("Skill trees don't have experience");
   }

   @Override
   public boolean shouldHandle(PlayerData playerData) {
      throw new RuntimeException("Skill trees don't have experience");
   }

   @Nullable
   @Override
   public ExpCurve getExpCurve() {
      throw new RuntimeException("Skill trees don't have experience");
   }
}

package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.util.FileUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.manager.registry.MMOCoreRegister;
import net.Indyuce.mmocore.skilltree.ParentType;
import net.Indyuce.mmocore.skilltree.SkillTreeNode;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;
import net.Indyuce.mmocore.skilltree.tree.SkillTreeType;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillTreeManager extends MMOCoreRegister<SkillTree> {
   private final Map<String, SkillTreeNode> skillTreeNodes = new HashMap<>();

   public void register(@NotNull SkillTree tree) {
      super.register(tree);
      tree.getNodes().forEach(node -> this.skillTreeNodes.put(node.getFullId(), node));
   }

   @Nullable
   public SkillTreeNode getNode(@NotNull String fullId) {
      return this.skillTreeNodes.get(fullId);
   }

   @NotNull
   public Collection<SkillTreeNode> getAllNodes() {
      return this.skillTreeNodes.values();
   }

   @Override
   public String getRegisteredObjectName() {
      return "skill tree";
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.registered.clear();
         this.skillTreeNodes.clear();
      }

      FileUtils.loadObjectsFromFolder(
         MMOCore.plugin, "skill-trees", true, (key, config) -> this.register(this.loadSkillTree(config)), "Could not load skill tree from file '%s': %s"
      );
   }

   @NotNull
   public SkillTree loadSkillTree(@NotNull ConfigurationSection config) {
      Validate.notNull(config, "Config cannot be null");

      SkillTreeType type;
      try {
         type = SkillTreeType.valueOf(UtilityMethods.enumName(config.getString("type", "custom")));
      } catch (RuntimeException exception) {
         throw new IllegalArgumentException("Not a valid skill tree type");
      }

      return type.construct(config);
   }

   @Deprecated
   public SkillTree get(int index) {
      return new ArrayList<>(this.registered.values()).get(index);
   }

   @Deprecated
   public List<SkillTreeNode> getRootNodes() {
      return this.skillTreeNodes.values().stream().filter(treeNode -> treeNode.getParents(ParentType.SOFT).isEmpty()).collect(Collectors.toList());
   }

   @Deprecated
   public boolean has(int index) {
      return index >= 0 && index < this.registered.size();
   }
}

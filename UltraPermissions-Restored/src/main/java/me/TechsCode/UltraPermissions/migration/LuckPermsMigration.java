package me.TechsCode.UltraPermissions.migration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import me.TechsCode.UltraPermissions.storage.objects.User;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;

public class LuckPermsMigration extends MigrationAssistant {
   public LuckPermsMigration() {
   }

   @Override
   public void migrate(UltraPermissions ultraPermissions, Runnable task) {
      LuckPerms luckPerms = LuckPermsProvider.get();
      HashMap<Group, me.TechsCode.UltraPermissions.storage.objects.Group> map = new HashMap<>();
      List<Group> groups = luckPerms.getGroupManager()
         .getLoadedGroups()
         .stream()
         .sorted(
            (group, other) -> group.getWeight().isPresent() && other.getWeight().isPresent() ? group.getWeight().getAsInt() - other.getWeight().getAsInt() : 0
         )
         .collect(Collectors.toList());
      int count = 1;

      for (Group group : groups) {
         CachedMetaData metaData = group.getCachedData().getMetaData();
         me.TechsCode.UltraPermissions.storage.objects.Group ultraGroup = ultraPermissions.newGroup(group.getName())
            .setDefault(group.getName().equals("default"))
            .setPrefix(metaData.getPrefix())
            .setSuffix(metaData.getSuffix())
            .setPriority(count)
            .create();
         this.addNodes(ultraGroup, group.getNodes(NodeType.PERMISSION));
         map.put(group, ultraGroup);
         count++;
      }

      map.forEach((group, ultraGroup) -> {
         for (Group inheritedGroup : group.getInheritedGroups(QueryOptions.nonContextual())) {
            ultraGroup.addInheritance((me.TechsCode.UltraPermissions.storage.objects.Group)map.get(inheritedGroup));
         }
      });
      AtomicInteger atomicInteger = new AtomicInteger();

      try {
         Set<UUID> uuids = (Set<UUID>)luckPerms.getUserManager().getUniqueUsers().get();
         uuids.forEach(uuid -> luckPerms.getUserManager().loadUser(uuid).whenComplete((user, exception) -> {
            User ultraUser = ultraPermissions.registerUser(user.getUniqueId(), user.getUsername() == null ? "NOT DEFINED" : user.getUsername(), false);
            this.addNodes(ultraUser, user.getNodes(NodeType.PERMISSION));

            for (Group inheritedGroup : user.getInheritedGroups(QueryOptions.nonContextual())) {
               ultraUser.addGroup((me.TechsCode.UltraPermissions.storage.objects.Group)map.get(inheritedGroup));
            }

            int processed = atomicInteger.incrementAndGet();
            if (uuids.size() == processed) {
               task.run();
            }
         }));
      } catch (InterruptedException | ExecutionException exception) {
         exception.printStackTrace();
      }
   }

   private void addNodes(PermissionHolder permissionHolder, Collection<PermissionNode> permissionNodes) {
      permissionNodes.forEach(
         permissionNode -> {
            Optional serverContext = permissionNode.getContexts().getAnyValue("server");
            Optional anyValue = permissionNode.getContexts().getAnyValue("world");
            permissionHolder.newPermission(permissionNode.getPermission())
               .setServer((String)serverContext.orElse(null))
               .setWorld((String)anyValue.orElse(null))
               .setPositive(!permissionNode.isNegated())
               .setExpiration(permissionNode.getExpiry() != null ? permissionNode.getExpiry().toEpochMilli() : 0L)
               .create();
         }
      );
   }

   @Override
   public String getPluginName() {
      return "Luck Perms";
   }
}

package me.TechsCode.UltraPermissions.storage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;
import me.TechsCode.UltraPermissions.events.user.UserCreationEvent;
import me.TechsCode.UltraPermissions.events.user.UserGroupAddedEvent;
import me.TechsCode.UltraPermissions.events.user.UserGroupRemovedEvent;
import me.TechsCode.UltraPermissions.events.user.UserPrefixChangeEvent;
import me.TechsCode.UltraPermissions.events.user.UserSuffixChangeEvent;
import me.TechsCode.UltraPermissions.events.user.UserSuperAdminChangeEvent;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import me.TechsCode.UltraPermissions.base.storage.Stored;

public class SpigotUserStorage extends UserStorage {
   public SpigotUserStorage(TechPlugin techPlugin, Class<? extends StorageImplementation> type) {
      super(techPlugin, type);
   }

   @Override
   public void onCreation(User user) {
      super.onCreation(user);
      UserCreationEvent event = new UserCreationEvent(user);
      event.call(this.plugin, event);
   }

   @Override
   public void onChange(User oldUser, User newUser) {
      super.onChange(oldUser, newUser);
      if (oldUser.isSuperadmin() != newUser.isSuperadmin()) {
         UserSuperAdminChangeEvent event = new UserSuperAdminChangeEvent(newUser, oldUser.isSuperadmin(), newUser.isSuperadmin());
         event.call(this.plugin, event);
      }

      if (!oldUser.getPrefix().equals(newUser.getPrefix())) {
         UserPrefixChangeEvent event = new UserPrefixChangeEvent(newUser, oldUser.getPrefix().orElse(null), newUser.getPrefix().orElse(null));
         event.call(this.plugin, event);
      }

      if (!oldUser.getSuffix().equals(newUser.getSuffix())) {
         UserSuffixChangeEvent event = new UserSuffixChangeEvent(newUser, oldUser.getSuffix().orElse(null), newUser.getSuffix().orElse(null));
         event.call(this.plugin, event);
      }

      Set<Stored<Group>> oldGroups = oldUser.getGroups();
      Set<Stored<Group>> newGroups = newUser.getGroups();
      List<Stored<Group>> addedGroups = newGroups.stream().filter(stored -> !oldGroups.contains(stored)).collect(Collectors.toList());
      List<Stored<Group>> removedGroups = oldGroups.stream().filter(stored -> !newGroups.contains(stored)).collect(Collectors.toList());
      if (addedGroups.size() > 0) {
         addedGroups.forEach(stored -> {
            UserGroupAddedEvent event = new UserGroupAddedEvent(newUser, stored, newUser.getGroupExpiry(stored));
            event.call(this.plugin, event);
         });
      }

      if (removedGroups.size() > 0) {
         removedGroups.forEach(stored -> {
            UserGroupRemovedEvent event = new UserGroupRemovedEvent(newUser, stored);
            event.call(this.plugin, event);
         });
      }
   }
}

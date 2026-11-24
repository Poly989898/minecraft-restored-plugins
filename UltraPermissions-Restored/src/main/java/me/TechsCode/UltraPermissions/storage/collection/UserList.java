package me.TechsCode.UltraPermissions.storage.collection;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.storage.Stored;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;

public class UserList extends ArrayList<User> {
   public UserList() {
   }

   public Optional<User> uuid(UUID targetUuid) {
      return this.stream().filter(user -> user.getUuid().equals(targetUuid)).findFirst();
   }

   public Optional<User> name(String text) {
      return this.stream().filter(user -> user.getName().equalsIgnoreCase(text)).findFirst();
   }

   public UserList usersInGroup(Stored<Group> stored) {
      return this.stream().filter(user -> user.getGroups().contains(stored)).collect(Collectors.toCollection(UserList::new));
   }
}

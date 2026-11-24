package fr.phoenixdevt.mmoprofiles.bukkit.compat.permission;

import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LuckPermsModule implements PermissionModule {
   private final LuckPerms api;

   public LuckPermsModule() {
      RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
      Validate.notNull(provider, "No service provided");
      this.api = (LuckPerms)provider.getProvider();
   }

   @Nullable
   @Override
   public ProfilePermissible getPermissible(@NotNull ProfileListImpl playerData) {
      User found = this.api.getUserManager().getUser(playerData.getUniqueId());
      Validate.notNull(found, "No player data found");
      return new LuckPermsModule.PermissibleImpl(found);
   }

   @NotNull
   @Override
   public CompletableFuture<ProfilePermissible> loadPermissible(@NotNull ProfileListImpl playerData) {
      return this.api.getUserManager().loadUser(playerData.getUniqueId()).thenApply(x$0 -> new LuckPermsModule.PermissibleImpl(x$0));
   }

   public class PermissibleImpl implements ProfilePermissible {
      final User user;

      public PermissibleImpl(User user) {
         this.user = user;
      }

      @NotNull
      @Override
      public List<String> getPermisions() {
         return this.user.data().toCollection().stream().filter(Node::getValue).<String>map(Node::getKey).collect(Collectors.toList());
      }

      @Override
      public void givePermissions(@NotNull List<String> permissions) {
         for (String permission : permissions) {
            this.user.data().add(Node.builder(permission).build());
         }
      }

      @Override
      public void removePermissions(@NotNull List<String> permissions) {
         this.user.data().clear(node -> permissions.contains(node.getKey()));
      }

      @Override
      public void save() {
         LuckPermsModule.this.api.getUserManager().saveUser(this.user);
      }
   }
}

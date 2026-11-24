package me.TechsCode.UltraPermissions.bungee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.TechsCode.UltraPermissions.ColorPalette;
import me.TechsCode.UltraPermissions.DefaultGroupAssignOption;
import me.TechsCode.UltraPermissions.PublicRegistry;
import me.TechsCode.UltraPermissions.StorageController;
import me.TechsCode.UltraPermissions.UltraPermissionsAPI;
import me.TechsCode.UltraPermissions.base.BungeeTechPlugin;
import me.TechsCode.UltraPermissions.base.networking.BungeeNetworkManager;
import me.TechsCode.UltraPermissions.base.registry.RegistrationChoice;
import me.TechsCode.UltraPermissions.base.storage.implementations.MySQL;
import me.TechsCode.UltraPermissions.base.visual.Color;
import me.TechsCode.UltraPermissions.internal.lookup.LookupCheck;
import me.TechsCode.UltraPermissions.internal.lookup.LookupOutcome;
import me.TechsCode.UltraPermissions.internal.lookup.PermissionLookup;
import me.TechsCode.UltraPermissions.internal.lookup.checks.DefinedPermissionCheck;
import me.TechsCode.UltraPermissions.storage.GroupCreator;
import me.TechsCode.UltraPermissions.storage.GroupPathStorage;
import me.TechsCode.UltraPermissions.storage.GroupStorage;
import me.TechsCode.UltraPermissions.storage.PermissionCreator;
import me.TechsCode.UltraPermissions.storage.PermissionStorage;
import me.TechsCode.UltraPermissions.storage.UserStorage;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.collection.GroupPathList;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.collection.UserList;
import me.TechsCode.UltraPermissions.storage.objects.Holder;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class UltraPermissionsBungee extends BungeeTechPlugin implements Listener, UltraPermissionsAPI, StorageController {
   private static UltraPermissionsAPI api;
   private boolean loaded;
   private HashMap<ProxiedPlayer, PermissionList> permissionsCache;
   private GroupStorage groupStorage;
   private PermissionStorage permissionStorage;
   private UserStorage userStorage;
   private GroupPathStorage groupPathStorage;
   private PublicRegistry publicRegistry;

   public UltraPermissionsBungee(Plugin plugin) {
      super(plugin);
   }

   @Override
   protected void onEnable() {
      api = this;
      this.loaded = false;
      new BungeeNetworkManager(this);
      if (this.getMySQLManager().isEnabled()) {
         this.onLoad();
      } else {
         this.log("Waiting for MySQL Credentials...");
      }

      this.getScheduler().runTaskTimer(() -> {
         if (!this.loaded && this.getMySQLManager().isEnabled()) {
            this.onLoad();
         }
      }, 20L, 20L);
   }

   private void onLoad() {
      this.loaded = true;
      this.permissionsCache = new HashMap<>();
      this.groupStorage = new GroupStorage(this, MySQL.class);
      this.permissionStorage = new PermissionStorage(this, MySQL.class);
      this.userStorage = new UserStorage(this, MySQL.class);
      this.groupPathStorage = new GroupPathStorage(this, MySQL.class);
      this.publicRegistry = this.getRegistry().register(PublicRegistry.class, RegistrationChoice.GLOBAL_IF_AVAILABLE);
      ProxyServer.getInstance().getPluginManager().registerListener(this.getBootstrap(), this);
   }

   @Override
   protected void onDisable() {
   }

   @EventHandler
   public void check(PermissionCheckEvent event) {
      if (event.getSender() instanceof ProxiedPlayer) {
         final ProxiedPlayer sender = (ProxiedPlayer)event.getSender();
         PermissionLookup permissionLookup = new PermissionLookup(event.getPermission()) {
            @Override
            public LookupCheck[] getChecks() {
               return new LookupCheck[]{new DefinedPermissionCheck() {
                  @Override
                  public List<Permission> getDefinedPermissions() {
                     PermissionList permissions = UltraPermissionsBungee.this.getPermissions(sender);
                     return permissions == null ? new ArrayList<>() : permissions;
                  }
               }};
            }
         };
         LookupOutcome lookupOutcome = permissionLookup.perform();
         event.setHasPermission(lookupOutcome.getResult());
      }
   }

   public PermissionList getPermissions(ProxiedPlayer proxiedPlayer) {
      if (this.permissionsCache.containsKey(proxiedPlayer)) {
         return this.permissionsCache.get(proxiedPlayer);
      } else {
         Optional optional = this.userStorage.getUsers().uuid(proxiedPlayer.getUniqueId());
         if (!optional.isPresent()) {
            this.log("Cannot find the user (" + proxiedPlayer.getUniqueId() + "). Please check your database connection.");
            return null;
         } else {
            PermissionList list = ((User)optional.get()).getPermissions().bungee();
            list.addAll(((User)optional.get()).getAdditionalPermissions().bungee());
            this.permissionsCache.put(proxiedPlayer, list);
            return list;
         }
      }
   }

   @EventHandler
   public void login(LoginEvent event) {
      this.login(event.getConnection().getUniqueId(), event.getConnection().getName());
   }

   public void login(@NotNull UUID uuid, @NotNull String playerName) {
      Optional optional = this.getUsers().uuid(uuid);
      if (optional.isPresent()) {
         if (!((User)optional.get()).getName().equals(playerName)) {
            ((User)optional.get()).setPlayerName(playerName);
         }
      } else {
         this.registerUser(uuid, playerName, true);
      }
   }

   @Override
   public User registerUser(UUID uuid, String playerName, boolean flag) {
      Optional<User> optional = this.userStorage.getUsers().uuid(uuid);
      if (optional.isPresent()) {
         return (User)optional.get();
      }

      Optional<User> first = this.userStorage.getUsers().stream().filter(user -> user.getName().equalsIgnoreCase(playerName)).findFirst();
      return first.orElseGet(() -> this.userStorage.registerUser(uuid, playerName, this.getDefaultGroupAssignOption() == DefaultGroupAssignOption.FIRST_JOIN));
   }

   private DefaultGroupAssignOption getDefaultGroupAssignOption() {
      return this.publicRegistry.getDefaultAssignOption();
   }

   @Override
   public boolean isAllowingInvalidMySQL() {
      return true;
   }

   @Override
   public PermissionCreator newPermission(String permission, Holder holder) {
      return this.permissionStorage.newPermission(permission, holder);
   }

   @Override
   public GroupCreator newGroup(String groupName) {
      return this.groupStorage.newGroup(groupName);
   }

   @Override
   public PermissionList getPermissions() {
      return this.permissionStorage.getPermissions();
   }

   @Override
   public GroupList getGroups() {
      return this.groupStorage.getGroups();
   }

   @Override
   public GroupPathList getGroupPaths() {
      return this.groupPathStorage.getGroupPaths();
   }

   @Override
   public UserList getUsers() {
      return this.userStorage.getUsers();
   }

   @Override
   public Color getPluginMainColor() {
      return ColorPalette.MAIN;
   }

   @Override
   public PublicRegistry getPublicRegistry() {
      return null;
   }

   public static UltraPermissionsAPI getAPI() {
      return api;
   }

   @Override
   public void onDataModification() {
      this.permissionsCache.clear();
   }

   @Override
   public GroupStorage getGroupStorage() {
      return this.groupStorage;
   }

   @Override
   public PermissionStorage getPermissionStorage() {
      return this.permissionStorage;
   }

   @Override
   public UserStorage getUserStorage() {
      return this.userStorage;
   }
}

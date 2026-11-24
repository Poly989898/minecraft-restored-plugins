package me.TechsCode.UltraPermissions.hooks.pluginHooks;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.hooks.placeholders.PrefixPlaceholder;
import me.TechsCode.UltraPermissions.hooks.placeholders.SuffixPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class VaultChatHook extends Chat {
   private final UltraPermissions plugin;
   private ConcurrentHashMap<String, String> prefixesCache;
   private ConcurrentHashMap<String, String> suffixesCache;

   public VaultChatHook(Permission permission, UltraPermissions ultraPermissions) {
      super(permission);
      this.plugin = ultraPermissions;
      this.loadInCache().run();
   }

   public String getName() {
      return "UltraPermissions";
   }

   public boolean isEnabled() {
      return true;
   }

   public void clearCache() {
      this.plugin.getScheduler().runAsync(this.loadInCache());
   }

   private Runnable loadInCache() {
      return () -> {
         this.prefixesCache = new ConcurrentHashMap<>();
         this.suffixesCache = new ConcurrentHashMap<>();
         NServer nServer = this.plugin.getThisServer().orElse(null);
         this.plugin.getUsers().stream().filter(Objects::nonNull).forEach(user -> {
            this.prefixesCache.put(user.getName() == null ? "UNDEFINED" : user.getName(), new PrefixPlaceholder().get(user, nServer));
            this.suffixesCache.put(user.getName() == null ? "UNDEFINED" : user.getName(), new SuffixPlaceholder().get(user, nServer));
         });
      };
   }

   public String getPlayerPrefix(String text, String playerName) {
      return this.prefixesCache.getOrDefault(playerName, null);
   }

   public void setPlayerPrefix(String text, String playerName, String value) {
      Optional<User> optional = this.user(playerName);
      optional.ifPresent(user -> user.setPrefix(value));
   }

   public String getPlayerSuffix(String text, String playerName) {
      return this.suffixesCache.getOrDefault(playerName, null);
   }

   public void setPlayerSuffix(String text, String playerName, String value) {
      Optional<User> optional = this.user(playerName);
      optional.ifPresent(user -> user.setSuffix(value));
   }

   public String getGroupPrefix(String text, String groupName) {
      Optional<Group> optional = this.group(groupName);
      return optional.<String>map(group -> group.getPrefix().orElse(null)).orElse(null);
   }

   public void setGroupPrefix(String text, String groupName, String value) {
      Optional<Group> optional = this.group(groupName);
      optional.ifPresent(group -> group.setPrefix(value));
   }

   public String getGroupSuffix(String text, String groupName) {
      Optional<Group> optional = this.group(groupName);
      return optional.<String>map(group -> group.getSuffix().orElse(null)).orElse(null);
   }

   public void setGroupSuffix(String text, String groupName, String value) {
      Optional<Group> optional = this.group(groupName);
      optional.ifPresent(group -> group.setSuffix(value));
   }

   public int getPlayerInfoInteger(String text, String playerName, String node, int count) {
      Optional<User> optional = this.user(playerName);
      return optional.<Integer>map(
            user -> user.getPermissions()
               .name(node)
               .servers(true, this.plugin.getThisServer().map(NServer::getName).orElse(null))
               .worlds(true, text)
               .stream()
               .filter(me.TechsCode.UltraPermissions.storage.objects.Permission::isPositive)
               .map(permission -> permission.getName().split("\\."))
               .filter(texts -> texts.length > 1)
               .mapToInt(texts -> this.convertOrDefault(texts[texts.length - 1], count))
               .max()
               .orElse(count)
         )
         .orElse(count);
   }

   private int convertOrDefault(String text, int count) {
      try {
         return Integer.parseInt(text);
      } catch (NumberFormatException exception) {
         return count;
      }
   }

   public void setPlayerInfoInteger(String text, String playerName, String node, int count) {
   }

   public int getGroupInfoInteger(String text, String groupName, String node, int count) {
      Optional<Group> optional = this.group(groupName);
      return optional.<Integer>map(
            group -> group.getPermissions()
               .name(node)
               .servers(true, this.plugin.getThisServer().map(NServer::getName).orElse(null))
               .worlds(true, text)
               .stream()
               .filter(me.TechsCode.UltraPermissions.storage.objects.Permission::isPositive)
               .map(permission -> permission.getName().split("\\."))
               .filter(texts -> texts.length > 1)
               .mapToInt(texts -> this.convertOrDefault(texts[texts.length - 1], count))
               .max()
               .orElse(count)
         )
         .orElse(count);
   }

   public void setGroupInfoInteger(String text, String groupName, String node, int count) {
   }

   public double getPlayerInfoDouble(String text, String playerName, String node, double value) {
      return 0.0;
   }

   public void setPlayerInfoDouble(String text, String playerName, String node, double value) {
   }

   public double getGroupInfoDouble(String text, String groupName, String node, double value) {
      return 0.0;
   }

   public void setGroupInfoDouble(String text, String groupName, String node, double value) {
   }

   public boolean getPlayerInfoBoolean(String text, String playerName, String node, boolean flag) {
      return false;
   }

   public void setPlayerInfoBoolean(String text, String playerName, String node, boolean flag) {
   }

   public boolean getGroupInfoBoolean(String text, String groupName, String node, boolean flag) {
      return false;
   }

   public void setGroupInfoBoolean(String text, String groupName, String node, boolean flag) {
   }

   public String getPlayerInfoString(String text, String playerName, String node, String defaultValue) {
      return null;
   }

   public void setPlayerInfoString(String text, String playerName, String node, String value) {
   }

   public String getGroupInfoString(String text, String groupName, String node, String defaultValue) {
      return null;
   }

   public void setGroupInfoString(String text, String groupName, String node, String value) {
   }

   public Optional<User> user(String text) {
      return this.plugin.getUsers().name(text);
   }

   public Optional<Group> group(String text) {
      return this.plugin.getGroups().name(text);
   }
}

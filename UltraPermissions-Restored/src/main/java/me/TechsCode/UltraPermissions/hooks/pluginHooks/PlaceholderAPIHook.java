package me.TechsCode.UltraPermissions.hooks.pluginHooks;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import me.TechsCode.UltraPermissions.CachedPlayerPermissionProvider;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.storage.Stored;
import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.internal.lookup.LookupCheck;
import me.TechsCode.UltraPermissions.internal.lookup.PermissionLookup;
import me.TechsCode.UltraPermissions.internal.lookup.checks.BlankCheck;
import me.TechsCode.UltraPermissions.internal.lookup.checks.DefaultPermissionCheck;
import me.TechsCode.UltraPermissions.internal.lookup.checks.DefinedPermissionCheck;
import me.TechsCode.UltraPermissions.internal.lookup.checks.OperatorLookupCheck;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.User;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook {
   private final UltraPermissions plugin;
   private final CachedPlayerPermissionProvider permissionProvider;

   public PlaceholderAPIHook(final UltraPermissions ultraPermissions) {
      this.plugin = ultraPermissions;
      this.permissionProvider = new CachedPlayerPermissionProvider(ultraPermissions);
      (new PlaceholderExpansion() {
            public String getIdentifier() {
               return "uperms";
            }

            public String getAuthor() {
               return "TechsCode";
            }

            public String getVersion() {
               return "1.0.0";
            }

            public boolean persist() {
               return true;
            }

            public String onRequest(final OfflinePlayer offlinePlayer, String text) {
               if (offlinePlayer == null) {
                  ultraPermissions.log("Plugin tried getting the placeholder %uperms_" + text + "% with no player assigned!");
                  return "";
               }

               Optional optional = ultraPermissions.getUsers().uuid(offlinePlayer.getUniqueId());
               if (!optional.isPresent()) {
                  return "";
               }

               String[] texts = text.split("_");
               if (texts.length == 3) {
                  if (texts[0].equalsIgnoreCase("has") && texts[1].equalsIgnoreCase("group")) {
                     try {
                        Optional name = ultraPermissions.getGroups().name(texts[2]);
                        if (((User)optional.get()).getGroups().contains(((Group)name.get()).toStored())) {
                           return "true";
                        }

                        return "false";
                     } catch (NullPointerException | IndexOutOfBoundsException | NoSuchElementException exception) {
                     }
                  }

                  if (texts[0].equalsIgnoreCase("has") && texts[1].equalsIgnoreCase("permission")) {
                     PermissionLookup permissionLookup = new PermissionLookup(texts[2]) {
                        @Override
                        public LookupCheck[] getChecks() {
                           return new LookupCheck[]{
                              new OperatorLookupCheck(ultraPermissions, offlinePlayer),
                              ultraPermissions.isDefaultPermissionsEnabled() ? new DefaultPermissionCheck(offlinePlayer) : new BlankCheck(),
                              new DefinedPermissionCheck() {
                                 @Override
                                 public List<Permission> getDefinedPermissions() {
                                    Optional targetUser = PlaceholderAPIHook.this.user(offlinePlayer.getName());
                                    return targetUser.isPresent()
                                       ? PlaceholderAPIHook.this.permissionProvider
                                          .retrieve(((User)targetUser.get()).getUuid(), offlinePlayer.getPlayer().getLocation().getWorld().getName())
                                       : Collections.emptyList();
                                 }
                              }
                           };
                        }
                     };
                     if (permissionLookup.perform().getResult()) {
                        return "true";
                     }

                     return "false";
                  }

                  if (texts[0].equalsIgnoreCase("permission")
                     && texts[1].equalsIgnoreCase("timer")
                     && !((User)optional.get()).getPermissions().name(texts[2]).isEmpty()) {
                     long expiration = ((User)optional.get()).getPermissions().name(texts[2]).stream().findFirst().get().getExpiration();
                     if (expiration != 0L) {
                        long value = expiration - System.currentTimeMillis();
                        return Tools.getTimeString(value, TimeUnit.MILLISECONDS);
                     }

                     return "Permanent";
                  }

                  if (texts[0].equalsIgnoreCase("inherits") && texts[1].equalsIgnoreCase("group")) {
                     try {
                        Optional name = ultraPermissions.getGroups().name(texts[2]);
                        if (((User)optional.get()).getGroups().contains(((Group)name.get()).toStored())) {
                           return "direct";
                        }

                        for (Stored group : ((User)optional.get()).getGroups()) {
                           for (Stored inheritedGroup : ((Group)group.get().get()).getInheritedGroups()) {
                              if (((Group)inheritedGroup.get().get()).equals(name.get())) {
                                 return "indirect";
                              }
                           }
                        }
                     } catch (NullPointerException | IndexOutOfBoundsException | NoSuchElementException exception) {
                     }
                  }

                  if (texts[0].equalsIgnoreCase("rank") && texts[1].equalsIgnoreCase("timer")) {
                     try {
                        Optional name = ultraPermissions.getGroups().name(texts[2]);
                        long groupExpiry = ((User)optional.get()).getGroupExpiry(((Group)name.get()).toStored());
                        if (groupExpiry != 0L) {
                           long value = groupExpiry - System.currentTimeMillis();
                           return Tools.getTimeString(value, TimeUnit.MILLISECONDS, 2);
                        }

                        return "Permanent";
                     } catch (NullPointerException | NoSuchElementException exception) {
                     }
                  }
               }

               if (texts.length == 4) {
                  if (texts[0].equalsIgnoreCase("rank") && texts[1].equalsIgnoreCase("timer") && texts[2].equalsIgnoreCase("detailed")) {
                     try {
                        Optional name = ultraPermissions.getGroups().name(texts[3]);
                        long groupExpiry = ((User)optional.get()).getGroupExpiry(((Group)name.get()).toStored());
                        if (groupExpiry != 0L) {
                           long value = groupExpiry - System.currentTimeMillis();
                           return Tools.getTimeString(value, TimeUnit.MILLISECONDS);
                        }

                        return "Permanent";
                     } catch (NullPointerException | NoSuchElementException exception) {
                     }
                  }

                  if (texts[1].equalsIgnoreCase("permission") && texts[2].equalsIgnoreCase("timer")) {
                     Optional name = ultraPermissions.getUserStorage().getUsers().name(texts[0]);
                     if (name.isPresent() && !((User)name.get()).getPermissions().name(texts[3]).isEmpty()) {
                        long expiration = ((User)name.get()).getPermissions().name(texts[3]).stream().findFirst().get().getExpiration();
                        if (expiration != 0L) {
                           long value = expiration - System.currentTimeMillis();
                           return Tools.getTimeString(value, TimeUnit.MILLISECONDS, 2);
                        }

                        return "Permanent";
                     }
                  }
               }

               if (texts.length == 5 && texts[1].equalsIgnoreCase("permission") && texts[2].equalsIgnoreCase("timer") && texts[3].equalsIgnoreCase("detailed")) {
                  Optional name = ultraPermissions.getUserStorage().getUsers().name(texts[0]);
                  if (name.isPresent() && !((User)name.get()).getPermissions().name(texts[4]).isEmpty()) {
                     long expiration = ((User)name.get()).getPermissions().name(texts[4]).stream().findFirst().get().getExpiration();
                     if (expiration != 0L) {
                        long value = expiration - System.currentTimeMillis();
                        return Tools.getTimeString(value, TimeUnit.MILLISECONDS);
                     }

                     return "Permanent";
                  }
               }

               for (UpermsPlaceholder placeholder : UltraPermissions.placeholders) {
                  if (placeholder.getName().equalsIgnoreCase(text)) {
                     return placeholder.get((User)optional.get(), ultraPermissions.getThisServer().orElse(null));
                  }
               }

               return "";
            }
         })
         .register();
   }

   public String replace(OfflinePlayer offlinePlayer, String text) {
      return PlaceholderAPI.setPlaceholders(offlinePlayer, text);
   }

   public Optional<User> user(String text) {
      return this.plugin.getUsers().name(text);
   }
}

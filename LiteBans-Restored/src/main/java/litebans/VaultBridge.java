package litebans;

import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(obtainByteValue = 0)
public final class VaultBridge {
   private final PlatformBridge bridge;
   private final Lazy permissionService;
   private final Lazy chatService;

   public VaultBridge(@NotNull PlatformBridge platformBridge) {
      this.bridge = platformBridge;
      this.permissionService = KotlinStub5.computeLazy(() -> this.getService("net.milkbowl.vault.permission.Permission"));
      this.chatService = KotlinStub5.computeLazy(() -> this.getService("net.milkbowl.vault.chat.Chat"));
   }

   public final PlatformBridge getBridge() {
      return this.bridge;
   }

   public final Object getPermission() {
      Lazy lazy = this.permissionService;
      return lazy.getValue();
   }

   public final Object getChat() {
      Lazy lazy = this.chatService;
      return lazy.getValue();
   }

   public final boolean checkText(@NotNull String text, @NotNull UUID uuid, @NotNull String text2, @NotNull String text3, @NotNull String text4, @NotNull String[] texts) {
      if (StringsKt.startsWithWithDefaults(text, '#', false, 2, null)) {
         return false;
      }

      PlatformBridge bridge = this.getBridge().getPlatform().createBridge();
      Intrinsics.checkNotNullExpressionValue(bridge, "");
      BukkitBridge bukkitBridge = (BukkitBridge)bridge;
      Object permission = this.getPermission();
      Permission permission2 = permission instanceof Permission ? (Permission)permission : null;
      if ((permission instanceof Permission ? (Permission)permission : null) == null) {
         return false;
      }

      Object offlinePlayer = bukkitBridge.getOfflinePlayer(uuid);
      OfflinePlayer offlinePlayer2 = offlinePlayer instanceof OfflinePlayer ? (OfflinePlayer)offlinePlayer : null;
      if ((offlinePlayer instanceof OfflinePlayer ? (OfflinePlayer)offlinePlayer : null) == null) {
         return false;
      }


      try {
         String text5 = Intrinsics.areEqual(text4, "default") ? null : text4;
         if (texts.length != 0 && permission2.hasGroupSupport()) {
            String[] playerGroups = permission2.getPlayerGroups(text5, offlinePlayer2);
            int value = 0;

            for (int i = texts.length; value < i; value++) {
               String text6 = texts[value];
               Iterator iterator = KotlinUtil12.computeIterator(playerGroups);

               while (iterator.hasNext()) {
                  String string = (String)iterator.next();
                  if (StringsKt.checkText(text6, string, true)) {
                     return true;
                  }
               }
            }
         }

         if (!permission2.playerHas(text5, offlinePlayer2, text3)) {
            StringBuilder builder = new StringBuilder().append(text3).append('.');
            String text7 = text2;
            if (!permission2.playerHas(text5, offlinePlayer2, builder.append(text7.toLowerCase(Locale.ENGLISH)).toString())) {
               return false;
            }
         }

         return true;
      } catch (IllegalArgumentException e) {
         return false;
      } catch (NoSuchMethodError e2) {
         return false;
      }
   }

   public final boolean hasPermission(@Nullable Object obj, @NotNull String text, @Nullable String text2) {
      Object permission = this.getPermission();
      Permission permission2 = permission instanceof Permission ? (Permission)permission : null;
      if ((permission instanceof Permission ? (Permission)permission : null) == null) {
         return false;
      }

      Intrinsics.checkNotNullExpressionValue(obj, "");
      return permission2.playerHas(text2, (OfflinePlayer)obj, text);
   }

   public static boolean hasPermissionWithDefaults(VaultBridge vaultBridge, Object obj, String text, String text2, int value, Object obj2) {
      if ((value & 4) != 0) {
         text2 = null;
      }

      return vaultBridge.hasPermission(obj, text, text2);
   }

   public final String getPrefix(@Nullable Object obj) {
      if (obj == null) {
         return "";
      }

      PlatformBridge platformBridge = this.bridge;
      Intrinsics.checkNotNullExpressionValue(this.bridge, "");
      BukkitBridge bukkitBridge = (BukkitBridge)platformBridge;
      Object permission = this.getPermission();
      Permission permission2 = permission instanceof Permission ? (Permission)permission : null;
      if ((permission instanceof Permission ? (Permission)permission : null) == null) {
         return "";
      }

      Object chat = this.getChat();
      Chat chat2 = chat instanceof Chat ? (Chat)chat : null;
      if ((chat instanceof Chat ? (Chat)chat : null) == null) {
         return "";
      }

      if (obj instanceof UUID) {
         Object offlinePlayer = bukkitBridge.getOfflinePlayer((UUID)obj);
         OfflinePlayer offlinePlayer2 = offlinePlayer instanceof OfflinePlayer ? (OfflinePlayer)offlinePlayer : null;
         if ((offlinePlayer instanceof OfflinePlayer ? (OfflinePlayer)offlinePlayer : null) == null) {
            return "";
         }

         String playerPrefix = chat2.getPlayerPrefix(null, offlinePlayer2);
         if (playerPrefix == null) {
            playerPrefix = "";
         }

         return playerPrefix;
      } else {
         String playerPrefix2 = chat2.getPlayerPrefix((Player)obj);
         if (playerPrefix2 == null) {
            playerPrefix2 = chat2.getGroupPrefix(((Player)obj).getWorld().getName(), permission2.getPrimaryGroup((Player)obj));
         }

         return playerPrefix2;
      }
   }

   public final String getSuffix(@Nullable Object obj) {
      if (obj == null) {
         return "";
      }

      PlatformBridge platformBridge = this.bridge;
      Intrinsics.checkNotNullExpressionValue(this.bridge, "");
      BukkitBridge bukkitBridge = (BukkitBridge)platformBridge;
      Object permission = this.getPermission();
      Permission permission2 = permission instanceof Permission ? (Permission)permission : null;
      if ((permission instanceof Permission ? (Permission)permission : null) == null) {
         return "";
      }

      Object chat = this.getChat();
      Chat chat2 = chat instanceof Chat ? (Chat)chat : null;
      if ((chat instanceof Chat ? (Chat)chat : null) == null) {
         return "";
      }

      if (obj instanceof UUID) {
         Object offlinePlayer = bukkitBridge.getOfflinePlayer((UUID)obj);
         OfflinePlayer offlinePlayer2 = offlinePlayer instanceof OfflinePlayer ? (OfflinePlayer)offlinePlayer : null;
         if ((offlinePlayer instanceof OfflinePlayer ? (OfflinePlayer)offlinePlayer : null) == null) {
            return "";
         }

         String playerSuffix = chat2.getPlayerSuffix(null, offlinePlayer2);
         if (playerSuffix == null) {
            playerSuffix = "";
         }

         return playerSuffix;
      } else {
         String playerSuffix2 = chat2.getPlayerSuffix((Player)obj);
         if (playerSuffix2 == null) {
            playerSuffix2 = chat2.getGroupSuffix(((Player)obj).getWorld().getName(), permission2.getPrimaryGroup((Player)obj));
         }

         return playerSuffix2;
      }
   }

   public final Object getService(@NotNull String text) {
      PlatformBridge platformBridge = this.bridge;
      Intrinsics.checkNotNullExpressionValue(this.bridge, "");
      BukkitBridge bukkitBridge = (BukkitBridge)platformBridge;

      try {
         RegisteredServiceProvider registration = bukkitBridge.asPlugin(this.getBridge().getPlatform()).getServer().getServicesManager().getRegistration(Class.forName(text));
         if (registration != null) {
            return registration.getProvider();
         }
      } catch (NoClassDefFoundError e) {
      } catch (ClassNotFoundException e2) {
      } catch (IllegalStateException e3) {
         String message = e3.getMessage();
         if (message == null) {
            message = "";
         }

         String text2 = message;
         if (StringsKt.containsWithDefaults((CharSequence)text2, (CharSequence)"zip file", false, 2, null)) {
            this.getBridge().getPlatform().getLogger().warning("Zip file was closed during access: " + text);
            return null;
         }

         throw e3;
      }

      return null;
   }

}

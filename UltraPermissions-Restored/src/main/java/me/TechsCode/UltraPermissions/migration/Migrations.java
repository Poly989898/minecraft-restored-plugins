package me.TechsCode.UltraPermissions.migration;

import java.util.Arrays;
import org.bukkit.Bukkit;

public enum Migrations {
   LuckPerms(LuckPermsMigration.class),
   PermissionsEx(PermissionsExMigration.class);

   private Class<? extends MigrationAssistant> migrationClass;

   Migrations(Class<? extends MigrationAssistant> type) {
      this.migrationClass = type;
   }

   public static MigrationAssistant getMigrationAssistant() {
      Migrations migrations = Arrays.stream(values()).filter(candidate -> Bukkit.getPluginManager().isPluginEnabled(candidate.name())).findFirst().orElse(null);
      if (migrations == null) {
         return null;
      }

      try {
         return migrations.migrationClass.newInstance();
      } catch (InstantiationException | IllegalAccessException exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public static boolean isAvailable() {
      return Arrays.stream(values()).anyMatch(migrations -> Bukkit.getPluginManager().isPluginEnabled(migrations.name()));
   }
}

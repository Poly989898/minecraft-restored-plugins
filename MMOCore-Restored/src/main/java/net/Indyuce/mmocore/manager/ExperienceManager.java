package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.util.FileUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.experience.ExpCurve;
import net.Indyuce.mmocore.experience.droptable.ExperienceTable;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExperienceManager implements MMOCoreManager {
   private final Map<String, ExpCurve> expCurves = new HashMap<>();
   private final Map<String, ExperienceTable> expTables = new HashMap<>();
   @Deprecated
   private final Map<String, List<ExperienceSource<?>>> publicExpSources = new HashMap<>();
   private final Map<Class<?>, ExperienceSourceManager<?>> managers = new HashMap<>();

   @Nullable
   public <T extends ExperienceSource> ExperienceSourceManager<T> getManager(Class<T> t) {
      return (ExperienceSourceManager<T>)this.managers.get(t);
   }

   public <T extends ExperienceSource> void registerSource(T source) {
      Class<T> path = (Class<T>)source.getClass();
      this.managers.computeIfAbsent(path, unused -> source.newManager());
      this.getManager(path).registerSource(source);
   }

   public boolean hasCurve(String id) {
      return this.expCurves.containsKey(id);
   }

   @NotNull
   public ExpCurve getCurveOrThrow(String id) {
      Validate.isTrue(this.hasCurve(id), "Could not find exp curve with ID '" + id + "'");
      return this.expCurves.get(id);
   }

   @Deprecated
   @Nullable
   public List<ExperienceSource<?>> getExperienceSourceList(String key) {
      return this.publicExpSources.get(key);
   }

   public boolean hasTable(String id) {
      return this.expTables.containsKey(id);
   }

   @NotNull
   public ExperienceTable getTableOrThrow(String id) {
      return Objects.requireNonNull(this.expTables.get(id), "Could not find exp table with ID '" + id + "'");
   }

   @NotNull
   public ExperienceTable loadExperienceTable(Object obj) {
      if (obj instanceof ConfigurationSection) {
         return new ExperienceTable((ConfigurationSection)obj);
      } else if (obj instanceof String) {
         return MMOCore.plugin.experience.getTableOrThrow(obj.toString());
      } else {
         throw new IllegalArgumentException("Please provide either a string (exp table name) or a config section (locally define an exp table)");
      }
   }

   public Collection<ExpCurve> getCurves() {
      return this.expCurves.values();
   }

   public Collection<ExperienceTable> getTables() {
      return this.expTables.values();
   }

   public boolean isClassSpecific(@NotNull String namespacedKey) {
      return true;
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.expCurves.clear();
         this.expTables.clear();
         this.managers.forEach((c, manager) -> manager.close());
         this.managers.clear();
      }

      FileUtils.loadObjectsFromFolderRaw(MMOCore.plugin, "expcurves", file -> {
         ExpCurve curve = new ExpCurve(file);
         this.expCurves.put(curve.getId(), curve);
      }, "Could not load exp curve from file '%s': %s");
      FileUtils.loadObjectsFromFolder(MMOCore.plugin, "exp-tables", false, (key, config) -> {
         ExperienceTable table = new ExperienceTable(config);
         this.expTables.put(table.getId(), table);
      }, "Could not load exp table '%s' from file '%s': %s");
   }
}

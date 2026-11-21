package io.lumine.mythic.bukkit.utils.config.properties;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class PropertyType<T> {
   protected final LuminePlugin plugin;
   protected ConfigurationSection config;
   protected final Map<String, ConfigurationSection> holderSections = new HashMap<>();
   protected String file = null;
   protected Object scope = null;
   protected boolean fileAbsolute;
   protected final String node;

   public PropertyType(LuminePlugin plugin, Object scope, String node) {
      plugin.registerProperty(this);
      this.plugin = plugin;
      this.node = node;
      this.setScope(scope);
   }

   public void reload() {
      this.load();
   }

   public void setScope(Object scope) {
      this.config = null;
      this.scope = scope;
      if (scope == null) {
         this.file = null;
         this.fileAbsolute = false;
      } else if (scope instanceof PropertyScope) {
         this.file = ((PropertyScope)scope).get();
         this.fileAbsolute = false;
      } else if (scope instanceof File) {
         this.file = ((File)scope).getAbsolutePath();
         this.fileAbsolute = true;
      } else if (scope instanceof ConfigurationSection) {
         this.config = (ConfigurationSection)scope;
         this.holderSections.clear();
         this.file = null;
      } else if (scope instanceof String) {
         this.file = (String)scope;
         this.fileAbsolute = false;
      } else {
         this.file = scope.toString();
         this.fileAbsolute = false;
      }

      this.load();
   }

   private void load() {
      if (this.file != null && !this.file.isEmpty()) {
         this.config = this.plugin.getPropertyConfig(this.file, this.fileAbsolute);
         this.holderSections.clear();
      }
   }

   public T get() {
      return this.compute(this.config, this.node);
   }

   public T get(PropertyHolder holder) {
      return this.get(holder.getPropertyNode());
   }

   public T get(String holder) {
      if (this.config != null || this.file != null && !this.file.isEmpty()) {
         this.holderSections.computeIfAbsent(holder, _holder -> this.config.getConfigurationSection(holder));
         return this.compute(this.holderSections.get(holder), this.node);
      } else {
         throw new IllegalArgumentException("Scope has not been set on property " + this.node);
      }
   }

   public T get(ConfigurationSection section) {
      if (this.config != null || this.file != null && !this.file.isEmpty()) {
         return this.compute(section, this.node);
      } else {
         throw new IllegalArgumentException("Scope has not been set on property " + this.node);
      }
   }

   public T get(PropertyHolder holder, String node) {
      return this.get(holder.getPropertyNode(), node);
   }

   public T get(String holder, String node) {
      if (this.config != null || this.file != null && !this.file.isEmpty()) {
         this.holderSections.computeIfAbsent(holder, _holder -> this.config.getConfigurationSection(holder));
         return this.compute(this.holderSections.get(holder), node);
      } else {
         throw new IllegalArgumentException("Scope has not been set on property " + this.node);
      }
   }

   public T get(ConfigurationSection section, String node) {
      if (this.config != null || this.file != null && !this.file.isEmpty()) {
         return this.compute(section, node);
      } else {
         throw new IllegalArgumentException("Scope has not been set on property " + this.node);
      }
   }

   public T fget(Object scope) {
      this.setScope(scope);
      return this.compute(this.config, this.node);
   }

   public T fget(Object scope, PropertyHolder holder) {
      this.setScope(scope);
      return this.get(holder.getPropertyNode());
   }

   public T fget(Object scope, String holder) {
      this.setScope(scope);
      if (this.config == null && this.file == null) {
         throw new IllegalArgumentException("Scope has not been set on property " + this.node);
      } else {
         this.holderSections.computeIfAbsent(holder, _holder -> this.config.getConfigurationSection(holder));
         return this.compute(this.holderSections.get(holder), this.node);
      }
   }

   public T fget(Object scope, ConfigurationSection section) {
      this.setScope(scope);
      if (this.config == null && this.file == null) {
         throw new IllegalArgumentException("Scope has not been set on property " + this.node);
      } else {
         return this.compute(section, this.node);
      }
   }

   public T fget(Object scope, PropertyHolder holder, String node) {
      this.setScope(scope);
      return this.get(holder.getPropertyNode(), node);
   }

   public T fget(Object scope, String holder, String node) {
      this.setScope(scope);
      if (this.config == null && this.file == null) {
         throw new IllegalArgumentException("Scope has not been set on property " + this.node);
      } else {
         this.holderSections.computeIfAbsent(holder, _holder -> this.config.getConfigurationSection(holder));
         return this.compute(this.holderSections.get(holder), node);
      }
   }

   public T fget(Object scope, ConfigurationSection section, String node) {
      this.setScope(scope);
      if (this.config == null && this.file == null) {
         throw new IllegalArgumentException("Scope has not been set on property " + this.node);
      } else {
         return this.compute(section, node);
      }
   }

   public T getset() {
      if (!this.config.isSet(this.node)) {
         T defaultValue = this.getDefaultValue();
         if (defaultValue != null) {
            this.set(defaultValue);
         }
      }

      return this.compute(this.config, this.node);
   }

   public T getset(PropertyHolder holder) {
      ConfigurationSection section = this.holderSections
         .computeIfAbsent(holder.getPropertyNode(), _holder -> this.config.getConfigurationSection(holder.getPropertyNode()));
      if (section == null || !section.isSet(this.node)) {
         T defaultValue = this.getDefaultValue();
         if (defaultValue != null) {
            this.set(holder.getPropertyNode(), defaultValue);
         }
      }

      return this.get(holder.getPropertyNode());
   }

   public T getset(String holder) {
      if (this.config != null || this.file != null && !this.file.isEmpty()) {
         ConfigurationSection section = this.holderSections.computeIfAbsent(holder, _holder -> this.config.getConfigurationSection(holder));
         if (section != null && !section.isSet(this.node)) {
            T defaultValue = this.getDefaultValue();
            if (defaultValue != null) {
               this.set(holder, defaultValue);
            }
         }

         return this.compute(section, this.node);
      } else {
         throw new IllegalArgumentException("Scope has not been set on property " + this.node);
      }
   }

   public T fgetset(Object scope) {
      this.setScope(scope);
      if (!this.config.isSet(this.node)) {
         T defaultValue = this.getDefaultValue();
         if (defaultValue != null) {
            this.fset(scope, defaultValue);
         }
      }

      return this.compute(this.config, this.node);
   }

   public T fgetset(Object scope, PropertyHolder holder) {
      this.setScope(scope);
      ConfigurationSection section = this.holderSections
         .computeIfAbsent(holder.getPropertyNode(), _holder -> this.config.getConfigurationSection(holder.getPropertyNode()));
      if (section != null && !section.isSet(this.node)) {
         T defaultValue = this.getDefaultValue();
         if (defaultValue != null) {
            this.fset(scope, holder.getPropertyNode(), defaultValue);
         }
      }

      return this.get(holder.getPropertyNode());
   }

   public T fgetset(Object scope, String holder) {
      this.setScope(scope);
      if (this.config == null && this.file == null) {
         throw new IllegalArgumentException("Scope has not been set on property " + this.node);
      } else {
         ConfigurationSection section = this.holderSections.computeIfAbsent(holder, _holder -> this.config.getConfigurationSection(holder));
         if (section != null && !section.isSet(this.node)) {
            T defaultValue = this.getDefaultValue();
            if (defaultValue != null) {
               this.fset(scope, holder, defaultValue);
            }
         }

         return this.compute(this.holderSections.get(holder), this.node);
      }
   }

   protected abstract T compute(ConfigurationSection configurationSection, String str);

   protected abstract T getDefaultValue();

   public void set(PropertyHolder holder, T value) {
      this.set(holder.getPropertyNode(), value);
   }

   public void set(T value) {
      this.set((String)null, value);
   }

   public void set(String holder, T value) {
      this.set(holder, value, true);
   }

   public void set(String holder, T value, boolean save) {
      if (this.config != null) {
         if (holder != null && !holder.isEmpty()) {
            this.config.set(holder + "." + this.node, value);
         } else if (this.node != null && !this.node.isEmpty()) {
            this.config.set(this.node, value);
         } else {
            this.config.set(holder, value);
         }

         if (save) {
            try {
               ((FileConfiguration)this.config).save(this.plugin.getPropertyFile(this.file));
            } catch (Error | Exception e) {
               e.printStackTrace();
            }
         }
      }
   }

   public void fset(Object scope, PropertyHolder holder, T value) {
      this.fset(scope, holder.getPropertyNode(), value);
   }

   public void fset(Object scope, T value) {
      this.setScope(scope);
      this.set(value);
   }

   public void fset(Object scope, String holder, T value) {
      this.setScope(scope);
      this.set(holder, value);
   }

   public void fsetStaged(Object scope, PropertyHolder holder, T value) {
      this.fsetStaged(scope, holder.getPropertyNode(), value);
   }

   public void fsetStaged(Object scope, T value) {
      this.setScope(scope);
      this.set((String)null, value, false);
   }

   public void fsetStaged(Object scope, String holder, T value) {
      this.setScope(scope);
      this.set(holder, value, false);
   }

   public void fdelete(Object scope, PropertyHolder holder) {
      this.fdelete(scope, holder.getPropertyNode());
   }

   public void fdelete(Object scope, String holder) {
      this.fdelete(scope, holder, true);
   }

   public void fdelete(Object scope, String holder, boolean save) {
      this.setScope(scope);
      String node;
      if (holder != null && !holder.isEmpty() && this.node != null && !this.node.isEmpty()) {
         node = holder + "." + this.node;
      } else if (this.node != null && !this.node.isEmpty()) {
         node = this.node;
      } else {
         node = holder;
      }

      this.config.set(node, null);
      if (save) {
         try {
            ((FileConfiguration)this.config).save(this.plugin.getPropertyFile(this.file));
         } catch (Error | Exception e) {
            e.printStackTrace();
         }
      }
   }

   public LuminePlugin getPlugin() {
      return this.plugin;
   }

   public ConfigurationSection getConfig() {
      return this.config;
   }

   public String getFile() {
      return this.file;
   }

   public Object getScope() {
      return this.scope;
   }

   public boolean isFileAbsolute() {
      return this.fileAbsolute;
   }

   public String getNode() {
      return this.node;
   }
}

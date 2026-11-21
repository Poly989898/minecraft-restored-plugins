package com.nexomc.nexo.pack.multipack;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.FileUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.io.File;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\"\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0005¢\u0006\u0004\b\f\u0010\rB\u0011\b\u0016\u0012\u0006\u0010\u000e\u001a\u00020\u000f¢\u0006\u0004\b\f\u0010\u0010J\u000e\u0010\u0007\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u0000J\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0005HÆ\u0003J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00030\bHÆ\u0003J\t\u0010 \u001a\u00020\nHÆ\u0003J\t\u0010!\u001a\u00020\u0005HÆ\u0003JK\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\u0005HÆ\u0001J\u0014\u0010#\u001a\u00020\u00052\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010$\u001a\u00020%HÖ\u0081\u0004J\n\u0010&\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u000b\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0014¨\u0006'"},
   d2 = {"Lcom/nexomc/nexo/pack/multipack/PackTemplate;", "", "id", "", "required", "", "default", "conflictsWith", "", "path", "Ljava/io/File;", "empty", "<init>", "(Ljava/lang/String;ZZLjava/util/Set;Ljava/io/File;Z)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getId", "()Ljava/lang/String;", "getRequired", "()Z", "getDefault", "getConflictsWith", "()Ljava/util/Set;", "getPath", "()Ljava/io/File;", "getEmpty", "other", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "hashCode", "", "toString", "com.nexomc:core"}
)
public final class PackTemplate {
   @NotNull
   private final String id;
   private final boolean required;
   private final boolean default_;
   @NotNull
   private final Set<String> conflictsWith;
   @NotNull
   private final File path;
   private final boolean empty;

   public PackTemplate(@NotNull String id, boolean required, boolean flag, @NotNull Set<String> conflictsWith, @NotNull File path, boolean empty) {
      super();
      this.id = id;
      this.required = required;
      this.default_ = flag;
      this.conflictsWith = conflictsWith;
      this.path = path;
      this.empty = empty;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   public final boolean getRequired() {
      return this.required;
   }

   public final boolean getDefault() {
      return this.default_;
   }

   @NotNull
   public final Set<String> getConflictsWith() {
      return this.conflictsWith;
   }

   @NotNull
   public final File getPath() {
      return this.path;
   }

   public final boolean getEmpty() {
      return this.empty;
   }

   public PackTemplate(@NotNull ConfigurationSection section) {
      /* this-alias removed */
      String name = section.getName();
      boolean flag = section.getBoolean("required");
      boolean flag2 = section.getBoolean("default");
      Set set = CollectionsKt.toSet((Iterable)NexoYamlKt.stringListOrSingle(section, "conflicts_with", (String)null));
      CharSequence charSequence = (CharSequence)NexoYamlKt.stringOrEmpty(section, "path");
      Object obj;
      if (charSequence.length() == 0) {
         Set set2 = set;
         boolean flag3 = flag2;
         boolean flag4 = flag;
         String str = name;
         int value = 0;
         String str2 = section.getName();
         String str3 = str2;
         /* this-alias removed */
         name = str;
         flag = flag4;
         flag2 = flag3;
         set = set2;
         obj = str3;
      } else {
         obj = charSequence;
      }

      String pathStr = (String)obj;
      Set set3 = set;
      boolean flag5 = flag2;
      boolean flag6 = flag;
      String str4 = name;
      int value2 = 0;
      File it = new File(pathStr);
      File it_3 = it;
      File file = it_3.isAbsolute() && it_3.exists() ? it_3 : null;
      if (file == null) {
         File it_2 = FilesKt.resolve(MultipackManager.Companion.getFolder(), pathStr);
         int value3 = 0;
         file = it_2.exists() ? it_2 : null;
         if (file == null) {
            file = NexoPlugin.Companion.instance().getDataFolder();
            File it_1 = FilesKt.resolve(file, pathStr);
            int value4 = 0;
            file = it_1.exists() ? it_1 : null;
            if (file == null) {
               file = FilesKt.resolve(FileUtils.INSTANCE.getServerFolder(), pathStr);
            }
         }
      }

      File file2 = file;
      this(str4, flag6, flag5, set3, file2, section.getBoolean("empty"));
   }

   public final boolean conflictsWith(@NotNull PackTemplate other) {
      return this.conflictsWith.contains(other.id) || other.conflictsWith.contains(this.id);
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   public final boolean component2() {
      return this.required;
   }

   public final boolean component3() {
      return this.default_;
   }

   @NotNull
   public final Set<String> component4() {
      return this.conflictsWith;
   }

   @NotNull
   public final File component5() {
      return this.path;
   }

   public final boolean component6() {
      return this.empty;
   }

   @NotNull
   public final PackTemplate copy(@NotNull String id, boolean required, boolean flag, @NotNull Set<String> conflictsWith, @NotNull File path, boolean empty) {
      return new PackTemplate(id, required, flag, conflictsWith, path, empty);
   }

   @NotNull
   public String toString() {
      return "PackTemplate(id=" + this.id + ", required=" + this.required + ", default_=" + this.default_ + ", conflictsWith=" + this.conflictsWith + ", path=" + this.path + ", empty=" + this.empty + ")";
   }

   public int hashCode() {
      int result = this.id.hashCode();
      result = result * 31 + Boolean.hashCode(this.required);
      result = result * 31 + Boolean.hashCode(this.default_);
      result = result * 31 + this.conflictsWith.hashCode();
      result = result * 31 + this.path.hashCode();
      result = result * 31 + Boolean.hashCode(this.empty);
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof PackTemplate)) {
         return false;
      } else {
         PackTemplate packTemplate = (PackTemplate)other;
         if (!Intrinsics.areEqual(this.id, packTemplate.id)) {
            return false;
         } else if (this.required != packTemplate.required) {
            return false;
         } else if (this.default_ != packTemplate.default_) {
            return false;
         } else if (!Intrinsics.areEqual(this.conflictsWith, packTemplate.conflictsWith)) {
            return false;
         } else if (!Intrinsics.areEqual(this.path, packTemplate.path)) {
            return false;
         } else {
            return this.empty == packTemplate.empty;
         }
      }
   }
}

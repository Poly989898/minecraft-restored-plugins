package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.gson.JsonObject;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.UpgradeTemplate;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpgradeData implements StatData, RandomStatData<UpgradeData> {
   @Nullable
   private final String reference;
   @Nullable
   private final String template;
   private final boolean workbench;
   private final boolean destroy;
   private final double success;
   private final int max;
   private final int min;
   private int level;

   @Nullable
   public String getReference() {
      return this.reference;
   }

   public boolean isWorkbench() {
      return this.workbench;
   }

   public boolean isDestroy() {
      return this.destroy;
   }

   public int getMax() {
      return this.max;
   }

   public int getMin() {
      return this.min;
   }

   public UpgradeData(@Nullable String var1, @Nullable String var2, boolean var3, boolean var4, int var5, double var6) {
      this(var1, var2, var3, var4, var5, 0, var6);
   }

   public UpgradeData(@Nullable String var1, @Nullable String var2, boolean var3, boolean var4, int var5, int var6, double var7) {
      this.reference = var1;
      this.template = var2;
      this.workbench = var3;
      this.destroy = var4;
      this.max = var5;
      this.min = var6;
      this.success = var7;
   }

   public UpgradeData(ConfigurationSection var1) {
      this.reference = var1.getString("reference");
      this.template = var1.getString("template");
      this.workbench = var1.getBoolean("workbench");
      this.destroy = var1.getBoolean("destroy");
      this.max = var1.getInt("max");
      this.min = var1.getInt("min", 0);
      this.success = var1.getDouble("success") / 100.0;
   }

   public UpgradeData(JsonObject var1) {
      this.workbench = var1.get("Workbench").getAsBoolean();
      this.destroy = var1.get("Destroy").getAsBoolean();
      this.template = var1.has("Template") ? var1.get("Template").getAsString() : null;
      this.reference = var1.has("Reference") ? var1.get("Reference").getAsString() : null;
      this.level = var1.get("Level").getAsInt();
      this.max = var1.get("Max").getAsInt();
      this.min = var1.has("Min") ? var1.get("Min").getAsInt() : 0;
      this.success = var1.get("Success").getAsDouble();
   }

   @Nullable
   public UpgradeTemplate getTemplate() {
      return this.template == null ? null : MMOItems.plugin.getUpgrades().getTemplate(this.template);
   }

   @Nullable
   public String getTemplateName() {
      return this.template;
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int var1) {
      this.level = var1;
   }

   public int getMaxUpgrades() {
      return this.max;
   }

   public boolean canLevelUp() {
      return this.max == 0 || this.level < this.max;
   }

   public boolean destroysOnFail() {
      return this.destroy;
   }

   public double getSuccess() {
      return this.success == 0.0 ? 1.0 : this.success;
   }

   @Deprecated
   public boolean matchesReference(UpgradeData var1) {
      return MMOUtils.checkReference(this.reference, var1.reference);
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   public void upgrade(@NotNull MMOItem var1) {
      if (this.getTemplate() == null) {
         MMOItems.plugin.getLogger().warning("Couldn't find upgrade template '" + this.template + "'. Does it exist?");
      } else {
         this.getTemplate().upgrade(var1);
      }
   }

   public JsonObject toJson() {
      JsonObject var1 = new JsonObject();
      if (this.reference != null && !this.reference.isEmpty()) {
         var1.addProperty("Reference", this.reference);
      }

      if (this.template != null && !this.template.isEmpty()) {
         var1.addProperty("Template", this.template);
      }

      var1.addProperty("Workbench", this.workbench);
      var1.addProperty("Destroy", this.destroy);
      var1.addProperty("Level", this.level);
      var1.addProperty("Max", this.max);
      var1.addProperty("Min", this.min);
      var1.addProperty("Success", this.success);
      return var1;
   }

   @Override
   public String toString() {
      return this.toJson().toString();
   }

   public UpgradeData randomize(MMOItemBuilder var1) {
      return this;
   }

   public UpgradeData clone() {
      return new UpgradeData(this.reference, this.template, this.workbench, this.destroy, this.max, this.min, this.success);
   }
}

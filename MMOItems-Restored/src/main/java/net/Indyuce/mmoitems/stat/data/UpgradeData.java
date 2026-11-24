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

   public UpgradeData(@Nullable String text, @Nullable String text2, boolean flag, boolean flag2, int count, double value) {
      this(text, text2, flag, flag2, count, 0, value);
   }

   public UpgradeData(@Nullable String text, @Nullable String text2, boolean flag, boolean flag2, int count, int count2, double value) {
      this.reference = text;
      this.template = text2;
      this.workbench = flag;
      this.destroy = flag2;
      this.max = count;
      this.min = count2;
      this.success = value;
   }

   public UpgradeData(ConfigurationSection section) {
      this.reference = section.getString("reference");
      this.template = section.getString("template");
      this.workbench = section.getBoolean("workbench");
      this.destroy = section.getBoolean("destroy");
      this.max = section.getInt("max");
      this.min = section.getInt("min", 0);
      this.success = section.getDouble("success") / 100.0;
   }

   public UpgradeData(JsonObject jsonObject) {
      this.workbench = jsonObject.get("Workbench").getAsBoolean();
      this.destroy = jsonObject.get("Destroy").getAsBoolean();
      this.template = jsonObject.has("Template") ? jsonObject.get("Template").getAsString() : null;
      this.reference = jsonObject.has("Reference") ? jsonObject.get("Reference").getAsString() : null;
      this.level = jsonObject.get("Level").getAsInt();
      this.max = jsonObject.get("Max").getAsInt();
      this.min = jsonObject.has("Min") ? jsonObject.get("Min").getAsInt() : 0;
      this.success = jsonObject.get("Success").getAsDouble();
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

   public void setLevel(int count) {
      this.level = count;
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
   public boolean matchesReference(UpgradeData upgradeData) {
      return MMOUtils.checkReference(this.reference, upgradeData.reference);
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   public void upgrade(@NotNull MMOItem mmoItem) {
      if (this.getTemplate() == null) {
         MMOItems.plugin.getLogger().warning("Couldn't find upgrade template '" + this.template + "'. Does it exist?");
      } else {
         this.getTemplate().upgrade(mmoItem);
      }
   }

   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      if (this.reference != null && !this.reference.isEmpty()) {
         jsonObject.addProperty("Reference", this.reference);
      }

      if (this.template != null && !this.template.isEmpty()) {
         jsonObject.addProperty("Template", this.template);
      }

      jsonObject.addProperty("Workbench", this.workbench);
      jsonObject.addProperty("Destroy", this.destroy);
      jsonObject.addProperty("Level", this.level);
      jsonObject.addProperty("Max", this.max);
      jsonObject.addProperty("Min", this.min);
      jsonObject.addProperty("Success", this.success);
      return jsonObject;
   }

   @Override
   public String toString() {
      return this.toJson().toString();
   }

   public UpgradeData randomize(MMOItemBuilder mmoItemBuilder) {
      return this;
   }

   public UpgradeData clone() {
      return new UpgradeData(this.reference, this.template, this.workbench, this.destroy, this.max, this.min, this.success);
   }
}

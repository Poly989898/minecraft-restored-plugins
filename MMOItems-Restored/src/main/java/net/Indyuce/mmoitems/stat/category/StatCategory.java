package net.Indyuce.mmoitems.stat.category;

import org.jetbrains.annotations.NotNull;

public class StatCategory {
   private final String id;
   private final String name;
   private final String loreTag;
   public static final StatCategory TEMPLATE_OPTION = new StatCategory("TEMPLATE_OPTION", "Template Option, Misc", "Template Option");
   public static final StatCategory SOULBOUND = new StatCategory("SOULBOUND", "Soulbound", "Soulbound");
   public static final StatCategory ELEMENTAL = new StatCategory("ELEMENTAL", "Elements", "Elements");
   public static final StatCategory VANILLA_ATTRIBUTE = new StatCategory("VANILLA_ATTRIBUTE", "Vanilla Attributes", "Vanilla Attribute");
   public static final StatCategory REQUIREMENT = new StatCategory("REQUIREMENT", "Item Requirements", "Item Requirement");
   public static final StatCategory USE_COST = new StatCategory("USE_COST", "Item Costs", "Use Cost");

   public StatCategory(String text, String text2, String text3) {
      this.id = text;
      this.name = text2;
      this.loreTag = text3;
   }

   @NotNull
   public String getId() {
      return this.id;
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   @NotNull
   public String getLoreTag() {
      return this.loreTag;
   }
}

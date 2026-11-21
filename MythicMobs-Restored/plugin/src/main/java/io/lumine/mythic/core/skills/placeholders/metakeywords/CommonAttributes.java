package io.lumine.mythic.core.skills.placeholders.metakeywords;

public final class CommonAttributes {
   public static final MetaKeywords.MetaKeywordsAttribute KEY = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("KEY")
      .names(new String[]{"key", "k"})
      .defaultValue("default")
      .build();
   public static final MetaKeywords.MetaKeywordsAttribute INDEX = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("INDEX")
      .names(new String[]{"index"})
      .defaultValue("0")
      .build();
   public static final MetaKeywords.MetaKeywordsAttribute VALUE = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("VALUE")
      .names(new String[]{"value", "val", "v"})
      .defaultValue("default")
      .build();
   public static final MetaKeywords.MetaKeywordsAttribute FROM = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("FROM")
      .names(new String[]{"from"})
      .defaultValue("0")
      .build();
   public static final MetaKeywords.MetaKeywordsAttribute TO = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("TO")
      .names(new String[]{"to"})
      .defaultValue("1")
      .build();
   public static final MetaKeywords.MetaKeywordsAttribute TYPE = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("TYPE")
      .names(new String[]{"type", "t"})
      .defaultValue("default")
      .build();
   public static final MetaKeywords.MetaKeywordsAttribute AMOUNT = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("AMOUNT")
      .names(new String[]{"amount", "a"})
      .defaultValue("0")
      .build();
   public static final MetaKeywords.MetaKeywordsAttribute WITH = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("WITH")
      .names(new String[]{"with"})
      .defaultValue("false")
      .build();
   public static final MetaKeywords.MetaKeywordsAttribute ENCHANTMENT = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("ENCHANTMENT")
      .names(new String[]{"enchantment", "enchant", "ench"})
      .defaultValue("default")
      .build();
   public static final MetaKeywords.MetaKeywordsAttribute NAMESPACE = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("NAMESPACE")
      .names(new String[]{"namespace"})
      .defaultValue("minecraft")
      .build();
   public static final MetaKeywords.MetaKeywordsAttribute REGEX = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("REGEX")
      .names(new String[]{"regex"})
      .defaultValue("\\w")
      .build();
}

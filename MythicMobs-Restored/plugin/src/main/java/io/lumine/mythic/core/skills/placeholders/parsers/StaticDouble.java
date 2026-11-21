package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Map;

public class StaticDouble extends PlaceholderDoubleImpl {
   private double value;

   public StaticDouble(String string) {
      this.value = Double.valueOf(string);
   }

   public StaticDouble(double amount) {
      this.value = amount;
   }

   @Override
   public boolean isStaticallyEqualTo(double value) {
      return this.value == value;
   }

   @Override
   public boolean isStatic() {
      return true;
   }

   @Override
   public double get() {
      return this.value;
   }

   @Override
   public double get(PlaceholderMeta meta) {
      return this.value;
   }

   @Override
   public double get(AbstractEntity entity) {
      return this.value;
   }

   @Override
   public double get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value;
   }

   @Override
   public double get(SkillCaster caster) {
      return this.value;
   }

   @Override
   public double get(PlaceholderContext context) {
      return this.value;
   }

   @Override
   public double get(PlaceholderMeta meta, Map<String, Double> extraVariables) {
      return this.value;
   }

   @Override
   public double get(AbstractEntity entity, Map<String, Double> extraVariables) {
      return this.value;
   }

   @Override
   public double get(PlaceholderMeta meta, AbstractEntity entity, Map<String, Double> extraVariables) {
      return this.value;
   }

   @Override
   public double get(SkillCaster caster, Map<String, Double> extraVariables) {
      return this.value;
   }

   @Override
   public double get(PlaceholderContext context, Map<String, Double> extraVariables) {
      return this.value;
   }

   @Override
   public String toString() {
      return String.valueOf(this.value);
   }

   @Override
   public String serialize() {
      return String.valueOf(this.value);
   }
}

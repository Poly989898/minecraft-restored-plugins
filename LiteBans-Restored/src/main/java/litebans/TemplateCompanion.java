package litebans;

public final class TemplateCompanion {
   private TemplateCompanion() {
   }

   public final TemplateInfo obtainTemplateInfo() {
      return Template.getEmptyInfo();
   }

   public final Template obtainTemplate() {
      return Template.getNone();
   }

   public TemplateCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}

package me.TechsCode.UltraPermissions.base.views.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XEnchantment;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.translations.TranslationManager;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public class LanguagePane extends SettingsPane {
   private static final Phrase NAME = Phrase.create("languagePane.name", "Language");
   private static final Phrase SELECT_ACTION = Phrase.create(
      "languagePane.select.action", "**Click** to **select** this language", Colors.GRAY, Colors.AQUA, Colors.YELLOW
   );
   private static final Phrase SELECTED_INDICATOR = Phrase.create(
      "languagePane.select.indicator", "This language is **currently** selected", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase PHRASES = Phrase.create("languagePane.phrases", "Phrases: **%phrases%**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase COVERAGE = Phrase.create("languagePane.coverage", "Coverage: **%coverage%%**", Colors.GRAY, Colors.GREEN);
   private static final Phrase NEXT_TITLE = Phrase.create("languagePane.next.title", "Next");
   private static final Phrase NEXT_ACTION = Phrase.create("languagePane.next.action", "Click to go to the next page", Colors.GRAY);
   private static final Phrase PREVIOUS_TITLE = Phrase.create("languagePane.previous.title", "Previous");
   private static final Phrase PREVIOUS_ACTION = Phrase.create("languagePane.previous.action", "Click to go to the previous page", Colors.GRAY);
   private final SpigotTechPlugin plugin;
   private final int maxObjectsPerPage = 14;
   private int page = 0;
   private int pages;

   public LanguagePane(Player player, SettingsView settingsView, SpigotTechPlugin plugin) {
      super(player, settingsView);
      this.plugin = plugin;
   }

   @Override
   public String getName() {
      return NAME.toString();
   }

   @Override
   public XMaterial getIcon() {
      return XMaterial.WRITABLE_BOOK;
   }

   @Override
   public void construct(Model model) {
      int[] intValues = this.getInnerContainerSlots();
      TranslationManager translationManager = this.plugin.getTranslationManager();
      List languages = translationManager.getAvailableLanguages().stream().filter(language -> !translationManager.getPhrases(language).isEmpty()).collect(Collectors.toList());
      Map phraseCounts = translationManager.getAvailableLanguages().stream().collect(Collectors.toMap(language -> (String)language, language -> translationManager.getPhrases(language).size()));
      int maxPhrases = Collections.<Integer>max(phraseCounts.values());
      int start = Math.min(languages.size(), this.page * 14);
      int end = Math.min(languages.size(), start + 14);
      Iterator iterator = new ArrayList(languages).subList(start, end).iterator();

      for (int i : intValues) {
         if (iterator.hasNext()) {
            String languageCode = (String)iterator.next();
            int phrases = (Integer)phraseCounts.get(languageCode);
            int coverage = Math.round((float)phrases / maxPhrases * 100.0F);
            boolean selected = translationManager.getSelectedLanguage().equals(languageCode);
            model.button(i, button -> this.LanguageButton(button, languageCode, phrases, coverage, selected));
         } else {
            model.button(i, language -> language.material(XMaterial.WHITE_STAINED_GLASS_PANE).name());
         }
      }

      this.pages = (int)Math.round(Math.ceil(languages.size() / 14.0));
      if (this.page != 0) {
         model.button(language -> {
            language.material(XMaterial.ARROW).name(Animation.fading(PREVIOUS_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(PREVIOUS_ACTION.get());
            language.action(actionType -> {
               this.page--;
               if (this.page < 0) {
                  this.page = 0;
               }
            });
         }, 46);
      }

      if (this.page + 1 < this.pages) {
         model.button(language -> {
            language.material(XMaterial.ARROW).name(Animation.fading(NEXT_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(NEXT_ACTION.get());
            language.action(actionType -> this.page++);
         }, 54);
      }
   }

   private void LanguageButton(Button button, String language, int phrases, int coverage, boolean selected) {
      button.material(XMaterial.PAPER)
         .name(Animation.wave(language, Colors.GOLD, Colors.YELLOW))
         .lore(
            selected ? SELECTED_INDICATOR.get() : SELECT_ACTION.get(),
            "",
            PHRASES.get().replace("%phrases%", phrases + ""),
            COVERAGE.get().replace("%coverage%", coverage + "")
         );
      if (selected) {
         button.item().showEnchantments(false).addEnchantment(XEnchantment.LUCK_OF_THE_SEA.getEnchant(), 1);
      }

      button.action(actionType -> {
         if (!selected) {
            this.plugin.getTranslationManager().setSelectedLanguage(language);
         }
      });
   }
}

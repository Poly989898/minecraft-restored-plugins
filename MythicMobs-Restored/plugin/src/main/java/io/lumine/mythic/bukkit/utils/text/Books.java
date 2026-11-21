package io.lumine.mythic.bukkit.utils.text;

import java.util.Collection;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.inventory.Book.Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public final class Books {
   private static final MiniMessage bookParser = MiniMessage.miniMessage();

   public static Book createBook(String title, String author, Collection<String> pages) {
      Component bookTitle = bookParser.deserialize(title);
      Component bookAuthor = bookParser.deserialize(author);
      Builder builder = Book.builder().title(bookTitle).author(bookAuthor);

      for (String page : pages) {
         builder.addPage(bookParser.deserialize(page));
      }

      return builder.build();
   }

   public static Book createBook(String title, String author, String... pages) {
      Component bookTitle = bookParser.deserialize(title);
      Component bookAuthor = bookParser.deserialize(author);
      Builder builder = Book.builder().title(bookTitle).author(bookAuthor);

      for (String page : pages) {
         builder.addPage(bookParser.deserialize(page));
      }

      return builder.build();
   }

   public static void openBook(Player player, Book book) {
      Audience audience = Audience.audience(new Audience[]{player});
      audience.openBook(book);
   }
}

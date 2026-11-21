package litebans;

import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PlatformLogger extends Logger {
   private final org.slf4j.Logger logger;

   public PlatformLogger(org.slf4j.Logger logger, String text) {
      super(text, null);
      this.logger = logger;
   }

   @Override
   public void addHandler(Handler handler) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void config(String text) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void entering(String text, String text2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void entering(String text, String text2, Object obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void entering(String text, String text2, Object[] objs) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void exiting(String text, String text2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void exiting(String text, String text2, Object obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void fine(String text) {
      this.logger.trace(text);
   }

   @Override
   public void fine(Supplier supplier) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void finer(String text) {
      this.logger.trace(text);
   }

   @Override
   public void finer(Supplier supplier) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void finest(String text) {
   }

   @Override
   public void finest(Supplier supplier) {
   }

   @Override
   public Filter getFilter() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setFilter(Filter filter) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Handler[] getHandlers() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Level getLevel() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setLevel(Level level) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getName() {
      return this.logger.getName();
   }

   @Override
   public Logger getParent() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setParent(Logger logger) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResourceBundle getResourceBundle() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setResourceBundle(ResourceBundle resourceBundle) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getResourceBundleName() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean getUseParentHandlers() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setUseParentHandlers(boolean flag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void info(String text) {
      this.logger.info(text);
   }

   @Override
   public void info(Supplier supplier) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isLoggable(Level level) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void log(LogRecord logRecord) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void log(Level level, String text) {
      switch (level.getName()) {
         case "WARNING":
            this.handleText(text);
            break;
         case "SEVERE":
            this.severe(text);
            break;
         default:
            this.info(text);
      }
   }

   @Override
   public void log(Level level, Supplier supplier) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void log(Level level, String text, Object obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void log(Level level, String text, Object[] objs) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void log(Level level, String text, Throwable error) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void log(Level level, Throwable error, Supplier supplier) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void logp(Level level, String text, String text2, String text3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void logp(Level level, String text, String text2, Supplier supplier) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void logp(Level level, String text, String text2, String text3, Object obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void logp(Level level, String text, String text2, String text3, Object[] objs) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void logp(Level level, String text, String text2, String text3, Throwable error) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void logp(Level level, String text, String text2, Throwable error, Supplier supplier) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void logrb(Level level, String text, String text2, ResourceBundle resourceBundle, String text3, Object[] objs) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void logrb(Level level, String text, String text2, ResourceBundle resourceBundle, String text3, Throwable error) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void removeHandler(Handler handler) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void severe(String text) {
      this.logger.error(text);
   }

   @Override
   public void severe(Supplier supplier) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void throwing(String text, String text2, Throwable error) {
      throw new UnsupportedOperationException();
   }

   public void handleText(String text) {
      this.logger.warn(text);
   }

   @Override
   public void warning(String text) {
      this.logger.warn(text);
   }

   @Override
   public void warning(Supplier supplier) {
      throw new UnsupportedOperationException();
   }
}

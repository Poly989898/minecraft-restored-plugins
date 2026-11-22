package net.Zrips.CMILib.FileHandler;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigReader extends YamlConfiguration {
  private HashMap<String, String> comments;
  private HashMap<String, Object> contents;
  YamlConfiguration config;
  private String p = null;
  private File file = null;
  private boolean recordContents = false;
  private Plugin plugin;
  String[] waitingComment = null;

  public ConfigReader(Plugin plugin, String fileName) throws Exception {
    this(new File(plugin.getDataFolder(), fileName));
    this.plugin = plugin;
  }

  public ConfigReader(File file) throws Exception {
    this(null, file);
  }

  public ConfigReader(Plugin plugin, File file) throws Exception {
    this.comments = new HashMap<>();
    this.contents = new HashMap<>();
    this.file = file;
    this.config = this.getyml(file);
    this.plugin = plugin;
  }

  public void load() {
    try {
      if (this.file.isFile()) {
        try {
          this.options().parseComments(false);
        } catch (Throwable var3) {
        }

        this.load(this.file);

        try {
          this.options().parseComments(true);
        } catch (Throwable var2) {
        }
      }
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  public void loadExistingData() {
    this.copyDefaults(true);
  }

  public void save(String file) throws IOException {
    if (file == null) {
      throw new IllegalArgumentException("File cannot be null");
    }

    this.save(new File(file));
  }

  public void save(File file) throws IOException {
    if (file == null) {
      throw new IllegalArgumentException("File cannot be null");
    }

    Files.createParentDirs(file);
    String data = this.insertComments(this.saveToString());
    PrintWriter writer = new PrintWriter(file, "UTF-8");

    try {
      writer.write(data);
    } finally {
      writer.close();
    }
  }

  private String insertComments(String yaml) {
    if (this.comments.isEmpty()) {
      return yaml;
    }

    String[] yamlContents = yaml.split("[" + System.getProperty("line.separator") + "]");
    StringBuilder newContents = new StringBuilder();
    StringBuilder currentPath = new StringBuilder();
    boolean commentedPath = false;
    boolean node = false;
    int depth = 0;
    boolean firstLine = true;

    for (String line : yamlContents) {
      if (firstLine) {
        firstLine = false;
        if (line.startsWith("#")) {
          continue;
        }
      }

      boolean keyOk = true;
      if (line.contains(": ")) {
        int index = 0;
        index = line.indexOf(": ");
        if (index < 0) {
          index = line.length() - 1;
        }

        int whiteSpace = 0;

        for (int n = 0; n < line.length() && line.charAt(n) == ' '; n++) {
          whiteSpace++;
        }

        String key = line.substring(whiteSpace, index);
        if (key.contains(" ")) {
          keyOk = false;
        } else if (key.contains("&")) {
          keyOk = false;
        } else if (key.contains(".")) {
          keyOk = false;
        } else if (key.contains("'")) {
          keyOk = false;
        } else if (key.contains("\"")) {
          keyOk = false;
        }
      }

      if (line.contains(": ") && keyOk
          || line.length() > 1 && line.charAt(line.length() - 1) == ':') {
        commentedPath = false;
        node = true;
        int index = line.indexOf(": ");
        if (index < 0) {
          index = line.length() - 1;
        }

        if (currentPath.toString().isEmpty()) {
          currentPath = new StringBuilder(line.substring(0, index));
        } else {
          int whiteSpace = 0;

          for (int n = 0; n < line.length() && line.charAt(n) == ' '; n++) {
            whiteSpace++;
          }

          if (whiteSpace / 2 > depth) {
            currentPath.append(".").append(line.substring(whiteSpace, index));
            depth++;
          } else if (whiteSpace / 2 < depth) {
            int newDepth = whiteSpace / 2;

            for (int i = 0; i < depth - newDepth; i++) {
              currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "");
            }

            int lastIndex = currentPath.lastIndexOf(".");
            if (lastIndex < 0) {
              currentPath = new StringBuilder();
            } else {
              currentPath
                  .replace(currentPath.lastIndexOf("."), currentPath.length(), "")
                  .append(".");
            }

            currentPath.append(line.substring(whiteSpace, index));
            depth = newDepth;
          } else {
            int lastIndex = currentPath.lastIndexOf(".");
            if (lastIndex < 0) {
              currentPath = new StringBuilder();
            } else {
              currentPath
                  .replace(currentPath.lastIndexOf("."), currentPath.length(), "")
                  .append(".");
            }

            currentPath.append(line.substring(whiteSpace, index));
          }
        }
      } else {
        node = false;
      }

      StringBuilder newLine = new StringBuilder(line);
      if (node) {
        String comment = null;
        if (!commentedPath) {
          comment = this.comments.get(currentPath.toString());
        }

        if (comment != null && !comment.isEmpty()) {
          newLine.insert(0, System.getProperty("line.separator")).insert(0, comment);
          String unusedString = null;
          commentedPath = true;
        }
      }

      newLine.append(System.getProperty("line.separator"));
      newContents.append(newLine.toString());
    }

    return newContents.toString();
  }

  public void addHeaderComments(List<String> comments) {
    if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
      try {
        this.getC().options().setHeader(comments);
        return;
      } catch (Throwable var3) {
      }
    }

    this.getC().options().header(formStringBuilder(comments).toString());
  }

  public void appendComment(String path, String... commentLines) {
    this.addComment(path, true, commentLines);
  }

  public void addComment(String path, String... commentLines) {
    this.addComment(path, false, commentLines);
  }

  public void addComment(String path, boolean append, String... commentLines) {
    if (this.p != null) {
      path = this.p + path;
    }

    StringBuilder commentstring = new StringBuilder();
    if (append && this.comments.containsKey(path)) {
      commentstring.append(this.comments.get(path));
    }

    String leadingSpaces = "";

    for (int n = 0; n < path.length(); n++) {
      if (path.charAt(n) == '.') {
        leadingSpaces = leadingSpaces + "  ";
      }
    }

    for (String line : commentLines) {
      if (!line.isEmpty()) {
        line = leadingSpaces + "# " + line;
      }

      if (commentstring.length() > 0) {
        commentstring.append(System.getProperty("line.separator"));
      }

      commentstring.append(line);
    }

    this.comments.put(path, commentstring.toString());
  }

  public YamlConfiguration getyml(File file) throws Exception {
    YamlConfiguration config = new YamlConfiguration();
    FileInputStream fileinputstream = null;

    try {
      fileinputstream = new FileInputStream(file);
      InputStreamReader str = new InputStreamReader(fileinputstream, Charset.forName("UTF-8"));
      config.load(str);
      str.close();
    } catch (FileNotFoundException var14) {
    } catch (InvalidConfigurationException | IOException e) {
      e.printStackTrace();
      this.saveToBackup();
      throw e;
    } finally {
      if (fileinputstream != null) {
        try {
          fileinputstream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return config;
  }

  public void saveToBackup() {
    this.saveToBackup(CMIBackupMessageType.Warning);
  }

  @Deprecated
  public void saveToBackup(boolean inform) {
    this.saveToBackup(CMIBackupMessageType.Info);
  }

  public void saveToBackup(CMIBackupMessageType type) {
    this.saveToBackup(this.plugin, type);
  }

  public void saveToBackup(Plugin originPlugin, CMIBackupMessageType type) {
    File dataFolder =
        originPlugin == null ? CMILib.getInstance().getDataFolder() : originPlugin.getDataFolder();
    File cc = new File(dataFolder, "FileBackups");
    if (!cc.isDirectory()) {
      cc.mkdir();
    }

    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss ");
    String newFileName = dateFormat.format(date) + this.file.getName();
    String path =
        dataFolder.getPath() + File.separator + "FileBackups" + File.separator + newFileName;
    switch (type) {
      case Error:
        CMIMessages.consoleMessage(
            "&cFailed to load &6"
                + this.file.getName()
                + "&c! Backup have been saved into &6"
                + path);
        break;
      case Info:
        CMIMessages.consoleMessage(
            "&eBackup file of &6" + this.file.getName() + " &ehave been saved into &6" + path);
        break;
      case Warning:
        CMIMessages.consoleMessage(
            "&4Something went wrong while loading &6"
                + this.file.getName()
                + "&4! Backup have been saved into &6"
                + path);
      case None:
    }

    File f = new File(dataFolder, "FileBackups" + File.separator + newFileName);

    try {
      Files.copy(this.file, f);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void save() {
    try {
      this.save(this.file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void newLn(StringBuilder header) {
    header.append(System.lineSeparator());
  }

  private static StringBuilder formStringBuilder(List<String> list) {
    StringBuilder header = new StringBuilder();

    for (String one : list) {
      header.append(one);
      newLn(header);
    }

    return header;
  }

  public void header(List<String> list) {
    if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
      try {
        this.options().setHeader(list);
        return;
      } catch (Throwable var3) {
      }
    }

    this.options().header(formStringBuilder(list).toString());
  }

  private void checkWaitingComment(String path) {
    if (this.waitingComment != null) {
      this.addComment(path, this.waitingComment);
      this.waitingComment = null;
    }
  }

  public YamlConfiguration getC() {
    return this.config;
  }

  public void copyDefaults(boolean value) {
    this.getC().options().copyDefaults(value);
  }

  public String process(String path, Object value) {
    if (this.p != null) {
      path = this.p + path;
    }

    this.checkWaitingComment(path);
    this.config.addDefault(path, value);
    this.copySetting(path);
    return path;
  }

  public Object get(String path, Location boo) {
    path = this.process(path, boo);
    return this.config.get(path);
  }

  public Boolean get(String path, Boolean boo) {
    path = this.process(path, boo);
    return this.config.getBoolean(path);
  }

  public int get(String path, int boo) {
    path = this.process(path, boo);
    return this.config.getInt(path);
  }

  public Map<String, Object> get(String path, Map<String, Object> boo) {
    path = this.process(path, boo);
    return (Map<String, Object>) this.config.get(path);
  }

  public int get(String path, Integer boo) {
    path = this.process(path, boo);
    return this.config.getInt(path);
  }

  public List<Integer> getIntList(String path, List<Integer> list) {
    path = this.process(path, list);
    return this.config.getIntegerList(path);
  }

  private static String convertUnicode(String st) {
    try {
      if (st == null) {
        return st;
      }

      if (!st.contains("\\u")) {
        return st;
      }

      StringBuilder sb = new StringBuilder(st.length());

      for (int i = 0; i < st.length(); i++) {
        char ch = st.charAt(i);
        if (ch == '\\') {
          char nextChar = i == st.length() - 1 ? '\\' : st.charAt(i + 1);
          if (nextChar >= '0' && nextChar <= '7') {
            String code = "" + nextChar;
            i++;
            if (i < st.length() - 1 && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7') {
              code = code + st.charAt(i + 1);
              i++;
              if (i < st.length() - 1 && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7') {
                code = code + st.charAt(i + 1);
                i++;
              }
            }

            sb.append((char) Integer.parseInt(code, 8));
            continue;
          }

          switch (nextChar) {
            case 'u':
              if (i < st.length() - 5) {
                try {
                  int code =
                      Integer.parseInt(
                          ""
                              + st.charAt(i + 2)
                              + st.charAt(i + 3)
                              + st.charAt(i + 4)
                              + st.charAt(i + 5),
                          16);
                  sb.append(Character.toChars(code));
                } catch (NumberFormatException e) {
                  sb.append("\\");
                  continue;
                }

                i += 5;
                continue;
              }

              ch = 'u';
              i++;
              break;
            default:
              sb.append("\\");
              continue;
          }
        }

        sb.append(ch);
      }

      return sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return st;
    }
  }

  public List<String> get(String path, List<String> list) {
    path = this.process(path, list);
    if (this.recordContents) {
      this.contents.put(path, this.config.isList(path) ? this.config.getStringList(path) : list);
    }

    List<String> ls = this.config.getStringList(path);

    for (int p = 0; p < ls.size(); p++) {
      String st = convertUnicode(ls.get(p));
      ls.set(p, st);
    }

    return ls;
  }

  public String get(String path, String boo) {
    path = this.process(path, boo);
    if (this.recordContents) {
      this.contents.put(path, this.config.isString(path) ? this.config.getString(path) : boo);
    }

    return convertUnicode(this.config.getString(path));
  }

  public Double get(String path, Double boo) {
    path = this.process(path, boo);
    return this.config.getDouble(path);
  }

  private synchronized void copySetting(String path) {
    this.set(path, this.config.get(path));
  }

  public void resetP() {
    this.p = null;
  }

  @Deprecated
  public void setP(String cmd) {
    this.p = "command." + cmd + ".info.";
  }

  public void setFullPath(String path) {
    this.p = path;
  }

  public String getPath() {
    return this.p;
  }

  public void setRecordContents(boolean recordContents) {
    this.recordContents = recordContents;
  }

  public HashMap<String, Object> getContents() {
    return this.contents;
  }

  public File getFile() {
    return this.file;
  }

  public void copyDefaults() {
    this.getC().options().copyDefaults(true);
  }

  public void forceSet(String path, Object obj) {
    this.config.set(path, obj);
    this.copySetting(path);
  }

  public String getObject(String path, Object boo) {
    if (path == null) {
      return convertUnicode(this.config.getString(path));
    }

    path = this.process(path, boo);
    if (this.recordContents) {
      this.contents.put(path, this.config.isString(path) ? this.config.getString(path) : boo);
    }

    return convertUnicode(this.config.getString(path));
  }
}

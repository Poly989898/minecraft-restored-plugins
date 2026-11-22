package com.Zrips.CMI.Modules.Backup;

import com.Zrips.CMI.Modules.DataBase.DBDAO;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CMIBackupFeedback {
  private File file = null;
  private Map<DBDAO.DBTables, Boolean> tables = new HashMap<>();

  public CMIBackupFeedback(File backupFile, Map<DBDAO.DBTables, Boolean> map) {
    this.file = backupFile;
    this.tables = map;
  }

  public CMIBackupFeedback(File backupFile) {
    this.file = backupFile;
  }

  public File getFile() {
    return this.file;
  }

  public CMIBackupFeedback setFile(File backupFile) {
    this.file = backupFile;
    return this;
  }

  public CMIBackupFeedback setTables(Map<DBDAO.DBTables, Boolean> map) {
    this.tables = map;
    return this;
  }

  public Map<DBDAO.DBTables, Boolean> getTables() {
    return this.tables;
  }
}

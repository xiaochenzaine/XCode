package com.itsaky.androidide.treesitter.bash;

import com.itsaky.androidide.treesitter.TSLanguage;
import com.itsaky.androidide.treesitter.TSLanguageCache;

public final class TSLanguageBash {
  static {
    System.loadLibrary("tree-sitter-cpp");
  }

  private TSLanguageBash() {
    throw new UnsupportedOperationException();
  }

  public static TSLanguage getInstance() {
    var language = TSLanguageCache.get("bash");
    if (language != null) {
      return language;
    }
    language = TSLanguage.create("bash", Native.getInstance());
    TSLanguageCache.cache("bash", language);
    return language;
  }

  public static class Native {
    public static native long getInstance();
  }
}

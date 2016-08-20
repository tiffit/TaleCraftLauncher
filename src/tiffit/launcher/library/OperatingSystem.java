package tiffit.launcher.library;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public enum OperatingSystem{
  LINUX("linux", new String[] { "linux", "unix" }),  WINDOWS("windows", new String[] { "win" }),  OSX("osx", new String[] { "mac" }),  UNKNOWN("unknown", new String[0]);
  
  private final String name;
  private final String[] aliases;
  
  private OperatingSystem(String name, String... aliases)
  {
    this.name = name;
    this.aliases = (aliases == null ? new String[0] : aliases);
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String[] getAliases()
  {
    return this.aliases;
  }
  
  public boolean isSupported()
  {
    return this != UNKNOWN;
  }
  
  public String getJavaDir()
  {
    String separator = System.getProperty("file.separator");
    String path = System.getProperty("java.home") + separator + "bin" + separator;
    if ((getCurrentPlatform() == WINDOWS) && 
      (new File(path + "javaw.exe").isFile())) {
      return path + "javaw.exe";
    }
    return path + "java";
  }
  
  public static OperatingSystem getCurrentPlatform()
  {
    String osName = System.getProperty("os.name").toLowerCase();
    for (OperatingSystem os : values()) {
      for (String alias : os.getAliases()) {
        if (osName.contains(alias)) {
          return os;
        }
      }
    }
    return UNKNOWN;
  }
  
  public static void openLink(URI link)
  {
    try
    {
      Class<?> desktopClass = Class.forName("java.awt.Desktop");
      Object o = desktopClass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
      desktopClass.getMethod("browse", new Class[] { URI.class }).invoke(o, new Object[] { link });
    }
    catch (Throwable e)
    {
      if (getCurrentPlatform() == OSX) {
        try
        {
          Runtime.getRuntime().exec(new String[] { "/usr/bin/open", link.toString() });
        }
        catch (IOException e1)
        {
          System.out.println("Failed to open link " + link.toString());
        }
      } else {
    	  System.out.println("Failed to open link " + link.toString());
      }
    }
  }
  
  public static void openFolder(File path)
  {
    String absolutePath = path.getAbsolutePath();
    OperatingSystem os = getCurrentPlatform();
    if (os == OSX){
      try{
        Runtime.getRuntime().exec(new String[] { "/usr/bin/open", absolutePath });
        
        return;
      }
      catch (IOException e){
        e.printStackTrace();
      }
    }
    else if (os == WINDOWS){
      String cmd = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[] { absolutePath });
      try{
        Runtime.getRuntime().exec(cmd);
        return;
      }
      catch (IOException e){
        e.printStackTrace();
      }
    }
    try
    {
      Class<?> desktopClass = Class.forName("java.awt.Desktop");
      Object desktop = desktopClass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
      desktopClass.getMethod("browse", new Class[] { URI.class }).invoke(desktop, new Object[] { path.toURI() });
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
  }
}

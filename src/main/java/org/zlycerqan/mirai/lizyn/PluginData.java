package org.zlycerqan.mirai.lizyn;

import org.zlycerqan.mirai.lizyn.core.file.FileDirectionBuilder;

public class PluginData {
    public static String id = "org.zlycerqan.mirai.lizyn.lizyn-mirai-plugin";

    public static String name = "LizynMiraiPlugin";

    public static String version = "2.1";

    public static String author = "ZlycerQan";

    public static String information = "A description of her";

    public static String configPath = new FileDirectionBuilder(name, "config.yml").build();

    public static String cachePath = new FileDirectionBuilder(name, "cache").build();

}

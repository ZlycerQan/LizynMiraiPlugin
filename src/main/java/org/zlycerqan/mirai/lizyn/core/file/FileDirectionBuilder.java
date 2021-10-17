package org.zlycerqan.mirai.lizyn.core.file;

public class FileDirectionBuilder {

    public static String PAD = System.getProperty("os.name").toLowerCase().contains("windows") ? "\\" : "/";

    public final StringBuilder data = new StringBuilder();

    public FileDirectionBuilder(String... args) {
        for (String arg : args) {
            append(arg);
        }
    }

    public FileDirectionBuilder(FileDirectionBuilder builder) {
        append(builder.build());
    }

    public FileDirectionBuilder append(String name) {
        if (data.length() != 0) {
            data.append(PAD);
        }
        data.append(name);
        return this;
    }

    public String build() {
        return data.toString();
    }
}

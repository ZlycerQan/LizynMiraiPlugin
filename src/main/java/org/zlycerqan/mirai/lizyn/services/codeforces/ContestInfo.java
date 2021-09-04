package org.zlycerqan.mirai.lizyn.services.codeforces;

import java.util.Arrays;
import java.util.Date;

public class ContestInfo {

    private final int id;
    private final String name;
    private final String[] writers;
    private final String[] colors;
    private final int length;
    private final Date start;
    private final Date register;
    private final int status;

    /**
     *
     * @param id contest id
     * @param name contest name
     * @param writers writers
     * @param colors colors of writers
     * @param length length of contest
     *                seconds
     * @param start start time of contest
     * @param register register time of contest
     * @param status 0 before register
     *                1 after register
     */
    ContestInfo(int id, String name, String[] writers, String[] colors, int length, Date start, Date register, int status) {
        this.id = id;
        this.name = name;
        this.writers = writers;
        this.colors = colors;
        this.length = length;
        this.start = start;
        this.register = register;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ContestInfo {" + "\n" +
                "  id = " + id + "\n" +
                "  name = " + name + "\n" +
                "  writers = " + Arrays.toString(writers) + "\n" +
                "  colors = " + Arrays.toString(colors) + "\n" +
                "  length = " + length + "\n" +
                "  start = " + start + "\n" +
                "  register = " + register + "\n" +
                "  status = " + status + "\n" +
                '}';
    }

    public Date getRegister() {
        return register;
    }

    public Date getStart() {
        return start;
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String[] getColors() {
        return colors;
    }

    public String[] getWriters() {
        return writers;
    }

}

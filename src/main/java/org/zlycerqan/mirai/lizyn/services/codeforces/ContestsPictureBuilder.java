package org.zlycerqan.mirai.lizyn.services.codeforces;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ContestsPictureBuilder {

    private static final int[] WIDTHS = new int[] {1000, 600, 500, 300, 600};

    private static final int WIDTH = Arrays.stream(WIDTHS).sum();

    private static final int PAGE_TOP_MARGIN = 30;
    private static final int PAGE_BOTTOM_MARGIN = 30;
    private static final int PAGE_LEFT_MARGIN = 30;
    private static final int PAGE_RIGHT_MARGIN = 30;
    private static final int TOP_MARGIN = 30;
    private static final int BOTTOM_MARGIN = 30;
    private static final int LINE_SPACE = 1;
    private static final String FONT = "Arial";
    private static final int FONT_SIZE = 50;
    private static final int WRITER_FONT_SIZE = 40;
    private static final int CONTEST_NAME_NUMBER_LIMIT = 30;
    private static final String[] HEADERS = new String[] {"Name", "Writers", "Start", "Length", "Register"};

    private static final Font PLAIN_FONT = new Font(FONT, Font.PLAIN, FONT_SIZE);
    private static final Font BOLD_FONT = new Font(FONT, Font.BOLD, FONT_SIZE);
    private static final Font WRITER_PLAIN_FONT = new Font(FONT, Font.PLAIN, WRITER_FONT_SIZE);
    private static final Font WRITER_BOLD_FONT = new Font(FONT, Font.BOLD, WRITER_FONT_SIZE);
    private static final BufferedImage TEMPLATE_BUFFERED_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    private static final Graphics2D TEMPLATE_GRAPHICS2D = (Graphics2D) TEMPLATE_BUFFERED_IMAGE.getGraphics();
    private static final FontMetrics PLAIN_FONT_METRICS;
    private static final FontMetrics BOLD_FONT_METRICS;
    private static final FontMetrics WRITER_PLAIN_FONT_METRICS;
    private static final FontMetrics WRITER_BOLD_FONT_METRICS;
    static {
        TEMPLATE_GRAPHICS2D.setFont(PLAIN_FONT);
        PLAIN_FONT_METRICS = TEMPLATE_GRAPHICS2D.getFontMetrics();
        TEMPLATE_GRAPHICS2D.setFont(BOLD_FONT);
        BOLD_FONT_METRICS = TEMPLATE_GRAPHICS2D.getFontMetrics();
        TEMPLATE_GRAPHICS2D.setFont(WRITER_PLAIN_FONT);
        WRITER_PLAIN_FONT_METRICS = TEMPLATE_GRAPHICS2D.getFontMetrics();
        TEMPLATE_GRAPHICS2D.setFont(WRITER_BOLD_FONT);
        WRITER_BOLD_FONT_METRICS = TEMPLATE_GRAPHICS2D.getFontMetrics();
    }

    public static BufferedImage builderContestsPicture(ArrayList<ContestInfo> contests) {
        if (contests.size() == 0) {
            int width = 500;
            int height = 500;
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(new Font("Arial", Font.PLAIN, 30));
            graphics2D.fillRect(0, 0, width, height);
            graphics2D.setColor(Color.BLACK);
            String sen = "No Contest Currently!";
            graphics2D.drawString(sen, (width - graphics2D.getFontMetrics().stringWidth(sen)) / 2, (height - graphics2D.getFontMetrics().getHeight()) / 2);
            return bufferedImage;
        }
        int headersHeight = BOLD_FONT_METRICS.getHeight() + TOP_MARGIN + BOTTOM_MARGIN;

        String[][] contestNameText = new String[contests.size()][];

        for (int i = 0; i < contestNameText.length; ++ i) {
            contestNameText[i] = splitContestName(contests.get(i).getName());
        }

        int[] heights = new int[contests.size()];
        for (int i = 0; i < contests.size(); ++ i) {
            int whs = 0;
            for (int j = 0; j < contests.get(i).getColors().length; ++ j) {
                if (contests.get(i).getColors()[j].equals("black")) {
                    whs += WRITER_PLAIN_FONT_METRICS.getHeight();
                } else {
                    whs += WRITER_BOLD_FONT_METRICS.getHeight();
                }
            }
            whs += LINE_SPACE * (contests.get(i).getColors().length - 1);

            heights[i] = Math.max(whs, contestNameText[i].length * PLAIN_FONT_METRICS.getHeight() + (contestNameText[i].length - 1) * LINE_SPACE);
            if (contests.get(i).getStatus() == 1) {
                heights[i] = Math.max(heights[i], BOLD_FONT_METRICS.getHeight() + LINE_SPACE + PLAIN_FONT_METRICS.getHeight());
            }
            heights[i] += TOP_MARGIN + BOTTOM_MARGIN;
        }
        int pageWidth = WIDTH + PAGE_LEFT_MARGIN + PAGE_RIGHT_MARGIN;
        int pageHeight = Arrays.stream(heights).sum() + headersHeight + PAGE_TOP_MARGIN + PAGE_BOTTOM_MARGIN;

        BufferedImage bufferedImage = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setStroke(new BasicStroke(3.0f));
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, pageWidth, pageHeight);
        addTableFrame(graphics2D, PAGE_LEFT_MARGIN, PAGE_TOP_MARGIN, headersHeight);

        graphics2D.setFont(BOLD_FONT);
        graphics2D.setColor(Color.BLACK);
        int nx = PAGE_LEFT_MARGIN;
        for (int i = 0; i < WIDTHS.length; ++ i) {
            graphics2D.drawString(HEADERS[i], nx + getStartPos(WIDTHS[i], BOLD_FONT_METRICS.stringWidth(HEADERS[i])), PAGE_TOP_MARGIN + getStartPos(headersHeight, BOLD_FONT_METRICS.getHeight()) + BOLD_FONT_METRICS.getHeight());
            nx += WIDTHS[i];
        }

        nx = PAGE_LEFT_MARGIN;
        int ny = PAGE_TOP_MARGIN + headersHeight;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        for (int i = 0; i < contests.size(); ++ i) {

            int cx = nx, cy = ny, px, py;

            addTableFrame(graphics2D, cx, cy, heights[i]);

            graphics2D.setFont(PLAIN_FONT);
            graphics2D.setColor(Color.BLACK);
            int charHeight = PLAIN_FONT_METRICS.getHeight();
            py = cy + getStartPos(heights[i], charHeight * contestNameText[i].length + LINE_SPACE * (contestNameText[i].length - 1));
            for (int j = 0; j < contestNameText[i].length; ++ j) {
                px = cx + getStartPos(WIDTHS[0], PLAIN_FONT_METRICS.stringWidth(contestNameText[i][j]));
                graphics2D.drawString(contestNameText[i][j], px, py + PLAIN_FONT_METRICS.getHeight());
                py += charHeight + LINE_SPACE;
            }

            cx = nx + WIDTHS[0];
            py = 0;
            for (int j = 0; j < contests.get(i).getWriters().length; ++ j) {
                String col = contests.get(i).getColors()[j];
                if (col.equals("black")) {
                    py += WRITER_PLAIN_FONT_METRICS.getHeight();
                } else {
                    py += WRITER_BOLD_FONT_METRICS.getHeight();
                }
            }
            cy = ny;
            py = cy + getStartPos(heights[i], py + LINE_SPACE * (contests.get(i).getWriters().length - 1));

            for (int j = 0; j < contests.get(i).getWriters().length; ++ j) {
                String col = contests.get(i).getColors()[j];
                if (col.equals("legendary")) {
                    graphics2D.setFont(WRITER_BOLD_FONT);
                    graphics2D.setColor(Color.BLACK);
                    px = cx + getStartPos(WIDTHS[1], WRITER_BOLD_FONT_METRICS.stringWidth(contests.get(i).getWriters()[j]));
                    int firstCharWidth = WRITER_BOLD_FONT_METRICS.stringWidth(String.valueOf(contests.get(i).getWriters()[j].charAt(0)));
                    graphics2D.drawString(String.valueOf(contests.get(i).getWriters()[j].charAt(0)), px, py + WRITER_BOLD_FONT_METRICS.getHeight());
                    graphics2D.setColor(Color.RED);
                    graphics2D.drawString(contests.get(i).getWriters()[j].substring(1), px + firstCharWidth, py + WRITER_BOLD_FONT_METRICS.getHeight());
                    py += LINE_SPACE + WRITER_BOLD_FONT_METRICS.getHeight();
                } else if (col.equals("black")) {
                    graphics2D.setFont(WRITER_PLAIN_FONT);
                    graphics2D.setColor(Color.BLACK);
                    px = cx + getStartPos(WIDTHS[1], WRITER_PLAIN_FONT_METRICS.stringWidth(contests.get(i).getWriters()[j]));
                    graphics2D.drawString(contests.get(i).getWriters()[j], px, py + WRITER_PLAIN_FONT_METRICS.getHeight());
                    py += LINE_SPACE + WRITER_PLAIN_FONT_METRICS.getHeight();
                } else {
                    graphics2D.setFont(WRITER_BOLD_FONT);
                    graphics2D.setColor(CodeforcesUtils.getColor(contests.get(i).getColors()[j]));
                    px = cx + getStartPos(WIDTHS[1], WRITER_BOLD_FONT_METRICS.stringWidth(contests.get(i).getWriters()[j]));
                    graphics2D.drawString(contests.get(i).getWriters()[j], px, py + WRITER_BOLD_FONT_METRICS.getHeight());
                    py += LINE_SPACE + WRITER_BOLD_FONT_METRICS.getHeight();
                }
            }

            cx += WIDTHS[1];

            graphics2D.setFont(PLAIN_FONT);
            graphics2D.setColor(Color.BLUE);
            String startString = simpleDateFormat.format(contests.get(i).getStart());
            px = cx + getStartPos(WIDTHS[2], PLAIN_FONT_METRICS.stringWidth(startString));
            py = cy + getStartPos(heights[i], PLAIN_FONT_METRICS.getHeight());
            graphics2D.drawString(startString, px, py + PLAIN_FONT_METRICS.getHeight());

            cx += WIDTHS[2];

            graphics2D.setFont(PLAIN_FONT);
            graphics2D.setColor(Color.BLACK);
            String lengthString = formatLength(contests.get(i).getLength());
            px = cx + getStartPos(WIDTHS[3], PLAIN_FONT_METRICS.stringWidth(lengthString));
            py = cy + getStartPos(heights[i], PLAIN_FONT_METRICS.getHeight());
            graphics2D.drawString(lengthString, px, py + PLAIN_FONT_METRICS.getHeight());

            cx += WIDTHS[3];

            switch(contests.get(i).getStatus()) {
                case 0: {
                    graphics2D.setFont(PLAIN_FONT);
                    graphics2D.setColor(Color.BLACK);
                    String registerString = simpleDateFormat.format(contests.get(i).getRegister());
                    px = cx + getStartPos(WIDTHS[4], PLAIN_FONT_METRICS.stringWidth(registerString));
                    py = cy + getStartPos(heights[i], PLAIN_FONT_METRICS.getHeight());
                    graphics2D.drawString(registerString, px, py + PLAIN_FONT_METRICS.getHeight());
                    break;
                }
                case 1: {
                    String registerTitleString = "Register >>";
                    String registerTextString = "Until " + simpleDateFormat.format(contests.get(i).getRegister());
                    px = cx + getStartPos(WIDTHS[4], BOLD_FONT_METRICS.stringWidth(registerTitleString));
                    py = cy + getStartPos(heights[i], BOLD_FONT_METRICS.getHeight() + LINE_SPACE + PLAIN_FONT_METRICS.getHeight());
                    graphics2D.setFont(BOLD_FONT);
                    graphics2D.setColor(Color.RED);
                    graphics2D.drawString(registerTitleString, px, py + BOLD_FONT_METRICS.getHeight());
                    py += BOLD_FONT_METRICS.getHeight() + LINE_SPACE;
                    px = cx + getStartPos(WIDTHS[4], BOLD_FONT_METRICS.stringWidth(registerTextString));
                    graphics2D.setFont(PLAIN_FONT);
                    graphics2D.setColor(Color.BLACK);
                    graphics2D.drawString(registerTextString, px, py + PLAIN_FONT_METRICS.getHeight());
                    break;
                }
                case 2: {

                    break;
                }

            }
            ny += heights[i];
        }

        graphics2D.setColor(Color.BLACK);
        graphics2D.drawLine(nx, ny, nx + WIDTH, ny);

        return bufferedImage;
    }

    private static String formatLength(int seconds) {
        int day = seconds / (60 * 60 * 24);
        seconds -= day * 24 * 60 * 60;
        int hour = seconds / (60 * 60);
        seconds -= hour * 60 * 60;
        int minute = seconds / 60;
        if (day != 0) {
            return String.format("%d", day) + ":" + String.format("%02d", hour) + ":" + String.format("%02d", minute);
        } else {
            return String.format("%02d", hour) + ":" + String.format("%02d", minute);
        }
    }

    private static int getStartPos(int tw, int sw) {
        return (tw - sw) / 2;
    }

    private static String[] splitContestName(String name) {
        String[] res = name.split(" ");
        StringBuilder stringBuffer = new StringBuilder();
        ArrayList<String> arrayList = new ArrayList<>();
        int sum = 0;
        for (String re : res) {
            if (sum + re.length() > CONTEST_NAME_NUMBER_LIMIT) {
                arrayList.add(stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString());
                stringBuffer.setLength(0);
                sum = re.length();
                stringBuffer.append(re).append(" ");
                continue;
            }
            sum += re.length();
            stringBuffer.append(re).append(" ");
        }
        if (sum != 0) {
            arrayList.add(stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString());
        }
        return arrayList.toArray(new String[0]);
    }

    private static void addTableFrame(Graphics2D graphics2D, int x, int y, int height) {
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawLine(x, y, x + WIDTH, y);
        int nx = x, ny = y + height;
        for (int i : WIDTHS) {
            graphics2D.drawLine(nx, y, nx, ny);
            nx += i;
        }
        graphics2D.drawLine(nx, y, nx, ny);
    }
}
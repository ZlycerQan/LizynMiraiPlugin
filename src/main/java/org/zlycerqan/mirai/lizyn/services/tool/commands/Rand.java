package org.zlycerqan.mirai.lizyn.services.tool.commands;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.utils.MessageUtils;
import org.zlycerqan.mirai.lizyn.services.tool.ToolUtils;

import java.util.function.Consumer;

public class Rand {

    public static String COMMAND = "#rand";

    public static Consumer<Event> makeFriendMessageEventCommand(@NotNull ExecutorManager executorManager) {
        return origin -> executorManager.getExecutor().execute(() -> {
            FriendMessageEvent event = (FriendMessageEvent) origin;
            event.getFriend().sendMessage(solveText(MessageUtils.getPlainText(event.getMessage())));
        });
    }

    public static Consumer<Event> makeGroupMessageEventCommand(@NotNull ExecutorManager executorManager) {
        return origin -> executorManager.getExecutor().execute(() -> {
            GroupMessageEvent event = (GroupMessageEvent) origin;
            String text = solveText(MessageUtils.getPlainText(event.getMessage()));
            event.getGroup().sendMessage((new At(event.getSender().getId())).plus(text));
        });
    }

    private static String solveText(String message) {
        message = message.trim();
        String[] args = message.split(" ");
        String text;
        final String paraError = "Parameters error.";
        if (args.length == 1) {
            text = String.valueOf(ToolUtils.getRand(0, Integer.MAX_VALUE - 1));
        } else {
            if (args.length == 2) {
                if (args[1].equals("single") || args[1].equals("s")) {
                    text = String.valueOf(ToolUtils.getRand(0, Integer.MAX_VALUE - 1));
                } else {
                    text = paraError;
                }
            } else {
                if (args.length == 3) {
                    if (args[1].equals("single") || args[1].equals("s")) {
                        if (args[2].startsWith("[") && args[2].endsWith("]")) {
                            int[] re = ToolUtils.parseInterval(args[2]);
                            if (re == null) {
                                text = paraError;
                            } else {
                                text = String.valueOf(ToolUtils.getRand(re[0], re[1]));
                            }
                        } else {
                            text = paraError;
                        }
                    } else if (args[1].equals("list") || args[1].equals("l")) {
                        try {
                            int number = Integer.parseInt(args[2]);
                            if (number <= 0) {
                                text = "At least 1 number";
                            } else if (number > 100) {
                                text = "Up to 100 numbers.";
                            } else {
                                text = ToolUtils.randListToString(ToolUtils.getRandList(0, Integer.MAX_VALUE - 1, false, number));
                            }
                        } catch (NumberFormatException e) {
                            text = paraError;
                        }
                    } else {
                        text = paraError;
                    }
                } else {
                    if (args.length == 4) {
                        if (args[1].equals("list") || args[1].equals("l")) {
                            if (args[2].equals("only") || args[2].equals("o")) {
                                try {
                                    int number = Integer.parseInt(args[3]);
                                    if (number <= 0) {
                                        text = "At least 1 number";
                                    } else if (number > 100) {
                                        text = "Up to 100 numbers.";
                                    } else {
                                        text = ToolUtils.randListToString(ToolUtils.getRandList(0, Integer.MAX_VALUE - 1, true, number));
                                    }
                                } catch (NumberFormatException e) {
                                    text = paraError;
                                }
                            } else if (args[2].startsWith("[") && args[2].endsWith("]")) {
                                int[] re = ToolUtils.parseInterval(args[2]);
                                if (re == null) {
                                    text = paraError;
                                } else {
                                    try {
                                        int number = Integer.parseInt(args[3]);
                                        if (number <= 0) {
                                            text = "At least 1 number";
                                        } else if (number > 100) {
                                            text = "Up to 100 numbers.";
                                        } else {
                                            text = ToolUtils.randListToString(ToolUtils.getRandList(re[0], re[1], false, number));
                                        }
                                    } catch (NumberFormatException e) {
                                        text = paraError;
                                    }
                                }
                            } else {
                                text = paraError;
                            }
                        } else {
                            text = paraError;
                        }
                    } else if (args.length == 5) {
                        if (args[1].equals("list") || args[1].equals("l")) {
                            if (args[2].startsWith("[") && args[2].endsWith("]")) {
                                int[] re = ToolUtils.parseInterval(args[2]);
                                if (re == null) {
                                    text = paraError;
                                } else {
                                    if (args[3].equals("only") || args[3].equals("o")) {
                                        try {
                                            int number = Integer.parseInt(args[4]);
                                            if (number > re[1] - re[0] + 1) {
                                                text = paraError;
                                            }
                                            else if (number <= 0) {
                                                text = "At least 1 number";
                                            } else if (number > 100) {
                                                text = "Up to 100 numbers.";
                                            } else {
                                                text = ToolUtils.randListToString(ToolUtils.getRandList(re[0], re[1], true, number));
                                            }
                                        } catch (NumberFormatException e) {
                                            text = paraError;
                                        }
                                    } else {
                                        text = paraError;
                                    }
                                }
                            } else {
                                text = paraError;
                            }
                        } else {
                            text = paraError;
                        }
                    } else {
                        text = paraError;
                    }
                }
            }
        }
        return text;
    }
}

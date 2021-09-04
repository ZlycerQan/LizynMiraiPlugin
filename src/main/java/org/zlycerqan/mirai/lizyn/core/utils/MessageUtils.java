package org.zlycerqan.mirai.lizyn.core.utils;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.List;
import java.util.stream.Collectors;

public class MessageUtils {

    public static long[] getAtList(MessageChain messages) {
        List<SingleMessage> list = messages.stream().filter(At.class::isInstance).collect(Collectors.toList());
        long[] result = new long[list.size()];
        for (int i = 0; i < result.length; ++ i) {
            result[i] = ((At) list.get(i)).getTarget();
        }
        return result;
    }

    public static String getPlainText(MessageChain messages) {
        List<SingleMessage> list = messages.stream().filter(PlainText.class::isInstance).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        for (SingleMessage i : list) {
            stringBuilder.append(i.contentToString());
        }
        return stringBuilder.toString();
    }
}

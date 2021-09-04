package org.zlycerqan.mirai.lizyn.core.service;

import net.mamoe.mirai.event.Event;

import java.util.ArrayList;
import java.util.function.Consumer;

public interface Service {
    Consumer<Event> solveEvent(Event event);
    ArrayList<Class<?>> getListenedEventClasses();
}

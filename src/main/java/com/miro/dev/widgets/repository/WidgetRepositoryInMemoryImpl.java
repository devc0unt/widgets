package com.miro.dev.widgets.repository;

import com.miro.dev.widgets.exceptions.WidgetNotFoundException;
import com.miro.dev.widgets.model.Limit;
import com.miro.dev.widgets.model.Widget;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * WidgetRepositoryInMemoryImpl stores all widgets
 */
@Repository
public class WidgetRepositoryInMemoryImpl implements WidgetRepository {

    /**
     * Base store for all widgets
     */
    private final Map<Long, Widget> widgets = new ConcurrentHashMap<>();

    /**
     * Counter for incrementally generating of widget's ids
     */
    private final AtomicLong counter = new AtomicLong();

    /**
     * Holder of max Z index value of all widgets
     */
    private final AtomicInteger maxZIndex = new AtomicInteger(1);

    /**
     * Lock for atomically operations with creating and updating widgets
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Default constructor
     */
    WidgetRepositoryInMemoryImpl() {}

    /**
     * Method for creating of widget.
     * Internally sets id for widget, computes and sets Z index, sets modification date.
     * @param widget instance of Widget
     * @return instance of Widget
     */
    @Override
    public synchronized Widget create(Widget widget) {
        widget.setId(counter.incrementAndGet());
        widget.setModifiedAt(LocalDateTime.now());

        lock.lock();
        try {
            checkZIndex(widget);
            widgets.put(widget.getId(), widget);
        } finally {
            lock.unlock();
        }

        return widget;
    }

    /**
     * Retrieves widget by id.
     * @param id of widget
     * @return Optional object of Widget instance founded by given id
     */
    @Override
    public Optional<Widget> getById(Long id) {
        return Optional.ofNullable(widgets.get(id));
    }

    /**
     * Retrieves all widgets applying limit and offset.
     * Sorts response list by Z index also.
     * @param limit instance of Limit holding limit and offset
     * @return list of founded widgets
     */
    @Override
    public List<Widget> getAll(Limit limit) {
        return widgets.values().stream()
                .sorted(Comparator.comparing(Widget::getZIndex))
                .skip(limit.getOffset())
                .limit(limit.getLimit())
                .collect(Collectors.toList());
    }

    /**
     * Updates values for given widget.
     * Check new Z index and applying its value.
     * Updates modification date.
     * @param widget instance of Widget
     * @return instance of Widget
     */
    @Override
    public synchronized Widget update(Widget widget) {

        widget.setModifiedAt(LocalDateTime.now());

        lock.lock();
        try {
            checkZIndex(widget);
            widgets.replace(widget.getId(), widget);
        } finally {
            lock.unlock();
        }

        return widget;
    }

    /**
     * Deletes widget from store by id.
     * @param id of widget
     */
    @Override
    public synchronized void delete(Long id) {
        Optional.ofNullable(widgets.get(id)).ifPresentOrElse(widget -> widgets.remove(id),
                () -> {throw new WidgetNotFoundException();});
    }

    /**
     * Clears storage.
     * Switches counters to default values.
     */
    @Override
    public void clear() {
        widgets.clear();
        counter.set(0);
        maxZIndex.set(1);
    }

    /**
     * Check and set Z index for widget
     * @param widget instance of Widget
     */
    private void checkZIndex(Widget widget) {
        if (Objects.isNull(widget.getZIndex())) {
            maxZIndex.set(widgets.values().stream().map(Widget::getZIndex).reduce(Math::max).orElse(0));
            widget.setZIndex(maxZIndex.incrementAndGet());
        } else {
            shift(widget.getZIndex());
        }
    }

    /**
     * Shift z indexes of all widgets appropriately new widget
     * @param index z index given by user
     */
    private void shift(Integer index) {
        boolean exists = widgets.entrySet().stream().anyMatch(w -> w.getValue().getZIndex().equals(index));

        if (exists) {
            widgets.forEach((key, value) -> {
                if (value.getZIndex() >= index)
                    value.incrementZIndex();
            });
            maxZIndex.incrementAndGet();
        }
    }
}

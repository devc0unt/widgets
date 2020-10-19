package com.miro.dev.widgets.service;

import com.miro.dev.widgets.exceptions.InvalidWidgetException;
import com.miro.dev.widgets.exceptions.WidgetNotFoundException;
import com.miro.dev.widgets.model.Limit;
import com.miro.dev.widgets.model.Widget;
import com.miro.dev.widgets.repository.WidgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Service that passes requests and responses
 * between controller and repository,
 * and applies checks
 */
@Service
public class WidgetService {

    private final WidgetRepository repository;

    public WidgetService(WidgetRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves widget by id.
     * @param id of widget
     * @return instance of Widget
     */
    public Widget getById(Long id) {
        if (Objects.isNull(id)) throw new InvalidWidgetException();
        return repository.getById(id).orElseThrow(WidgetNotFoundException::new);
    }

    /**
     * Retrieves all widgets.
     * @param limit entity that holds limit and offset
     * @return list of all widgets
     */
    public List<Widget> getAll(Limit limit) {
        return repository.getAll(limit);
    }

    /**
     * Deletes widget by id.
     * @param id of widget
     * @return wrapper for empty value
     */
    public Void delete(Long id) {
        repository.delete(id);
        return null;
    }

    /**
     * Creates widget. Checks widget for mandatory values.
     * @param widget instance of Widget
     * @return instance of created Widget
     */
    public Widget create(Widget widget) {
        check(widget);
        return repository.create(widget);
    }

    /**
     * Updates widget. Checks widget for mandatory values.
     * @param widget instance of Widget
     * @return instance of updated Widget
     */
    public Widget update(Widget widget) {
        getById(widget.getId());
        check(widget);
        return repository.update(widget);
    }

    /**
     * Check that widget has x, y, width and height,
     * and that width and height is not negative
     * @param widget instance of Widget
     */
    private void check(Widget widget) {
        if (Stream.of(widget.getXIndex(), widget.getYIndex(), widget.getWidth(), widget.getHeight())
                .anyMatch(Objects::isNull)) throw new InvalidWidgetException();

        if (Stream.of(widget.getWidth(), widget.getHeight())
                .anyMatch(el -> el < 0)) throw new InvalidWidgetException();
    }
}

package com.miro.dev.widgets.repository;

import com.miro.dev.widgets.model.Limit;
import com.miro.dev.widgets.model.Widget;

import java.util.List;
import java.util.Optional;

public interface WidgetRepository {
    Optional<Widget> getById(Long id);
    List<Widget> getAll(Limit limit);
    Widget create(Widget widget);
    Widget update(Widget widget);
    void delete(Long id);
    void clear();
}

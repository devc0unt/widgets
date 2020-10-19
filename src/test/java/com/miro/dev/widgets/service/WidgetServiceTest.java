package com.miro.dev.widgets.service;

import com.miro.dev.widgets.exceptions.InvalidWidgetException;
import com.miro.dev.widgets.exceptions.WidgetNotFoundException;
import com.miro.dev.widgets.model.Limit;
import com.miro.dev.widgets.model.Widget;
import com.miro.dev.widgets.repository.WidgetRepository;
import com.miro.dev.widgets.repository.WidgetRepositoryInMemoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class WidgetServiceTest {
    private WidgetRepository widgetRepository;

    @Before
    public void init() {
        this.widgetRepository = mock(WidgetRepositoryInMemoryImpl.class);
    }

    @Test
    public void testWidgetService_WhenGetAll_ReturnResult() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setId(1L);
        List<Widget> widgets = Collections.singletonList(widget);
        Limit limit = Limit.defaultLimit();
        doReturn(widgets).when(widgetRepository).getAll(limit);

        // Act
        List<Widget> widgetsReturned = widgetService.getAll(limit);

        // Assert
        Assert.assertEquals(1, widgetsReturned.size());
        Assert.assertEquals(1L, (long) widgetsReturned.get(0).getId());
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenGetByIdWithNullArgument_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);

        // Act
        widgetService.getById(null);
    }

    @Test()
    public void testWidgetService_WhenGetById_ReturnResult() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setId(1L);
        doReturn(Optional.of(widget)).when(widgetRepository).getById(widget.getId());

        // Act
        Widget widgetsReturned = widgetService.getById(widget.getId());

        // Assert
        Assert.assertNotNull(widgetsReturned);
        Assert.assertEquals(widget.getId(), widgetsReturned.getId());
    }

    @Test(expected = WidgetNotFoundException.class)
    public void testWidgetService_WhenGetByIdReturnEmpty_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        doReturn(Optional.empty()).when(widgetRepository).getById(1L);

        // Act
        widgetService.getById(1L);
    }

    @Test
    public void testWidgetService_WhenDelete_CallRepositoryDelete() {
        // Arrange
        Long id = 1L;
        WidgetService widgetService = new WidgetService(widgetRepository);

        // Act
        widgetService.delete(id);

        // Assert
        verify(widgetRepository, times(1)).delete(id);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenCreateWithNullXIndex_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setXIndex(null);

        // Act
        widgetService.create(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenCreateWithNullYIndex_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setYIndex(null);

        // Act
        widgetService.create(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenCreateWithNullWidth_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setWidth(null);

        // Act
        widgetService.create(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenCreateWithNullHeight_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setHeight(null);

        // Act
        widgetService.create(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenCreateWithNegativeWidth_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setWidth(-1);

        // Act
        widgetService.create(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenCreateWithNegativeHeight_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setHeight(-1);

        // Act
        widgetService.create(widget);
    }

    @Test
    public void testWidgetService_WhenCreate_CallRepositoryCreate() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        doReturn(widget).when(widgetRepository).create(widget);

        // Act
        Widget result = widgetService.create(widget);

        // Assert
        verify(widgetRepository, times(1)).create(widget);
        Assert.assertEquals(widget, result);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenUpdateWithNullWidgetId_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();

        // Act
        widgetService.update(widget);
    }

    @Test(expected = WidgetNotFoundException.class)
    public void testWidgetService_WhenUpdateReturnEmpty_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setId(1L);
        doReturn(Optional.empty()).when(widgetRepository).getById(1L);

        // Act
        widgetService.update(widget);
    }

    @Test()
    public void testWidgetService_WhenUpdate_CallRepositoryUpdate() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setId(1L);
        doReturn(Optional.of(widget)).when(widgetRepository).getById(widget.getId());
        doReturn(widget).when(widgetRepository).update(widget);

        // Act
        Widget widgetsReturned = widgetService.update(widget);

        // Assert
        Assert.assertNotNull(widgetsReturned);
        Assert.assertEquals(widget.getId(), widgetsReturned.getId());
        verify(widgetRepository, times(1)).update(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenUpdateWithNullXIndex_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setXIndex(null);
        widget.setId(1L);
        doReturn(Optional.of(widget)).when(widgetRepository).getById(widget.getId());

        // Act
        widgetService.update(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenUpdateWithNullYIndex_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setYIndex(null);
        widget.setId(1L);
        doReturn(Optional.of(widget)).when(widgetRepository).getById(widget.getId());

        // Act
        widgetService.update(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenUpdateWithNullWidth_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setWidth(null);
        widget.setId(1L);
        doReturn(Optional.of(widget)).when(widgetRepository).getById(widget.getId());

        // Act
        widgetService.update(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenUpdateWithNullHeight_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setHeight(null);
        widget.setId(1L);
        doReturn(Optional.of(widget)).when(widgetRepository).getById(widget.getId());

        // Act
        widgetService.update(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenUpdateWithNegativeWidth_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setWidth(-1);
        widget.setId(1L);
        doReturn(Optional.of(widget)).when(widgetRepository).getById(widget.getId());

        // Act
        widgetService.update(widget);
    }

    @Test(expected = InvalidWidgetException.class)
    public void testWidgetService_WhenUpdateWithNegativeHeight_ThrowsException() {
        // Arrange
        WidgetService widgetService = new WidgetService(widgetRepository);
        Widget widget = getWidget();
        widget.setHeight(-1);
        widget.setId(1L);
        doReturn(Optional.of(widget)).when(widgetRepository).getById(widget.getId());

        // Act
        widgetService.update(widget);
    }

    private Widget getWidget() {
        return Widget.builder()
                .height(1)
                .width(2)
                .xIndex(11)
                .yIndex(22)
                .zIndex(55)
                .build();
    }
}

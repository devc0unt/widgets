package com.miro.dev.widgets.repository;

import com.miro.dev.widgets.exceptions.WidgetNotFoundException;
import com.miro.dev.widgets.model.Limit;
import com.miro.dev.widgets.model.Widget;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class WidgetRepositoryInMemoryImplTest {
    private WidgetRepositoryInMemoryImpl repository;

    @Before
    public void init() {
        repository = new WidgetRepositoryInMemoryImpl();
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenGetByIdOnEmptyWidgets_ThenReturnEmptyOptional() {
        // Act
        Optional<Widget> result = repository.getById(1L);

        // Assert
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenGetByIdOnNotEmptyWidgets_ThenReturnNotEmptyOptional() {
        // Arrange
        Widget widget = getWidget();
        Widget created = repository.create(widget);

        // Act
        Optional<Widget> result = repository.getById(created.getId());

        // Assert
        Assert.assertTrue(result.isPresent());
        Assert.assertNotNull(result.get().getId());
        Assert.assertEquals(created.getId(), result.get().getId());
        Assert.assertEquals(widget.getHeight(), result.get().getHeight());
        Assert.assertEquals(widget.getWidth(), result.get().getWidth());
        Assert.assertEquals(widget.getXIndex(), result.get().getXIndex());
        Assert.assertEquals(widget.getYIndex(), result.get().getYIndex());
        Assert.assertEquals(widget.getZIndex(), result.get().getZIndex());
        Assert.assertNotNull(result.get().getModifiedAt());
        Assert.assertEquals(created.getModifiedAt(), result.get().getModifiedAt());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenCreate_ThenSetId() {
        // Arrange
        Widget widget = getWidget();
        Assert.assertNull(widget.getId());

        // Act
        Widget created = repository.create(widget);
        Optional<Widget> result = repository.getById(created.getId());

        // Assert
        Assert.assertTrue(result.isPresent());
        Assert.assertNotNull(result.get().getId());
        Assert.assertEquals(created.getId(), result.get().getId());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenCreate_ThenSetModifiedAt() {
        // Arrange
        Widget widget = getWidget();
        LocalDateTime instant = LocalDateTime.now();
        widget.setModifiedAt(instant);
        try {
            Thread.sleep(1);
        } catch (Exception ignored) {}

        // Act
        Widget created = repository.create(widget);
        Optional<Widget> result = repository.getById(created.getId());

        // Assert
        Assert.assertTrue(result.isPresent());
        // assert that modified datetime greater than initial instant
        Assert.assertEquals(1, created.getModifiedAt().compareTo(instant));
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenCreate_ThenIncrementsZIndex() {
        // Arrange
        Widget widget = getWidget();
        widget.setZIndex(null);
        Widget widgetSecond = getWidget();
        widgetSecond.setZIndex(null);

        // Act
        Widget created = repository.create(widget);
        Optional<Widget> result = repository.getById(created.getId());
        Widget createdSecond = repository.create(widgetSecond);
        Optional<Widget> resultSecond = repository.getById(createdSecond.getId());

        // Assert
        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(resultSecond.isPresent());
        Assert.assertEquals(1, (int) result.get().getZIndex());
        Assert.assertEquals(2, (int) resultSecond.get().getZIndex());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenCreate_ThenShiftZIndex() {
        // Arrange
        Widget widget = getWidget();
        widget.setZIndex(2);
        Widget widgetSecond = getWidget();
        widgetSecond.setZIndex(2);

        // Act
        Widget created = repository.create(widget);
        Optional<Widget> result = repository.getById(created.getId());
        // check before inserting second widget
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(2, (int) result.get().getZIndex());
        // insert second widget
        Widget createdSecond = repository.create(widgetSecond);
        Optional<Widget> resultSecond = repository.getById(createdSecond.getId());
        Optional<Widget> resultAfterSecond = repository.getById(created.getId());

        // Assert
        Assert.assertTrue(resultSecond.isPresent());
        Assert.assertTrue(resultAfterSecond.isPresent());
        Assert.assertEquals(2, (int) resultSecond.get().getZIndex());
        Assert.assertEquals(3, (int) resultAfterSecond.get().getZIndex());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenCreate_ThenNotShiftZIndexWhenIndexFree() {
        // Arrange
        Widget widget = getWidget();
        widget.setZIndex(1);
        Widget widgetSecond = getWidget();
        widgetSecond.setZIndex(3);
        Widget widgetThird = getWidget();
        widgetThird.setZIndex(2);

        // Act
        Widget created = repository.create(widget);
        Optional<Widget> result = repository.getById(created.getId());
        // check before inserting second widget
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(1, (int) result.get().getZIndex());
        // insert second widget
        Widget createdSecond = repository.create(widgetSecond);
        Optional<Widget> resultSecond = repository.getById(createdSecond.getId());
        Optional<Widget> resultAfterSecond = repository.getById(created.getId());
        Widget createdThird = repository.create(widgetThird);
        Optional<Widget> resultThird = repository.getById(createdThird.getId());

        // Assert
        Assert.assertTrue(resultSecond.isPresent());
        Assert.assertTrue(resultAfterSecond.isPresent());
        Assert.assertTrue(resultThird.isPresent());
        Assert.assertEquals(3, (int) resultSecond.get().getZIndex());
        Assert.assertEquals(1, (int) resultAfterSecond.get().getZIndex());
        Assert.assertEquals(2, (int) resultThird.get().getZIndex());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenGetAllOnEmptyRepository_ThenReturnEmptyList() {
        // Act
        List<Widget> widgets = repository.getAll(Limit.defaultLimit());

        // Assert
        Assert.assertTrue(widgets.isEmpty());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenGetAllOnEmptyRepositoryAfterDelete_ThenReturnEmptyList() {
        // Arrange
        Widget widget = getWidget();
        Widget created = repository.create(widget);
        List<Widget> widgetsNotEmpty = repository.getAll(Limit.defaultLimit());
        Assert.assertFalse(widgetsNotEmpty.isEmpty());
        repository.delete(created.getId());

        // Act
        List<Widget> widgets = repository.getAll(Limit.defaultLimit());

        // Assert
        Assert.assertTrue(widgets.isEmpty());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenGetAll_ThenReturnSortedByZIndexList() {
        // Arrange
        Widget widget = getWidget();
        widget.setZIndex(11);
        Widget widgetSecond = getWidget();
        widgetSecond.setZIndex(3);
        Widget widgetThird = getWidget();
        widgetThird.setZIndex(7);
        Widget created = repository.create(widget);
        Widget createdSecond = repository.create(widgetSecond);
        Widget createdThird = repository.create(widgetThird);

        // Act
        List<Widget> widgets = repository.getAll(Limit.defaultLimit());

        // Assert
        Assert.assertFalse(widgets.isEmpty());
        Assert.assertEquals(3, widgets.size());
        Assert.assertEquals(createdSecond.getId(), widgets.get(0).getId());
        Assert.assertEquals(createdThird.getId(), widgets.get(1).getId());
        Assert.assertEquals(created.getId(), widgets.get(2).getId());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenClear_ThenReturnEmptyList() {
        // Arrange
        Widget widget = getWidget();
        widget.setZIndex(11);
        Widget widgetSecond = getWidget();
        repository.create(widget);
        repository.create(widgetSecond);
        List<Widget> widgetsNotEmpty = repository.getAll(Limit.defaultLimit());
        Assert.assertFalse(widgetsNotEmpty.isEmpty());

        // Act
        repository.clear();

        // Assert
        List<Widget> widgetsEmpty = repository.getAll(Limit.defaultLimit());
        Assert.assertTrue(widgetsEmpty.isEmpty());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenDeleteAndWidgetExists_ThenDeleteThisWidget() {
        // Arrange
        Widget widget = getWidget();
        Widget created = repository.create(widget);
        List<Widget> widgets = repository.getAll(Limit.defaultLimit());
        Assert.assertFalse(widgets.isEmpty());

        // Act
        repository.delete(created.getId());

        // Assert
        List<Widget> widgetsAfterDelete = repository.getAll(Limit.defaultLimit());
        Assert.assertTrue(widgetsAfterDelete.isEmpty());
    }

    @Test(expected = WidgetNotFoundException.class)
    public void testWidgetRepositoryInMemoryImpl_WhenDeleteAndWidgetNotExists_ThenThrownException() {
        // Arrange
        Widget widget = getWidget();
        Widget created = repository.create(widget);
        List<Widget> widgets = repository.getAll(Limit.defaultLimit());
        Assert.assertFalse(widgets.isEmpty());

        // Act
        repository.delete(created.getId() + 1L);
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenUpdate_ThenUpdateFields() {
        // Arrange
        Widget widget = getWidget();
        Widget created = repository.create(widget);
        Optional<Widget> result = repository.getById(created.getId());
        Assert.assertTrue(result.isPresent());
        created.setZIndex(2);
        created.setYIndex(3);
        created.setXIndex(9);
        created.setWidth(98);
        created.setHeight(97);
        LocalDateTime createdInstant = created.getModifiedAt();
        try {
            Thread.sleep(1);
        } catch (Exception ignored) {}

        // Act
        repository.update(created);

        // Assert
        Optional<Widget> resultAfterUpdate = repository.getById(created.getId());
        Assert.assertTrue(resultAfterUpdate.isPresent());
        Assert.assertEquals(created.getId(), resultAfterUpdate.get().getId());
        Assert.assertEquals(created.getHeight(), resultAfterUpdate.get().getHeight());
        Assert.assertEquals(created.getWidth(), resultAfterUpdate.get().getWidth());
        Assert.assertEquals(created.getXIndex(), resultAfterUpdate.get().getXIndex());
        Assert.assertEquals(created.getYIndex(), resultAfterUpdate.get().getYIndex());
        Assert.assertEquals(created.getZIndex(), resultAfterUpdate.get().getZIndex());
        // assert that modified datetime greater than initial instant
        Assert.assertEquals(1, resultAfterUpdate.get().getModifiedAt().compareTo(createdInstant));
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenUpdate_ThenIncrementsZIndex() {
        // Arrange
        Widget widget = getWidget();
        widget.setZIndex(null);
        Widget widgetSecond = getWidget();
        widgetSecond.setZIndex(null);
        Widget created = repository.create(widget);
        Optional<Widget> result = repository.getById(created.getId());
        Widget createdSecond = repository.create(widgetSecond);
        Optional<Widget> resultSecond = repository.getById(createdSecond.getId());
        Widget clonedSecondWidget = getWidget();
        clonedSecondWidget.setId(createdSecond.getId());
        clonedSecondWidget.setZIndex(null);

        // Act
        repository.update(clonedSecondWidget);

        // Assert
        Optional<Widget> resultThird = repository.getById(createdSecond.getId());
        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(resultSecond.isPresent());
        Assert.assertTrue(resultThird.isPresent());
        Assert.assertEquals(1, (int) result.get().getZIndex());
        Assert.assertEquals(3, (int) resultThird.get().getZIndex());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenUpdate_ThenShiftZIndex() {
        // Arrange
        Widget widget = getWidget();
        widget.setZIndex(1);
        Widget widgetSecond = getWidget();
        widgetSecond.setZIndex(2);
        Widget created = repository.create(widget);
        Optional<Widget> result = repository.getById(created.getId());
        // check before inserting second widget
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(1, (int) result.get().getZIndex());
        // insert second widget
        Widget createdSecond = repository.create(widgetSecond);
        Widget clonedFirstWidget = getWidget();
        clonedFirstWidget.setId(created.getId());
        clonedFirstWidget.setZIndex(2);

        // Act
        repository.update(clonedFirstWidget);

        // Assert
        Optional<Widget> resultSecond = repository.getById(createdSecond.getId());
        Optional<Widget> resultAfterSecond = repository.getById(created.getId());
        Assert.assertTrue(resultSecond.isPresent());
        Assert.assertTrue(resultAfterSecond.isPresent());
        Assert.assertEquals(3, (int) resultSecond.get().getZIndex());
        Assert.assertEquals(2, (int) resultAfterSecond.get().getZIndex());
    }

    @Test
    public void testWidgetRepositoryInMemoryImpl_WhenUpdate_ThenNotShiftZIndexWhenIndexFree() {
        // Arrange
        Widget widget = getWidget();
        widget.setZIndex(1);
        Widget widgetSecond = getWidget();
        widgetSecond.setZIndex(3);
        Widget created = repository.create(widget);
        Optional<Widget> result = repository.getById(created.getId());
        // check before inserting second widget
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(1, (int) result.get().getZIndex());
        // insert second widget
        Widget createdSecond = repository.create(widgetSecond);
        Optional<Widget> resultSecond = repository.getById(createdSecond.getId());
        Widget clonedFirstWidget = getWidget();
        clonedFirstWidget.setId(created.getId());
        clonedFirstWidget.setZIndex(2);

        // Act
        repository.update(clonedFirstWidget);

        // Assert
        Optional<Widget> resultAfterUpdate = repository.getById(created.getId());
        Assert.assertTrue(resultSecond.isPresent());
        Assert.assertTrue(resultAfterUpdate.isPresent());
        Assert.assertEquals(3, (int) resultSecond.get().getZIndex());
        Assert.assertEquals(2, (int) resultAfterUpdate.get().getZIndex());
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

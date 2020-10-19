package com.miro.dev.widgets.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miro.dev.widgets.controller.CustomErrorController;
import com.miro.dev.widgets.model.Limit;
import com.miro.dev.widgets.model.Widget;
import com.miro.dev.widgets.repository.WidgetRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WidgetsApiTests {
    private static final String url = "/api/v1/widgets";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WidgetRepository widgetRepository;

    @Test
    public void testWhenSendPost_WidgetRepository_CreateWidget() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();

        // Act
        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // Assert
        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        Assert.assertNotNull(responded);
        Assert.assertEquals(1L, (long) responded.getId());
        Assert.assertTrue(widgetRepository.getById(responded.getId()).isPresent());
        Widget saved = widgetRepository.getById(responded.getId()).get();
        Assert.assertEquals(widget.getHeight(), saved.getHeight());
        Assert.assertEquals(widget.getWidth(), saved.getWidth());
        Assert.assertEquals(widget.getXIndex(), saved.getXIndex());
        Assert.assertEquals(widget.getYIndex(), saved.getYIndex());
        Assert.assertEquals(widget.getZIndex(), saved.getZIndex());
        Assert.assertNotNull(saved.getModifiedAt());
    }

    @Test
    public void testWhenSendPostInvalidData_WidgetController_ResponseWithBadRequestStatus() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = Widget.builder()
                .height(-1)
                .width(2)
                .xIndex(11)
                .yIndex(22)
                .zIndex(55)
                .build();

        // Act
        int result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getStatus();

        // Assert
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result);
        Assert.assertEquals(0, widgetRepository.getAll(Limit.defaultLimit()).size());
    }

    @Test
    public void testWhenSendPut_WidgetRepository_UpdateWidget() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        widget.setId(responded.getId());
        widget.setWidth(123);
        widget.setHeight(321);
        widget.setZIndex(2);
        widget.setXIndex(33);
        widget.setYIndex(44);

        // Act
        MockHttpServletResponse putResult = mockMvc.perform(put(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // Assert
        Widget putResponded = objectMapper.readValue(putResult.getContentAsString(), Widget.class);
        Assert.assertNotNull(putResponded);
        Assert.assertEquals(1L, (long) putResponded.getId());
        Assert.assertTrue(widgetRepository.getById(putResponded.getId()).isPresent());
        Widget saved = widgetRepository.getById(putResponded.getId()).get();
        Assert.assertEquals(widget.getHeight(), saved.getHeight());
        Assert.assertEquals(widget.getWidth(), saved.getWidth());
        Assert.assertEquals(widget.getXIndex(), saved.getXIndex());
        Assert.assertEquals(widget.getYIndex(), saved.getYIndex());
        Assert.assertEquals(widget.getZIndex(), saved.getZIndex());
        Assert.assertNotNull(saved.getModifiedAt());
        Assert.assertNotEquals(responded.getModifiedAt(), saved.getModifiedAt());
        Assert.assertEquals(putResponded.getModifiedAt(), saved.getModifiedAt());
    }

    @Test
    public void testWhenSendPutWithWrongParams_WidgetController_ResponseWithBadRequestStatus() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        widget.setId(responded.getId());
        widget.setWidth(-123);

        // Act
        int putResult = mockMvc.perform(put(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getStatus();

        // Assert
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), putResult);
        Assert.assertTrue(widgetRepository.getById(widget.getId()).isPresent());
        Widget saved = widgetRepository.getById(widget.getId()).get();
        widget.setWidth(2);
        Assert.assertEquals(widget.getHeight(), saved.getHeight());
        Assert.assertEquals(widget.getWidth(), saved.getWidth());
        Assert.assertEquals(widget.getXIndex(), saved.getXIndex());
        Assert.assertEquals(widget.getYIndex(), saved.getYIndex());
        Assert.assertEquals(widget.getZIndex(), saved.getZIndex());
        Assert.assertNotNull(saved.getModifiedAt());
        Assert.assertEquals(responded.getModifiedAt(), saved.getModifiedAt());
    }

    @Test
    public void testWhenSendPutWithWrongIdParam_WidgetController_ResponseWithNotFoundStatus() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        widget.setId(2L);

        // Act
        int putResult = mockMvc.perform(put(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getStatus();

        // Assert
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), putResult);
        Assert.assertTrue(widgetRepository.getById(responded.getId()).isPresent());
        Widget saved = widgetRepository.getById(responded.getId()).get();
        Assert.assertEquals(widget.getHeight(), saved.getHeight());
        Assert.assertEquals(widget.getWidth(), saved.getWidth());
        Assert.assertEquals(widget.getXIndex(), saved.getXIndex());
        Assert.assertEquals(widget.getYIndex(), saved.getYIndex());
        Assert.assertEquals(widget.getZIndex(), saved.getZIndex());
        Assert.assertNotNull(saved.getModifiedAt());
        Assert.assertEquals(responded.getModifiedAt(), saved.getModifiedAt());
    }

    @Test
    public void testWhenSendDelete_WidgetRepository_DeleteWidget() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        Assert.assertTrue(widgetRepository.getById(responded.getId()).isPresent());

        // Act
        int deleteResult = mockMvc.perform(delete(url + "/" + responded.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getStatus();

        // Assert
        Assert.assertEquals(HttpStatus.OK.value(), deleteResult);
        Assert.assertFalse(widgetRepository.getById(responded.getId()).isPresent());
    }

    @Test
    public void testWhenSendDeleteWithWrongIdParam_WidgetController_ResponseWithNotFoundStatus() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        Assert.assertTrue(widgetRepository.getById(responded.getId()).isPresent());

        // Act
        int deleteResult = mockMvc.perform(delete(url + "/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getStatus();

        // Assert
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), deleteResult);
        Assert.assertTrue(widgetRepository.getById(responded.getId()).isPresent());
    }

    @Test
    public void testWhenSendGet_WidgetRepository_ReturnWidget() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        widget.setId(responded.getId());
        widget.setModifiedAt(responded.getModifiedAt());

        // Act
        MockHttpServletResponse getResult = mockMvc.perform(get(url + "/" + responded.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // Assert
        Widget getResponded = objectMapper.readValue(getResult.getContentAsString(), Widget.class);
        Assert.assertNotNull(getResponded);
        Assert.assertEquals(1L, (long) getResponded.getId());
        Assert.assertTrue(widgetRepository.getById(getResponded.getId()).isPresent());
        Widget saved = widgetRepository.getById(getResponded.getId()).get();
        Assert.assertEquals(widget.getId(), saved.getId());
        Assert.assertEquals(widget.getHeight(), saved.getHeight());
        Assert.assertEquals(widget.getWidth(), saved.getWidth());
        Assert.assertEquals(widget.getXIndex(), saved.getXIndex());
        Assert.assertEquals(widget.getYIndex(), saved.getYIndex());
        Assert.assertEquals(widget.getZIndex(), saved.getZIndex());
        Assert.assertEquals(widget.getModifiedAt(), saved.getModifiedAt());
        Assert.assertEquals(getResponded.getHeight(), saved.getHeight());
        Assert.assertEquals(getResponded.getWidth(), saved.getWidth());
        Assert.assertEquals(getResponded.getXIndex(), saved.getXIndex());
        Assert.assertEquals(getResponded.getYIndex(), saved.getYIndex());
        Assert.assertEquals(getResponded.getZIndex(), saved.getZIndex());
        Assert.assertEquals(getResponded.getModifiedAt(), saved.getModifiedAt());
    }

    @Test
    public void testWhenSendGetWithWrongIdParam_WidgetController_ResponseWithNotFoundStatus() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        widget.setId(responded.getId());
        widget.setModifiedAt(responded.getModifiedAt());

        // Act
        int getResult = mockMvc.perform(get(url + "/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getStatus();

        // Assert
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), getResult);
        Assert.assertTrue(widgetRepository.getById(responded.getId()).isPresent());
    }

    @Test
    public void testWhenSendGetList_WidgetRepository_ReturnWidgets() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();
        Widget widgetSecond = Widget.builder()
                .height(11)
                .width(21)
                .xIndex(111)
                .yIndex(221)
                .zIndex(551)
                .build();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        widget.setId(responded.getId());
        widget.setModifiedAt(responded.getModifiedAt());

        MockHttpServletResponse resultSecond = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widgetSecond))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget respondedSecond = objectMapper.readValue(resultSecond.getContentAsString(), Widget.class);
        widgetSecond.setId(respondedSecond.getId());
        widgetSecond.setModifiedAt(respondedSecond.getModifiedAt());

        // Act
        MockHttpServletResponse getResult = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // Assert
        List<Widget> getResponded = objectMapper.readValue(getResult.getContentAsString(), new TypeReference<>(){});
        Assert.assertNotNull(getResponded);
        Assert.assertEquals(2, getResponded.size());
        Assert.assertEquals(1L, (long) getResponded.get(0).getId());
        Assert.assertTrue(widgetRepository.getById(getResponded.get(0).getId()).isPresent());
        Widget saved = widgetRepository.getById(getResponded.get(0).getId()).get();
        Assert.assertEquals(widget.getId(), saved.getId());
        Assert.assertEquals(widget.getHeight(), saved.getHeight());
        Assert.assertEquals(widget.getWidth(), saved.getWidth());
        Assert.assertEquals(widget.getXIndex(), saved.getXIndex());
        Assert.assertEquals(widget.getYIndex(), saved.getYIndex());
        Assert.assertEquals(widget.getZIndex(), saved.getZIndex());
        Assert.assertEquals(widget.getModifiedAt(), saved.getModifiedAt());
        Assert.assertEquals(getResponded.get(0).getHeight(), saved.getHeight());
        Assert.assertEquals(getResponded.get(0).getWidth(), saved.getWidth());
        Assert.assertEquals(getResponded.get(0).getXIndex(), saved.getXIndex());
        Assert.assertEquals(getResponded.get(0).getYIndex(), saved.getYIndex());
        Assert.assertEquals(getResponded.get(0).getZIndex(), saved.getZIndex());
        Assert.assertEquals(getResponded.get(0).getModifiedAt(), saved.getModifiedAt());

        Assert.assertEquals(2L, (long) getResponded.get(1).getId());
        Assert.assertTrue(widgetRepository.getById(getResponded.get(1).getId()).isPresent());
        Widget savedSecond = widgetRepository.getById(getResponded.get(1).getId()).get();
        Assert.assertEquals(widgetSecond.getId(), savedSecond.getId());
        Assert.assertEquals(widgetSecond.getHeight(), savedSecond.getHeight());
        Assert.assertEquals(widgetSecond.getWidth(), savedSecond.getWidth());
        Assert.assertEquals(widgetSecond.getXIndex(), savedSecond.getXIndex());
        Assert.assertEquals(widgetSecond.getYIndex(), savedSecond.getYIndex());
        Assert.assertEquals(widgetSecond.getZIndex(), savedSecond.getZIndex());
        Assert.assertEquals(widgetSecond.getModifiedAt(), savedSecond.getModifiedAt());
        Assert.assertEquals(getResponded.get(1).getHeight(), savedSecond.getHeight());
        Assert.assertEquals(getResponded.get(1).getWidth(), savedSecond.getWidth());
        Assert.assertEquals(getResponded.get(1).getXIndex(), savedSecond.getXIndex());
        Assert.assertEquals(getResponded.get(1).getYIndex(), savedSecond.getYIndex());
        Assert.assertEquals(getResponded.get(1).getZIndex(), savedSecond.getZIndex());
        Assert.assertEquals(getResponded.get(1).getModifiedAt(), savedSecond.getModifiedAt());
    }

    @Test
    public void testWhenSendGetListWithLimit_WidgetRepository_ReturnWidgets() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();
        Widget widgetSecond = Widget.builder()
                .height(11)
                .width(21)
                .xIndex(111)
                .yIndex(221)
                .zIndex(551)
                .build();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        widget.setId(responded.getId());
        widget.setModifiedAt(responded.getModifiedAt());

        MockHttpServletResponse resultSecond = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widgetSecond))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget respondedSecond = objectMapper.readValue(resultSecond.getContentAsString(), Widget.class);
        widgetSecond.setId(respondedSecond.getId());
        widgetSecond.setModifiedAt(respondedSecond.getModifiedAt());

        // Act
        MockHttpServletResponse getResult = mockMvc.perform(get(url)
                .param("limit", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // Assert
        List<Widget> getResponded = objectMapper.readValue(getResult.getContentAsString(), new TypeReference<>(){});
        Assert.assertNotNull(getResponded);
        Assert.assertEquals(1, getResponded.size());
        Assert.assertEquals(1L, (long) getResponded.get(0).getId());
        Assert.assertTrue(widgetRepository.getById(getResponded.get(0).getId()).isPresent());
        Widget saved = widgetRepository.getById(getResponded.get(0).getId()).get();
        Assert.assertEquals(widget.getId(), saved.getId());
        Assert.assertEquals(widget.getHeight(), saved.getHeight());
        Assert.assertEquals(widget.getWidth(), saved.getWidth());
        Assert.assertEquals(widget.getXIndex(), saved.getXIndex());
        Assert.assertEquals(widget.getYIndex(), saved.getYIndex());
        Assert.assertEquals(widget.getZIndex(), saved.getZIndex());
        Assert.assertEquals(widget.getModifiedAt(), saved.getModifiedAt());
        Assert.assertEquals(getResponded.get(0).getHeight(), saved.getHeight());
        Assert.assertEquals(getResponded.get(0).getWidth(), saved.getWidth());
        Assert.assertEquals(getResponded.get(0).getXIndex(), saved.getXIndex());
        Assert.assertEquals(getResponded.get(0).getYIndex(), saved.getYIndex());
        Assert.assertEquals(getResponded.get(0).getZIndex(), saved.getZIndex());
        Assert.assertEquals(getResponded.get(0).getModifiedAt(), saved.getModifiedAt());
    }

    @Test
    public void testWhenSendGetListWithOffset_WidgetRepository_ReturnWidgets() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();
        Widget widgetSecond = Widget.builder()
                .height(11)
                .width(21)
                .xIndex(111)
                .yIndex(221)
                .zIndex(551)
                .build();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        widget.setId(responded.getId());
        widget.setModifiedAt(responded.getModifiedAt());

        MockHttpServletResponse resultSecond = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widgetSecond))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget respondedSecond = objectMapper.readValue(resultSecond.getContentAsString(), Widget.class);
        widgetSecond.setId(respondedSecond.getId());
        widgetSecond.setModifiedAt(respondedSecond.getModifiedAt());

        // Act
        MockHttpServletResponse getResult = mockMvc.perform(get(url)
                .param("offset", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // Assert
        List<Widget> getResponded = objectMapper.readValue(getResult.getContentAsString(), new TypeReference<>(){});
        Assert.assertNotNull(getResponded);
        Assert.assertEquals(1, getResponded.size());
        Assert.assertEquals(2L, (long) getResponded.get(0).getId());
        Assert.assertTrue(widgetRepository.getById(getResponded.get(0).getId()).isPresent());
        Widget savedSecond = widgetRepository.getById(getResponded.get(0).getId()).get();
        Assert.assertEquals(widgetSecond.getId(), savedSecond.getId());
        Assert.assertEquals(widgetSecond.getHeight(), savedSecond.getHeight());
        Assert.assertEquals(widgetSecond.getWidth(), savedSecond.getWidth());
        Assert.assertEquals(widgetSecond.getXIndex(), savedSecond.getXIndex());
        Assert.assertEquals(widgetSecond.getYIndex(), savedSecond.getYIndex());
        Assert.assertEquals(widgetSecond.getZIndex(), savedSecond.getZIndex());
        Assert.assertEquals(widgetSecond.getModifiedAt(), savedSecond.getModifiedAt());
        Assert.assertEquals(getResponded.get(0).getHeight(), savedSecond.getHeight());
        Assert.assertEquals(getResponded.get(0).getWidth(), savedSecond.getWidth());
        Assert.assertEquals(getResponded.get(0).getXIndex(), savedSecond.getXIndex());
        Assert.assertEquals(getResponded.get(0).getYIndex(), savedSecond.getYIndex());
        Assert.assertEquals(getResponded.get(0).getZIndex(), savedSecond.getZIndex());
        Assert.assertEquals(getResponded.get(0).getModifiedAt(), savedSecond.getModifiedAt());
    }

    @Test
    public void testWhenSendGetListWithLimitAndOffset_WidgetRepository_ReturnWidgets() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();
        Widget widgetSecond = Widget.builder()
                .height(11)
                .width(21)
                .xIndex(111)
                .yIndex(221)
                .zIndex(551)
                .build();
        Widget widgetThird = Widget.builder()
                .height(12)
                .width(22)
                .xIndex(112)
                .yIndex(222)
                .zIndex(552)
                .build();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        widget.setId(responded.getId());
        widget.setModifiedAt(responded.getModifiedAt());

        MockHttpServletResponse resultSecond = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widgetSecond))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget respondedSecond = objectMapper.readValue(resultSecond.getContentAsString(), Widget.class);
        widgetSecond.setId(respondedSecond.getId());
        widgetSecond.setModifiedAt(respondedSecond.getModifiedAt());

        MockHttpServletResponse resultThird = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widgetThird))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget respondedThird = objectMapper.readValue(resultSecond.getContentAsString(), Widget.class);
        widgetThird.setId(respondedThird.getId());
        widgetThird.setModifiedAt(respondedThird.getModifiedAt());

        // Act
        MockHttpServletResponse getResult = mockMvc.perform(get(url)
                .param("limit", "1")
                .param("offset", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // Assert
        List<Widget> getResponded = objectMapper.readValue(getResult.getContentAsString(), new TypeReference<>(){});
        Assert.assertNotNull(getResponded);
        Assert.assertEquals(1, getResponded.size());
        Assert.assertEquals(2L, (long) getResponded.get(0).getId());
        Assert.assertTrue(widgetRepository.getById(getResponded.get(0).getId()).isPresent());
        Widget saved = widgetRepository.getById(getResponded.get(0).getId()).get();
        Assert.assertEquals(widgetSecond.getId(), saved.getId());
        Assert.assertEquals(widgetSecond.getHeight(), saved.getHeight());
        Assert.assertEquals(widgetSecond.getWidth(), saved.getWidth());
        Assert.assertEquals(widgetSecond.getXIndex(), saved.getXIndex());
        Assert.assertEquals(widgetSecond.getYIndex(), saved.getYIndex());
        Assert.assertEquals(widgetSecond.getZIndex(), saved.getZIndex());
        Assert.assertEquals(widgetSecond.getModifiedAt(), saved.getModifiedAt());
        Assert.assertEquals(getResponded.get(0).getHeight(), saved.getHeight());
        Assert.assertEquals(getResponded.get(0).getWidth(), saved.getWidth());
        Assert.assertEquals(getResponded.get(0).getXIndex(), saved.getXIndex());
        Assert.assertEquals(getResponded.get(0).getYIndex(), saved.getYIndex());
        Assert.assertEquals(getResponded.get(0).getZIndex(), saved.getZIndex());
        Assert.assertEquals(getResponded.get(0).getModifiedAt(), saved.getModifiedAt());
    }

    @Test
    public void testWhenSendGetListWithoutPost_WidgetRepository_ReturnEmptyList() throws Exception {
        // Arrange
        widgetRepository.clear();

        // Act
        MockHttpServletResponse getResult = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // Assert
        List<Widget> getResponded = objectMapper.readValue(getResult.getContentAsString(), new TypeReference<>(){});
        Assert.assertNotNull(getResponded);
        Assert.assertEquals(0, getResponded.size());
        Assert.assertTrue(widgetRepository.getAll(Limit.defaultLimit()).isEmpty());
    }

    @Test
    public void testWhenSendGetListAfterAllDeleted_WidgetRepository_ReturnEmptyList() throws Exception {
        // Arrange
        widgetRepository.clear();
        Widget widget = getWidget();

        MockHttpServletResponse result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Widget responded = objectMapper.readValue(result.getContentAsString(), Widget.class);
        Assert.assertTrue(widgetRepository.getById(responded.getId()).isPresent());

        int deleteResult = mockMvc.perform(delete(url + "/" + responded.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getStatus();

        Assert.assertEquals(HttpStatus.OK.value(), deleteResult);

        // Act
        MockHttpServletResponse getResult = mockMvc.perform(get(url )
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // Assert
        List<Widget> getResponded = objectMapper.readValue(getResult.getContentAsString(), new TypeReference<>(){});
        Assert.assertNotNull(getResponded);
        Assert.assertEquals(0, getResponded.size());
        Assert.assertTrue(widgetRepository.getAll(Limit.defaultLimit()).isEmpty());
    }

    @Test
    public void testWhenSendError_CustomErrorController_Return() throws Exception {
        // Act
        MockHttpServletResponse getResult = mockMvc.perform(get("/error")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse();

        // Assert
        Assert.assertEquals(500, getResult.getStatus());
    }

    @Test
    public void testWhenSendError_CustomErrorController_CheckPath() {
        // Arrange
        ErrorAttributes errorAttributes = mock(ErrorAttributes.class);
        CustomErrorController customErrorController = new CustomErrorController(errorAttributes);

        // Act
        String errorPath = customErrorController.getErrorPath();

        // Assert
        Assert.assertNull(errorPath);
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

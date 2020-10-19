package com.miro.dev.widgets.model;

import org.junit.Assert;
import org.junit.Test;

public class LimitTest {
    @Test
    public void testDefaultLimit() {
        // Arrange and act
        Limit limit = Limit.defaultLimit();

        // Assert
        Assert.assertEquals(0, (int) limit.getOffset());
        Assert.assertEquals(10, (int) limit.getLimit());
    }

    @Test
    public void testLimit_Constructor_NullValues() {
        // Arrange and act
        Limit limit = new Limit(null, null);

        // Assert
        Assert.assertEquals(0, (int) limit.getOffset());
        Assert.assertEquals(10, (int) limit.getLimit());
    }

    @Test
    public void testLimit_Constructor_NegativeValues() {
        // Arrange and act
        Limit limit = new Limit(-1, -1);

        // Assert
        Assert.assertEquals(0, (int) limit.getOffset());
        Assert.assertEquals(10, (int) limit.getLimit());
    }

    @Test
    public void testLimit_Constructor_MaxLimit() {
        // Arrange and act
        Limit limit = new Limit(501, 100);

        // Assert
        Assert.assertEquals(100, (int) limit.getOffset());
        Assert.assertEquals(500, (int) limit.getLimit());
    }
}

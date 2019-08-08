package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class IntervalsAdjacencyDetectorTest {

    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }

    @Test
    public void isAdjacent_interval1BeforeInterval2_falseReturned() {
        // Arrange
        Interval interval1 = new Interval(-5, -1);
        Interval interval2 = new Interval(1, 5);
        // Act
        boolean result = SUT.isAdjacent(interval1, interval2);
        // Assert
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1BeforeAndAdjacentToInterval2_trueReturned() {
        // Arrange
        Interval interval1 = new Interval(-5, -1);
        Interval interval2 = new Interval(-1, 5);
        // Act
        boolean result = SUT.isAdjacent(interval1, interval2);
        // Assert
        assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_interval1OverlappingInterval2AtTheStart_falseReturned() {
        // Arrange
        Interval interval1 = new Interval(-5, 2);
        Interval interval2 = new Interval(-1, 5);
        // Act
        boolean result = SUT.isAdjacent(interval1, interval2);
        // Assert
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1ContainedWithinInterval2_falseReturned() {
        // Arrange
        Interval interval1 = new Interval(-5, 5);
        Interval interval2 = new Interval(-10, 10);
        // Act
        boolean result = SUT.isAdjacent(interval1, interval2);
        // Assert
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1ContainsInterval2_falseReturned() {
        // Arrange
        Interval interval1 = new Interval(-5, 10);
        Interval interval2 = new Interval(-1, 5);
        // Act
        boolean result = SUT.isAdjacent(interval1, interval2);
        // Assert
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_intervalEqualsInterval2_falseReturned() {
        // Arrange
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(-1, 5);
        // Act
        boolean result = SUT.isAdjacent(interval1, interval2);
        // Assert
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1OverlapsInterval2AtTHeEnd_falseReturned() {
        // Arrange
        Interval interval1 = new Interval(3, 6);
        Interval interval2 = new Interval(-1, 5);
        // Act
        boolean result = SUT.isAdjacent(interval1, interval2);
        // Assert
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1AfterAndAdjacentToInterval2_trueReturned() {
        // Arrange
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(-1, 5);
        // Act
        boolean result = SUT.isAdjacent(interval1, interval2);
        // Assert
        assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_interval1AfterInterval2_falseReturned() {
        // Arrange
        Interval interval1 = new Interval(6, 10);
        Interval interval2 = new Interval(-1, 5);
        // Act
        boolean result = SUT.isAdjacent(interval1, interval2);
        // Assert
        assertThat(result, is(false));
    }
}
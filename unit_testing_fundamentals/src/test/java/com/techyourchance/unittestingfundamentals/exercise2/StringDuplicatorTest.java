package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class StringDuplicatorTest {

    StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new StringDuplicator();
    }

    @Test
    public void duplicate_emptyString_emptyStringReturned() {
        // Arrange
        // Act
        String result = SUT.duplicate("");
        // Assert
        assertThat(result, is(""));
    }

    @Test
    public void duplicate_singleChar_singleCharDpulicated() {
        // Arrange
        // Act
        String result = SUT.duplicate("a");
        // Assert
        assertThat(result, is("aa"));
    }

    @Test
    public void duplicate_myName_myNameDuplicated() {
        String result = SUT.duplicate("Peter");
        assertThat(result, is("PeterPeter"));
    }
}
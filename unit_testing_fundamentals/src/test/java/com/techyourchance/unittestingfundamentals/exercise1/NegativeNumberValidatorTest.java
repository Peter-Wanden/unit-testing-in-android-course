package com.techyourchance.unittestingfundamentals.exercise1;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NegativeNumberValidatorTest {

    private NegativeNumberValidator SUT;

    @Before
    public void setup() {
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void test1() {
        Assert.assertTrue(SUT.isNegative(-1));
    }

    @Test
    public void test2() {
        Assert.assertFalse(SUT.isNegative(0));
    }

    @Test
    public void test3() {
        Assert.assertFalse(SUT.isNegative(1));
    }
}
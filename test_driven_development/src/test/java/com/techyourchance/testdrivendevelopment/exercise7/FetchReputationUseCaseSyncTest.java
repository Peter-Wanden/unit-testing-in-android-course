package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.*;
import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    // region constants
    public static final int REPUTATION = 1;
    public static final int REPUTATION_FAILED = 0;
    // endregion constants

    // region helper fields
    @Mock GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;
    // endregion helper fields    

    FetchReputationUseCaseSync SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchReputationUseCaseSync(getReputationHttpEndpointSyncMock);
        success();
    }

    @Test
    public void fetchReputation_success_successReturned() throws Exception {
        // Arrange
        // Act
        UseCaseResult result = SUT.fetchReputation();
        // Assert
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void fetchReputation_generalError_failureReturned() throws Exception {
        // Arrange
        generalError();
        // Act
        UseCaseResult result = SUT.fetchReputation();
        // Assert
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchReputation_networkEtrror_failureReturned() throws Exception {
        // Arrange
        networkError();
        // Act
        UseCaseResult result = SUT.fetchReputation();
        // Assert
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    // region for helper methods
    private void success() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(
                new EndpointResult(EndpointStatus.SUCCESS, REPUTATION));
    }

    private void generalError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(
                new EndpointResult(EndpointStatus.GENERAL_ERROR, REPUTATION_FAILED));
    }

    private void networkError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(
                new EndpointResult(EndpointStatus.NETWORK_ERROR, 0));
    }
    // endregion helper methods

    // region helper classes
    // endregion helper classes
}
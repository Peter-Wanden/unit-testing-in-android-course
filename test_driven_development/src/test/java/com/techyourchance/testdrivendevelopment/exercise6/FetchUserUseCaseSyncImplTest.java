package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncImplTest {

    // region constants
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final User USER = new User(USER_ID, USER_NAME);
    // endregion constants

    // region helper fields
    FetchUserHttpEndpointSyncTd fetchUserHttpEndpointSyncTd;
    @Mock UsersCache usersCacheMock;
    // endregion helper fields    

    FetchUserUseCaseSyncImpl SUT;

    @Before
    public void setup() throws Exception {
        fetchUserHttpEndpointSyncTd = new FetchUserHttpEndpointSyncTd();
        SUT = new FetchUserUseCaseSyncImpl(fetchUserHttpEndpointSyncTd, usersCacheMock);
        userNotInCache();
        endpointSuccess();
    }

    @Test
    public void fetchUserSync_notInCache_correctUserIdPassedToEndpoint() throws Exception {
        // Arrange
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(fetchUserHttpEndpointSyncTd.userId, is(USER_ID));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_successStatus() throws Exception {
        // Arrange
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.SUCCESS));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_correctUserReturned() throws Exception {
        // Arrange
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(USER));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_userCached() throws Exception {
        // Arrange
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock).cacheUser(ac.capture());
        assertThat(ac.getValue(), is(USER));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_failureStatus() throws Exception {
        // Arrange
        endpointAuthError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_nullUserReturned() throws Exception {
        // Arrange
        endpointAuthError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_nothingCached() throws Exception {
        // Arrange
        endpointAuthError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointServerError_failureStatus() throws Exception {
        // Arrange
        endpointServerError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointServerError_nullUserReturned() throws Exception {
        // Arrange
        endpointServerError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointServerError_nothingCached() throws Exception {
        // Arrange
        endpointServerError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_failureStatus() throws Exception {
        // Arrange
        endpointNetworkError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.NETWORK_ERROR));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_nullUserReturned() throws Exception {
        // Arrange
        endpointNetworkError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_nothingCached() throws Exception {
        // Arrange
        endpointNetworkError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_correctUserIdPassedToCache() throws Exception {
        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock).getUser(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    @Test
    public void fetchUserSync_userCached_successStatus() throws Exception {
        // Arrange
        userInCache();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.SUCCESS));
    }

    @Test
    public void fetchUserSync_userCached_correctUserReturned() throws Exception {
        // Arrange
        userInCache();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(USER));
    }

    @Test
    public void fetchUserSync_userCached_endpointNotPolled() throws Exception {
        // Arrange
        userInCache();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(fetchUserHttpEndpointSyncTd.requestCount, is(0));
    }

    // region for helper methods
    private void userNotInCache() {
        when(usersCacheMock.getUser(anyString())).thenReturn(null);
    }

    private void endpointSuccess() {
        // endpoint test double is set up for success by default; this method is for clarity of intent
    }

    private void endpointAuthError() {
        fetchUserHttpEndpointSyncTd.authError = true;
    }

    private void endpointServerError() {
        fetchUserHttpEndpointSyncTd.serverError = true;
    }

    private void endpointNetworkError() {
        fetchUserHttpEndpointSyncTd.networkError = true;
    }

    private void userInCache() {
        when(usersCacheMock.getUser(anyString())).thenReturn(USER);
    }
    // endregion helper methods

    // region helper classes
    private class FetchUserHttpEndpointSyncTd implements FetchUserHttpEndpointSync {

        public int requestCount;
        private String userId = "";
        private boolean authError;
        public boolean serverError;
        public boolean networkError;

        @Override
        public EndpointResult fetchUserSync(String userId) throws NetworkErrorException {
            requestCount ++;
            this.userId = userId;

            if (authError)
                return new EndpointResult(EndpointStatus.AUTH_ERROR, "", "");
            else if (serverError)
                return new EndpointResult(EndpointStatus.GENERAL_ERROR, "", "");
            else if (networkError)
                throw new NetworkErrorException();
            else
                return new EndpointResult(EndpointStatus.SUCCESS, USER_ID, USER_NAME);
        }
    }
    // endregion helper classes
}
package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTestAlternative {

    public static final String USER_ID = "user_id";
    public static final String FULL_NAME = "full_name";
    public static final String IMAGE_URL = "image_url";

    UserProfileHttpEndpointSyncTd userProfileHttpEndpointSyncTd;
    UsersCacheTd usersCacheTd;

    FetchUserProfileUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        userProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        usersCacheTd = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(userProfileHttpEndpointSyncTd, usersCacheTd);
    }

    @Test
    public void fetchUserProfileSync_success_userIdPassedToEndpoint() {
        // Arrange
        // Act
        SUT.fetchUserProfileSync(USER_ID);
        // Assert
        assertThat(userProfileHttpEndpointSyncTd.userId, is(USER_ID));
    }

    @Test
    public void fetchUserProfileSync_success_userPassedToUsersCache() {
        // Arrange
        // Act
        SUT.fetchUserProfileSync(USER_ID);
        User cachedUser = usersCacheTd.getUser(USER_ID);
        // Assert
        assertThat(cachedUser.getUserId(), is(USER_ID));
        assertThat(cachedUser.getFullName(), is(FULL_NAME));
        assertThat(cachedUser.getImageUrl(), is(IMAGE_URL));
    }

    @Test
    public void fetchUserProfileSync_authError_userNotCached() {
        // Arrange
        userProfileHttpEndpointSyncTd.isAuthError = true;
        // Act
        SUT.fetchUserProfileSync(USER_ID);
        // Assert
        assertThat(usersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserProfileSync_serverError_userNotCached() {
        // Arrange
        userProfileHttpEndpointSyncTd.isServerError = true;
        // Act
        SUT.fetchUserProfileSync(USER_ID);
        // Assert
        assertThat(usersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserProfileSync_generalError_userNotCached() {
        // Arrange
        userProfileHttpEndpointSyncTd.isGeneralError = true;
        // Act
        SUT.fetchUserProfileSync(USER_ID);
        // Assert
        assertThat(usersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserProfileSync_success_successReturned() {
        // Arrange
        // Act
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        // Assert
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void fetchUserProfileSync_authError_failureReturned() {
        // Arrange
        userProfileHttpEndpointSyncTd.isAuthError = true;
        // Act
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        // Assert
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_serverError_failureReturned() {
        // Arrange
        userProfileHttpEndpointSyncTd.isServerError = true;
        // Act
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        // Assert
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_generalError_failureReturned() {
        // Arrange
        userProfileHttpEndpointSyncTd.isGeneralError = true;
        // Act
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        // Assert
        assertThat(result, is(UseCaseResult.FAILURE));
    }


    // network error, network error returned
    @Test
    public void fetchUserProfileSync_NetworkError_networkErrorExceptionThrown() {
        // Arrange
        userProfileHttpEndpointSyncTd.isNetworkError = true;
        // Act
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        // Assert
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    // Helper classes
    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {

        String userId = "";
        boolean isAuthError;
        boolean isServerError;
        boolean isGeneralError;
        boolean isNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            this.userId = userId;
            if (isAuthError)
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            if (isServerError)
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            if (isGeneralError)
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            if (isNetworkError)
                throw new NetworkErrorException();
            else
                return new EndpointResult(EndpointResultStatus.SUCCESS, userId, FULL_NAME, IMAGE_URL);
        }
    }

    private static class UsersCacheTd implements UsersCache {

        LinkedHashMap<String, User> users = new LinkedHashMap<>();

        @Override
        public void cacheUser(User user) {
            users.put(user.getUserId(), user);
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            if (users.containsKey(userId))
                return users.get(userId);
            else
                return null;
        }
    }
}
package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;
import static com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync.UseCaseResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.*;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.charThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest {

    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";

    UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSyncMock;
    UsersCache usersCacheMock;
    EventBusPoster eventBusPosterMock;

    UpdateUsernameUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        updateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync.class);
        usersCacheMock = mock(UsersCache.class);
        eventBusPosterMock = mock(EventBusPoster.class);

        SUT = new UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSyncMock, usersCacheMock, eventBusPosterMock);

        success();
    }

    @Test
    public void updateUserName_success_userIdAndNamePassedToEndPoint() throws Exception{
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);

        verify(updateUsernameHttpEndpointSyncMock, times(1))
                .updateUsername(ac.capture(), ac.capture());
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USER_ID));
        assertThat(captures.get(1), is(USER_NAME));
    }

    // updateUserName_success_UserCached

    @Test
    public void updateUserName_success_UserCached() throws Exception{
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);

        verify(usersCacheMock).cacheUser(ac.capture());
        User cachedUser = ac.getValue();

        assertThat(cachedUser.getUserId(), is(USER_ID));
        assertThat(cachedUser.getUsername(), is(USER_NAME));
    }

    @Test
    public void updateUserName_authError_userNotCached() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUserName_serverError_userNotCached() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUserName_generalError_userNotCached() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUserName_success_logInEventPostedToBus() throws Exception {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);

        verify(eventBusPosterMock).postEvent(ac.capture());
        assertThat(ac.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void updateUserName_authError_noInteractionWithEventBusPoster() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUserName_serverError_noInteractionWithEventBusPoster() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUserName_generalError_noInteractionWithEventBusPoster() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUserName_success_successReturned() {
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUserName_generalError_failureReturned() throws Exception {
        generalError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUserName_authError_failureReturned() throws Exception {
        authError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUserName_serverError_failureReturned() throws Exception {
        serverError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUserName_networkError_networkErrorReturned() throws Exception{
        networkError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    private void generalError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString())).thenReturn(
                new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void authError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString())).thenReturn(
                new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString())).thenReturn(
                new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void networkError() throws Exception{
        doThrow(new NetworkErrorException())
                .when(updateUsernameHttpEndpointSyncMock).updateUsername(anyString(), anyString());
    }

    private void success() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, USER_NAME));
    }
}
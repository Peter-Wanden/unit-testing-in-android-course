package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    // region constants
    public static final String FILTER_TERM = "filter_term";
    public static final String ID = "id";
    public static final String FULL_NAME = "full_name";
    public static final String FULL_PHONE_NUMBER = "full_phone_number";
    public static final String IMAGE_URL = "image_url";
    public static final double AGE = 9.5;
    // endregion constants

    // region helper fields
    @Mock
    GetContactsHttpEndpoint getContactsHttpEndpointMock;
    @Mock
    FetchContactsUseCase.Listener listenerMock1;
    @Mock
    FetchContactsUseCase.Listener listenerMock2;
    @Captor
    ArgumentCaptor<List<Contact>> listContacts;
    // endregion helper fields    

    FetchContactsUseCase SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchContactsUseCase(getContactsHttpEndpointMock);
        success();
    }

    @Test
    public void fetchContacts_correctFilterTermPassedToEndpoint() throws Exception {
        // Arrange
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        // Act
        SUT.fetchContactsAndNotify(FILTER_TERM);
        // Assert
        verify(getContactsHttpEndpointMock).getContacts(acString.capture(), any(GetContactsHttpEndpoint.Callback.class));
        assertThat(acString.getValue(), is(FILTER_TERM));
    }

    @Test
    public void fetchContacts_success_allObserversNotifiedWithCorrectData() throws Exception {
        // Arrange
        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        // Assert
        verify(listenerMock1).onFetchedContacts(listContacts.capture());
        verify(listenerMock2).onFetchedContacts(listContacts.capture());
        List<List<Contact>> captures = listContacts.getAllValues();
        List<Contact> capture1 = captures.get(0);
        List<Contact> capture2 = captures.get(1);
        assertThat(capture1, is(getContacts()));
        assertThat(capture2, is(getContacts()));
    }

    @Test
    public void fetchContacts_success_unsubscribedObserversNotNotified() throws Exception {
        // Arrange
        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.unregisterListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        // Assert
        verify(listenerMock1).onFetchedContacts(any(List.class));
        verifyNoMoreInteractions(listenerMock2);
    }

    @Test
    public void fetchContacts_generalError_observersNotifiedOfFailure() throws Exception {
        // Arrange
        generalError();
        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        // Assert
        verify(listenerMock1).onFetchContactsFailed();
        verify(listenerMock2).onFetchContactsFailed();
    }

    @Test
    public void fetchContacts_networkError_observersNotifiedOfNetworkError() throws Exception {
        // Arrange
        networkError();
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        // Assert
        verify(listenerMock1).onFetchContactsFailed();
        verify(listenerMock2).onFetchContactsFailed();
    }

    // region for helper methods
    private void success() {
        // Mock to answer the callbacks
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                // Capture the args set to the callback (string[0] and callback[1]
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactsSchemes());
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private List<ContactSchema> getContactsSchemes() {
        List<ContactSchema> schemas = new ArrayList<>();
        schemas.add(new ContactSchema(
                ID,
                FULL_NAME,
                FULL_PHONE_NUMBER,
                IMAGE_URL, AGE));
        return schemas;
    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(
                ID,
                FULL_NAME,
                IMAGE_URL));
        return contacts;
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }
    // endregion helper methods

    // region helper classes
    // endregion helper classes
}
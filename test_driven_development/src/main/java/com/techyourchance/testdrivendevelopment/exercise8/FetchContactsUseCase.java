package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.*;

public class FetchContactsUseCase {

    public interface Listener {
        void onFetchedContacts(List<Contact> contacts);

        void onFetchContactsFailed();
    }

    private final List<Listener> listeners = new ArrayList<>();
    private final GetContactsHttpEndpoint getContactsHttpEndpoint;

    public FetchContactsUseCase(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        this.getContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    public void fetchContactsAndNotify(final String filterTerm) {
        getContactsHttpEndpoint.getContacts(filterTerm, new Callback() {

            @Override
            public void onGetContactsSucceeded(List<ContactSchema> schemaContacts) {
                notifyListenersSucceded(schemaContacts);
            }

            @Override
            public void onGetContactsFailed(FailReason failReason) {
                if (failReason == FailReason.GENERAL_ERROR ||
                        failReason == FailReason.NETWORK_ERROR)
                    notifyListenersFailure();

                else
                    throw new RuntimeException("Failure reason unsupported: " + failReason);
            }
        });
    }

    private List<Contact> contactsFromSchemas(List<ContactSchema> contactSchemaList) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema schema : contactSchemaList)
            contacts.add(new Contact(
                    schema.getId(),
                    schema.getFullName(),
                    schema.getImageUrl()
            ));
        return contacts;
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyListenersSucceded(List<ContactSchema> contactSchemaList) {
        for (Listener listener : listeners)
            listener.onFetchedContacts(contactsFromSchemas(contactSchemaList));
    }

    private void notifyListenersFailure() {
        for (Listener listener : listeners)
            listener.onFetchContactsFailed();
    }
}

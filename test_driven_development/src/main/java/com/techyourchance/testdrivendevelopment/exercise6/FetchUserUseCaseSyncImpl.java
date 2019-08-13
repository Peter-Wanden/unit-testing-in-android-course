package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.*;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    private final FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    private final UsersCache usersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync,
                                    UsersCache usersCache) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;

    }

    public UseCaseResult fetchUserSync(String userId) {
        EndpointResult result;

        if (usersCache.getUser(userId) != null) {
            return new UseCaseResult(Status.SUCCESS, usersCache.getUser(userId));

        } else {

            try {
                result = fetchUserHttpEndpointSync.fetchUserSync(userId);
            } catch (NetworkErrorException e) {
                return new UseCaseResult(Status.NETWORK_ERROR, null);
            }

            if (result.getStatus() == EndpointStatus.SUCCESS) {
                User user = new User(result.getUserId(), result.getUsername());
                usersCache.cacheUser(user);
                return new UseCaseResult(Status.SUCCESS, user);

            } else if (result.getStatus() == EndpointStatus.AUTH_ERROR
                    || result.getStatus() == EndpointStatus.GENERAL_ERROR) {
                return new UseCaseResult(Status.FAILURE, null);

            } else {
                throw new RuntimeException("invalid status: " + result);
            }
        }
    }
}

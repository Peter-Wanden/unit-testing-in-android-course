package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import javax.management.RuntimeErrorException;

import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.*;

public class FetchReputationUseCaseSync {

    private final GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    public FetchReputationUseCaseSync(
            GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        this.getReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }

    public enum UseCaseResult {
        FAILURE, SUCCESS
    }

    public UseCaseResult fetchReputation() {
        EndpointResult result = getReputationHttpEndpointSync.getReputationSync();

        if (result.getStatus() == EndpointStatus.GENERAL_ERROR ||
                result.getStatus() == EndpointStatus.NETWORK_ERROR)
            return UseCaseResult.FAILURE;

        else if (result.getStatus() == EndpointStatus.SUCCESS)
            return UseCaseResult.SUCCESS;

        else
            throw new RuntimeException("invalid result: " + result);
    }
}

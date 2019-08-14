package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    // region constants ----------------------------------------------------------------------------
    public static final String QUESTION_ID = "question_id";
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    public EndpointTd endpointTd;
    @Mock
    FetchQuestionDetailsUseCase.Listener listener1;
    @Mock
    FetchQuestionDetailsUseCase.Listener listener2;
    // endregion helper fields ---------------------------------------------------------------------

    FetchQuestionDetailsUseCase SUT;

    @Before
    public void setup() throws Exception {
        endpointTd = new EndpointTd();
        SUT = new FetchQuestionDetailsUseCase(endpointTd);
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_listenersNotifiedWithCorrectData() throws Exception {
        // Arrange
        ArgumentCaptor<QuestionDetails> ac = ArgumentCaptor.forClass(QuestionDetails.class);
        success();
        // Act
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        // Assert
        verify(listener1).onQuestionDetailsFetched(ac.capture());
        verify(listener2).onQuestionDetailsFetched(ac.capture());
        List<QuestionDetails> questionDetailsList = ac.getAllValues();
        assertThat(questionDetailsList.get(0), is(getExpectedQuestion()));
        assertThat(questionDetailsList.get(1), is(getExpectedQuestion()));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedOfFailure() throws Exception {
        // Arrange
        failure();
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        // Assert
        verify(listener1).onQuestionDetailsFetchFailed();
        verify(listener2).onQuestionDetailsFetchFailed();
    }

    // region for helper methods -------------------------------------------------------------------
    private void success() {
        // currently no-op
    }

    private QuestionDetails getExpectedQuestion() {
        return new QuestionDetails("id", "title", "body");
    }

    private void failure() {
        endpointTd.failure = true;
    }
    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    private static class EndpointTd extends FetchQuestionDetailsEndpoint {
        public boolean failure;

        public EndpointTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(String questionId, Listener listener) {
            if (failure) {
                listener.onQuestionDetailsFetchFailed();
            } else {
                QuestionSchema questionSchema = new QuestionSchema("title", "id", "body");
                listener.onQuestionDetailsFetched(questionSchema);
            }
        }
    }
    // endregion helper classes --------------------------------------------------------------------
}
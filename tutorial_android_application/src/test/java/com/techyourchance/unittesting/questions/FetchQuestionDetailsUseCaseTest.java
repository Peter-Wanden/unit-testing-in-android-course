package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;
import com.techyourchance.unittesting.testdata.QuestionDetailsTestData;

import net.bytebuddy.implementation.bytecode.Throw;

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
    public static final QuestionDetails QUESTION_DETAILS_ONE = QuestionDetailsTestData.getQuestionDetails();
    public static final String QUESTION_ONE_ID = QUESTION_DETAILS_ONE.getId();
    public static final QuestionDetails QUESTION_DETAILS_TWO = QuestionDetailsTestData.getQuestionDetails2();
    public static final String QUESTION_TWO_ID = QUESTION_DETAILS_TWO.getId();
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    private EndpointTd endpointTd;
    @Mock TimeProvider timeProviderMock;
    @Mock FetchQuestionDetailsUseCase.Listener listener1;
    @Mock FetchQuestionDetailsUseCase.Listener listener2;
    // endregion helper fields ---------------------------------------------------------------------

    FetchQuestionDetailsUseCase SUT;

    @Before
    public void setup() throws Exception {
        endpointTd = new EndpointTd();
        SUT = new FetchQuestionDetailsUseCase(endpointTd, timeProviderMock);
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_listenersNotifiedWithCorrectData() throws Exception {
        // Arrange
        ArgumentCaptor<QuestionDetails> ac = ArgumentCaptor.forClass(QuestionDetails.class);
        success();
        // Act
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ONE_ID);
        // Assert
        verify(listener1).onQuestionDetailsFetched(ac.capture());
        verify(listener2).onQuestionDetailsFetched(ac.capture());
        List<QuestionDetails> questionDetailsList = ac.getAllValues();
        assertThat(questionDetailsList.get(0), is(QUESTION_DETAILS_ONE));
        assertThat(questionDetailsList.get(1), is(QUESTION_DETAILS_ONE));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedOfFailure() throws Exception {
        // Arrange
        failure();
        // Act
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ONE_ID);
        // Assert
        verify(listener1).onQuestionDetailsFetchFailed();
        verify(listener2).onQuestionDetailsFetchFailed();
    }

    @Test
    public void fetchQuestionDetailsAndNotify_firstTime_questionOneReturned() throws Exception {
        // Arrange
        ArgumentCaptor<QuestionDetails> ac = ArgumentCaptor.forClass(QuestionDetails.class);
        // Act
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ONE_ID);
        // Assert
        verify(listener1).onQuestionDetailsFetched(ac.capture());
        verify(listener2).onQuestionDetailsFetched(ac.capture());
        List<QuestionDetails> questionDetailsList = ac.getAllValues();
        QuestionDetails questionDetails1 = questionDetailsList.get(0);
        QuestionDetails questionDetails2 = questionDetailsList.get(1);
        assertThat(questionDetails1, is(QUESTION_DETAILS_ONE));
        assertThat(questionDetails2, is(QUESTION_DETAILS_ONE));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeBeforeCachingTimeout_EndpointCalledOnce() throws Exception {
        // Arrange
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ONE_ID);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(30000L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ONE_ID);
        // Assert
        assertThat(endpointTd.callCount, is(1));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeBeforeCachingTimeout_questionOneReturned() throws Exception {
        // Arrange
        ArgumentCaptor<QuestionDetails> ac = ArgumentCaptor.forClass(QuestionDetails.class);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ONE_ID);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(59999L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ONE_ID);
        // Assert
        verify(listener1, times(2)).onQuestionDetailsFetched(ac.capture());
        verify(listener2, times(2)).onQuestionDetailsFetched(ac.capture());
        List<QuestionDetails> questionDetailsList = ac.getAllValues();
        QuestionDetails questionDetails1 = questionDetailsList.get(0);
        QuestionDetails questionDetails2 = questionDetailsList.get(1);
        assertThat(questionDetails1, is(QUESTION_DETAILS_ONE));
        assertThat(questionDetails2, is(QUESTION_DETAILS_ONE));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeAfterCachingTimeout_questionDetailsReturned() throws Exception {
        // Arrange
        ArgumentCaptor<QuestionDetails> ac = ArgumentCaptor.forClass(QuestionDetails.class);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ONE_ID);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(60001L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ONE_ID);
        // Assert
        verify(listener1, times(2)).onQuestionDetailsFetched(ac.capture());
        verify(listener1, times(2)).onQuestionDetailsFetched(ac.capture());
        List<QuestionDetails> questionDetailsList = ac.getAllValues();
        QuestionDetails questionDetails1 = questionDetailsList.get(0);
        QuestionDetails questionDetails2 = questionDetailsList.get(1);
        assertThat(questionDetails1, is(QUESTION_DETAILS_ONE));
        assertThat(questionDetails1, is(QUESTION_DETAILS_ONE));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeBeforeCachingTimeoutQuestionIdTwo_QuestionTwoReturned() throws Exception {
        // Arrange
        ArgumentCaptor<QuestionDetails> ac = ArgumentCaptor.forClass(QuestionDetails.class);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ONE_ID);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(30000L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_TWO_ID);
        // Assert
        verify(listener1, times(2)).onQuestionDetailsFetched(ac.capture());
        verify(listener2, times(2)).onQuestionDetailsFetched(ac.capture());
        List<QuestionDetails> questionDetailsList = ac.getAllValues();
        QuestionDetails questionDetails2 = questionDetailsList.get(1);
        QuestionDetails questionDetails4 = questionDetailsList.get(3);
        assertThat(questionDetails2, is(QUESTION_DETAILS_TWO));
        assertThat(questionDetails4, is(QUESTION_DETAILS_TWO));
    }

    // region for helper methods -------------------------------------------------------------------
    private void success() {
        // no-op
    }

    private void failure() {
        endpointTd.failure = true;
    }

    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    private static class EndpointTd extends FetchQuestionDetailsEndpoint {
        private boolean failure;
        private int callCount;

        public EndpointTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(String questionId, Listener listener) {
            callCount ++;
            if (failure) {
                listener.onQuestionDetailsFetchFailed();
            } else {
                QuestionSchema questionSchema = getCorrectQuestion(questionId);
                listener.onQuestionDetailsFetched(questionSchema);
            }
        }

        private QuestionSchema getCorrectQuestion(String questionId) {
            if (questionId.equals(QUESTION_ONE_ID))
                return new QuestionSchema(
                        QUESTION_DETAILS_ONE.getTitle(),
                        QUESTION_DETAILS_ONE.getId(),
                        QUESTION_DETAILS_ONE.getBody());

            else if (questionId.equals(QUESTION_TWO_ID))
                return new QuestionSchema(
                        QUESTION_DETAILS_TWO.getTitle(),
                        QUESTION_DETAILS_TWO.getId(),
                        QUESTION_DETAILS_TWO.getBody()) ;

            else
                throw new RuntimeException("Invalid question id:" + questionId);
        }
    }
    // endregion helper classes --------------------------------------------------------------------
}
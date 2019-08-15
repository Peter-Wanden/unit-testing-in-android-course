package com.techyourchance.unittesting.screens.questiondetails;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;
import com.techyourchance.unittesting.testdata.QuestionsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest {

    // region constants ----------------------------------------------------------------------------
    public static final QuestionDetails QUESTION_DETAILS = QuestionsTestData.getQuestionDetails();
    public static final String QUESTION_ID = QUESTION_DETAILS.getId();
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    private UseCaseTd fetchQuestionUseCaseTd;
    @Mock
    ScreensNavigator screensNavigatorMock;
    @Mock
    ToastsHelper toastsHelperMock;
    @Mock
    QuestionDetailsViewMvc questionDetailsViewMvcMock;
    // endregion helper fields ---------------------------------------------------------------------

    QuestionDetailsController SUT;

    @Before
    public void setup() throws Exception {
        fetchQuestionUseCaseTd = new UseCaseTd();

        SUT = new QuestionDetailsController(
                fetchQuestionUseCaseTd,
                screensNavigatorMock,
                toastsHelperMock);

        SUT.bindView(questionDetailsViewMvcMock);
        SUT.bindQuestionId(QUESTION_ID);
    }

    @Test
    public void onStart_listenersRegistered() throws Exception {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).registerListener(SUT);
        fetchQuestionUseCaseTd.verifyListenerRegistered(SUT);
    }

    @Test
    public void onStop_useCaseListenerUnregistered() throws Exception {
        // Arrange
        // Act
        SUT.onStop();
        // Assert
        verify(questionDetailsViewMvcMock).unregisterListener(SUT);
        fetchQuestionUseCaseTd.verifyListenerUnregistered(SUT);
    }

    @Test
    public void onStart_progressIndicationShown() throws Exception {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).showProgressIndication();
    }

    @Test
    public void onStart_success_questionDetailsBoundToView() throws Exception {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).bindQuestion(QUESTION_DETAILS);
    }

    @Test
    public void onStart_success_progressIndicationHidden() throws Exception {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).hideProgressIndication();
    }

    @Test
    public void onStart_failure_progressIndicationShown() throws Exception {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).showProgressIndication();
    }

    @Test
    public void onStart_failure_progressIndicatorHidden() throws Exception {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).hideProgressIndication();
    }

    @Test
    public void onStart_failure_errorToastShown() throws Exception {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(toastsHelperMock).showUseCaseError();
    }

    @Test
    public void onNavigateUpClicked_navigateUp() throws Exception {
        // Arrange
        success();
        // Act
        SUT.onNavigateUpClicked();
        // Assert
        verify(screensNavigatorMock).navigateUp();
    }

    // region for helper methods -------------------------------------------------------------------
    private void success() {
        // no-op
    }

    private void failure() {
        fetchQuestionUseCaseTd.failure = true;
    }
    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------

    private static class UseCaseTd extends FetchQuestionDetailsUseCase {

        public boolean failure;

        public UseCaseTd() {
            super(null);
        }

        public void verifyListenerRegistered(QuestionDetailsController candidate) {
            for (Listener listener : getListeners())
                if (listener == candidate)
                    return;
            throw new RuntimeException("listener not registered");
        }

        public void verifyListenerUnregistered(QuestionDetailsController candidate) {
            for (Listener listener : getListeners())
                if (listener == candidate)
                    throw new RuntimeException("listener registered");

        }

        @Override
        public void fetchQuestionDetailsAndNotify(String questionId) {
            if (!questionId.equals(QUESTION_ID))
                throw new RuntimeException("invalid question ID" + questionId);

            for (Listener listener : getListeners())
                if (failure)
                    listener.onQuestionDetailsFetchFailed();
                else
                    listener.onQuestionDetailsFetched(QUESTION_DETAILS);
        }
    }
    // endregion helper classes --------------------------------------------------------------------
}
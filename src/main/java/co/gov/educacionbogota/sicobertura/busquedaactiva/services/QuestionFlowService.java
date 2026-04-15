package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.QuestionResponse;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.QuestionResponse;

public interface QuestionFlowService {
    QuestionResponse processAnswer(Long registroId, Integer questionId, String answer);
    QuestionResponse.QuestionDTO getQuestion(Integer questionId);
    QuestionResponse getInitialState(Long registroId);
    java.util.List<QuestionResponse.QuestionDTO> getAllQuestions();
}

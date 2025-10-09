package vn.iwork4se.service;

import org.springframework.data.domain.Page;
import vn.iwork4se.controller.request.SavedApplicantCreationRequest;
import vn.iwork4se.controller.response.SavedApplicantPageResponse;
import vn.iwork4se.controller.response.SavedApplicantResponse;
import vn.iwork4se.elasticsearch.document.ApplicantDocument;

public interface SavedApplicantService {
    SavedApplicantResponse saveApplicant(SavedApplicantCreationRequest request);
    SavedApplicantPageResponse getApplicantsByList(String listId, int page, int size);
    SavedApplicantPageResponse getApplicantsByListAndContactStatus(String listId, Boolean isContacted, int page, int size);
    SavedApplicantPageResponse getAllSavedApplicantsByEmployer(String employerId, int page, int size);
    SavedApplicantResponse updateContactStatus(String savedApplicantId, Boolean isContacted);
    SavedApplicantResponse updateNotes(String savedApplicantId, String notes);
    void removeApplicantFromList(String savedApplicantId);
    void removeApplicantFromListByIds(String listId, String applicantId);
    boolean isApplicantSavedInList(String listId, String applicantId);
}

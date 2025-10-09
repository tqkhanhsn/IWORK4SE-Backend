package vn.iwork4se.service;

import vn.iwork4se.controller.request.SavedApplicantListCreationRequest;
import vn.iwork4se.controller.response.SavedApplicantListResponse;
import vn.iwork4se.model.SavedApplicantList;

import java.util.List;

public interface SavedApplicantListService {
    SavedApplicantListResponse createList(SavedApplicantListCreationRequest request);
    SavedApplicantListResponse createDefaultList(String employerId);
    List<SavedApplicantListResponse> getAllListsByEmployer(String employerId);
    SavedApplicantListResponse getListById(String listId);
    void deleteList(String listId);
    SavedApplicantList getListEntityById(String listId);
}

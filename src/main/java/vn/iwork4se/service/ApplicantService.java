package vn.iwork4se.service;

import vn.iwork4se.controller.request.ApplicantUpdateRequest;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.response.UserCreationResponse;
import vn.iwork4se.controller.response.ApplicantPageResponse;
import vn.iwork4se.controller.response.ApplicantResponse;

public interface ApplicantService {

    void updateApplicant(ApplicantUpdateRequest req);
    void changePasswordApplicant(ChangePasswordRequest req);
    ApplicantResponse findApplicantById(String id);
    ApplicantPageResponse findAllApplicants(String keyword, String sort, int page, int size);
    void deleteApplicantById(String id);
}

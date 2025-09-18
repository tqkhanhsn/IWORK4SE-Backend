package vn.iwork4se.service;

import vn.iwork4se.controller.request.ApplicantCreationRequest;
import vn.iwork4se.controller.request.ApplicantUpdateRequest;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.EmployerCreationRequest;
import vn.iwork4se.controller.response.ApplicantCreationResponse;
import vn.iwork4se.controller.response.ApplicantPageResponse;
import vn.iwork4se.controller.response.ApplicantResponse;
import vn.iwork4se.controller.response.EmployerCreationResponse;

public interface ApplicantService {
    ApplicantCreationResponse save(ApplicantCreationRequest req);
    void updateApplicant(ApplicantUpdateRequest req);
    void changePasswordApplicant(ChangePasswordRequest req);
    ApplicantResponse findApplicantById(String id);
    ApplicantPageResponse findAllApplicants(String keyword, String sort, int page, int size);
    void deleteApplicantById(String id);
}

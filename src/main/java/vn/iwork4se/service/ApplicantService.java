package vn.iwork4se.service;

import vn.iwork4se.controller.request.ApplicantCreationRequest;
import vn.iwork4se.controller.request.ApplicantUpdateRequest;
import vn.iwork4se.controller.request.EmployerCreationRequest;
import vn.iwork4se.controller.response.ApplicantCreationResponse;
import vn.iwork4se.controller.response.EmployerCreationResponse;

public interface ApplicantService {
    ApplicantCreationResponse save(ApplicantCreationRequest req);
    void updateApplicant(ApplicantUpdateRequest req);
}

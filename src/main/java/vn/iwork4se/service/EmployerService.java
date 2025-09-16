package vn.iwork4se.service;

import vn.iwork4se.controller.request.EmployerCreationRequest;
import vn.iwork4se.controller.request.EmployerUpdateRequest;
import vn.iwork4se.controller.response.EmployerCreationResponse;

public interface EmployerService {
    EmployerCreationResponse save(EmployerCreationRequest req);
    void updateEmployer(EmployerUpdateRequest req);
}

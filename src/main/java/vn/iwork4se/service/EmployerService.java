package vn.iwork4se.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.EmployerCreationRequest;
import vn.iwork4se.controller.request.EmployerUpdateRequest;
import vn.iwork4se.controller.response.EmployerCreationResponse;
import vn.iwork4se.controller.response.EmployerPageResponse;
import vn.iwork4se.controller.response.EmployerResponse;
import vn.iwork4se.model.Employer;

import java.util.Optional;

public interface EmployerService {
    EmployerCreationResponse save(EmployerCreationRequest req);
    void updateEmployer(EmployerUpdateRequest req);
    void changePasswordEmployer(ChangePasswordRequest req);
    EmployerResponse findEmployerById(String id);
    EmployerPageResponse findAllEmployers(String keyword, String sort, int page, int size);

}

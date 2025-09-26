package vn.iwork4se.service;

import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.EmployerUpdateRequest;
import vn.iwork4se.controller.response.EmployerPageResponse;
import vn.iwork4se.controller.response.EmployerResponse;

public interface EmployerService {
    void updateEmployer(EmployerUpdateRequest req);
    EmployerResponse findEmployerById(String id);
    EmployerPageResponse findAllEmployers(String keyword, String sort, int page, int size);
    void deleteEmployerById(String id);
}

package vn.iwork4se.service;

import vn.iwork4se.common.UserStatus;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.EmployerUpdateRequest;
import vn.iwork4se.controller.response.CompanyDetailResponse;
import vn.iwork4se.controller.response.CompanyListResponse;
import vn.iwork4se.controller.response.EmployerPageResponse;
import vn.iwork4se.controller.response.EmployerResponse;

import java.util.List;

public interface EmployerService {
    void updateEmployer(EmployerUpdateRequest req);
    EmployerResponse findEmployerById(String id);
    EmployerPageResponse findAllEmployers(String keyword, String sort, int page, int size);
    void deleteEmployerById(String id);
    void updateEmployerStatus(String id, UserStatus status);
    List<CompanyListResponse> findDistinctCompanies();
    CompanyDetailResponse getCompanyDetailByName(String companyName);
}

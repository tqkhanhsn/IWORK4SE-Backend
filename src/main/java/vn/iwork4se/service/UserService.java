package vn.iwork4se.service;

import vn.iwork4se.controller.request.ApplicantCreationRequest;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.EmployerCreationRequest;
import vn.iwork4se.controller.request.UserCreationRequest;
import vn.iwork4se.controller.response.EmailVerificationResponse;
import vn.iwork4se.controller.response.UserCreationResponse;

public interface UserService {
    UserCreationResponse createUser(UserCreationRequest request);
    UserCreationResponse createApplicant(ApplicantCreationRequest request);
    UserCreationResponse createEmployer(EmployerCreationRequest request);
    void changePassword(ChangePasswordRequest req);
    public boolean verifySecretCode(String secretCode,String email);
}

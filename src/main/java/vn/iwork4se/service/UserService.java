package vn.iwork4se.service;

import vn.iwork4se.controller.request.UserCreationRequest;
import vn.iwork4se.controller.response.UserCreationResponse;

public interface UserService {
    UserCreationResponse createUser(UserCreationRequest request);
}

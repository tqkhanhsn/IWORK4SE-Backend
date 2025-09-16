package vn.iwork4se.controller.response;

import lombok.Getter;
import lombok.Setter;
import vn.iwork4se.model.Applicant;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class EmployerPageResponse extends  PageResponseAbstract implements Serializable {
    private List<EmployerResponse> employers;
}

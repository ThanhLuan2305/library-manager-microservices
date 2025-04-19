package com.project.libmanage.user_service.criteria;

import lombok.Data;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.StringFilter;

@Data
public class UserCriteria {
    private StringFilter email;

    private StringFilter fullName;

    private StringFilter phoneNumber;

    private InstantFilter birthDate;

    private IntegerFilter lateReturnCount;
}

package com.project.libmanage.user_service.specification;


import com.project.libmanage.user_service.criteria.UserCriteria;
import com.project.libmanage.user_service.entity.User;
import com.project.libmanage.user_service.entity.User_;
import com.project.libmanage.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.jhipster.service.QueryService;

@Service
@RequiredArgsConstructor
public class UserQueryService extends QueryService<User> {
    private final UserRepository userRepository;

    public Page<User> findByCriteria(UserCriteria criteria, Pageable pageable) {
        Specification<User> specification = createSpecification(criteria);
        return userRepository.findAll(specification, pageable);
    }

    private Specification<User> createSpecification(UserCriteria criteria) {
        Specification<User> specification = Specification.where(null);
        if (criteria.getEmail() != null) {
            specification = specification.and(buildStringSpecification(criteria.getEmail(), User_.email));
        }

        if (criteria.getFullName() != null) {
            specification = specification.and(buildStringSpecification(criteria.getFullName(), User_.fullName));
        }

        if (criteria.getPhoneNumber() != null) {
            specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), User_.phoneNumber));
        }

        if (criteria.getBirthDate() != null) {
            specification = specification.and(buildRangeSpecification(criteria.getBirthDate(), User_.birthDate));
        }

        if (criteria.getLateReturnCount() != null) {
            specification = specification
                    .and(buildRangeSpecification(criteria.getLateReturnCount(), User_.lateReturnCount));
        }

        return specification;
    }
}

package rs.ac.uns.ftn.informatika.jpa.enumeration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import rs.ac.uns.ftn.informatika.jpa.model.Company;

import javax.persistence.criteria.Predicate;

public class CompanySpecification {

    public static Specification<Company> searchAndFilter(String search) {
        return (root, query, criteriaBuilder) -> {
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"+search.toLowerCase()+"%");
            Predicate locationCountryPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("location.country")), "%" + search.toLowerCase() + "%");
            Predicate locationCityPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("location.city")), "%" + search.toLowerCase() + "%");
            return criteriaBuilder.or(namePredicate, locationCountryPredicate, locationCityPredicate);
        };
    }
}

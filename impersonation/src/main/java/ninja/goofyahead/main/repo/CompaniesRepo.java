package ninja.goofyahead.main.repo;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class CompaniesRepo {

    public String getCompany(Long customerId) {
        return "You are getting the companies for user: " + customerId;
    }

}

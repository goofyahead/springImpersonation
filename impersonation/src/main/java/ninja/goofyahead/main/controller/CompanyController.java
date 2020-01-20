package ninja.goofyahead.main.controller;

import com.fasterxml.jackson.annotation.JsonView;
import ninja.goofyahead.main.annotations.AllowImpersonation;
import ninja.goofyahead.main.model.Company;
import ninja.goofyahead.main.model.CustomUserDetails;
import ninja.goofyahead.main.model.User;
import ninja.goofyahead.main.repo.CompaniesRepo;
import ninja.goofyahead.main.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("company")
public class CompanyController {

    private static final Logger log = LoggerFactory.getLogger(CompanyController.class);
    private CompaniesRepo companiesRepo;

    public CompanyController(CompaniesRepo companiesRepo) {
        this.companiesRepo = companiesRepo;
    }

    @GetMapping("/{id}")
    @AllowImpersonation
    @JsonView(View.Public.class)
    @PreAuthorize("hasAuthority('READ_COMPANY')")
    public String getUser(@PathVariable int id, Authentication authentication) {
        return companiesRepo.getCompany(((CustomUserDetails) authentication.getPrincipal()).getCustomerId());
    }

    @PostMapping
    public Company saveUser(@JsonView(View.Public.class) @RequestBody Company company) {
        log.info(company.toString());
        return company;
    }
}

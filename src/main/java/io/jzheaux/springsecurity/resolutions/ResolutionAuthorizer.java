package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Replaces @PostFilter("filterObject.owner == authentication.name || hasRole('ADMIN')") in controller
@Component("post")
public class ResolutionAuthorizer {

    // Replaces @PostFilter("filterObject.owner == authentication.name || hasRole('ADMIN')") in controller with @PostFilter("@post.filter(#root)")
    public boolean filter(MethodSecurityExpressionOperations operations) {
        if (operations.hasRole("ADMIN")) {
            return true;
        }
        String name = operations.getAuthentication().getName();
        Resolution resolution = (Resolution) operations.getFilterObject();

        return resolution.getOwner().equals(name);
    }

    // Replaces @PostAuthorize("returnObject.orElse(null)?.owner == authentication.name") in controller with @PostFilter("@post.authorize(#root)")
    public boolean authorize(MethodSecurityExpressionOperations operations) {
        if (operations.hasRole("ADMIN")) {
            return true;
        }

        String name = operations.getAuthentication().getName();
        Optional<Resolution> resolution = (Optional<Resolution>) operations.getReturnObject();
        return resolution.map(Resolution::getOwner)
                .filter(owner -> owner.equals(name)).isPresent();
    }
}

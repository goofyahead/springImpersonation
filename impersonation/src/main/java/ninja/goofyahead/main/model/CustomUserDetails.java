package ninja.goofyahead.main.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomUserDetails  {

    public Long customerId;
    public String location;
    public String username;
    public boolean isAdminImpersonating;
}

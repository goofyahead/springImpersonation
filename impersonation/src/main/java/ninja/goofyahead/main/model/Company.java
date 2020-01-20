package ninja.goofyahead.main.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ninja.goofyahead.main.views.View;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @JsonView(View.Public.class)
    private String id;
    @JsonView(View.Public.class)
    private String name;
    @JsonView(View.Internal.class)
    private int nOfEmployees;
}

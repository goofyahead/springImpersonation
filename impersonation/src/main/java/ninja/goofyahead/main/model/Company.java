package ninja.goofyahead.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    private String id;
    private String name;
    private int nOfEmployees;
}

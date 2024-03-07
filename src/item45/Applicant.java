package item45;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Applicant {
    private final long id;
    private final String name;
    private final String department;
    private final List<Career> careers;

    @Builder
    public Applicant(long id, String name, String department, List<Career> careers) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.careers = careers;
    }
}

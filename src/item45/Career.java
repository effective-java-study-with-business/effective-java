package item45;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Career {

    private final String company;
    private final String department;
    private final int workingDays;

    @Builder
    public Career(String company, String department, int workingDays) {
        this.company = company;
        this.department = department;
        this.workingDays = workingDays;
    }
}

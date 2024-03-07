package item45;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BlindInterviewee {

    private final long id;
    private final String department;

    @Builder
    public BlindInterviewee(long id, String department) {
        this.id = id;
        this.department = department;
    }
}

package item45;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicantServiceImpl {

    public List<BlindInterviewee> pickIntervieweeFromApplicants(List<Applicant> applicantList) {
        return applicantList.stream()
                .filter(applicant -> !applicant.getCareers().isEmpty())
                .map(passed -> BlindInterviewee.builder()
                        .id(passed.getId())
                        .department(passed.getDepartment())
                        .build())
                .sorted(Comparator.comparing(BlindInterviewee::getDepartment))
                .collect(Collectors.toList());
    }

    public double getAverageWorkingDays(List<Applicant> applicantList) {
        return applicantList.stream()
                .flatMapToInt(applicant -> applicant.getCareers().stream()
                        .mapToInt(Career::getWorkingDays))
                .average()
                .getAsDouble();
    }


}

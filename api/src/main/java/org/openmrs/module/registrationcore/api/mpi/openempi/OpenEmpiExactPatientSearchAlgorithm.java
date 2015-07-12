package org.openmrs.module.registrationcore.api.mpi.openempi;

import org.openmrs.Patient;
import org.openmrs.module.registrationcore.api.mpi.common.MpiSimilarPatientSearchAlgorithm;
import org.openmrs.module.registrationcore.api.search.PatientAndMatchQuality;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service("registrationcore.OpenEmpiExactPatientSearchAlgorithm")
public class OpenEmpiExactPatientSearchAlgorithm implements MpiSimilarPatientSearchAlgorithm {

    private RestQueryCreator restQueryCreator;

    @Override
    public List<PatientAndMatchQuality> findSimilarPatients(Patient patient,
                                                            Map<String, Object> otherDataPoints,
                                                            Double cutoff, Integer maxResults) {
        OpenEmpiPatientQuery patientQuery = PatientQueryMapper.convert(patient);

        List<OpenEmpiPatientQuery> mpiPatients = restQueryCreator.findPatients(patientQuery);

        if (mpiPatients.size() > maxResults) {
            mpiPatients.subList(0, maxResults);
        }

        return convertMpiPatients(mpiPatients, cutoff);
    }

    @Override
    public List<PatientAndMatchQuality> findExactSimilarPatients(Patient patient, Map<String, Object> otherDataPoints, Double cutoff, Integer maxResults) {
        //TODO should be changed to implementation which query to SimilarPatients, not exact.
        return findSimilarPatients(patient, otherDataPoints, cutoff, maxResults);
    }

    private List<PatientAndMatchQuality> convertMpiPatients(List<OpenEmpiPatientQuery> mpiPatients,
                                                            Double cutoff) {
        List<String> matchedFields = Arrays.asList("personName", "gender", "birthdate");
        List<PatientAndMatchQuality> result = new LinkedList<PatientAndMatchQuality>();
        for (OpenEmpiPatientQuery mpiPatient : mpiPatients) {
            Patient convertedPatient = PatientQueryMapper.convert(mpiPatient);
            PatientAndMatchQuality resultPatient =
                    new PatientAndMatchQuality(convertedPatient, cutoff, matchedFields);
            result.add(resultPatient);
        }
        return result;
    }

    public void setRestQueryCreator(RestQueryCreator restQueryCreator) {
        this.restQueryCreator = restQueryCreator;
    }
}

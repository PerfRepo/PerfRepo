package org.perfrepo.client;

import org.perfrepo.dto.metric.MetricDto;
import java.util.List;

/**
 *
 * @author Adam Krajcik
 */
public class MetricClient {
    
    private PerfRepoClient parentClient;

    public MetricClient(PerfRepoClient parentClient) {
        this.parentClient = parentClient;
    }
       
    public MetricDto getById(Long metricId) {
        return null;
    }

    public void update(MetricDto metricDto) {

    }

    public List<MetricDto> getAll() {
        return null;
    }
    

}

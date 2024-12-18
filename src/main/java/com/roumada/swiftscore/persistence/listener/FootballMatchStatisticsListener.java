package com.roumada.swiftscore.persistence.listener;

import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.persistence.sequence.PrimarySequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FootballMatchStatisticsListener extends AbstractMongoEventListener<FootballMatchStatistics> {

    private final PrimarySequenceService primarySequenceService;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<FootballMatchStatistics> event) {
        if (event.getSource().getId() == null) {
            event.getSource().setId(primarySequenceService.getNextValue());
        }
    }

}
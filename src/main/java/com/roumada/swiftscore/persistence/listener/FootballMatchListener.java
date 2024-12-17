package com.roumada.swiftscore.persistence.listener;

import com.roumada.swiftscore.data.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.sequence.PrimarySequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FootballMatchListener extends AbstractMongoEventListener<FootballMatch> {

    private final PrimarySequenceService primarySequenceService;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<FootballMatch> event) {
        if (event.getSource().getId() == null) {
            event.getSource().setId(primarySequenceService.getNextValue());
        }
    }

}
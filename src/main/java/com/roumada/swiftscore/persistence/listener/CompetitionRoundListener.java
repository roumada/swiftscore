package com.roumada.swiftscore.persistence.listener;

import com.roumada.swiftscore.data.model.match.CompetitionRound;
import com.roumada.swiftscore.persistence.sequence.PrimarySequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompetitionRoundListener extends AbstractMongoEventListener<CompetitionRound> {

    private final PrimarySequenceService primarySequenceService;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<CompetitionRound> event) {
        if (event.getSource().getId() == null) {
            event.getSource().setId(primarySequenceService.getNextValue());
        }
    }

}
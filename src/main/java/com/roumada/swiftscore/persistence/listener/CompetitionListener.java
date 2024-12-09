package com.roumada.swiftscore.persistence.listener;

import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.persistence.PrimarySequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompetitionListener extends AbstractMongoEventListener<Competition> {

    private final PrimarySequenceService primarySequenceService;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Competition> event) {
        if (event.getSource().getId() == null) {
            event.getSource().setId(primarySequenceService.getNextValue());
        }
    }

}
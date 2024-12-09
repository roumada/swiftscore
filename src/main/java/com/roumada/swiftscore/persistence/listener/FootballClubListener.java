package com.roumada.swiftscore.persistence.listener;

import com.roumada.swiftscore.persistence.sequence.PrimarySequenceService;
import com.roumada.swiftscore.model.FootballClub;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FootballClubListener extends AbstractMongoEventListener<FootballClub> {

    private final PrimarySequenceService primarySequenceService;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<FootballClub> event) {
        if (event.getSource().getId() == null) {
            event.getSource().setId(primarySequenceService.getNextValue());
        }
    }

}
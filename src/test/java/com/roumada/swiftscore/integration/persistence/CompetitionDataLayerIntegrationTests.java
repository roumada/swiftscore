package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import org.springframework.beans.factory.annotation.Autowired;

class CompetitionDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubDataLayer fcDataLayer;
    @Autowired
    private CompetitionDataLayer dataLayer;


}

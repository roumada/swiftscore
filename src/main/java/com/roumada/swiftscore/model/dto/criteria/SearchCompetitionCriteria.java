package com.roumada.swiftscore.model.dto.criteria;

import com.neovisionaries.i18n.CountryCode;
import org.apache.commons.lang3.StringUtils;

public record SearchCompetitionCriteria(String name, CountryCode country, String season) implements SearchCriteria {
    @Override
    public boolean hasNoCriteria() {
        return StringUtils.isEmpty(name) && StringUtils.isEmpty(season) && country == null;
    }

    @Override
    public boolean hasOneCriteria() {
        int criteriaCount = 0;
        if (StringUtils.isNotEmpty(name)) criteriaCount++;
        if (StringUtils.isNotEmpty(season)) criteriaCount++;
        if (country != null) criteriaCount++;
        return criteriaCount == 1;
    }

    @Override
    public SingleCriteriaType getSingleCriteriaType() {
        if (StringUtils.isNotEmpty(name)) return SingleCriteriaType.NAME;
        if (StringUtils.isNotEmpty(season)) return SingleCriteriaType.SEASON;
        if (country != null) return SingleCriteriaType.COUNTRY;
        return SingleCriteriaType.NONE;
    }
}

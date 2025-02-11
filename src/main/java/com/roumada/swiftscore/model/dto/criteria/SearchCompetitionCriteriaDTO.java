package com.roumada.swiftscore.model.dto.criteria;

import com.neovisionaries.i18n.CountryCode;
import org.apache.commons.lang3.StringUtils;

public record SearchCompetitionCriteriaDTO(String name, CountryCode country) implements SearchCriteria {
    @Override
    public boolean hasNoCriteria() {
        return StringUtils.isEmpty(name) && country == null;
    }

    @Override
    public boolean hasOneCriteria() {
        int criteriaCount = 0;
        if (StringUtils.isNotEmpty(name)) criteriaCount++;
        if (country != null) criteriaCount++;
        return criteriaCount == 1;
    }

    @Override
    public SingleCriteriaType getSingleCriteriaType() {
        if (StringUtils.isNotEmpty(name)) return SingleCriteriaType.NAME;
        if (country != null) return SingleCriteriaType.COUNTRY;
        return SingleCriteriaType.NONE;
    }
}

package com.roumada.swiftscore.model.dto.criteria;

public interface SearchCriteria {
    boolean hasNoCriteria();
    boolean hasOneCriteria();
    SingleCriteriaType getSingleCriteriaType();

    public enum SingleCriteriaType{
        NAME,
        COUNTRY,
        STADIUM_NAME
    }
}

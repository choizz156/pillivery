package com.team33.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Data;

@Data
public class ItemDto {

    @JsonProperty("ENTRPS")
    private String entrps;
    @JsonProperty("PRDUCT")
    private String prduct;
    @JsonProperty("STTEMNT_NO")
    private String sttemntNo;
    @JsonProperty("REGIST_DT")
    private String registDt;
    @JsonProperty("DISTB_PD")
    private String distbPd;
    @JsonProperty("SUNGSANG")
    private String sungsang;
    @JsonProperty("SRV_USE")
    private String srvUse;
    @JsonProperty("PRSRV_PD")
    private String prsrvPd;
    @JsonProperty("INTAKE_HINT1")
    private String intakeHint1;
    @JsonProperty("MAIN_FNCTN")
    private String mainFnctn;
    @JsonProperty("BASE_STANDARD")
    private String baseStandard;
    @Min(1000) @Max(500000)
    private int originPrice;
    @Min(10) @Max(80)
    private int discountRate;

}

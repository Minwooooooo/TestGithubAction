package com.example.TestActionANDIgnore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class LottoResponseDto {

    /*
    몇회차 당첨번호
    1등 당첨금액 :
    1등 당첨자 :
    2등 당첨자 :
    3등 당첨자 :
     */

    private int[] num;

    private int money1st;

    private int member1st;
    private int member2rd;
    private int member3nd;


}

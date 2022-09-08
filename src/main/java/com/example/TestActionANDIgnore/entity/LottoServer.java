package com.example.TestActionANDIgnore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LottoServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int point;

    @Column
    private int luckyNum1;
    @Column
    private int luckyNum2;
    @Column
    private int luckyNum3;
    @Column
    private int luckyNum4;
    @Column
    private int luckyNum5;
    @Column
    private int luckyNum6;

    @Column
    private int bonusNum;

    @Column
    private int point1st;

    @Column
    private int point2nd;

    @Column
    private int point3rd;

    public void plusPoint(int point){
        this.point+=point;
    }



    public void setLuckyNum(int[] luckyNum) {
        this.luckyNum1=luckyNum[0];
        this.luckyNum2=luckyNum[1];
        this.luckyNum3=luckyNum[2];
        this.luckyNum4=luckyNum[3];
        this.luckyNum5=luckyNum[4];
        this.luckyNum6=luckyNum[5];
        this.bonusNum=luckyNum[6];
    }

    public void setPoint(long point1st,long point2nd, long point3rd){
        this.point1st=(int) point1st;
        this.point2nd=(int) point2nd;
        this.point3rd=(int) point3rd;

    }
}

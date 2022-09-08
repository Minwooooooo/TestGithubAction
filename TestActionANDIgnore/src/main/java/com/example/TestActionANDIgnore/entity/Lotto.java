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
public class Lotto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int num1;

    @Column
    private int num2;

    @Column
    private int num3;

    @Column
    private int num4;

    @Column
    private int num5;

    @Column
    private int num6;

    @JoinColumn(name = "lottosever_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private LottoServer lottoServer;

    @Column
    private long result;

    @Column
    private int earnPoint;


    public void plusOne(){
        this.result+=1;
    }
    public void plusBonus(){
        this.result+=10;
    }

    public void rank(){
        if(this.result==6){
            this.result=1;
        }
        else if(this.result==15){
            this.result=2;
        }
        else if(this.result==5){
            this.result=3;
        }
        else if(this.result==4||this.result==14){
            this.result=4;
        }
        else if(this.result==3||this.result==13){
            this.result=5;
        }
        else{
            this.result=6;
        }
    }
    public void setEarnPoint(int point){
        this.earnPoint+=point;
    }

}

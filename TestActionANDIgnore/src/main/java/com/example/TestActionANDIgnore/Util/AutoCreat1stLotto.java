package com.example.TestActionANDIgnore.Util;

import com.example.TestActionANDIgnore.entity.LottoServer;
import com.example.TestActionANDIgnore.repo.LottoServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AutoCreat1stLotto implements ApplicationRunner {

    private final LottoServerRepository lottoServerRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception{
        System.out.println("AutoCreat1stLotto 실행 ");
        if(lottoServerRepository.count()==0){
            LottoServer firstLottoServer= LottoServer.builder()
                    .point(3000)
                    .luckyNum1(0)
                    .luckyNum2(0)
                    .luckyNum3(0)
                    .luckyNum4(0)
                    .luckyNum4(0)
                    .luckyNum5(0)
                    .luckyNum6(0)
                    .bonusNum(0)
                    .point1st(0)
                    .point2nd(0)
                    .point3rd(0)
                    .build();

            lottoServerRepository.save(firstLottoServer);
        }
    }


}

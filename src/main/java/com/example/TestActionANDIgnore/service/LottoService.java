package com.example.TestActionANDIgnore.service;


import com.example.TestActionANDIgnore.dto.request.LottoRequestDto;
import com.example.TestActionANDIgnore.dto.response.LottoResponseDto;
import com.example.TestActionANDIgnore.dto.response.ResponseDto;
import com.example.TestActionANDIgnore.entity.Lotto;
import com.example.TestActionANDIgnore.entity.LottoServer;
import com.example.TestActionANDIgnore.repo.LottoRepository;
import com.example.TestActionANDIgnore.repo.LottoServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LottoService {

    /*
    매시 00분 n+1회차 LottoServer 테이블 생성
            n회차 결산
            n회차 결산 후 남은금액 n+1회차로 넘기기
            ->서버 오픈시 1회차 자동생성

    매시 03분 ~ 57분 n+1회차 로또 구매 받기
     */

    private final LottoRepository lottoRepository;
    private final LottoServerRepository lottoServerRepository;


    //상수

    static final int LOTTO_POINT=1000; // 로또 금액
    static final int LOTTO_MAX_NUM=20; // 로또 추첨 범위
    // static final int BASIC_1ST_POINT = 15000000; // 1등 기본 당첨금
    @Value("${point.1st}")
    int BASIC_1ST_POINT;
    @Value("${point.2nd}")
    int BASIC_2ND_POINT; // 2등 기본 당첨금
    @Value("${point.3rd}")
    int BASIC_3RD_POINT; // 3등 기본 당첨금


    /* 왜 Java는 21억까지밖에 계산이 안되는것인가 ㅡㅡ
    MaxNum에 따른 확률 -> 기댓값을 통한 대략적인 당첨금액(단, 로또 금액은 1000p)
    1등 : 1 / 38760  -> 30,000,000
    2등 : 1 / 6460   ->  6,000,000
    3등 : 1 / 497    ->    400,000
    4등 : 1 / 33     ->     20,000
    5등 : 1 / 7      ->      5,000
     */

    /*
    <실제 로또 등수 및 당첨금 산정 방법>
    1등 : 6개 번호 모두 일치
        => 총 포인트 중 4~5등을 제외한 포인트의 75%
    2등 : 5개 번호 일치 + 보너스볼 번호 일치
        => 총 포인트 중 4~5등을 제외한 포인트의 12.5%
    3등 : 5개 번호 일치
        => 총 포인트 중 4~5등을 제외한 포인트의 12.5%
    4등 : 4개 번호 일치
        => 로또 금액 * 50
    5등 : 3개 번호 일치
        => 로또 금액 * 5
     */

    /*
    결론 :
    1등 15,000,000 + 총 포인트의 75%
    2등 3,000,000 + 총 포인트의 12.5%
    3등 200,000 + 총 포인트의 12.5%
    4등 20,000
    5등 5,000
    */



    //매시 00분 실행
    @Transactional
    public ResponseDto<?> runLotto(){
        long lastId=lottoServerRepository.count(); //로또 회차 구하기
        int[] luckyNum=luckyNum();
        lottoServerRepository.findById(lastId).get().setLuckyNum(luckyNum);
        checkLotto(lastId,luckyNum);

        LottoResponseDto temp_dto = lottoFinal(lastId);
        LottoResponseDto lottoResponseDto = LottoResponseDto.builder()
                .num(luckyNum)
                .member1st(temp_dto.getMember1st())
                .member2rd(temp_dto.getMember2rd())
                .member3nd(temp_dto.getMember3nd())
                .money1st(temp_dto.getMoney1st())
                .build();
        return ResponseDto.success(lottoResponseDto);
    }


    @Transactional
    // 제출 번호 저장 및 당첨금 설정
    public ResponseDto<?> saveNum(LottoRequestDto lottoRequestDto){

        // 구매 회차 확인
        long lastId=lottoServerRepository.count();
        LottoServer nowSever=lottoServerRepository.findById(lastId).get();

        //구매 정보 저장
        Lotto lotto;

        // 번호 자동 선택
        if(lottoRequestDto.getNum1()==0){
            int[] randomNum=luckyNum();
            lotto = Lotto.builder()
                    .lottoServer(nowSever)
                    .num1(randomNum[0])
                    .num2(randomNum[1])
                    .num3(randomNum[2])
                    .num4(randomNum[3])
                    .num5(randomNum[4])
                    .num6(randomNum[5])
                    .result(0)
                    .earnPoint(0)
                    .build();
        }
        // 번호 수동 선택
        else {
            lotto = Lotto.builder()
                    .lottoServer(nowSever)
                    .num1(lottoRequestDto.getNum1())
                    .num2(lottoRequestDto.getNum2())
                    .num3(lottoRequestDto.getNum3())
                    .num4(lottoRequestDto.getNum4())
                    .num5(lottoRequestDto.getNum5())
                    .num6(lottoRequestDto.getNum6())
                    .result(0)
                    .earnPoint(0)
                    .build();
        }

        int[] numList=new int[6];
        numList[0]= lotto.getNum1();
        numList[1]= lotto.getNum2();
        numList[2]= lotto.getNum3();
        numList[3]= lotto.getNum4();
        numList[4]= lotto.getNum5();
        numList[5]= lotto.getNum6();



        nowSever.plusPoint(LOTTO_POINT);

        lottoRepository.save(lotto);
        return ResponseDto.success(numList);
    }


    // 번호 추첨

    public int[] luckyNum(){
        Random random = new Random();
        int[] luckynum=new int[7];
        luckynum[0]=random.nextInt(LOTTO_MAX_NUM)+1;
        for (int i = 1; i < 7; i++) {
            luckynum[i]=random.nextInt(LOTTO_MAX_NUM)+1;
            for (int j = 0; j < i; j++) {
                if(luckynum[i]==luckynum[j]){
                    i-=1;
                }
            }

        }
        return luckynum;
    }

    // 맞은 갯수 확인
    @Transactional
    public void checkLotto(long lastid,int[] luckyNum){
        List<Lotto> lottoList=lottoRepository.findByLottoServerId(lastid);
        for (int i = 0; i < lottoList.size(); i++) {
            Lotto lotto=lottoList.get(i);
            for (int j = 0; j < 6; j++) {
                if(lotto.getNum1()==luckyNum[j]||lotto.getNum2()==luckyNum[j]||lotto.getNum3()==luckyNum[j]||lotto.getNum4()==luckyNum[j]||lotto.getNum5()==luckyNum[j]||lotto.getNum6()==luckyNum[j]){
                    lotto.plusOne();
                }
            }
            if (lotto.getNum1()==luckyNum[6]||lotto.getNum2()==luckyNum[6]||lotto.getNum3()==luckyNum[6]||lotto.getNum4()==luckyNum[6]||lotto.getNum5()==luckyNum[6]||lotto.getNum6()==luckyNum[6]){
                lotto.plusBonus();
            }

            lotto.rank();

        }
    }

    /*
    Lotto Entity에서 result에 따른 결과
    6= 1등
    15= 2등
    5= 3등
    4.14= 4등
    3,13= 5등
    else =6등(꽝)
     */


    // 정산
    @Transactional
    public LottoResponseDto lottoFinal(long lastId){
        List<Lotto> lottoList=lottoRepository.findByLottoServerId(lastId);
        LottoServer lottoServer=lottoServerRepository.findById(lastId).get();
        // DB에서 구매 총액 가져오기
        long totalPoint=lottoServer.getPoint();
        LottoResponseDto lottoResponseDto;

        //구매자가 0명일때
        if(lottoList.size()==0){
            lottoResponseDto = LottoResponseDto.builder()
                    .money1st(0)
                    .member1st(0)
                    .member2rd(0)
                    .member3nd(0)
                    .build();        }
        else {
            // 등수 확인
            List<Lotto> count1st = lottoRepository.findAllByLottoServerIdAndResult(lastId,1);
            List<Lotto> count2nd = lottoRepository.findAllByLottoServerIdAndResult(lastId,2);
            List<Lotto> count3rd = lottoRepository.findAllByLottoServerIdAndResult(lastId,3);
            List<Lotto> count4th = lottoRepository.findAllByLottoServerIdAndResult(lastId,4);
            List<Lotto> count5th = lottoRepository.findAllByLottoServerIdAndResult(lastId,5);
            List<Lotto> count6th = lottoRepository.findAllByLottoServerIdAndResult(lastId,6);



            // 구매금액으로부터 당첨 금액 분할
            long temp_point1st = (long) (totalPoint * 0.75);//  +15,000,000
            long temp_point2nd = (long) (totalPoint * 0.125);// +3,000,000
            long temp_point3rd = (long) (totalPoint * 0.125);// +200,000
            long temp_point4th = LOTTO_POINT * 20;
            long temp_point5th = LOTTO_POINT * 5;

            // 분할된 금액과 당첨금액 합산(최종 지급 금액)
            long point1st=temp_point1st+BASIC_1ST_POINT;
            long point2nd=temp_point2nd+BASIC_2ND_POINT;
            long point3rd=temp_point3rd+BASIC_3RD_POINT;
            long point4th=temp_point4th;
            long point5th=temp_point5th;


            // 당첨금액 분할시 0으로 나누어지는 오류 사전 방지
            if (count1st.size() != 0) {
                point1st = point1st / count1st.size();
            }

            if (count2nd.size() != 0) {
                point2nd = point2nd / count2nd.size();
            }

            if (count3rd.size() != 0) {
                point3rd = point3rd / count3rd.size();
            }

            // 당첨금액 DB에 저장
            lottoServer.setPoint(point1st,point2nd,point3rd);

            lottoResponseDto = LottoResponseDto.builder()
                    .money1st((int) point1st)
                    .member1st(count1st.size())
                    .member2rd(count2nd.size())
                    .member3nd(count3rd.size())
                    .build();
        }
        // +남은 totalPoint는 DB에 저장
        LottoServer nextLottoServer= LottoServer.builder()
                .point((int) totalPoint)
                .luckyNum1(0)
                .luckyNum2(0)
                .luckyNum3(0)
                .luckyNum4(0)
                .luckyNum4(0)
                .luckyNum5(0)
                .luckyNum6(0)
                .point1st(0)
                .point2nd(0)
                .point3rd(0)
                .build();

        lottoServerRepository.save(nextLottoServer);

        return lottoResponseDto;
    }




}

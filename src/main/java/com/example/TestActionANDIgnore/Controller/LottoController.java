package com.example.TestActionANDIgnore.Controller;

import com.example.TestActionANDIgnore.dto.request.LottoRequestDto;
import com.example.TestActionANDIgnore.dto.response.ResponseDto;
import com.example.TestActionANDIgnore.service.LottoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LottoController {
    private final LottoService lottoService;

    // 로또구매
    @RequestMapping(value = "/api/game/lotto", method = RequestMethod.POST)
    public ResponseDto<?> runLotto(@RequestBody LottoRequestDto lottoRequestDto) {
        return lottoService.saveNum(lottoRequestDto);
    }


    // 로또 추첨
    @RequestMapping (value = "/api/game/lottotest", method = RequestMethod.GET)
    public ResponseDto<?> testLotto() {
        return lottoService.runLotto();
    }

    @RequestMapping(value = "test",method = RequestMethod.GET)
    public int testnum(){
        return 1;
    }
}

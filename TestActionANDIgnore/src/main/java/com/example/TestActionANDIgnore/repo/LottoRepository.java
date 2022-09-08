package com.example.TestActionANDIgnore.repo;


import com.example.TestActionANDIgnore.entity.Lotto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LottoRepository extends JpaRepository<Lotto, Long> {
    List<Lotto> findByLottoServerId(long lastid);

    List<Lotto> findAllByLottoServerIdAndResult(long lottoserver_id,long result);

    List<Lotto> findAllByLottoServerId(long lottosever_id);
}

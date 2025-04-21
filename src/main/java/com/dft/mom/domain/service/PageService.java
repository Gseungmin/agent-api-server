package com.dft.mom.domain.service;

import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.repository.PageRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.dft.mom.domain.util.PostConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PageService {

    private final PageRepository pageRepository;

    @PostConstruct
    public void init() {
        initPages(TYPE_PREGNANCY_EXAM,   List.of(PERIOD_TOTAL));
        initPages(TYPE_CHILDCARE_EXAM,   List.of(PERIOD_TOTAL));
        initPages(TYPE_PREGNANCY_GUIDE,  FETAL_PERIOD_LIST);
        initPages(TYPE_CHILDCARE_GUIDE,  BABY_PERIOD_LIST);
    }

    private void initPages(int type, List<Integer> periods) {
        for (Integer period : periods) {
            createPages(type, period);
        }
    }

    private void createPages(int type, int period) {
        Optional<BabyPage> page = pageRepository.findBabyByTypeAndPeriod(type, period);

        if (page.isEmpty()) {
            pageRepository.save(new BabyPage(type, period));
        }
    }
}
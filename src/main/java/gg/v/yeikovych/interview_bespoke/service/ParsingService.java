package gg.v.yeikovych.interview_bespoke.service;

import gg.v.yeikovych.interview_bespoke.model.IdHolder;
import org.springframework.stereotype.Service;

@Service
public class ParsingService {

    public IdHolder parseId(String id) {
        return new IdHolder(id);
    }
}

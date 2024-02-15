package ru.bereshs.HHWorkSearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;

@Service
public class NegotiationsService {

    private final HeadHunterClient client;

    @Autowired
    public NegotiationsService(HeadHunterClient client) {
        this.client = client;
    }


}

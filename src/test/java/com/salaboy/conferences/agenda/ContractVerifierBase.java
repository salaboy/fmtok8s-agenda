package com.salaboy.conferences.agenda;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0")
@Import(TestConfiguration.class)
public abstract class ContractVerifierBase {

    @LocalServerPort
    int port;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RestAssured.baseURI = "http://localhost:" + this.port;
    }


}
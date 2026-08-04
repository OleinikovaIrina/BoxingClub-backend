package de.oleinikova.boxingclub.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "ai.groq.key=test-groq-key",
        "security.jwt.secret=Ym94aW5nLWNsdWItdGVzdC1qd3Qtc2VjcmV0LTMyYnl0ZXM=",
        "telegram.bot.enabled=false"
})
class BoxingClubBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}

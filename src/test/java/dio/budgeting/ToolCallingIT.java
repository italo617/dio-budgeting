package dio.budgeting;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".*")
public class ToolCallingIT {
    @Autowired
    OpenAiChatModel openAiChatModel;

    static class MathTools {
        @Tool(description = "sum two integers, a and b.")
        public int sum(int a, int b) {
            return a + b;
        }

        @Tool(description = "subtract two integers, a and b")
        public int diff(int a, int b) {
            return a - b;
        }
    }

    @Test
    void should_executeSum_when_prompted() {
        var chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem("You are a mathematician. Answer the prompts using numbers only. Do not include any words or explanations.")
                .defaultTools(new MathTools())
                .build();

        var response = chatClient
                .prompt("Sum 10 and 20. After that, subtract 30 from the previous result.")
                .call()
                .content();

        Assertions.assertThat(response).contains("0");
        System.out.println(response);
    }
}

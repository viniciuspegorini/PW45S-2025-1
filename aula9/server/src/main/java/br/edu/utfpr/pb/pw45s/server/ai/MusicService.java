package br.edu.utfpr.pb.pw45s.server.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MusicService {
    public static final String BASE_MUSIC = """
        Escreva uma Música do gênero Rock sobre a Vida de um programador java,
        a música deve ter 2 versos e um refrão
        """;

    private final ChatModel aiClient;

    @Autowired
    public MusicService(@Qualifier("openAiChatModel") ChatModel aiClient) {
        this.aiClient = aiClient;
    }
    public String getMusic() {
        return aiClient.call(BASE_MUSIC);
    }

    public MusicDTO getMusicByGenreAndTheme(String genre, String theme) {
        BeanOutputConverter<MusicDTO> outputConverter = new BeanOutputConverter<>(MusicDTO.class);

        String promptString = """
            Escreva uma música do gênero {genre} sobre {theme}
            a música deve ter 2 versos e um refrão
            {format}
            """;

        PromptTemplate promptTemplate = new PromptTemplate(promptString);
        promptTemplate.add("genre", genre);
        promptTemplate.add("theme", theme);
        promptTemplate.add("format", outputConverter.getFormat());

        ChatResponse response = aiClient.call(promptTemplate.create());
        return outputConverter.convert(response.getResult().getOutput().getText());
    }
}

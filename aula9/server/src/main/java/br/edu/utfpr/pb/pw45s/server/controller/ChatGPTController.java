package br.edu.utfpr.pb.pw45s.server.controller;
import br.edu.utfpr.pb.pw45s.server.service.ChatGPTService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gpt")
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    public ChatGPTController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @PostMapping
    public String askQuestion(@RequestBody String message) {
        return chatGPTService.ask(message);
    }
}
